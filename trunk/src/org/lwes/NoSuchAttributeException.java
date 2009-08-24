package org.lwes;

/**
 * Exception thrown when an attribute does not exist in an Event
 * @author Anthony Molinaro
 */
public class NoSuchAttributeException extends EventSystemException {
	/**
	 * Overrides <tt>EventSystemException</tt> constructor
	 */
	public NoSuchAttributeException(String s) {
		super(s);
	}

}
