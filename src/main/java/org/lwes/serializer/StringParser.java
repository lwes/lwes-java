package org.lwes.serializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwes.EventSystemException;
import org.lwes.TypeID;
import org.lwes.util.IPAddress;
import org.lwes.util.NumberCodec;

import java.util.regex.Pattern;

/**
 * This contains low level type serialization used by the rest of the system.
 *
 * @author Anthony Molinaro
 * @author Michael P. Lum
 */
public class StringParser {

    private static transient Log log = LogFactory.getLog(StringParser.class);

	public static Object fromStringBYTE(String string)
			throws EventSystemException {
		Object toReturn = null;

		return toReturn;
	}

	public static Object fromStringBOOLEAN(String string)
			throws EventSystemException {
		log.trace("Parsing boolean");
		Object toReturn = Boolean.valueOf(string);
		log.trace("Got '" + toReturn + "'");
		return toReturn;
	}

	public static Object fromStringUINT16(String string)
			throws EventSystemException {
		Object toReturn = null;

		log.trace("Parsing uint16");
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
		log.trace("received '" + toReturn + "'");

		return toReturn;
	}

	public static Object fromStringINT16(String string)
			throws EventSystemException {
		Object toReturn = null;

		log.trace("Parsing int16");
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
		log.trace("received '" + toReturn + "'");

		return toReturn;
	}

	public static Object fromStringUINT32(String string)
			throws EventSystemException {
		Object toReturn = null;

		log.trace("Parsing uint32");
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
		log.trace("received '" + toReturn + "'");

		return toReturn;
	}

	public static Object fromStringINT32(String string)
			throws EventSystemException {
		Object toReturn = null;

		log.trace("Parsing int32");
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
		log.trace("received '" + toReturn + "'");

		return toReturn;
	}

	public static Object fromStringUINT64(String string)
			throws EventSystemException {
		Object toReturn = null;

		log.trace("Parsing uint64");
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
		log.trace("received '" + toReturn + "'");

		return toReturn;
	}

	public static Object fromStringINT64(String string)
			throws EventSystemException {
		Object toReturn = null;

		log.trace("Parsing int64");
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
		log.trace("received '" + toReturn + "'");

		return toReturn;
	}

	public static Object fromStringSTRING(String string)
			throws EventSystemException {

		log.trace("Parsing string '" + string + "'");
		return string;
	}

	public static Object fromStringIPADDR(String string)
			throws EventSystemException {
		Object toReturn = null;

		log.trace("Parsing IPAddress");

		if (Pattern.matches(TypeID.IP_ADDR_REGEX, string)) {
			toReturn = new IPAddress(string);
			if (((IPAddress) toReturn).toInt() == 0) {
				throw new EventSystemException("Possible Bad IP Address "
						+ string);
			}
		} else {
			throw new EventSystemException("Invalid IP Address");
		}
		log.trace("received '" + toReturn + "'");

		return toReturn;
	}
}
