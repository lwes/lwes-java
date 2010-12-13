/*======================================================================*
 * Copyright (c) 2010, Frank Maritato All rights reserved.              *
 *                                                                      *
 * Licensed under the New BSD License (the "License"); you may not use  *
 * this file except in compliance with the License.  Unless required    *
 * by applicable law or agreed to in writing, software distributed      *
 * under the License is distributed on an "AS IS" BASIS, WITHOUT        *
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     *
 * See the License for the specific language governing permissions and  *
 * limitations under the License. See accompanying LICENSE file.        *
 *======================================================================*/

package org.lwes.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import java.math.BigInteger;

/**
 * @author fmaritato
 */

public class NumberCodecTest {

    @Test
    public void testToHexString() {
        String s = NumberCodec.toHexString((byte) 1);
        assertNotNull(s);
        assertEquals("Byte to hex incorrect", "01", s);

        s = NumberCodec.toHexString((short) 1);
        assertNotNull(s);
        assertEquals("Short to hex incorrect", "0001", s);

        s = NumberCodec.toHexString(1);
        assertNotNull(s);
        assertEquals("Int to hex incorrect", "00000001", s);

        s = NumberCodec.toHexString((long) 1);
        assertNotNull(s);
        assertEquals("Long to hex incorrect", "0000000000000001", s);

        s = NumberCodec.toHexString(new BigInteger("1000"));
        assertNotNull(s);
        assertEquals("BigInteger to hex incorrect", "3e8", s);
    }

    @Test
    public void testWriteHexStringToBuffer() {
        StringBuffer buf = new StringBuffer(16);

        NumberCodec.writeHexString((byte) 1, buf);
        assertEquals("01", buf.toString());
        buf.delete(0, buf.length());

        NumberCodec.writeHexString((short) 1, buf);
        assertEquals("0001", buf.toString());
        buf.delete(0, buf.length());

        NumberCodec.writeHexString(1, buf);
        assertEquals("00000001", buf.toString());
        buf.delete(0, buf.length());

        NumberCodec.writeHexString((long) 1, buf);
        assertEquals("0000000000000001", buf.toString());
        buf.delete(0, buf.length());
    }

    @Test
    public void testByteArrayToHexString() {
        byte[] buf = NumberCodec.hexStringToByteArray("011e");
        String hex1 = NumberCodec.byteArrayToHexString(buf);
        assertEquals("byteArrayToHexString failed", "011e", hex1);
        String hex2 = NumberCodec.byteArrayToHexString(buf, 0, buf.length);
        assertEquals("byteArrayToHexString failed", "011e", hex2);

        byte b = NumberCodec.byteFromHexString("0a");
        assertEquals("byteFromHexString fail", 10, b);
    }

    @Test
    public void testEncodeDecode() {

        // encodeLong and decodeLong call the unchecked versions underneath.
        byte[] buf = new byte[64];
        NumberCodec.encodeLong(123l, buf, 0, buf.length);
        long lval = NumberCodec.decodeLong(buf, 0, buf.length);
        assertEquals("encode/decode long unchecked fail", 123l, lval);

        NumberCodec.encodeInt(123, buf, 0, buf.length);
        int ival = NumberCodec.decodeInt(buf, 0, buf.length);
        assertEquals("encode/decode int unchecked fail", 123, ival);

        NumberCodec.encodeShort((short) 12, buf, 0, buf.length);
        short sval = NumberCodec.decodeShort(buf, 0, buf.length);
        assertEquals("encode/decode short unchecked fail", 12, sval);

    }
}
