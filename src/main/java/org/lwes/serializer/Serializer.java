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
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwes.Event;
import org.lwes.FieldType;
import org.lwes.util.EncodedString;
import org.lwes.util.IPAddress;
import org.lwes.util.NumberCodec;

/**
 * This contains low level type serialization used by the
 * rest of the system.
 *
 * @author Anthony Molinaro
 * @author Frank Maritato
 */
public class Serializer {
    private static final transient Log log = LogFactory.getLog(Serializer.class);

    public static int serializeIPV4(Inet4Address value, byte[] bytes, int offset) {
        byte[] bs = value.getAddress();
        int i = offset;
        for (byte b : bs) {
            bytes[i++] = b;
        }
        return offset - i;
    }

    public static int serializeDOUBLE(Double value, byte[] bytes, int offset) {
        long j = Double.doubleToLongBits(value);
        bytes[offset + 7] = (byte) (j >>> 0);
        bytes[offset + 6] = (byte) (j >>> 8);
        bytes[offset + 5] = (byte) (j >>> 16);
        bytes[offset + 4] = (byte) (j >>> 24);
        bytes[offset + 3] = (byte) (j >>> 32);
        bytes[offset + 2] = (byte) (j >>> 40);
        bytes[offset + 1] = (byte) (j >>> 48);
        bytes[offset + 0] = (byte) (j >>> 56);
        return 8;
    }

    public static int serializeFLOAT(Float value, byte[] bytes, int offset) {
        int i = Float.floatToIntBits(value);
        bytes[offset + 3] = (byte) (i >>> 0);
        bytes[offset + 2] = (byte) (i >>> 8);
        bytes[offset + 1] = (byte) (i >>> 16);
        bytes[offset + 0] = (byte) (i >>> 24);
        return 4;
    }

    public static int serializeBYTE(byte value, byte[] bytes, int offset) {
        bytes[offset] = value;
        return 1;
    }

    public static int serializeUBYTE(short value, byte[] bytes, int offset) throws IllegalArgumentException {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException("Unsigned byte " + value + " out of range 0..255");
        }
        bytes[offset] = (byte) (value & 0xff);
        return 1;
    }

    public static int serializeBOOLEAN(boolean value, byte[] bytes, int offset) {
        bytes[offset] = (byte) (value ? 1 : 0);
        return 1;
    }

    public static int serializeUINT16(int value, byte[] bytes, int offset) {
        return serializeINT16((short) value, bytes, offset);
    }

    public static int serializeINT16(short value, byte[] bytes, int offset) {
        bytes[offset + 1] = (byte) (value >>> 0);
        bytes[offset + 0] = (byte) (value >>> 8);
        return 2;
    }

    public static int serializeUINT32(long anUnsignedInt, byte[] bytes, int offset) {
        bytes[offset] = (byte) ((anUnsignedInt & 0xff000000) >> 24);
        bytes[offset + 1] = (byte) ((anUnsignedInt & 0x00ff0000) >> 16);
        bytes[offset + 2] = (byte) ((anUnsignedInt & 0x0000ff00) >> 8);
        bytes[offset + 3] = (byte) ((anUnsignedInt & 0x000000ff) >> 0);
        return (4);
    }

    public static int serializeINT32(int value, byte[] bytes, int offset) {
        bytes[offset + 3] = (byte) (value >>> 0);
        bytes[offset + 2] = (byte) (value >>> 8);
        bytes[offset + 1] = (byte) (value >>> 16);
        bytes[offset + 0] = (byte) (value >>> 24);
        return 4;
    }

    public static int serializeINT64(long value, byte[] bytes, int offset) {
        bytes[offset + 7] = (byte) (value >>> 0);
        bytes[offset + 6] = (byte) (value >>> 8);
        bytes[offset + 5] = (byte) (value >>> 16);
        bytes[offset + 4] = (byte) (value >>> 24);
        bytes[offset + 3] = (byte) (value >>> 32);
        bytes[offset + 2] = (byte) (value >>> 40);
        bytes[offset + 1] = (byte) (value >>> 48);
        bytes[offset + 0] = (byte) (value >>> 56);
        return 8;
    }

    public static int serializeUINT64(long anInt, byte[] bytes, int offset) {
        NumberCodec.encodeLongUnchecked(anInt, bytes, offset);
        return (8);
    }

    public static int serializeUINT64(BigInteger anInt, byte[] bytes, int offset) {
        // TODO: write a BigInteger serialization method
        NumberCodec.encodeLongUnchecked(anInt.longValue(), bytes, offset);
        return (8);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static int serializeSTRING(String aString, byte[] bytes, int offset) {
        return serializeSTRING(aString, bytes, offset, Event.DEFAULT_ENCODING);
    }

    public static int serializeSTRING(String aString, byte[] bytes, int offset,
                                      short encoding) {
        byte[] stringBytes =
                EncodedString.getBytes(aString, Event.ENCODING_STRINGS[encoding]);
        int length = stringBytes.length;
        if (length < 65535 && length >= 0) {
            offset += serializeUINT16(length, bytes, offset);
            System.arraycopy(stringBytes, 0, bytes, offset, length);
            return (length + 2);
        }
        return 0;

    }

    /**
     * String arrays are serialized as follows:
     * <array_name_len><array_name_bytes><type>
     * <array_length><serialized_type_1>...<serialized_type_n>
     *
     * @param value
     * @param bytes
     * @param offset
     * @param encoding
     * @return the offset
     */
    public static int serializeStringArray(String[] value,
                                           byte[] bytes,
                                           int offset,
                                           short encoding) {

        int numbytes = 0;
        int offsetStart = offset;
        numbytes = serializeUINT16(value.length, bytes, offset);
        offset += numbytes;
        for (String s : value) {
            numbytes = serializeSTRING(s, bytes, offset, encoding);
            offset += numbytes;
        }
        return (offset - offsetStart);
    }

    public static int serializeIPADDRArray(IPAddress[] value,
                                           byte[] bytes,
                                           int offset) {
        int numbytes = 0;
        int offsetStart = offset;
        numbytes = serializeUINT16(value.length, bytes, offset);
        offset += numbytes;
        for (IPAddress s : value) {
            numbytes = serializeIPADDR(s, bytes, offset);
            offset += numbytes;
        }
        return (offset - offsetStart);
    }

    public static int serializeInt16Array(short[] value,
                                          byte[] bytes,
                                          int offset) {
        int numbytes = 0;
        int offsetStart = offset;
        numbytes = serializeUINT16(value.length, bytes, offset);
        offset += numbytes;
        for (short s : value) {
            numbytes = serializeINT16(s, bytes, offset);
            offset += numbytes;
        }
        return (offset - offsetStart);
    }

    public static int serializeInt32Array(int[] value,
                                          byte[] bytes,
                                          int offset) {
        int numbytes = 0;
        int offsetStart = offset;
        numbytes = serializeUINT16(value.length, bytes, offset);
        offset += numbytes;
        for (int s : value) {
            numbytes = serializeINT32(s, bytes, offset);
            offset += numbytes;
        }
        return (offset - offsetStart);
    }

    public static int serializeInt64Array(long[] value,
                                          byte[] bytes,
                                          int offset) {
        int numbytes = 0;
        int offsetStart = offset;
        numbytes = serializeUINT16(value.length, bytes, offset);
        offset += numbytes;
        for (long s : value) {
            numbytes = serializeINT64(s, bytes, offset);
            offset += numbytes;
        }
        return (offset - offsetStart);
    }

    public static int serializeUInt16Array(int[] value,
                                           byte[] bytes,
                                           int offset) {
        int numbytes = 0;
        int offsetStart = offset;
        numbytes = serializeUINT16(value.length, bytes, offset);
        offset += numbytes;
        for (int s : value) {
            numbytes = serializeUINT16(s, bytes, offset);
            offset += numbytes;
        }
        return (offset - offsetStart);
    }

    public static int serializeUInt32Array(long[] value,
                                           byte[] bytes,
                                           int offset) {
        int numbytes = 0;
        int offsetStart = offset;
        numbytes = serializeUINT16(value.length, bytes, offset);
        offset += numbytes;
        for (long s : value) {
            numbytes = serializeUINT32(s, bytes, offset);
            offset += numbytes;
        }
        return (offset - offsetStart);
    }

    public static int serializeUInt64Array(BigInteger[] value,
                                           byte[] bytes,
                                           int offset) {
        int numbytes = 0;
        int offsetStart = offset;
        numbytes = serializeUINT16(value.length, bytes, offset);
        offset += numbytes;
        for (BigInteger s : value) {
            numbytes = serializeUINT64(s, bytes, offset);
            offset += numbytes;
        }
        return (offset - offsetStart);
    }

    public static int serializeBooleanArray(boolean[] value,
                                            byte[] bytes,
                                            int offset) {
        int numbytes = 0;
        int offsetStart = offset;
        numbytes = serializeUINT16(value.length, bytes, offset);
        offset += numbytes;
        for (boolean s : value) {
            numbytes = serializeBOOLEAN(s, bytes, offset);
            offset += numbytes;
        }
        return (offset - offsetStart);
    }

    public static int serializeByteArray(byte[] value,
                                         byte[] bytes,
                                         int offset) {
        int numbytes = 0;
        int offsetStart = offset;
        numbytes = serializeUINT16(value.length, bytes, offset);
        offset += numbytes;
        for (byte s : value) {
            numbytes = serializeBYTE(s, bytes, offset);
            offset += numbytes;
        }
        return (offset - offsetStart);
    }

    public static int serializeDoubleArray(double[] value, byte[] bytes, int offset) {
        int numbytes = 0;
        int offsetStart = offset;
        numbytes = serializeUINT16(value.length, bytes, offset);
        offset += numbytes;
        for (double s : value) {
            numbytes = serializeDOUBLE(s, bytes, offset);
            offset += numbytes;
        }
        return (offset - offsetStart);
    }

    public static int serializeFloatArray(float[] value, byte[] bytes, int offset) {
        int numbytes = 0;
        int offsetStart = offset;
        numbytes = serializeUINT16(value.length, bytes, offset);
        offset += numbytes;
        for (float s : value) {
            numbytes = serializeFLOAT(s, bytes, offset);
            offset += numbytes;
        }
        return (offset - offsetStart);
    }

    public static int serializeEVENTWORD(String aString, byte[] bytes, int offset) {
        return serializeEVENTWORD(aString, bytes, offset, Event.DEFAULT_ENCODING);
    }

    private static int serializeEVENTWORD(String aString,
                                          byte[] bytes,
                                          int offset,
                                          short encoding) {
        byte[] stringBytes =
                EncodedString.getBytes(aString, Event.ENCODING_STRINGS[encoding]);
        int length = stringBytes.length;
        if (0 <= length && length <= 255) {
            offset += serializeUBYTE((short) length, bytes, offset);
            System.arraycopy(stringBytes, 0, bytes, offset, length);
            return (length + 1);
        }
        return 0;

    }

    public static int serializeATTRIBUTEWORD(String aString, byte[] bytes,
                                             int offset) {
        return serializeEVENTWORD(aString, bytes, offset, Event.DEFAULT_ENCODING);
    }

    /**
     * Serialize IPAddress in *reverse* network order. Don't use this.
     *
     * @param anIPAddress the ip address to serialize
     * @param bytes       the byte array to modify
     * @param offset      what index in the array to start at
     * @return how many bytes were set in the array
     * @deprecated
     */
    @Deprecated
    public static int serializeIPADDR(IPAddress anIPAddress, byte[] bytes, int offset) {
        byte[] inetaddr = anIPAddress.getInetAddressAsBytes();
        bytes[offset + 3] = inetaddr[0];
        bytes[offset + 2] = inetaddr[1];
        bytes[offset + 1] = inetaddr[2];
        bytes[offset] = inetaddr[3];
        return (4);
    }

    /**
     * Serializes an ip address in *reverse* network order. Don't use this.
     *
     * @param anIPAddress the ip address to serialize
     * @param bytes       the byte array to modify
     * @param offset      what index in the array to start at
     * @return how many bytes were set in the array
     * @deprecated
     */
    @Deprecated
    public static int serializeIPADDR(InetAddress anIPAddress, byte[] bytes, int offset) {
        byte[] inetaddr = anIPAddress.getAddress();
        bytes[offset + 3] = inetaddr[0];
        bytes[offset + 2] = inetaddr[1];
        bytes[offset + 1] = inetaddr[2];
        bytes[offset] = inetaddr[3];
        return (4);
    }

    /**
     * Serialize InetAddress in network order.
     *
     * @param ip     the ip address to serialize
     * @param bytes  the byte array to modify
     * @param offset what index in the array to start at
     * @return how many bytes were set in the array
     */
    public static int serializeIPV4(InetAddress ip, byte[] bytes, int offset) {
        byte[] inetaddr = ip.getAddress();
        bytes[offset] = inetaddr[0];
        bytes[offset + 1] = inetaddr[1];
        bytes[offset + 2] = inetaddr[2];
        bytes[offset + 3] = inetaddr[3];
        return (4);
    }

    public static int serializeValue(FieldType type,
                                     Object data,
                                     short encoding,
                                     byte[] bytes,
                                     final int offset) {
        switch (type) {
            case BYTE:
                return Serializer.serializeBYTE((Byte) data, bytes, offset);
            case BOOLEAN:
                return Serializer.serializeBOOLEAN((Boolean) data, bytes, offset);
            case UINT16:
                return Serializer.serializeUINT16((Integer) data, bytes, offset);
            case INT16:
                return Serializer.serializeINT16((Short) data, bytes, offset);
            case UINT32:
                return Serializer.serializeUINT32((Long) data, bytes, offset);
            case INT32:
                return Serializer.serializeINT32((Integer) data, bytes, offset);
            case UINT64:
                return Serializer.serializeUINT64((BigInteger) data, bytes, offset);
            case INT64:
                return Serializer.serializeINT64((Long) data, bytes, offset);
            case STRING:
                return Serializer.serializeSTRING(((String) data), bytes, offset, encoding);
            case DOUBLE:
                return Serializer.serializeDOUBLE(((Double) data), bytes, offset);
            case FLOAT:
                return Serializer.serializeFLOAT(((Float) data), bytes, offset);
            case IPADDR:
                return Serializer.serializeIPADDR(((IPAddress) data), bytes, offset);
            case STRING_ARRAY:
                return Serializer.serializeStringArray
                        (((String[]) data), bytes, offset, encoding);
            case NUINT32_ARRAY:
                return Serializer.serializeNUInt32Array((Long[]) data, bytes, offset);
            case NINT32_ARRAY:
                return Serializer.serializeNInt32Array((Integer[]) data, bytes, offset);
            case NUINT16_ARRAY:
                return Serializer.serializeNUInt16Array((Integer[]) data, bytes, offset);
            case NINT16_ARRAY:
                return Serializer.serializeNInt16Array((Short[]) data, bytes, offset);
            case NINT64_ARRAY:
                return Serializer.serializeNInt64Array((Long[]) data, bytes, offset);
            case NDOUBLE_ARRAY:
                return Serializer.serializeNDoubleArray((Double[]) data, bytes, offset);
            case NFLOAT_ARRAY:
                return Serializer.serializeNFloatArray((Float[]) data, bytes, offset);
            case INT16_ARRAY:
                return Serializer.serializeInt16Array((short[]) data, bytes, offset);
            case INT32_ARRAY:
                return Serializer.serializeInt32Array((int[]) data, bytes, offset);
            case INT64_ARRAY:
                return Serializer.serializeInt64Array((long[]) data, bytes, offset);
            case UINT16_ARRAY:
                return Serializer.serializeUInt16Array((int[]) data, bytes, offset);
            case UINT32_ARRAY:
                return Serializer.serializeUInt32Array((long[]) data, bytes, offset);
            case UINT64_ARRAY:
                return Serializer.serializeUInt64Array((BigInteger[]) data, bytes, offset);
            case NUINT64_ARRAY:
                return Serializer.serializeNUInt64Array((BigInteger[]) data, bytes, offset);
            case NBOOLEAN_ARRAY:
                return Serializer.serializeNBooleanArray((Boolean[]) data, bytes, offset);
            case BOOLEAN_ARRAY:
                return Serializer.serializeBooleanArray((boolean[]) data, bytes, offset);
            case NBYTE_ARRAY:
                return Serializer.serializeNByteArray((Byte[]) data, bytes, offset);
            case BYTE_ARRAY:
                return Serializer.serializeByteArray((byte[]) data, bytes, offset);
            case DOUBLE_ARRAY:
                return Serializer.serializeDoubleArray((double[]) data, bytes, offset);
            case FLOAT_ARRAY:
                return Serializer.serializeFloatArray((float[]) data, bytes, offset);
            case IP_ADDR_ARRAY:
                return Serializer.serializeIPADDRArray((IPAddress[]) data, bytes, offset);
            case NSTRING_ARRAY:
                return Serializer.serializeNStringArray((String[]) data, bytes, offset, encoding);

        }

        throw new IllegalArgumentException("Unknown BaseType token: " + type);
    }

    /**
     * Pack as many bitset elements into one byte as possible.
     *
     * @param bitSet BitSet that indicates which indexes in an array are not null.
     * @param data the byte array to write the packed version of the bitSet to
     * @param offset the index to start writing to data
     * @return the number of bytes written to data
     */
    public static int serializeBitSet(BitSet bitSet, int arrayLength, byte[] data, int offset) {
        int offsetStart = offset;
        offset += serializeUINT16((short) arrayLength, data, offset);
        int lastIndex = 0;
        for (int i = 0, c=0; i < arrayLength; i++,c++) {
            int index = i >>> 3;
            if (index != lastIndex) {
                c = 0;
                lastIndex = index;
            }
            if (bitSet.get(i)) {
                data[offset + index] |= (1 << c);
            }
        }
        // Set the offset to how many bits we had to use
        offset += (int) Math.ceil((double) arrayLength / 8.0);
        return (offset - offsetStart);
    }

    /**
     * For serializing arrays that can contain nulls. For strings, we need to figure out how long each string is
     * in order to create the temporary array to write into
     *
     * @param data array to serialize
     * @param bytes byte array to write to
     * @param offset index in byte array to start at
     * @param encoding encoding to use for strings
     * @return number of bytes written
     */
    public static int serializeNStringArray(String[] data, byte[] bytes, int offset, short encoding) {
        int numbytes = 0;
        int offsetStart = offset;

        // Number of items in the array
        numbytes = serializeUINT16((short) data.length, bytes, offset);
        offset += numbytes;

        List<byte[]> tmp = new ArrayList<byte[]>(data.length);

        // use a bitset to determine which indexes have values and which are null.
        // serialize the actual values into a temporary byte array
        BitSet bitSet = new BitSet(data.length);
        int i = 0;
        for (String s : data) {
            if (s != null) {
                byte[] stringBytes = EncodedString.getBytes(s, Event.ENCODING_STRINGS[encoding]);
                byte[] tmpArr = new byte[stringBytes.length + 2];
                serializeUINT16(stringBytes.length, tmpArr, 0);
                System.arraycopy(stringBytes, 0, tmpArr, 2, stringBytes.length);
                tmp.add(i, tmpArr);
                bitSet.set(i);
            }
            else {
                tmp.add(i, null);
            }
            i++;
        }

        // Write the bitset first to ease with deserialization
        offset += serializeBitSet(bitSet, data.length, bytes, offset);
        // Now write the values
        for (byte[] a : tmp) {
            if (a != null) {
                System.arraycopy(a, 0, bytes, offset, a.length);
                offset += a.length;
            }
        }

        return (offset - offsetStart);
    }

    /**
     * For serializing arrays that can contain nulls, blahblah
     *
     * @param data array to serialize
     * @param bytes byte array to write to
     * @param offset index in byte array to start at
     * @return number of bytes written
     */
    public static int serializeNFloatArray(Float[] data, byte[] bytes, int offset) {
        int numbytes = 0;
        int offsetStart = offset;

        // Number of items in the array
        numbytes = serializeUINT16((short) data.length, bytes, offset);
        offset += numbytes;

        byte[] tmp = new byte[4 * data.length];
        int tmpOffset = 0;

        // use a bitset to determine which indexes have values and which are null.
        // serialize the actual values into a temporary byte array
        BitSet bitSet = new BitSet(data.length);
        int i = 0;
        for (Float s : data) {
            if (s != null) {
                numbytes = serializeFLOAT(s, tmp, tmpOffset);
                tmpOffset += numbytes;
                bitSet.set(i);
            }
            i++;
        }

        // Write the bitset first to ease with deserialization
        offset += serializeBitSet(bitSet, data.length, bytes, offset);
        // Now write the float values
        System.arraycopy(tmp, 0, bytes, offset, tmpOffset);
        offset += tmpOffset;

        return (offset - offsetStart);
    }

    public static int serializeNByteArray(Byte[] data, byte[] bytes, int offset) {
        int numbytes = 0;
        int offsetStart = offset;

        // Number of items in the array
        numbytes = serializeUINT16((short) data.length, bytes, offset);
        offset += numbytes;

        byte[] tmp = new byte[data.length];
        int tmpOffset = 0;

        // use a bitset to determine which indexes have values and which are null.
        // serialize the actual values into a temporary byte array
        BitSet bitSet = new BitSet(data.length);
        int i = 0;
        for (Byte s : data) {
            if (s != null) {
                numbytes = serializeBYTE(s, tmp, tmpOffset);
                tmpOffset += numbytes;
                bitSet.set(i);
            }
            i++;
        }

        // Write the bitset first to ease with deserialization
        offset += serializeBitSet(bitSet, data.length, bytes, offset);
        // Now write the float values
        System.arraycopy(tmp, 0, bytes, offset, tmpOffset);
        offset += tmpOffset;

        return (offset - offsetStart);
    }

    public static int serializeNBooleanArray(Boolean[] data, byte[] bytes, int offset) {
        int numbytes = 0;
        int offsetStart = offset;

        // Number of items in the array
        numbytes = serializeUINT16((short) data.length, bytes, offset);
        offset += numbytes;

        byte[] tmp = new byte[data.length];
        int tmpOffset = 0;

        // use a bitset to determine which indexes have values and which are null.
        // serialize the actual values into a temporary byte array
        BitSet bitSet = new BitSet(data.length);
        int i = 0;
        for (Boolean s : data) {
            if (s != null) {
                numbytes = serializeBOOLEAN(s, tmp, tmpOffset);
                tmpOffset += numbytes;
                bitSet.set(i);
            }
            i++;
        }

        // Write the bitset first to ease with deserialization
        offset += serializeBitSet(bitSet, data.length, bytes, offset);
        // Now write the float values
        System.arraycopy(tmp, 0, bytes, offset, tmpOffset);
        offset += tmpOffset;

        return (offset - offsetStart);
    }

    public static int serializeNDoubleArray(Double[] data, byte[] bytes, int offset) {
        int numbytes = 0;
        int offsetStart = offset;

        // Number of items in the array
        numbytes = serializeUINT16((short) data.length, bytes, offset);
        offset += numbytes;

        byte[] tmp = new byte[8 * data.length];
        int tmpOffset = 0;

        // use a bitset to determine which indexes have values and which are null.
        // serialize the actual values into a temporary byte array
        BitSet bitSet = new BitSet(data.length);
        int i = 0;
        for (Double s : data) {
            if (s != null) {
                numbytes = serializeDOUBLE(s, tmp, tmpOffset);
                tmpOffset += numbytes;
                bitSet.set(i);
            }
            i++;
        }

        // Write the bitset first to ease with deserialization
        offset += serializeBitSet(bitSet, data.length, bytes, offset);
        // Now write the float values
        System.arraycopy(tmp, 0, bytes, offset, tmpOffset);
        offset += tmpOffset;

        return (offset - offsetStart);
    }

    public static int serializeNInt16Array(Short[] data, byte[] bytes, int offset) {
        int numbytes = 0;
        int offsetStart = offset;

        // Number of items in the array
        numbytes = serializeUINT16((short) data.length, bytes, offset);
        offset += numbytes;

        byte[] tmp = new byte[2 * data.length];
        int tmpOffset = 0;

        // use a bitset to determine which indexes have values and which are null.
        // serialize the actual values into a temporary byte array
        BitSet bitSet = new BitSet(data.length);
        int i = 0;
        for (Short s : data) {
            if (s != null) {
                numbytes = serializeINT16(s, tmp, tmpOffset);
                tmpOffset += numbytes;
                bitSet.set(i);
            }
            i++;
        }

        // Write the bitset first to ease with deserialization
        offset += serializeBitSet(bitSet, data.length, bytes, offset);
        // Now write the float values
        System.arraycopy(tmp, 0, bytes, offset, tmpOffset);
        offset += tmpOffset;

        return (offset - offsetStart);
    }

    public static int serializeNUInt16Array(Integer[] data, byte[] bytes, int offset) {
        int numbytes = 0;
        int offsetStart = offset;

        // Number of items in the array
        numbytes = serializeUINT16((short) data.length, bytes, offset);
        offset += numbytes;

        byte[] tmp = new byte[2 * data.length];
        int tmpOffset = 0;

        // use a bitset to determine which indexes have values and which are null.
        // serialize the actual values into a temporary byte array
        BitSet bitSet = new BitSet(data.length);
        int i = 0;
        for (Integer s : data) {
            if (s != null) {
                numbytes = serializeUINT16(s, tmp, tmpOffset);
                tmpOffset += numbytes;
                bitSet.set(i);
            }
            i++;
        }

        // Write the bitset first to ease with deserialization
        offset += serializeBitSet(bitSet, data.length, bytes, offset);
        // Now write the float values
        System.arraycopy(tmp, 0, bytes, offset, tmpOffset);
        offset += tmpOffset;

        return (offset - offsetStart);
    }

    public static int serializeNInt32Array(Integer[] data, byte[] bytes, int offset) {
        int numbytes = 0;
        int offsetStart = offset;

        // Number of items in the array
        numbytes = serializeUINT16((short) data.length, bytes, offset);
        offset += numbytes;

        byte[] tmp = new byte[4 * data.length];
        int tmpOffset = 0;

        // use a bitset to determine which indexes have values and which are null.
        // serialize the actual values into a temporary byte array
        BitSet bitSet = new BitSet(data.length);
        int i = 0;
        for (Integer s : data) {
            if (s != null) {
                numbytes = serializeINT32(s, tmp, tmpOffset);
                tmpOffset += numbytes;
                bitSet.set(i);
            }
            i++;
        }

        // Write the bitset first to ease with deserialization
        offset += serializeBitSet(bitSet, data.length, bytes, offset);
        // Now write the float values
        System.arraycopy(tmp, 0, bytes, offset, tmpOffset);
        offset += tmpOffset;

        return (offset - offsetStart);
    }

    public static int serializeNUInt32Array(Long[] data, byte[] bytes, int offset) {
        int numbytes = 0;
        int offsetStart = offset;

        // Number of items in the array
        numbytes = serializeUINT16((short) data.length, bytes, offset);
        offset += numbytes;

        byte[] tmp = new byte[4 * data.length];
        int tmpOffset = 0;

        // use a bitset to determine which indexes have values and which are null.
        // serialize the actual values into a temporary byte array
        BitSet bitSet = new BitSet(data.length);
        int i = 0;
        for (Long s : data) {
            if (s != null) {
                numbytes = serializeUINT32(s, tmp, tmpOffset);
                tmpOffset += numbytes;
                bitSet.set(i);
            }
            i++;
        }

        // Write the bitset first to ease with deserialization
        offset += serializeBitSet(bitSet, data.length, bytes, offset);
        // Now write the float values
        System.arraycopy(tmp, 0, bytes, offset, tmpOffset);
        offset += tmpOffset;

        return (offset - offsetStart);
    }

    public static int serializeNInt64Array(Long[] data, byte[] bytes, int offset) {
        int numbytes = 0;
        int offsetStart = offset;

        // Number of items in the array
        numbytes = serializeUINT16((short) data.length, bytes, offset);
        offset += numbytes;

        byte[] tmp = new byte[8 * data.length];
        int tmpOffset = 0;

        // use a bitset to determine which indexes have values and which are null.
        // serialize the actual values into a temporary byte array
        BitSet bitSet = new BitSet(data.length);
        int i = 0;
        for (Long s : data) {
            if (s != null) {
                numbytes = serializeINT64(s, tmp, tmpOffset);
                tmpOffset += numbytes;
                bitSet.set(i);
            }
            i++;
        }

        // Write the bitset first to ease with deserialization
        offset += serializeBitSet(bitSet, data.length, bytes, offset);
        // Now write the float values
        System.arraycopy(tmp, 0, bytes, offset, tmpOffset);
        offset += tmpOffset;

        return (offset - offsetStart);
    }

    public static int serializeNUInt64Array(BigInteger[] data, byte[] bytes, int offset) {
        int numbytes = 0;
        int offsetStart = offset;

        // Number of items in the array
        numbytes = serializeUINT16((short) data.length, bytes, offset);
        offset += numbytes;

        byte[] tmp = new byte[8 * data.length];
        int tmpOffset = 0;

        // use a bitset to determine which indexes have values and which are null.
        // serialize the actual values into a temporary byte array
        BitSet bitSet = new BitSet(data.length);
        int i = 0;
        for (BigInteger s : data) {
            if (s != null) {
                numbytes = serializeUINT64(s, tmp, tmpOffset);
                tmpOffset += numbytes;
                bitSet.set(i);
            }
            i++;
        }

        // Write the bitset first to ease with deserialization
        offset += serializeBitSet(bitSet, data.length, bytes, offset);
        // Now write the float values
        System.arraycopy(tmp, 0, bytes, offset, tmpOffset);
        offset += tmpOffset;

        return (offset - offsetStart);
    }
}
