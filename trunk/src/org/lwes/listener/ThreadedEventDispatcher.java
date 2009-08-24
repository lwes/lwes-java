package org.lwes.listener;

import org.lwes.Event;
import org.lwes.util.Log;

/**
 * Dispatches events to downstream handlers using threads.
 * @author Preston Pfarner
 * @author Michael P. Lum
 */
public class ThreadedEventDispatcher extends Thread {
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
						Log.warning("Caught exception handling event", e);
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
