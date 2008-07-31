package org.lwes;

/**
 * This is the parent class of all the Exceptions thrown by the event system
 * 
 * @author Anthony Molinaro
 */
public class EventSystemException extends Exception {
	/**
	 * Overrides <tt>Exception</tt> constructor
	 * @param e the parent exception
	 */
	
	public EventSystemException(Exception e) {
		super(e);
	}
	
	/**
	 * Overrides <tt>Exception</tt> Constructor
	 * @param s the exception message
	 */
	public EventSystemException(String s) {
		super(s);
	}
}
