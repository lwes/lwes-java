/*======================================================================*
 * Copyright (c) 2008, Yahoo! Inc. All rights reserved.                 *
 *                                                                      *
 * Licensed under the New BSD License (the "License"); you may not use  *
 * this file except in compliance with the License.  Unless required    *
 * by applicable law or agreed to in writing, software distributed      *
 * under the License is distributed on an "AS IS" BASIS, WITHOUT        *
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     *
 * See the License for the specific language governing permissions and  *
 * limitations under the License. See accompanying LICENSE file.        *
 *======================================================================*/

package org.lwes.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwes.Event;

/**
 * Dispatches events to downstream handlers using threads.
 * @author Preston Pfarner
 * @author Michael P. Lum
 */
public class ThreadedEventDispatcher extends Thread {

    private static transient Log log = LogFactory.getLog(ThreadedEventDispatcher.class);

	/* dequeuer controlling this object */
	private ThreadedDequeuer dequeuer;
	private EventHandler eventHandler;
	private Event event;

	protected ThreadedEventDispatcher(ThreadedDequeuer aDequeuer) {
		this.dequeuer = aDequeuer;
		super.start();
		setName("EventDispatcher");
	}

	public void setTask(EventHandler aHandler, Event anEvent) throws IllegalStateException {
		if(isIdle()) {
			synchronized(this) {
				eventHandler = aHandler;
				event = anEvent;
			}
			synchronized(this) { notifyAll(); }
		} else {
			throw new IllegalStateException("Processor already has a listener");
		}
	}

	public final boolean isActive() {
		return (! isIdle());
	}

	public final boolean isIdle() {
		final boolean p1 = (eventHandler == null);
		final boolean p2 = (event == null);
		if(p1 == p2) return p1;
		else throw new IllegalStateException("Contradictory state indication");
	}

	public void run() {
		while(true) {
			synchronized(this) {
				if(isActive()) {
					try {
						eventHandler.handleEvent(event);
					} catch(Exception e) {
						log.warn("Caught exception handling event", e);
					}
					clearTask();
				} else {
					try {
						wait();
					} catch(InterruptedException e) {}
				}
			}
		}
	}

	private void clearTask() {
		synchronized(this) {
			eventHandler = null;
			event = null;
		}

		dequeuer.makeAvailable(this);
	}

}
