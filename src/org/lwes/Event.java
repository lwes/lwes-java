package org.lwes;

import java.util.concurrent.ConcurrentHashMap;

import org.lwes.util.CharacterEncoding;

public class Event {
	/**
	 * Encoding variables
	 */
	public static final short ISO_8859_1 = 0;
	public static final short UTF_8 = 1;
	public static final short DEFAULT_ENCODING = UTF_8;
	public static final CharacterEncoding[] ENCODING_STRINGS = {
			CharacterEncoding.ISO_8859_1, CharacterEncoding.UTF_8 };

	/**
	 * Event data
	 */
	private ConcurrentHashMap<String, BaseType> attributes = new ConcurrentHashMap<String, BaseType>();
	private String name = null;
	private short encoding = DEFAULT_ENCODING;

	/**
	 * the size of the event in bytes
	 */
	private int bytesStoreSize = 0;

	/**
	 * 
	 * @return the serialized byte array
	 */
	public byte[] serialize() {
		byte[] bytes = new byte[this.bytesStoreSize];

		return bytes;
	}
}
