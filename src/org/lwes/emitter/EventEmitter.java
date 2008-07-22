package org.lwes.emitter;

import java.io.IOException;

import org.lwes.Event;

/**
 * EventEmitter is the interface that defines a component that takes an Event and performs an emit task
 * on that event.  This may emit the event to the network, to a disk, or to a database.	
 * 
 * @author      Michael P. Lum
 * @author      Anthony Molinaro
 * @version     %I%, %G%
 * @since       0.0.1
 */
public interface EventEmitter {
	/**
	 * Called before the system is started.  Allows for initialization of data and creation
	 * of network sockets, where applicable.
	 * 
	 * @throws IOException if an I/O error occurs during initialization
	 */
	public void initialize() throws IOException;
	
	/**
	 * Called before the system is shut down.  Allows for cleanup of data and destruction of
	 * network sockets, where applicable.
	 * 
	 * @throws IOException if an I/O error occurs during initialization
	 */
	public void shutdown() throws IOException;
	
	/**
	 * Emits an event
	 * 
	 * @param  event  the event being emitted
	 * @throws IOException if an I/O error occurs
	 */	
	public void emit(Event event) throws IOException;	
}
