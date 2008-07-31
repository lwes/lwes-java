package org.lwes.serializer;

/** 
 * An internal class used by the Deserializer to keep track of its state
 * 
 * @author Anthony Molinaro
 */
public class DeserializerState
{
	private int index;

	/**
	 * Constructor
	 */
	public DeserializerState()
	{
		index = 0;
	}

	/**
	 * Increments the index into a byte array, by a specified amount
	 * and returns the new index value.
	 * 
	 * @param amount the amount to increment by
	 * @return the new index value as an int
	 */
	public int incr(int amount)
	{
		index += amount;
		return index;
	}

	/**
	 * return the current index
	 * 
	 * @return the current index as an int
	 */
	public int currentIndex()
	{
		return index;
	}

	/**
	 * reset the object to a clean state
	 *
	 */
	public void reset()
	{
		index = 0;
	}

	/**
	 * Returns a String representation of this object
	 * Overrides method in <tt>Object</tt>
	 *
	 * @return a String return of this object.
	 */
	public String toString()
	{
		return "DeserializeState = "+index;
	} 
}

