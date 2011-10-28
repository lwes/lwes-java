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

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class ThreadedEnqueuer implements Runnable {
	protected LinkedBlockingQueue<QueueElement> queue = null;

	/**
	 * Default constructor.
	 */
	public ThreadedEnqueuer() {
	}

	/**
	 * Returns the queue to use for this enqueuer
	 * @return the List queue
	 */
	public synchronized LinkedBlockingQueue<QueueElement> getQueue() {
		return this.queue;
	}

	/**
	 * Sets the queue to use for this enqueuer.
	 * Warning: this List must be thread-synchronized!
	 * @param queue the thread-synchronized List element
	 */
	public synchronized void setQueue(LinkedBlockingQueue<QueueElement> queue) {
		this.queue = queue;
	}

	/**
	 * Default initialize() method.  Should be overridden by classes extending ThreadedEnqueuer.
     * @throws IOException only to allow potential subclasses to throw it. 
	 */
	public void initialize() throws IOException {
	}

	/**
	 * Default shutdown() method.  Should be overridden by classes extending ThreadedDequeuer.
	 */
	public void shutdown() {
	}

	/**
	 * Default run() method.  Should be overridden by classes extending ThreadedDequeuer.
	 */
	public void run() {
	}
}
