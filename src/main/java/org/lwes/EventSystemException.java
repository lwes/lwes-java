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
	public EventSystemException(Throwable e) {
		super(e);
	}
	
	/**
	 * Overrides <tt>Exception</tt> constructor
	 * @param s the exception message
	 */
	public EventSystemException(String s) {
		super(s);
	}
	
	/**
	 * Overrides the <tt>Exception</tt> constructor
	 * @param s the exception message
	 * @param e the parent exception
	 */
	public EventSystemException(String s, Throwable e) {
		super(s, e);
	}
}
