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
import java.net.InetAddress;

/**
 * This contains low level type serialization used by the
 * rest of the system.
 *
 * @author Anthony Molinaro
 */
public class Serializer {

    public static int serializeBYTE(byte aByte, byte[] bytes, int offset) {
        bytes[offset] = aByte;
        return (1);
    }

    public static int serializeBOOLEAN(boolean aBoolean, byte[] bytes, int offset) {
        if (aBoolean) {
            bytes[offset] = (byte) 0x01;
        }
        else {
            bytes[offset] = (byte) 0x00;
        }
        return (1);
    }

    public static int serializeUINT16(int anUnsignedShortInt, byte[] bytes,
                                      int offset) {
        bytes[offset] = (byte) ((anUnsignedShortInt & (255 << 8)) >> 8);
        bytes[offset + 1] = (byte) ((anUnsignedShortInt & (255 << 0)) >> 0);
        return (2);
    }

    public static int serializeINT16(short aShortInt, byte[] bytes, int offset) {
        bytes[offset] = (byte) ((aShortInt & (255 << 8)) >> 8);
        bytes[offset + 1] = (byte) ((aShortInt & (255 << 0)) >> 0);
        return (2);
    }

    public static int serializeUINT32(long anUnsignedInt, byte[] bytes,
                                      int offset) {
        bytes[offset] = (byte) ((anUnsignedInt & 0xff000000) >> 24);
        bytes[offset + 1] = (byte) ((anUnsignedInt & 0x00ff0000) >> 16);
        bytes[offset + 2] = (byte) ((anUnsignedInt & 0x0000ff00) >> 8);
        bytes[offset + 3] = (byte) ((anUnsignedInt & 0x000000ff) >> 0);
        return (4);
    }

    public static int serializeINT32(int anInt, byte[] bytes,
                                     int offset) {
        bytes[offset] = (byte) ((anInt & (255 << 24)) >> 24);
        bytes[offset + 1] = (byte) ((anInt & (255 << 16)) >> 16);
        bytes[offset + 2] = (byte) ((anInt & (255 << 8)) >> 8);
        bytes[offset + 3] = (byte) ((anInt & (255 << 0)) >> 0);
        return (4);
    }

    public static int serializeINT64(long anInt, byte[] bytes, int offset) {
        NumberCodec.encodeLongUnchecked(anInt, bytes, offset);
        return (8);
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

    /*
       * @deprecated
       */
    public static int serializeEVENTWORD(String aString, byte[] bytes, int offset) {
        return serializeEVENTWORD(aString, bytes, offset, Event.DEFAULT_ENCODING);
    }

    private static int serializeEVENTWORD(String aString, byte[] bytes,
                                          int offset, short encoding) {
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

    public static int serializeIPADDR(IPAddress anIPAddress, byte[] bytes,
                                      int offset) {
        byte[] inetaddr = anIPAddress.getInetAddressAsBytes();
        bytes[offset + 3] = inetaddr[0];
        bytes[offset + 2] = inetaddr[1];
        bytes[offset + 1] = inetaddr[2];
        bytes[offset] = inetaddr[3];
        return (4);
    }

    public static int serializeIPADDR(InetAddress anIPAddress, byte[] bytes,
                                      int offset) {
        byte[] inetaddr = anIPAddress.getAddress();
        bytes[offset + 3] = inetaddr[0];
        bytes[offset + 2] = inetaddr[1];
        bytes[offset + 1] = inetaddr[2];
        bytes[offset] = inetaddr[3];
        return (4);
    }
}
