package org.lwes;

/**
 * Thrown when the Type does not exist for a given attribute in an Event.
 * 
 * @author Anthony Molinaro
 */
public class NoSuchAttributeTypeException extends EventSystemException {
	/**
	 * Overrides <tt>EventSystemException</tt> Constructor
	 */
	public NoSuchAttributeTypeException(String s) {
		super(s);
	}
}
