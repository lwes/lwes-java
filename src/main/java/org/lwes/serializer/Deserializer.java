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

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.BitSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwes.Event;
import org.lwes.EventSystemException;
import org.lwes.FieldType;
import org.lwes.util.EncodedString;
import org.lwes.util.IPAddress;
import org.lwes.util.NumberCodec;

/**
 * This encapuslates the information needed to deserialize the base types
 * of the event system.
 *
 * @author Anthony Molinaro
 * @author Michael P. Lum
 * @author Frank Maritato
 */
public class Deserializer {

    private static final BigInteger UINT64_MASK = new BigInteger("ffffffffffffffff", 16);
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
     * Deserialize a byte out of the byte array <tt>bytes</tt> and
     * interpret as a number in the range 0..255.
     *
     * @param myState the DeserializeState object giving the current index
     *                in the byte array <tt>bytes</tt>
     * @param bytes   the bytes to deserialize
     * @return a short containing the unsigned byte value.
     */
    public static short deserializeUBYTE(DeserializerState myState, byte[] bytes) {
        return (short) (deserializeBYTE(myState, bytes) & 0xff);
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
        int anUnsignedShort =
                (((bytes[myState.currentIndex()] << 8) & 0x0000ff00)
                 | (bytes[myState.currentIndex() + 1] & 0x000000ff));

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
                ((((long) bytes[myState.currentIndex()] << 24) & 0x00000000ff000000L)
                 | (((long) bytes[myState.currentIndex() + 1] << 16) & 0x0000000000ff0000L)
                 | (((long) bytes[myState.currentIndex() + 2] << 8) & 0x000000000000ff00L)
                 | (bytes[myState.currentIndex() + 3] & 0x00000000000000ffL));
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

    public static BigInteger deserializeUInt64ToBigInteger(DeserializerState state,
                                                           byte[] bytes) {
        final long l = NumberCodec.decodeLongUnchecked(bytes, state.currentIndex());
        state.incr(8);
        return BigInteger.valueOf(l).and(UINT64_MASK);
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
    public static IPAddress deserializeIPADDR(DeserializerState myState, byte[] bytes) {
        byte[] inetaddr = new byte[4];
        inetaddr[0] = bytes[myState.currentIndex() + 3];
        inetaddr[1] = bytes[myState.currentIndex() + 2];
        inetaddr[2] = bytes[myState.currentIndex() + 1];
        inetaddr[3] = bytes[myState.currentIndex()];
        myState.incr(4);
        return new IPAddress(inetaddr);
    }

    public static String deserializeIPADDRtoHexString(DeserializerState myState,
                                                      byte[] bytes) {
        String aString =
                NumberCodec.byteArrayToHexString(bytes, myState.currentIndex(), 4);
        myState.incr(4);
        return aString;
    }

    public static String[] deserializeStringArray(DeserializerState state,
                                                  byte[] bytes,
                                                  short encoding) {
        int length = deserializeUINT16(state, bytes);
        String[] rtn = new String[length];
        for (int i = 0; i < length; i++) {
            rtn[i] = deserializeSTRING(state, bytes, encoding);
        }
        return rtn;
    }

    public static IPAddress[] deserializeIPADDRArray(DeserializerState state,
                                                     byte[] bytes) {
        int length = deserializeUINT16(state, bytes);
        IPAddress[] rtn = new IPAddress[length];
        for (int i = 0; i < length; i++) {
            rtn[i] = deserializeIPADDR(state, bytes);
        }
        return rtn;
    }

    public static short[] deserializeInt16Array(DeserializerState state,
                                                byte[] bytes) {
        int length = deserializeUINT16(state, bytes);
        short[] rtn = new short[length];
        for (int i = 0; i < length; i++) {
            rtn[i] = deserializeINT16(state, bytes);
        }
        return rtn;
    }

    public static int[] deserializeInt32Array(DeserializerState state,
                                              byte[] bytes) {
        int length = deserializeUINT16(state, bytes);
        int[] rtn = new int[length];
        for (int i = 0; i < length; i++) {
            rtn[i] = deserializeINT32(state, bytes);
        }
        return rtn;
    }

    public static long[] deserializeInt64Array(DeserializerState state,
                                               byte[] bytes) {
        int length = deserializeUINT16(state, bytes);
        long[] rtn = new long[length];
        for (int i = 0; i < length; i++) {
            rtn[i] = deserializeINT64(state, bytes);
        }
        return rtn;
    }

    public static int[] deserializeUInt16Array(DeserializerState state,
                                               byte[] bytes) {
        int length = deserializeUINT16(state, bytes);
        int[] rtn = new int[length];
        for (int i = 0; i < length; i++) {
            rtn[i] = deserializeUINT16(state, bytes);
        }
        return rtn;
    }

    public static long[] deserializeUInt32Array(DeserializerState state,
                                                byte[] bytes) {
        int length = deserializeUINT16(state, bytes);
        long[] rtn = new long[length];
        for (int i = 0; i < length; i++) {
            rtn[i] = deserializeUINT32(state, bytes);
        }
        return rtn;
    }

    public static BigInteger[] deserializeUInt64Array(DeserializerState state,
                                                      byte[] bytes) {
        int length = deserializeUINT16(state, bytes);
        BigInteger[] rtn = new BigInteger[length];
        for (int i = 0; i < length; i++) {
            rtn[i] = deserializeUInt64ToBigInteger(state, bytes);
        }
        return rtn;
    }

    public static boolean[] deserializeBooleanArray(DeserializerState state,
                                                    byte[] bytes) {
        int length = deserializeUINT16(state, bytes);
        boolean[] rtn = new boolean[length];
        for (int i = 0; i < length; i++) {
            rtn[i] = deserializeBOOLEAN(state, bytes);
        }
        return rtn;
    }

    public static byte[] deserializeByteArray(DeserializerState state,
                                              byte[] bytes) {
        int length = deserializeUINT16(state, bytes);
        byte[] rtn = new byte[length];
        for (int i = 0; i < length; i++) {
            rtn[i] = deserializeBYTE(state, bytes);
        }
        return rtn;
    }

    public static double[] deserializeDoubleArray(DeserializerState state,
                                                  byte[] bytes) {
        int length = deserializeUINT16(state, bytes);
        double[] rtn = new double[length];
        for (int i = 0; i < length; i++) {
            rtn[i] = deserializeDOUBLE(state, bytes);
        }
        return rtn;
    }

    public static float[] deserializeFloatArray(DeserializerState state,
                                                byte[] bytes) {
        int length = deserializeUINT16(state, bytes);
        float[] rtn = new float[length];
        for (int i = 0; i < length; i++) {
            rtn[i] = deserializeFLOAT(state, bytes);
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
    @Deprecated
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
            len = deserializeUBYTE(myState, bytes);

            if (log.isTraceEnabled()) {
                log.trace("Datagram Bytes: " +
                          NumberCodec.byteArrayToHexString(bytes, 0, bytes.length));
                log.trace("String Length: " + len);
                log.trace("State: " + myState);
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

    public static BitSet deserializeBitSet(DeserializerState myState, byte[] bytes) {

        int size = deserializeINT16(myState, bytes);
        int numBytes = (int) Math.ceil((double) size / 8.0);
        BitSet bitSet = new BitSet(size);
        int offset = myState.currentIndex();
        int index = 0;
        for (int i = 0; i < numBytes; i++) {
            int val = bytes[offset + i];
            for (int j = 0; j < 8; j++) {
                bitSet.set(index++, ((val & (1 << j)) != 0));
            }
        }
        myState.incr(numBytes);
        return bitSet;
    }

    public static String[] deserializeNStringArray(DeserializerState state,
                                                   byte[] bytes,
                                                   short encoding) {
        int length = deserializeUINT16(state, bytes);
        BitSet bs = deserializeBitSet(state, bytes);

        String[] rtn = new String[length];
        for (int i = 0; i < length; i++) {
            if (bs.get(i)) {
                rtn[i] = deserializeSTRING(state, bytes, encoding);
            }
            else {
                rtn[i] = null;
            }
        }
        return rtn;
    }

    public static Float[] deserializeNFloatArray(DeserializerState state, byte[] bytes) {
        // get the number of items in the array
        int length = deserializeINT16(state, bytes);    // 2 bytes
        BitSet bs = deserializeBitSet(state, bytes);    // 2 bytes * length worst case
        Float[] rtn = new Float[length];
        for (int i = 0; i < length; i++) {
            if (bs.get(i)) {
                rtn[i] = deserializeFLOAT(state, bytes);
            }
            else {
                rtn[i] = null;
            }
        }
        return rtn;
    }

    public static Double[] deserializeNDoubleArray(DeserializerState state, byte[] bytes) {
        // get the number of items in the array
        int length = deserializeINT16(state, bytes);    // 2 bytes
        BitSet bs = deserializeBitSet(state, bytes);    // 2 bytes * length worst case
        Double[] rtn = new Double[length];
        for (int i = 0; i < length; i++) {
            if (bs.get(i)) {
                rtn[i] = deserializeDOUBLE(state, bytes);
            }
            else {
                rtn[i] = null;
            }
        }
        return rtn;
    }

    public static Long[] deserializeNLongArray(DeserializerState state, byte[] bytes) {
        // get the number of items in the array
        int length = deserializeINT16(state, bytes);    // 2 bytes
        BitSet bs = deserializeBitSet(state, bytes);    // 2 bytes * length worst case
        Long[] rtn = new Long[length];
        for (int i = 0; i < length; i++) {
            if (bs.get(i)) {
                rtn[i] = deserializeINT64(state, bytes);
            }
            else {
                rtn[i] = null;
            }
        }
        return rtn;
    }

    public static Integer[] deserializeNIntegerArray(DeserializerState state, byte[] bytes) {
        // get the number of items in the array
        int length = deserializeINT16(state, bytes);    // 2 bytes
        BitSet bs = deserializeBitSet(state, bytes);    // 2 bytes * length worst case
        Integer[] rtn = new Integer[length];
        for (int i = 0; i < length; i++) {
            if (bs.get(i)) {
                rtn[i] = deserializeINT32(state, bytes);
            }
            else {
                rtn[i] = null;
            }
        }
        return rtn;
    }

    public static Short[] deserializeNShortArray(DeserializerState state, byte[] bytes) {
        // get the number of items in the array
        int length = deserializeINT16(state, bytes);    // 2 bytes
        BitSet bs = deserializeBitSet(state, bytes);    // 2 bytes * length worst case
        Short[] rtn = new Short[length];
        for (int i = 0; i < length; i++) {
            if (bs.get(i)) {
                rtn[i] = deserializeINT16(state, bytes);
            }
            else {
                rtn[i] = null;
            }
        }
        return rtn;
    }

    public static BigInteger[] deserializeNBigIntegerArray(DeserializerState state,
                                                           byte[] bytes) {
        int length = deserializeUINT16(state, bytes);
        BitSet bs = deserializeBitSet(state, bytes);

        BigInteger[] rtn = new BigInteger[length];
        for (int i = 0; i < length; i++) {
            if (bs.get(i)) {
                rtn[i] = deserializeUInt64ToBigInteger(state, bytes);
            }
            else {
                rtn[i] = null;
            }
        }
        return rtn;
    }

    public static Boolean[] deserializeNBooleanArray(DeserializerState state, byte[] bytes) {
        // get the number of items in the array
        int length = deserializeINT16(state, bytes);    // 2 bytes
        BitSet bs = deserializeBitSet(state, bytes);    // 2 bytes * length worst case
        Boolean[] rtn = new Boolean[length];
        for (int i = 0; i < length; i++) {
            if (bs.get(i)) {
                rtn[i] = deserializeBOOLEAN(state, bytes);
            }
            else {
                rtn[i] = null;
            }
        }
        return rtn;
    }

    public static Byte[] deserializeNByteArray(DeserializerState state,
                                               byte[] bytes) {
        int length = deserializeUINT16(state, bytes);
        BitSet bs = deserializeBitSet(state, bytes);

        Byte[] rtn = new Byte[length];
        for (int i = 0; i < length; i++) {
            if (bs.get(i)) {
                rtn[i] = deserializeBYTE(state, bytes);
            }
            else {
                rtn[i] = null;
            }
        }
        return rtn;
    }

    public static Object deserializeValue(DeserializerState state,
                                          byte[] bytes,
                                          FieldType type,
                                          short encoding) throws EventSystemException {
        switch (type) {
            case BOOLEAN:
                return Deserializer.deserializeBOOLEAN(state, bytes);
            case BYTE:
                return Deserializer.deserializeBYTE(state, bytes);
            case UINT16:
                return Deserializer.deserializeUINT16(state, bytes);
            case INT16:
                return Deserializer.deserializeINT16(state, bytes);
            case UINT32:
                return Deserializer.deserializeUINT32(state, bytes);
            case INT32:
                return Deserializer.deserializeINT32(state, bytes);
            case FLOAT:
                return Deserializer.deserializeFLOAT(state, bytes);
            case UINT64:
                return Deserializer.deserializeUInt64ToBigInteger(state, bytes);
            case INT64:
                return Deserializer.deserializeINT64(state, bytes);
            case DOUBLE:
                return Deserializer.deserializeDOUBLE(state, bytes);
            case STRING:
                return Deserializer.deserializeSTRING(state, bytes, encoding);
            case IPADDR:
                return Deserializer.deserializeIPADDR(state, bytes);
            case STRING_ARRAY:
                return Deserializer.deserializeStringArray(state, bytes, encoding);
            case INT16_ARRAY:
                return Deserializer.deserializeInt16Array(state, bytes);
            case INT32_ARRAY:
                return Deserializer.deserializeInt32Array(state, bytes);
            case INT64_ARRAY:
                return Deserializer.deserializeInt64Array(state, bytes);
            case UINT16_ARRAY:
                return Deserializer.deserializeUInt16Array(state, bytes);
            case UINT32_ARRAY:
                return Deserializer.deserializeUInt32Array(state, bytes);
            case UINT64_ARRAY:
                return Deserializer.deserializeUInt64Array(state, bytes);
            case BOOLEAN_ARRAY:
                return Deserializer.deserializeBooleanArray(state, bytes);
            case BYTE_ARRAY:
                return Deserializer.deserializeByteArray(state, bytes);
            case DOUBLE_ARRAY:
                return Deserializer.deserializeDoubleArray(state, bytes);
            case FLOAT_ARRAY:
                return Deserializer.deserializeFloatArray(state, bytes);
            case IP_ADDR_ARRAY:
                return Deserializer.deserializeIPADDRArray(state, bytes);
            case NDOUBLE_ARRAY:
                return Deserializer.deserializeNDoubleArray(state, bytes);
            case NFLOAT_ARRAY:
                return Deserializer.deserializeNFloatArray(state, bytes);
            case NINTEGER_ARRAY:
                return Deserializer.deserializeNIntegerArray(state, bytes);
            case NLONG_ARRAY:
                return Deserializer.deserializeNLongArray(state, bytes);
            case NSHORT_ARRAY:
                return Deserializer.deserializeNShortArray(state, bytes);
            case NBOOLEAN_ARRAY:
                return Deserializer.deserializeNBooleanArray(state, bytes);
            case NSTRING_ARRAY:
                return Deserializer.deserializeNStringArray(state, bytes, encoding);
            case NBIGINT_ARRAY:
              return Deserializer.deserializeNBigIntegerArray(state, bytes);
            case NBYTE_ARRAY:
              return Deserializer.deserializeNByteArray(state, bytes);
        }
        throw new EventSystemException("Unrecognized type: " + type);
    }
}
