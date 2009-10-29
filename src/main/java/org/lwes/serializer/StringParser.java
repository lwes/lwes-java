package org.lwes.serializer;

import org.lwes.EventSystemException;
import org.lwes.TypeID;
import org.lwes.util.IPAddress;
import org.lwes.util.Log;
import org.lwes.util.NumberCodec;

import java.util.regex.Pattern;

/**
 * This contains low level type serialization used by the rest of the system.
 *
 * @author Anthony Molinaro
 * @author Michael P. Lum
 */
public class StringParser {

	public static Object fromStringBYTE(String string)
			throws EventSystemException {
		Object toReturn = null;

		return toReturn;
	}

	public static Object fromStringBOOLEAN(String string)
			throws EventSystemException {
		Log.trace("Parsing boolean");
		Object toReturn = Boolean.valueOf(string);
		Log.trace("Got '" + toReturn + "'");
		return toReturn;
	}

	public static Object fromStringUINT16(String string)
			throws EventSystemException {
		Object toReturn = null;

		Log.trace("Parsing uint16");
		if (Pattern.matches(TypeID.HEX_SHORT_REGEX, string)) {
			if (string.startsWith("0x"))
				string = string.substring(2);

			/* pad with zeros since NumberCodec.decodeInt expects a length of 8 */
			string = "0000" + string;
			byte[] bytes = NumberCodec.hexStringToByteArray(string);
			toReturn = NumberCodec.decodeInt(bytes, 0, bytes.length);
		} else {
			try {
				toReturn = Integer.valueOf(string);
			} catch (NumberFormatException nfe) {
				throw new EventSystemException(nfe);
			}
			int intValue = (Integer) toReturn;
			if (intValue < 0 || intValue > 65535) {
				throw new EventSystemException("Unsigned Short must be in the "
						+ "range [0-65535] ");
			}
		}
		Log.trace("received '" + toReturn + "'");

		return toReturn;
	}

	public static Object fromStringINT16(String string)
			throws EventSystemException {
		Object toReturn = null;

		Log.trace("Parsing int16");
		if (Pattern.matches(TypeID.HEX_SHORT_REGEX, string)) {
			if (string.startsWith("0x"))
				string = string.substring(2);

			byte[] bytes = NumberCodec.hexStringToByteArray(string);
			toReturn = NumberCodec.decodeShort(bytes, 0, bytes.length);
		} else {
			try {
				toReturn = Short.valueOf(string);
			} catch (NumberFormatException nfe) {
				throw new EventSystemException("Probably not a short, "
						+ "got exception " + nfe);
			}
			short shortValue = (Short) toReturn;
			if (shortValue < -32768 || shortValue > 32767) {
				throw new EventSystemException("Signed Short must be in the "
						+ "range [-32768 - 32767] ");
			}
		}
		Log.trace("received '" + toReturn + "'");

		return toReturn;
	}

	public static Object fromStringUINT32(String string)
			throws EventSystemException {
		Object toReturn = null;

		Log.trace("Parsing uint32");
		if (Pattern.matches(TypeID.HEX_INT_REGEX, string)) {
			if (string.startsWith("0x"))
				string = string.substring(2);

			/* pad with zeros since NumberCodec.decodeLong expects a length of 8 */
			string = "00000000" + string;
			byte[] bytes = NumberCodec.hexStringToByteArray(string);
			toReturn = NumberCodec.decodeLong(bytes, 0, bytes.length);
		} else {
			try {
				toReturn = Long.valueOf(string);
			} catch (NumberFormatException nfe) {
				throw new EventSystemException(nfe);
			}
			long longValue = (Long) toReturn;
			if (longValue < 0
					|| longValue > ((long) Integer.MAX_VALUE - ((long) Integer.MIN_VALUE))) {
				throw new EventSystemException("Unsigned Int must be in the "
						+ "range [0-"
						+ ((long) Integer.MAX_VALUE - (long) Integer.MIN_VALUE)
						+ "] ");
			}
		}
		Log.trace("received '" + toReturn + "'");

		return toReturn;
	}

	public static Object fromStringINT32(String string)
			throws EventSystemException {
		Object toReturn = null;

		Log.trace("Parsing int32");
		if (Pattern.matches(TypeID.HEX_INT_REGEX, string)) {
			if (string.startsWith("0x"))
				string = string.substring(2);

			byte[] bytes = NumberCodec.hexStringToByteArray(string);
			toReturn = NumberCodec.decodeInt(bytes, 0, bytes.length);
		} else {
			try {
				toReturn = Integer.valueOf(string);
			} catch (NumberFormatException nfe) {
				throw new EventSystemException(nfe);
			}
		}
		Log.trace("received '" + toReturn + "'");

		return toReturn;
	}

	public static Object fromStringUINT64(String string)
			throws EventSystemException {
		Object toReturn = null;

		Log.trace("Parsing uint64");
		if (Pattern.matches(TypeID.HEX_LONG_REGEX, string)) {
			if (string.startsWith("0x"))
				string = string.substring(2);

			byte[] bytes = NumberCodec.hexStringToByteArray(string);
			toReturn = NumberCodec.decodeLong(bytes, 0, bytes.length);
		} else {
			try {
				toReturn = Long.valueOf(string);
			} catch (NumberFormatException nfe) {
				throw new EventSystemException("Got Exception " + nfe);
			}
		}
		Log.trace("received '" + toReturn + "'");

		return toReturn;
	}

	public static Object fromStringINT64(String string)
			throws EventSystemException {
		Object toReturn = null;

		Log.trace("Parsing int64");
		if (Pattern.matches(TypeID.HEX_LONG_REGEX, string)) {
			if (string.startsWith("0x"))
				string = string.substring(2);

			byte[] bytes = NumberCodec.hexStringToByteArray(string);
			toReturn = NumberCodec.decodeLong(bytes, 0, bytes.length);
		} else {
			try {
				toReturn = Long.valueOf(string);
			} catch (NumberFormatException nfe) {
				throw new EventSystemException(nfe);
			}
		}
		Log.trace("received '" + toReturn + "'");

		return toReturn;
	}

	public static Object fromStringSTRING(String string)
			throws EventSystemException {

		Log.trace("Parsing string '" + string + "'");
		return string;
	}

	public static Object fromStringIPADDR(String string)
			throws EventSystemException {
		Object toReturn = null;

		Log.trace("Parsing IPAddress");

		if (Pattern.matches(TypeID.IP_ADDR_REGEX, string)) {
			toReturn = new IPAddress(string);
			if (((IPAddress) toReturn).toInt() == 0) {
				throw new EventSystemException("Possible Bad IP Address "
						+ string);
			}
		} else {
			throw new EventSystemException("Invalid IP Address");
		}
		Log.trace("received '" + toReturn + "'");

		return toReturn;
	}
}
