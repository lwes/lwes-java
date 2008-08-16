package org.lwes.listener;

/**
 * EventListener is an interface defining an event listener that has pluggable
 * event acquisition and handling.  Events can be acquired over the network or 
 * from a file, for example.  Handlers can be registered using a callback interface 
 * that will be called as events come into the system.
 * 
 * @author      Michael P. Lum
 * @author      Anthony Molinaro
 */
public interface EventListener {
	/**
	 * Add an EventHandler to handle events for processing.
	 * @param handler the EventHandler to add
	 */
	public void addHandler(EventHandler handler);
}

