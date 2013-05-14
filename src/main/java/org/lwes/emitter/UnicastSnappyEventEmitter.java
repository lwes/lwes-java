package org.lwes.emitter;

import java.io.IOException;

import org.lwes.Event;
import org.lwes.EventSystemException;
import org.xerial.snappy.Snappy;

/**
 * @author dgoya
 *
 */
public class UnicastSnappyEventEmitter extends UnicastEventEmitter {

	/**
	 * Emits the compressed event to the network.
	 *
	 * @param event the event to emit
	 * @exception IOException throws an IOException is there is a network error.
     * @throws EventSystemException if unable to serialize the event
	 */
	public void emit(Event event) throws IOException, EventSystemException {
		byte[] msgBytes = event.serialize();
		synchronized(lock) {
			emit(Snappy.compress(msgBytes));
		}
	}

}
