package org.lwes.listener;

import java.net.DatagramPacket;

/**
 * Element objects to use in the threaded queueing system.  This encapsulates
 * datagrams and timestamps.
 *
 * @author Michael P. Lum
 *
 */
public class DatagramQueueElement implements QueueElement {
	private DatagramPacket packet = null;
	private long timestamp = 0L;

	/**
	 * Default constructor.
	 */
	public DatagramQueueElement() {
	}

	/**
	 * Gets the datagram packet payload.
	 *
	 * @return the DatagramPacket
	 */
	public DatagramPacket getPacket() {
		return packet;
	}

	/**
	 * Sets the datagram packet payload
	 *
	 * @param packet
	 *            the DatagramPacket
	 */
	public void setPacket(DatagramPacket packet) {
		this.packet = packet;
	}

	/**
	 * Gets the timestamp associated with this packet
	 *
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets the timestamp associated with this packet
	 *
	 * @param timestamp
	 *            the timestamp
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Resets this packet to null
	 */
	public void clear() {
		packet = null;
		timestamp = 0x0L;
	}

	/**
	 * Determine equality with an object
	 * @param object the object to compare with
	 * @return true if the object is equal to this one, false if not
	 */
	public boolean equals(Object object) {
		if(object == null) return false;

		if (object instanceof DatagramQueueElement)
			return equals((DatagramQueueElement) object);

		return false;
	}

    @Override
    public int hashCode() {
        int result = packet != null ? packet.hashCode() : 0;
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }

    /**
	 * Determine equality with another DatagramQueueElement
	 * @param element the object to compare with
	 * @return true if the object is equal to this one, false if not
	 */
	public boolean equals(DatagramQueueElement element) {
		if (element == null)
			return false;
		if (element.getPacket() == null)
			return false;

		return ((element.getPacket().equals(this.packet)) && (element.timestamp == this.timestamp));
	}

}
