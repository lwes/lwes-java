package org.lwes.listener;

import org.lwes.Event;

/**
 * This interface is implemented by any object that wishes to
 * receive incoming Events.  Once an EventListener registers
 * with an EventDispatcher, it will receive access to any new
 * Events that are sent through that EventDispatcher.
 * @author Anthony Molinaro
 */
public interface EventHandler {
	public void handleEvent(Event event);
}
