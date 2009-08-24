package org.lwes;

/**
 * Exception thrown when the given event does not exist in the event system
 * @author Anthony Molinaro
 *
 */
public class NoSuchEventException extends EventSystemException {
	/**
	 * Overrides <tt>EventSystemException</tt> constructor
	 */
	public NoSuchEventException(String s) {
		super(s);
	}

}
