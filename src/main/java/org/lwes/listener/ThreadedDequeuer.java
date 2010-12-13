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

import org.lwes.Event;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * An abstract consumer of events.
 *
 * @author Michael P. Lum
 *
 */
public abstract class ThreadedDequeuer implements Runnable {
	/* the minimum and maximum threads to allow */
	private static final int MIN_THREADS = 1;
	private static final int MAX_THREADS = 64;

	protected LinkedBlockingQueue<QueueElement> queue = null;
	private HashMap<String, EventHandler> handlers = null;

	/* the maximum number of threads allowed */
	private int maxThreads = 20;

	/* the event dispatchers */
	private List<ThreadedEventDispatcher> idleProcessors = null;

	/**
	 * Default constructor.
	 */
	public ThreadedDequeuer() {
	}

	/**
	 * Returns the queue to use for this dequeuer
	 *
	 * @return the List queue
	 */
	public synchronized LinkedBlockingQueue<QueueElement> getQueue() {
		return this.queue;
	}

	/**
	 * Sets the queue to use for this dequeuer. Warning: this List must be
	 * thread-synchronized!
	 *
	 * @param queue
	 *            the thread-synchronized List element
	 */
	public synchronized void setQueue(LinkedBlockingQueue<QueueElement> queue) {
		this.queue = queue;
	}

	/**
	 * Gets the maximum number of threads allowed in the system
	 * @return the number of threads
	 */
	public int getMaxThreads() {
		return this.maxThreads;
	}

	/**
	 * Sets the maximum number of threads allowed in the system, up to 64.  The default is 20.
	 * @param threads the number of threads to allow.
	 */
	public synchronized void setMaxThreads(int threads) {
		if(threads >= MIN_THREADS && threads <= MAX_THREADS) {
			this.maxThreads = threads;
		}
	}

	/**
	 * Get an event handler by name.  Returns null if the handler does not exist.
	 * @param name the name of the event handler to fetch
	 * @return the EventHandler
	 */
	public EventHandler getHandler(String name) {
		if(handlers == null || name == null) return null;
		return (EventHandler) handlers.get(name);
	}

	/**
	 * Add an event handler to this dequeuer. Events coming into the system will
	 * call all handlers via their callback.
	 *
	 * @param handler
	 *            the actual handler
	 */
	public void addHandler(EventHandler handler) {
		if (handler == null)
			return;
		addHandler("handler" + handler.hashCode(), handler);
	}

	/**
	 * Adds a handler to this dequeuer with a specified name. Events coming into
	 * the system will call all handlers via their callback.
	 *
	 * @param name
	 *            the name of this handler
	 * @param handler
	 *            the actual handler
	 */
	public void addHandler(String name, EventHandler handler) {
		if (name == null || handler == null)
			return;
		if (handlers == null)
			handlers = new HashMap<String, EventHandler>();
		if (!handlers.containsKey(name)) {
			handlers.put(name, handler);
		}
	}

	/**
	 * Removes a handler so it no longer is processing events
	 *
	 * @param handler
	 *            the handler to remove
	 */
	public void removeHandler(EventHandler handler) {
		if (handlers == null || handler == null)
			return;
		handlers.remove("handler" + handler.hashCode());
	}

	/**
	 * Removes a handler by name so it no longer is processing events
	 *
	 * @param name
	 *            the name of the handler to remove
	 */
	public void removeHandler(String name) {
		if (handlers == null || name == null)
			return;
		handlers.remove(name);
	}

	/**
	 * Default initialize() method. Should be overridden by classes extending
	 * ThreadedEnqueuer.
	 */
	public void initialize() throws IOException {
		idleProcessors = Collections.synchronizedList(new LinkedList<ThreadedEventDispatcher>());
		while(idleProcessors.size() < maxThreads) {
			makeAvailable(new ThreadedEventDispatcher(this));
		}
	}

	/**
	 * Default shutdown() method. Should be overridden by classes extending
	 * ThreadedDequeuer.
	 */
	public void shutdown() {
	}

	/**
	 * Default run loop.  Should be overridden by classes extending ThreadedDequeuer
	 */
	public void run() {
	}

	/**
	 * Handles events and calls EventHandler handlers.  Typically called from the dequeuer implementation
	 * to invoke the handler callbacks.
	 * @param event the Event to dispatch to the EventHandlers
	 */
	protected void dispatchEvent(Event event) {
		if(handlers == null) return;

		Iterator<String> iterator = handlers.keySet().iterator();
		while(iterator.hasNext()) {
			EventHandler handler = (EventHandler) handlers.get(iterator.next());
			ThreadedEventDispatcher d = getIdleProcessor();
			d.setTask(handler, event);
		}
	}

	/**
	 * Gets an idle processor from the list
	 * @return a ThreadedEventDispatcher
	 */
	protected ThreadedEventDispatcher getIdleProcessor() {
		synchronized(idleProcessors) {
			while(idleProcessors.isEmpty()) {
				try {
					idleProcessors.wait();
				} catch(InterruptedException ie) {}
			}

			return (ThreadedEventDispatcher) idleProcessors.remove(0);
		}
	}

	/**
	 * Makes a process available for dispatch
	 * @param dispatcher the dispatcher to make available
	 */
	public synchronized void makeAvailable(ThreadedEventDispatcher dispatcher) {
		if(dispatcher == null) return;

		if(dispatcher.isIdle()) {
			idleProcessors.add(dispatcher);
			synchronized(idleProcessors) {
				idleProcessors.notifyAll();
			}
		} else {
			throw new RuntimeException("Active processor made available");
		}
	}
}
