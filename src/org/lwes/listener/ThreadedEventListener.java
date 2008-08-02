package org.lwes.listener;

import org.lwes.EventSystemException;

public abstract class ThreadedEventListener implements EventListener {
	/* the processor for handling events */
	protected ThreadedProcessor processor = new ThreadedProcessor();
	
	/* the event enqueuer and dequeuer */
	private ThreadedEnqueuer enqueuer = null;
	private ThreadedDequeuer dequeuer = null;
	
	/**
	 * Default constructor.
	 */
	public ThreadedEventListener() {
	}
		
	/**
	 * Gets the enqueuer being used by this listener
	 * @return the enqueuer
	 */
	public ThreadedEnqueuer getEnqueuer() {
		return enqueuer;
	}

	/**
	 * Sets the enqueuer to use for this listener
	 * @param enqueuer the enqueuer to set
	 */
	public void setEnqueuer(ThreadedEnqueuer enqueuer) {
		this.enqueuer = enqueuer;
	}

	/**
	 * Gets the dequeuer being used by this listener
	 * @return the dequeuer
	 */
	public ThreadedDequeuer getDequeuer() {
		return dequeuer;
	}

	/**
	 * Sets the dequeuer used by this listener
	 * @param dequeuer the dequeuer to set
	 */
	public void setDequeuer(ThreadedDequeuer dequeuer) {
		this.dequeuer = dequeuer;
	}

	/**
	 * Add an EventHandler to handle events for processing.
	 * @param handler the EventHandler to add
	 */
	public void addHandler(EventHandler handler) {
		
	}
	
	/**
	 * Initializes this listener, and starts the processor threads
	 * @throws EventSystemException if there is a problem initializing the listener
	 */
	public void initialize() throws EventSystemException {
		if(processor == null) throw new EventSystemException("No processor exists");
		if(enqueuer == null) throw new EventSystemException("No enqueuer exists");
		if(dequeuer == null) throw new EventSystemException("No dequeuer exists");
		
		processor.setEnqueuerPriority(Thread.MAX_PRIORITY);
		processor.setDequeuerPriority(Thread.NORM_PRIORITY);
		processor.setEnqueuer(enqueuer);
		processor.setDequeuer(dequeuer);
		processor.initialize();
	}
	
	public void shutdown() throws EventSystemException {
		if(processor == null) throw new EventSystemException("No processor exists");
		processor.shutdown();
	}

}
