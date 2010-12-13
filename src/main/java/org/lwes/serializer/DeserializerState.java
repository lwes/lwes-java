/*======================================================================*
 * Copyright (c) 2008, Yahoo! Inc. All rights reserved.                 *
 *                                                                      *
 * Licensed under the New BSD License (the "License"); you may not use  *
 * this file except in compliance with the License.  Unless required    *
 * by applicable law or agreed to in writing, software distributed      *
 * under the License is distributed on an "AS IS" BASIS, WITHOUT        *
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     *
 * See the License for the specific language governing permissions and  *
 * limitations under the License. See accompanying LICENSE file.        *
 *======================================================================*/

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

