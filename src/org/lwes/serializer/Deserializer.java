package org.lwes.serializer;

import org.lwes.Event;
import org.lwes.util.EncodedString;
import org.lwes.util.Log;
import org.lwes.util.NumberCodec;

/**
 * This encapuslates the information needed to deserialize the base types
 * of the event system.
 *
 * @author Anthony Molinaro
 * @author Michael P. Lum
 */
public class Deserializer
{
	/**
	 * Deserialize a byte out of the byte array <tt>bytes</tt>
	 *
	 * @param myState the DeserializeState object giving the current index
	 *                in the byte array <tt>bytes</tt>
	 * @return a byte.
	 */
	public static byte deserializeBYTE(DeserializerState myState, byte[] bytes)
	{
		byte aByte = bytes[myState.currentIndex()];
		myState.incr(1);
		return aByte;
	}

	/**
	 * Deserialize a boolean value out of the byte array <tt>bytes</tt>
	 *
	 * @param myState the DeserializeState object giving the current index
	 *                in the byte array <tt>bytes</tt>
	 * @return a boolean.
	 */
	public static boolean deserializeBOOLEAN(DeserializerState myState, 
			byte[] bytes)
	{
		byte aBooleanAsByte = Deserializer.deserializeBYTE(myState,bytes);
		boolean aBoolean;
		if ( aBooleanAsByte == (byte)0x00 )
			aBoolean = false;
		else
			aBoolean = true;
		return aBoolean;
	}

	/**
	 * Deserialize an int16 out of the byte array <tt>bytes</tt>
	 *
	 * @param myState the DeserializeState object giving the current index
	 *                in the byte array <tt>bytes</tt>
	 * @return a short.
	 */
	public static short deserializeINT16(DeserializerState myState, byte[] bytes)
	{
		/* deserialize in net order (i.e. Big Endian) */
		short aShort = 
			(short)( (((short)bytes[myState.currentIndex()] << 8) & 0xff00)
					|(((short)bytes[myState.currentIndex()+1]) &0x00ff) );
		myState.incr(2);
		return aShort;
	}

	/**
	 * Deserialize a uint16 out of the byte array <tt>bytes</tt>
	 *
	 * @param myState the DeserializeState object giving the current index
	 *                in the byte array <tt>bytes</tt>
	 * @return an int containing the unsigned short.
	 */
	public static int deserializeUINT16(DeserializerState myState, byte[] bytes)
	{

		/* deserialize in net order (i.e. Big Endian) */
		int anUnsignedShort = (int)
		( (((int)bytes[myState.currentIndex()  ] << 8) & 0x0000ff00)
				| (((int)bytes[myState.currentIndex()+1] << 0) & 0x000000ff) );

		myState.incr(2);
		return anUnsignedShort;
	}

	/**
	 * Deserialize an int32 out of the byte array <tt>bytes</tt>
	 *
	 * @param myState the DeserializeState object giving the current index
	 *                in the byte array <tt>bytes</tt>
	 * @return an int.
	 */
	public static int deserializeINT32(DeserializerState myState, byte[] bytes)
	{
		int anInt = 
			((int)( ( ((int)bytes[myState.currentIndex()  ] << 24) & 0xff000000 )
					| ( ((int)bytes[myState.currentIndex()+1] << 16) & 0x00ff0000 )
					| ( ((int)bytes[myState.currentIndex()+2] << 8 ) & 0x0000ff00 )
					| ( ((int)bytes[myState.currentIndex()+3] << 0 ) & 0x000000ff )
			)
			);

		myState.incr(4);
		return anInt;

	}

	/**
	 * Deserialize a uint32 out of the byte array <tt>bytes</tt>
	 *
	 * @param myState the DeserializeState object giving the current index
	 *                in the byte array <tt>bytes</tt>
	 * @return a long because java doesn't have unsigned types.
	 */
	public static long deserializeUINT32(DeserializerState myState, byte[] bytes)
	{
		long anUnsignedInt = 
			((long)
					( ( ((long)bytes[myState.currentIndex()  ] << 24) & 0x00000000ff000000L )
							| ( ((long)bytes[myState.currentIndex()+1] << 16) & 0x0000000000ff0000L )
							| ( ((long)bytes[myState.currentIndex()+2] << 8 ) & 0x000000000000ff00L )
							| ( ((long)bytes[myState.currentIndex()+3] << 0 ) & 0x00000000000000ffL )
					)
			);
		myState.incr(4);
		return anUnsignedInt;
	}

	/**
	 * Deserialize a int64 out of the byte array <tt>bytes</tt>
	 *
	 * @param myState the DeserializeState object giving the current index
	 *                in the byte array <tt>bytes</tt>
	 * @return a long.
	 */
	public static long deserializeINT64(DeserializerState myState, byte[] bytes)
	{
		long aLong = NumberCodec.decodeLongUnchecked(bytes,myState.currentIndex());

		myState.incr(8);
		return aLong;

	}

	/**
	 * Deserialize a uint64 out of the byte array <tt>bytes</tt>
	 *
	 * @param myState the DeserializeState object giving the current index
	 *                in the byte array <tt>bytes</tt>
	 * @return a long (because java doesn't have unsigned types do not expect
	 *         to do any math on this).
	 */
	public static long deserializeUINT64(DeserializerState myState, byte[] bytes)
	{
		long aLong = NumberCodec.decodeLongUnchecked(bytes,myState.currentIndex());
		myState.incr(8);
		return aLong;
	}

	public static String deserializeUINT64toHexString(DeserializerState myState, 
			byte[] bytes)
	{
		String aString =
			NumberCodec.byteArrayToHexString(bytes,myState.currentIndex(),8);
		myState.incr(8);
		return aString;
	}

	/**
	 * Deserialize an ip_addr out of the byte array <tt>bytes</tt>
	 *
	 * @param myState the DeserializeState object giving the current index
	 *                in the byte array <tt>bytes</tt>
	 * @return a byte array with the ip_addr with byte order 1234.
	 */
	public static byte[] deserializeIPADDR(DeserializerState myState, byte[] bytes)
	{
		byte[] inetaddr = new byte[4];
		inetaddr[0] = bytes[myState.currentIndex()+3];
		inetaddr[1] = bytes[myState.currentIndex()+2];
		inetaddr[2] = bytes[myState.currentIndex()+1];
		inetaddr[3] = bytes[myState.currentIndex()];
		myState.incr(4);
		return inetaddr;
	}

	public static String deserializeIPADDRtoHexString(DeserializerState myState,
			byte[] bytes)
	{
		String aString = 
			NumberCodec.byteArrayToHexString(bytes,myState.currentIndex(),4);
		myState.incr(4);
		return aString;
	}

	/**
	 * Deserialize a String out of the byte array <tt>bytes</tt>
	 *
	 * @deprecated
	 * @param myState the DeserializeState object giving the current index
	 *                in the byte array <tt>bytes</tt>
	 * @return a String.
	 */
	public static String deserializeSTRING(DeserializerState myState, byte[] bytes)
	{
		return deserializeSTRING(myState, bytes, Event.DEFAULT_ENCODING);
	}

	public static String deserializeSTRING(DeserializerState myState, 
			byte[] bytes, short encoding)
	{
		String aString = null;
		int len = -1;
		try {
			len = deserializeUINT16(myState,bytes);

			Log.debug("Datagram Bytes: " + 
					NumberCodec.byteArrayToHexString(bytes, 0, bytes.length));
			Log.debug("String Length: " + len);
			Log.debug("State: " + myState);

			aString = EncodedString.bytesToString(bytes,myState.currentIndex(),len,
					Event.ENCODING_STRINGS[encoding]);
			myState.incr(len);
		} catch ( ArrayIndexOutOfBoundsException aioobe ) {
			Log.info("Exception: " + aioobe.toString());
			Log.info("Datagram Bytes: " +
					NumberCodec.byteArrayToHexString(bytes, 0, bytes.length));
			Log.info("String Length: " + len);
			Log.info("State: " + myState);
		}
		return aString;

	}

	/**
	 * Deserialize a String out of the byte array <tt>bytes</tt> which
	 * represents an Event name.
	 *
	 * @param myState the DeserializeState object giving the current index
	 *                in the byte array <tt>bytes</tt>
	 * @return a String.
	 */
	public static String deserializeEVENTWORD(DeserializerState myState,
			byte[] bytes)
	{
		return deserializeEVENTWORD(myState, bytes, Event.DEFAULT_ENCODING);
	}

	public static String deserializeEVENTWORD(DeserializerState myState,
			byte[] bytes, short encoding)
	{
		String aString = null;
		int len = -1;
		try {
			len = (int)deserializeBYTE(myState,bytes);

			Log.debug("Datagram Bytes: " +
					NumberCodec.byteArrayToHexString(bytes, 0, bytes.length));
			Log.debug("String Length: " + len);
			Log.debug("State: " + myState);

			aString = EncodedString.bytesToString(bytes,myState.currentIndex(),len,
					Event.ENCODING_STRINGS[encoding]);
			myState.incr(len);
		} catch ( ArrayIndexOutOfBoundsException aioobe ) {
			Log.info("Exception: " + aioobe.toString());
			Log.info("Datagram Bytes: " +
				NumberCodec.byteArrayToHexString(bytes, 0, bytes.length));
			Log.info("String Length: " + len);
			Log.info("State: " + myState);
		}
		return aString;
	}

	/**
	 * Deserialize a String out of the byte array <tt>bytes</tt> which
	 * represents an Attribute name.
	 *
	 * @param myState the DeserializeState object giving the current index
	 *                in the byte array <tt>bytes</tt>
	 * @return a String.
	 */
	public static String deserializeATTRIBUTEWORD(DeserializerState myState, 
			byte[] bytes)
	{
		return deserializeEVENTWORD(myState,bytes, Event.DEFAULT_ENCODING);
	}
}
