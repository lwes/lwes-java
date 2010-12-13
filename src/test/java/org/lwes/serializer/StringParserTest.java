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

package org.lwes.serializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.lwes.EventSystemException;
import org.lwes.util.IPAddress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author fmaritato
 */
public class StringParserTest {

    private static transient Log log = LogFactory.getLog(StringParserTest.class);

    @Test
    public void testFromStringBOOLEAN() {
        try {
            Boolean b = (Boolean) StringParser.fromStringBOOLEAN("true");
            assertNotNull(b);
            assertTrue(b);
        }
        catch (EventSystemException e) {
            log.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    @Test
    public void testFromStringBYTE() {
        try {
            // hmm this method just returns null....?
            assertNull(StringParser.fromStringBYTE("1"));
        }
        catch (EventSystemException e) {
            log.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    @Test
    public void testFromStringINT16() {
        try {
            Short s = (Short) StringParser.fromStringINT16("12");
            assertNotNull(s);
            assertEquals("fromStringINT16 failed", (short) 12, s.shortValue());
        }
        catch (EventSystemException e) {
            log.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    @Test
    public void testFromStringINT32() {
        try {
            long val = Integer.MAX_VALUE + 1l;
            StringParser.fromStringINT32(val + "");
        }
        catch (EventSystemException e) {
            if (!(e.getCause() instanceof NumberFormatException)) {
                fail(e.getMessage());
            }
        }

        try {
            int intVal = (Integer) StringParser.fromStringINT32("355567");
            assertEquals("int value was incorrect", 355567, intVal);
        }
        catch (EventSystemException e) {
            log.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    @Test
    public void testFromStringINT64() {
        try {
            Long val = Long.MAX_VALUE;
            Long v = (Long) StringParser.fromStringINT64(val.toString());
            assertNotNull(v);
            assertEquals("fromStringINT64 failed", Long.MAX_VALUE, v.longValue());
        }
        catch (EventSystemException e) {
            log.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    @Test
    public void testFromStringIPADDR() {
        try {
            IPAddress ip = (IPAddress) StringParser.fromStringIPADDR("192.168.1.1");
            assertNotNull(ip);
            assertEquals("fromStringIPADDR failed", "192.168.1.1", ip.toString());
        }
        catch (EventSystemException e) {
            log.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    @Test
    public void testFromStringUINT16() {
        try {
            Integer s = (Integer) StringParser.fromStringUINT16("65534");
            assertNotNull(s);
            assertEquals("fromStringINT16 failed", 65534, s.intValue());

            s = (Integer) StringParser.fromStringUINT16("0xFFFE");
            assertNotNull(s);
            assertEquals("fromStringINT16 failed", 65534, s.intValue());
        }
        catch (EventSystemException e) {
            log.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    @Test
    public void testFromStringUINT32() {
        try {
            Long obj = (Long) StringParser.fromStringUINT32("4294967294");
            assertNotNull(obj);
            assertEquals("fromStringUINT32", 4294967294l, obj.longValue());

            obj = (Long) StringParser.fromStringUINT32("0xFFFFFFFE");
            assertNotNull(obj);
            assertEquals("fromStringUINT32", 4294967294l, obj.longValue());
        }
        catch (EventSystemException e) {
            log.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    @Test
    public void testFromStringUINT64() {
        try {
            Long val = Long.MAX_VALUE;
            Long v = (Long) StringParser.fromStringUINT64(val.toString());
            assertNotNull(v);
            assertEquals("fromStringINT64 failed", Long.MAX_VALUE, v.longValue());

            // now try it as a hex string...
            v = (Long) StringParser.fromStringUINT64("0x7fffffffffffffff");
            assertNotNull(v);
            assertEquals("fromStringINT64 failed", Long.MAX_VALUE, v.longValue());
        }
        catch (EventSystemException e) {
            log.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

}
