package org.lwes.serializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwes.Event;
import org.lwes.util.EncodedString;
import org.lwes.util.NumberCodec;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * This encapuslates the information needed to deserialize the base types
 * of the event system.
 *
 * @author Anthony Molinaro
 * @author Michael P. Lum
 * @author Frank Maritato
 */
public class Deserializer {

    private static transient Log log = LogFactory.getLog(Deserializer.class);

    /**
     * Deserialize a byte out of the byte array <tt>bytes</tt>
     *
     * @param myState the DeserializeState object giving the current index
     *                in the byte array <tt>bytes</tt>
     * @param bytes   the bytes to deserialize
     * @return a byte.
     */
    public static byte deserializeBYTE(DeserializerState myState, byte[] bytes) {
        byte aByte = bytes[myState.currentIndex()];
        myState.incr(1);
        return aByte;
    }

    /**
     * Deserialize a boolean value out of the byte array <tt>bytes</tt>
     *
     * @param myState the DeserializeState object giving the current index
     *                in the byte array <tt>bytes</tt>
     * @param bytes   the bytes to deserialize
     * @return a boolean.
     */
    public static boolean deserializeBOOLEAN(DeserializerState myState,
                                             byte[] bytes) {
        byte aBooleanAsByte = Deserializer.deserializeBYTE(myState, bytes);
        return aBooleanAsByte != (byte) 0x00;
    }

    /**
     * Deserialize an int16 out of the byte array <tt>bytes</tt>
     *
     * @param myState the DeserializeState object giving the current index
     *                in the byte array <tt>bytes</tt>
     * @param bytes   the bytes to deserialize
     * @return a short.
     */
    public static short deserializeINT16(DeserializerState myState, byte[] bytes) {
        /* deserialize in net order (i.e. Big Endian) */
        int off = myState.currentIndex();
        short aShort = (short) (((bytes[off + 1] & 0xFF) << 0) +
                                ((bytes[off + 0]) << 8));
        myState.incr(2);
        return aShort;
    }

    /**
     * Deserialize a uint16 out of the byte array <tt>bytes</tt>
     *
     * @param myState the DeserializeState object giving the current index
     *                in the byte array <tt>bytes</tt>
     * @param bytes   the bytes to deserialize
     * @return an int containing the unsigned short.
     */
    public static int deserializeUINT16(DeserializerState myState, byte[] bytes) {

        /* deserialize in net order (i.e. Big Endian) */
        int anUnsignedShort = (int)
                ((((int) bytes[myState.currentIndex()] << 8) & 0x0000ff00)
                 | (((int) bytes[myState.currentIndex() + 1] << 0) & 0x000000ff));

        myState.incr(2);
        return anUnsignedShort;
    }

    /**
     * Deserialize an int32 out of the byte array <tt>bytes</tt>
     *
     * @param myState the DeserializeState object giving the current index
     *                in the byte array <tt>bytes</tt>
     * @param bytes   the bytes to deserialize
     * @return an int.
     */
    public static int deserializeINT32(DeserializerState myState, byte[] bytes) {
        int off = myState.currentIndex();
        int anInt = ((bytes[off + 3] & 0xFF) << 0) +
                    ((bytes[off + 2] & 0xFF) << 8) +
                    ((bytes[off + 1] & 0xFF) << 16) +
                    ((bytes[off + 0]) << 24);
        myState.incr(4);
        return anInt;

    }

    /**
     * Deserialize a uint32 out of the byte array <tt>bytes</tt>
     *
     * @param myState the DeserializeState object giving the current index
     *                in the byte array <tt>bytes</tt>
     * @param bytes   the bytes to deserialize
     * @return a long because java doesn't have unsigned types.
     */
    public static long deserializeUINT32(DeserializerState myState, byte[] bytes) {
        long anUnsignedInt =
                ((long)
                        ((((long) bytes[myState.currentIndex()] << 24) & 0x00000000ff000000L)
                         | (((long) bytes[myState.currentIndex() + 1] << 16) & 0x0000000000ff0000L)
                         | (((long) bytes[myState.currentIndex() + 2] << 8) & 0x000000000000ff00L)
                         | (((long) bytes[myState.currentIndex() + 3] << 0) & 0x00000000000000ffL)
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
     * @param bytes   the bytes to deserialize
     * @return a long.
     */
    public static long deserializeINT64(DeserializerState myState, byte[] bytes) {
        int off = myState.currentIndex();
        long aLong = ((bytes[off + 7] & 0xFFL) << 0) +
                     ((bytes[off + 6] & 0xFFL) << 8) +
                     ((bytes[off + 5] & 0xFFL) << 16) +
                     ((bytes[off + 4] & 0xFFL) << 24) +
                     ((bytes[off + 3] & 0xFFL) << 32) +
                     ((bytes[off + 2] & 0xFFL) << 40) +
                     ((bytes[off + 1] & 0xFFL) << 48) +
                     (((long) bytes[off + 0]) << 56);
        myState.incr(8);
        return aLong;
    }

    /**
     * Deserialize a uint64 out of the byte array <tt>bytes</tt>
     *
     * @param myState the DeserializeState object giving the current index
     *                in the byte array <tt>bytes</tt>
     * @param bytes   the bytes to deserialize
     * @return a long (because java doesn't have unsigned types do not expect
     *         to do any math on this).
     */
    public static long deserializeUINT64(DeserializerState myState, byte[] bytes) {
        long aLong = NumberCodec.decodeLongUnchecked(bytes, myState.currentIndex());
        myState.incr(8);
        return aLong;
    }

    public static String deserializeUINT64toHexString(DeserializerState myState,
                                                      byte[] bytes) {
        String aString =
                NumberCodec.byteArrayToHexString(bytes, myState.currentIndex(), 8);
        myState.incr(8);
        return aString;
    }

    public static InetAddress deserializeIPV4(DeserializerState myState, byte[] bytes)
            throws UnknownHostException {
        int offset = myState.currentIndex();
        byte[] b = new byte[4];
        b[0] = bytes[offset];
        b[1] = bytes[offset + 1];
        b[2] = bytes[offset + 2];
        b[3] = bytes[offset + 3];
        myState.incr(4);
        return Inet4Address.getByAddress(b);
    }

    public static Double deserializeDOUBLE(DeserializerState myState, byte[] bytes) {
        int off = myState.currentIndex();
        long j = ((bytes[off + 7] & 0xFFL) << 0) +
                 ((bytes[off + 6] & 0xFFL) << 8) +
                 ((bytes[off + 5] & 0xFFL) << 16) +
                 ((bytes[off + 4] & 0xFFL) << 24) +
                 ((bytes[off + 3] & 0xFFL) << 32) +
                 ((bytes[off + 2] & 0xFFL) << 40) +
                 ((bytes[off + 1] & 0xFFL) << 48) +
                 (((long) bytes[off + 0]) << 56);
        myState.incr(8);
        return Double.longBitsToDouble(j);
    }

    public static Float deserializeFLOAT(DeserializerState myState, byte[] bytes) {
        int off = myState.currentIndex();
        int i = ((bytes[off + 3] & 0xFF) << 0) +
                ((bytes[off + 2] & 0xFF) << 8) +
                ((bytes[off + 1] & 0xFF) << 16) +
                ((bytes[off + 0]) << 24);
        myState.incr(4);
        return Float.intBitsToFloat(i);
    }

    /**
     * Deserialize an ip_addr out of the byte array <tt>bytes</tt>
     *
     * @param myState the DeserializeState object giving the current index
     *                in the byte array <tt>bytes</tt>
     * @param bytes   the bytes to deserialize
     * @return a byte array with the ip_addr with byte order 1234.
     */
    public static byte[] deserializeIPADDR(DeserializerState myState, byte[] bytes) {
        byte[] inetaddr = new byte[4];
        inetaddr[0] = bytes[myState.currentIndex() + 3];
        inetaddr[1] = bytes[myState.currentIndex() + 2];
        inetaddr[2] = bytes[myState.currentIndex() + 1];
        inetaddr[3] = bytes[myState.currentIndex()];
        myState.incr(4);
        return inetaddr;
    }

    public static String deserializeIPADDRtoHexString(DeserializerState myState,
                                                      byte[] bytes) {
        String aString =
                NumberCodec.byteArrayToHexString(bytes, myState.currentIndex(), 4);
        myState.incr(4);
        return aString;
    }

    public static List deserializeStringArray(DeserializerState state,
                                              byte[] bytes,
                                              short encoding) {
        int length = deserializeUINT16(state, bytes);
        List<String> rtn = new ArrayList<String>(length);
        for (int i = 0; i < length; i++) {
            rtn.add(deserializeSTRING(state, bytes, encoding));
        }
        return rtn;
    }

    public static List deserializeInt16Array(DeserializerState state,
                                             byte[] bytes) {
        int length = deserializeUINT16(state, bytes);
        List<Short> rtn = new ArrayList<Short>(length);
        for (int i = 0; i < length; i++) {
            rtn.add(deserializeINT16(state, bytes));
        }
        return rtn;
    }

    public static List deserializeInt32Array(DeserializerState state,
                                             byte[] bytes) {
        int length = deserializeUINT16(state, bytes);
        List<Integer> rtn = new ArrayList<Integer>(length);
        for (int i = 0; i < length; i++) {
            rtn.add(deserializeINT32(state, bytes));
        }
        return rtn;
    }

    public static List deserializeInt64Array(DeserializerState state,
                                             byte[] bytes) {
        int length = deserializeUINT16(state, bytes);
        List<Long> rtn = new ArrayList<Long>(length);
        for (int i = 0; i < length; i++) {
            rtn.add(deserializeINT64(state, bytes));
        }
        return rtn;
    }

    public static List deserializeUInt16Array(DeserializerState state,
                                              byte[] bytes) {
        int length = deserializeUINT16(state, bytes);
        List<Integer> rtn = new ArrayList<Integer>(length);
        for (int i = 0; i < length; i++) {
            rtn.add(deserializeUINT16(state, bytes));
        }
        return rtn;
    }

    public static List deserializeUInt32Array(DeserializerState state,
                                              byte[] bytes) {
        int length = deserializeUINT16(state, bytes);
        List<Long> rtn = new ArrayList<Long>(length);
        for (int i = 0; i < length; i++) {
            rtn.add(deserializeUINT32(state, bytes));
        }
        return rtn;
    }

    public static List deserializeUInt64Array(DeserializerState state,
                                              byte[] bytes) {
        int length = deserializeUINT16(state, bytes);
        List<Long> rtn = new ArrayList<Long>(length);
        for (int i = 0; i < length; i++) {
            rtn.add(deserializeUINT64(state, bytes));
        }
        return rtn;
    }

    public static List deserializeBooleanArray(DeserializerState state,
                                               byte[] bytes) {
        int length = deserializeUINT16(state, bytes);
        List<Boolean> rtn = new ArrayList<Boolean>(length);
        for (int i = 0; i < length; i++) {
            rtn.add(deserializeBOOLEAN(state, bytes));
        }
        return rtn;
    }

    public static List deserializeByteArray(DeserializerState state,
                                            byte[] bytes) {
        int length = deserializeUINT16(state, bytes);
        List<Byte> rtn = new ArrayList<Byte>(length);
        for (int i = 0; i < length; i++) {
            rtn.add(deserializeBYTE(state, bytes));
        }
        return rtn;
    }

    public static List deserializeDoubleArray(DeserializerState state,
                                              byte[] bytes) {
        int length = deserializeUINT16(state, bytes);
        List<Double> rtn = new ArrayList<Double>(length);
        for (int i = 0; i < length; i++) {
            rtn.add(deserializeDOUBLE(state, bytes));
        }
        return rtn;
    }

    public static List deserializeFloatArray(DeserializerState state,
                                             byte[] bytes) {
        int length = deserializeUINT16(state, bytes);
        List<Float> rtn = new ArrayList<Float>(length);
        for (int i = 0; i < length; i++) {
            rtn.add(deserializeFLOAT(state, bytes));
        }
        return rtn;
    }

    /**
     * Deserialize a String out of the byte array <tt>bytes</tt>
     *
     * @param myState the DeserializeState object giving the current index
     *                in the byte array <tt>bytes</tt>
     * @param bytes   the bytes to deserialize
     * @return a String.
     * @deprecated
     */
    public static String deserializeSTRING(DeserializerState myState, byte[] bytes) {
        return deserializeSTRING(myState, bytes, Event.DEFAULT_ENCODING);
    }

    public static String deserializeSTRING(DeserializerState myState,
                                           byte[] bytes, short encoding) {
        String aString = null;
        int len = -1;
        try {
            len = deserializeUINT16(myState, bytes);

            if (log.isDebugEnabled()) {
                log.debug("Datagram Bytes: " +
                          NumberCodec.byteArrayToHexString(bytes, 0, bytes.length));
                log.debug("String Length: " + len);
                log.debug("State: " + myState);
            }

            aString = EncodedString.bytesToString(bytes, myState.currentIndex(), len,
                                                  Event.ENCODING_STRINGS[encoding]);
            myState.incr(len);
        }
        catch (ArrayIndexOutOfBoundsException aioobe) {
            if (log.isInfoEnabled()) {
                log.info("Exception: " + aioobe.toString());
                log.info("Datagram Bytes: " +
                         NumberCodec.byteArrayToHexString(bytes, 0, bytes.length));
                log.info("String Length: " + len);
                log.info("State: " + myState);
            }
        }
        return aString;
    }

    /**
     * Deserialize a String out of the byte array <tt>bytes</tt> which
     * represents an Event name.
     *
     * @param myState the DeserializeState object giving the current index
     *                in the byte array <tt>bytes</tt>
     * @param bytes   the bytes to deserialize
     * @return a String.
     */
    public static String deserializeEVENTWORD(DeserializerState myState,
                                              byte[] bytes) {
        return deserializeEVENTWORD(myState, bytes, Event.DEFAULT_ENCODING);
    }

    public static String deserializeEVENTWORD(DeserializerState myState,
                                              byte[] bytes, short encoding) {
        String aString = null;
        int len = -1;
        try {
            len = (int) deserializeBYTE(myState, bytes);

            if (log.isDebugEnabled()) {
                log.debug("Datagram Bytes: " +
                          NumberCodec.byteArrayToHexString(bytes, 0, bytes.length));
                log.debug("String Length: " + len);
                log.debug("State: " + myState);
            }

            aString = EncodedString.bytesToString(bytes, myState.currentIndex(), len,
                                                  Event.ENCODING_STRINGS[encoding]);
            myState.incr(len);
        }
        catch (ArrayIndexOutOfBoundsException aioobe) {
            if (log.isInfoEnabled()) {
                log.info("Exception: " + aioobe.toString());
                log.info("Datagram Bytes: " +
                         NumberCodec.byteArrayToHexString(bytes, 0, bytes.length));
                log.info("String Length: " + len);
                log.info("State: " + myState);
            }
        }
        return aString;
    }

    /**
     * Deserialize a String out of the byte array <tt>bytes</tt> which
     * represents an Attribute name.
     *
     * @param myState the DeserializeState object giving the current index
     *                in the byte array <tt>bytes</tt>
     * @param bytes   the bytes to deserialize
     * @return a String.
     */
    public static String deserializeATTRIBUTEWORD(DeserializerState myState,
                                                  byte[] bytes) {
        return deserializeEVENTWORD(myState, bytes, Event.DEFAULT_ENCODING);
    }
}
