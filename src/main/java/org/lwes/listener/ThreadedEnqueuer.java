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
