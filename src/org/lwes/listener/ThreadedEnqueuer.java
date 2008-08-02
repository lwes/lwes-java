package org.lwes.listener;

import java.io.IOException;
import java.util.List;

public abstract class ThreadedEnqueuer implements Runnable {
	protected List<QueueElement> queue = null;
	
	/**
	 * Default constructor.
	 */
	public ThreadedEnqueuer() {
	}
	
	/**
	 * Returns the queue to use for this enqueuer
	 * @return the List queue
	 */
	public synchronized List<QueueElement> getQueue() {
		return this.queue;
	}
	
	/**
	 * Sets the queue to use for this enqueuer.
	 * Warning: this List must be thread-synchronized!
	 * @param queue the thread-synchronized List element
	 */
	public synchronized void setQueue(List<QueueElement> queue) {
		this.queue = queue;
	}	
	
	/**
	 * Default initialize() method.  Should be overridden by classes extending ThreadedEnqueuer.
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
