package org.lwes.listener;

import java.io.IOException;
import java.net.DatagramPacket;

import org.lwes.Event;
import org.lwes.EventFactory;
import org.lwes.util.IPAddress;
import org.lwes.util.Log;

public class DatagramDequeuer extends ThreadedDequeuer {	
	private boolean running = false;

	/* an event factory */
	private EventFactory factory = new EventFactory();
	
	public DatagramDequeuer() {
	}
	
	public void initialize() throws IOException {
		super.initialize();
	}

	public synchronized void shutdown() {
		running = false;
	}

	public void run() {
		running = true;

		while (running) {
			if(hasPending()) {
				try {
					DatagramQueueElement element = (DatagramQueueElement) queue.remove(0);
					handleElement(element);
				} catch(UnsupportedOperationException uoe) {
					// not a problem, someone grabbed the event before we did
				} catch(Exception e) {
					Log.error("Error in dequeueing event for processing", e);
				}
			} else {
				synchronized(queue) {
					try {
						queue.wait();
					} catch(InterruptedException e) {}
				}
			}
		}
	}

	/**
	 * Determines whether the collection of pending tasks has any elements. Note
	 * that if this returns false at some point and then someone calls
	 * <code>addTask()</code> the state will change to true. Do not use this
	 * function to determine when there cannot be any more tasks.
	 * 
	 * @return true iff there are any pending tasks.
	 */
	protected final boolean hasPending() {
		return queue.size() > 0;
	}
	
	protected void handleElement(DatagramQueueElement element) {
		if(element == null) return;
		
		DatagramPacket packet = element.getPacket();
		if(packet == null) return;	
	
		/* get some metadata */
		long timestamp = element.getTimestamp();
		IPAddress address = new IPAddress(packet.getAddress());
		int port = packet.getPort();

		/* now try to deserialize the packet */
		try {
			/* don't validate the event for now to save time */
			Event event = factory.createEvent(packet.getData(), false);
			event.setInt64(Event.RECEIPT_TIME, timestamp);
			event.setIPAddress(Event.SENDER_IP, address);
			event.setUInt16(Event.SENDER_PORT, port);
			Log.trace("Dispatching event " + event.toString());
			dispatchEvent(event);
		} catch(Exception e) {
			Log.warning("Unable to deserialize event in handleElement()", e);
		}
	}
}
