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

package org.lwes;
/**
 * @author fmaritato
 */

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.lwes.db.EventTemplateDB;

import java.io.File;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class EventTest {

    private static transient final Log log = LogFactory.getLog(EventTest.class);

    private EventTemplateDB eventTemplate;

    @Before
    public void setUp() {
        eventTemplate = new EventTemplateDB();
        eventTemplate.setESFFile(new File("src/test/java/org/lwes/EventTest.esf"));
        eventTemplate.initialize();
    }

    @Test
    public void testGetAttributeNames() throws EventSystemException {
        Event evt = new Event("Test", false, eventTemplate);
        evt.setString("str", "string");

        boolean success = false;
        Enumeration en = evt.getEventAttributeNames();
        while (en.hasMoreElements()) {
            String name = (String) en.nextElement();
            if ("str".equals(name)) {
                success = true;
            }
        }
        assertTrue(success);
    }

    @Test
    public void testIPV4() throws EventSystemException, UnknownHostException {
        Event evt = new Event("Test", true, eventTemplate);
        evt.setIPV4Address("userIP", InetAddress.getByName("www.yahoo.com"));
        InetAddress a = evt.getIPV4Address("userIP");
        assertNotNull(a);

        byte[] bytes = evt.serialize();
        Event evt2 = new Event(bytes, true, eventTemplate);
        assertNotNull(evt2);
        InetAddress b = evt2.getIPV4Address("userIP");
        assertNotNull(b);
        // Can't test the exact hostname b/c you actually get a vip hostname back...
        assertTrue("Not a yahoo.com address", b.getHostName().endsWith(".yahoo.com"));
    }

    @Test
    public void testGetInetAddress() throws EventSystemException, UnknownHostException {
        Event evt = new Event("Test", false, eventTemplate);
        evt.setIPAddress("ip", InetAddress.getByName("www.yahoo.com"));
        InetAddress a = evt.getInetAddress("ip");
        assertNotNull(a);
    }

    @Test
    public void testIsSet() throws EventSystemException {
        Event evt = new Event("Test", false, eventTemplate);
        assertFalse(evt.isSet("notset"));

        evt.setInt32("set", 32);
        assertTrue(evt.isSet("set"));
    }

    @Test
    public void testNullValue() throws EventSystemException {
        Event evt = new Event("Test", false, eventTemplate);
        Short s = evt.getInt16("a");
        assertNull(s);
        evt.setInt16("a", (short) 1);
        s = evt.getInt16("a");
        assertNotNull(s);
        assertEquals("short value incorrect", (short) 1, s.shortValue());
    }

    @Test
    public void testUnsignedTypesValidate() throws EventSystemException {
        Event evt = new Event("Test", false, eventTemplate);
        try {
            evt.setUInt16("SiteID", 0);
            evt.validate();
        }
        catch (EventSystemException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testValidateEventName() throws EventSystemException {
        boolean exceptionThrown = false;
        Event evt = new Event("Test2", false, eventTemplate);
        try {
            evt.validate();
        }
        catch (ValidationExceptions e) {
            exceptionThrown = true;
        }
        assertTrue("No exception for invalid event", exceptionThrown);
    }

    @Test
    public void testValidateField() throws EventSystemException {
        Event evt = new Event("Test", false, eventTemplate);
        try {
            evt.setString("field1", "avalue");
            evt.validate();
        }
        catch (EventSystemException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testValidateBadTypeField() throws EventSystemException {
        Event evt = new Event("Test", false, eventTemplate);
        try {
            evt.setInt16("field1", (short) 15);
            evt.validate();
        }
        catch (ValidationExceptions e) {
            List<EventSystemException> exc = e.getAllExceptions();
            assertEquals("Wrong num of exceptions", 1, exc.size());
            assertEquals("Wrong exception",
                         "org.lwes.NoSuchAttributeTypeException",
                         exc.get(0).getClass().getName());
        }
    }

    @Test
    public void testValidateBadField() throws EventSystemException {
        Event evt = new Event("Test", false, eventTemplate);
        try {
            evt.setInt16("field3", (short) 15);
            evt.validate();
        }
        catch (ValidationExceptions e) {
            List<EventSystemException> exc = e.getAllExceptions();
            assertEquals("Wrong num of exceptions", 1, exc.size());
            assertEquals("Wrong exception",
                         "org.lwes.NoSuchAttributeException",
                         exc.get(0).getClass().getName());
        }
    }

    @Test
    public void testSerialize() throws EventSystemException {
        Event evt = new Event("Test", false, eventTemplate);
        evt.setString("attr_s", "str_value");
        evt.setInt32("attr_i", 1);
        byte[] bytes = evt.serialize();
        String str = new String(bytes);
        byte[] encoded = Base64.encodeBase64(bytes);
        // TODO this test not finished yet.
    }

    @Test
    public void testEventAccessors() throws EventSystemException, UnknownHostException {
        Event evt = new Event("Test", false, eventTemplate);

        evt.setInt16("int16", (short) 1);
        evt.setInt32("int32", 1337);
        evt.setInt64("int64", 1337133713371337l);
        evt.setBoolean("bool", true);
        evt.setString("str", "string");
        evt.setUInt16("uint16", 1337); // uint16 in java is just an int
        evt.setUInt32("uint32", 1337133713371337l); // uint32 in java is a long
        evt.setUInt64("uint64", 1337133713371337l); // uint64 is a BigInteger
        evt.setIPAddress("ipaddr", InetAddress.getByName("localhost"));

        Short s = evt.getInt16("int16");
        assertNotNull(s);
        assertEquals("int16 wrong", 1, s.shortValue());
        Integer i = evt.getInt32("int32");
        assertNotNull(i);
        assertEquals("int32 wrong", 1337, i.intValue());
        Long l = evt.getInt64("int64");
        assertNotNull(l);
        assertEquals("int64 wrong", 1337133713371337l, l.longValue());
        assertEquals("bool wrong", true, evt.getBoolean("bool"));
        assertEquals("str wrong", "string", evt.getString("str"));
        i = evt.getUInt16("uint16");
        assertNotNull(i);
        assertEquals("uint16 wrong", 1337, i.intValue());
        l = evt.getUInt32("uint32");
        assertEquals("uint32 wrong", 1337133713371337l, l.longValue());
        assertEquals("uint64 wrong",
                     BigInteger.valueOf(1337133713371337l),
                     evt.getUInt64("uint64"));

    }

    @Test
    public void testEventSize() throws EventSystemException {

        Event evt = new Event("Test", false, eventTemplate);

        for (int i = 0; i < 5000; i++) {
            evt.setInt32("" + i, i);
        }

        byte[] bytes = evt.serialize();
        assertEquals("number of bytes wrong?", 48904, bytes.length);

        boolean exceptionThrown = false;
        try {
            for (int i = 5001; i < 10000; i++) {
                evt.setInt32("" + i, i);
            }
        }
        catch (Exception e) {
            exceptionThrown = true;
            assertEquals("Different exception",
                         "org.lwes.EventSystemException",
                         e.getClass().getName());
        }
        assertTrue("Size exception was not thrown", exceptionThrown);
    }
}
