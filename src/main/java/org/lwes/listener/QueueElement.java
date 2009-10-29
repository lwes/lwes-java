package org.lwes.listener;

/**
 * Interface needed to define queue elements, which encapsulate incoming events
 * @author Michael P. Lum
 */
public interface QueueElement {
	/**
	 * Get the timestamp this event was created
	 * @return the timestamp
	 */
	public long getTimestamp();
	
	/**
	 * Sets the timestamp for this event
	 * @param timestamp the timestamp
	 */
	public void setTimestamp(long timestamp);
}
