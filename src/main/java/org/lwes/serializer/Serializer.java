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

import org.lwes.Event;
import org.lwes.util.EncodedString;
import org.lwes.util.IPAddress;
import org.lwes.util.NumberCodec;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;

/**
 * This contains low level type serialization used by the
 * rest of the system.
 *
 * @author Anthony Molinaro
 */
public class Serializer {

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
     * @return
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

    public static int serializeUInt64Array(long[] value,
                                           byte[] bytes,
                                           int offset) {
        int numbytes = 0;
        int offsetStart = offset;
        numbytes = serializeUINT16(value.length, bytes, offset);
        offset += numbytes;
        for (long s : value) {
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
        if (length < 255 && length > 0) {
            offset += serializeBYTE((byte) length, bytes, offset);
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
}
