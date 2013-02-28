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

package org.lwes.db;

import java.io.File;
import java.math.BigInteger;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.lwes.Event;
import org.lwes.EventAttributeSizeException;
import org.lwes.EventSystemException;
import org.lwes.MapEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


/**
 * @author fmaritato
 */

public class ArrayTest {

    private static transient Log log = LogFactory.getLog(ArrayTest.class);

    private static final String ESF = "src/test/java/org/lwes/db/ArrayTest.esf";
    private static final String TEST_EVENT = "TestEvent";

    private EventTemplateDB template = null;

    @Before
    public void setUp() {
        template = new EventTemplateDB();
        template.setESFFile(new File(ESF));
        template.initialize();
    }

    @Test
    public void testArrayWithNulls() {

        EventTemplateDB template = new EventTemplateDB();
        template.setESFFile(new File(ESF));
        assertTrue("Template did not initialize", template.initialize());

        Double[] doubleArray = new Double[] {
            2.1, null, 5.5, 1.1, 3.2
        };

        Event evt = new MapEvent("TestEvent", true, template);
        evt.setDoubleArray("doubleObjArr", doubleArray);

        byte[] serializedEvent = evt.serialize();
        Event evt2 = new MapEvent(serializedEvent, true, template);
        assertNotNull(evt2);
        Double[] rtnArray = evt2.getDoubleObjArray("doubleObjArr");
        assertNotNull(rtnArray);
        assertEquals(2.1, rtnArray[0], 1e-15);
        assertNull(rtnArray[1]);
        assertEquals(5.5, rtnArray[2], 1e-15);
        assertEquals(1.1, rtnArray[3], 1e-15);

    }

    @Test
    public void testArrayParse() {

        EventTemplateDB template = new EventTemplateDB();
        template.setESFFile(new File(ESF));
        assertTrue("Template did not initialize", template.initialize());

        Enumeration<String> eventNames = template.getEventNames();
        assertNotNull("Event names enum was null", eventNames);

        assertTrue("TestEvent was not known to the template",
                   template.checkForEvent(TEST_EVENT));

        assertTrue("field1 attribute not known to the template",
                   template.checkForAttribute(TEST_EVENT, "field1"));

    }

    @Test
    public void testStringArray() throws EventSystemException {

        // First try to set a valid string array
        Event evt = new MapEvent("TestEvent", true, template);
        String[] data = new String[]{"1", "2", "3"};
        evt.setStringArray("field1", data);
        String[] ar = evt.getStringArray("field1");
        assertNotNull("string array was null", ar);
        assertEquals("length was incorrect", 3, ar.length);

        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new MapEvent(serializedEvent, true, template);
        assertNotNull(evt2);
        String[] a2 = evt2.getStringArray("field1");
        assertNotNull(a2);
        assertEquals("deserialized array length was incorrect", 3, a2.length);

        // Now try to set an invalid string array and make
        // sure an exception is thrown.
        boolean exceptionThrown = false;
        try {
            evt.setStringArray("field1",
                               new String[]{"1", "2", "3",
                                            "4", "5", "6",
                                            "7", "8", "9",
                                            "10", "11", "12"});
        }
        catch (EventAttributeSizeException e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage());
            }
            exceptionThrown = true;
        }
        assertTrue("No exception was thrown for array length", exceptionThrown);
    }

    @Test
    public void testInt16() throws EventSystemException {
        // First try to set a valid string array
        Event evt = new MapEvent("TestEvent", true, template);
        evt.setInt16Array("field2", new short[]{(short) 1, (short) 2, (short) 3});
        short[] ar = evt.getInt16Array("field2");
        assertNotNull("string array was null", ar);
        assertEquals("length was incorrect", 3, ar.length);

        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new MapEvent(serializedEvent, true, template);
        assertNotNull(evt2);
        short[] a2 = evt2.getInt16Array("field2");
        assertNotNull(a2);
        assertEquals("deserialized array length was incorrect", 3, a2.length);

        // Now try to set an invalid string array and make
        // sure an exception is thrown.
        boolean exceptionThrown = false;
        try {
            evt.setInt16Array("field2", new short[]{1, 2, 3,
                                                    4, 5, 6,
                                                    7, 8, 9,
                                                    10, 11, 12});
        }
        catch (EventAttributeSizeException e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage());
            }
            exceptionThrown = true;
        }
        assertTrue("No exception was thrown for array length", exceptionThrown);
    }

    @Test
    public void testInt32() throws EventSystemException {
        // First try to set a valid string array
        Event evt = new MapEvent("TestEvent", true, template);
        evt.setInt32Array("field3", new int[]{1234567890, 234567890, 345678901});
        int[] ar = evt.getInt32Array("field3");
        assertNotNull("string array was null", ar);
        assertEquals("length was incorrect", 3, ar.length);

        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new MapEvent(serializedEvent, true, template);
        assertNotNull(evt2);
        int[] a2 = evt2.getInt32Array("field3");
        assertNotNull(a2);
        assertEquals("deserialized array length was incorrect", 3, a2.length);
        assertEquals("a2[0]", a2[0], 1234567890);
        assertEquals("a2[1]", a2[1], 234567890);
        assertEquals("a2[2]", a2[2], 345678901);

        // Now try to set an invalid string array and make
        // sure an exception is thrown.
        boolean exceptionThrown = false;
        try {
            evt.setInt32Array("field3", new int[]{1, 2, 3,
                                                  4, 5, 6,
                                                  7, 8, 9,
                                                  10, 11, 12});
        }
        catch (EventAttributeSizeException e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage());
            }
            exceptionThrown = true;
        }
        assertTrue("No exception was thrown for array length", exceptionThrown);
    }

    @Test
    public void testInt64() throws EventSystemException {
        // First try to set a valid string array
        Event evt = new MapEvent("TestEvent", true, template);
        evt.setInt64Array("field4", new long[]{123456789012l, 234567890123l});
        long[] ar = evt.getInt64Array("field4");
        assertNotNull("string array was null", ar);
        assertEquals("length was incorrect", 2, ar.length);

        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new MapEvent(serializedEvent, true, template);
        assertNotNull(evt2);
        long[] a2 = evt2.getInt64Array("field4");
        assertNotNull(a2);
        assertEquals("deserialized array length was incorrect", 2, a2.length);
        assertEquals("a2[0]", a2[0], 123456789012l);
        assertEquals("a2[1]", a2[1], 234567890123l);

        // Now try to set an invalid string array and make
        // sure an exception is thrown.
        boolean exceptionThrown = false;
        try {
            evt.setInt64Array("field4", new long[]{1, 2, 3,
                                                   4, 5, 6,
                                                   7, 8, 9,
                                                   10, 11, 12});
        }
        catch (EventAttributeSizeException e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage());
            }
            exceptionThrown = true;
        }
        assertTrue("No exception was thrown for array length", exceptionThrown);
    }

    @Test
    public void testUInt16() throws EventSystemException {
        // First try to set a valid string array
        Event evt = new MapEvent("TestEvent", true, template);
        evt.setUInt16Array("field5", new int[]{1, 2, 3});
        int[] ar = evt.getUInt16Array("field5");
        assertNotNull("string array was null", ar);
        assertEquals("length was incorrect", 3, ar.length);

        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new MapEvent(serializedEvent, true, template);
        assertNotNull(evt2);
        int[] a2 = evt2.getUInt16Array("field5");
        assertNotNull(a2);
        assertEquals("deserialized array length was incorrect", 3, a2.length);

        // Now try to set an invalid string array and make
        // sure an exception is thrown.
        boolean exceptionThrown = false;
        try {
            evt.setUInt16Array("field5", new int[]{1, 2, 3,
                                                   4, 5, 6,
                                                   7, 8, 9,
                                                   10, 11, 12});
        }
        catch (EventAttributeSizeException e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage());
            }
            exceptionThrown = true;
        }
        assertTrue("No exception was thrown for array length", exceptionThrown);
    }

    @Test
    public void testUInt32() throws EventSystemException {
        // First try to set a valid string array
        Event evt = new MapEvent("TestEvent", true, template);
        evt.setUInt32Array("field6", new long[]{1234567890l, 234567890l, 345678901l});
        long[] ar = evt.getUInt32Array("field6");
        assertNotNull("string array was null", ar);
        assertEquals("length was incorrect", 3, ar.length);

        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new MapEvent(serializedEvent, true, template);
        assertNotNull(evt2);
        long[] a2 = evt2.getUInt32Array("field6");
        assertNotNull(a2);
        assertEquals("deserialized array length was incorrect", 3, a2.length);
        assertEquals("a2[0]", a2[0], 1234567890);
        assertEquals("a2[1]", a2[1], 234567890);
        assertEquals("a2[2]", a2[2], 345678901);

        // Now try to set an invalid string array and make
        // sure an exception is thrown.
        boolean exceptionThrown = false;
        try {
            evt.setUInt32Array("field6", new long[]{1, 2, 3,
                                                    4, 5, 6,
                                                    7, 8, 9,
                                                    10, 11, 12});
        }
        catch (EventAttributeSizeException e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage());
            }
            exceptionThrown = true;
        }
        assertTrue("No exception was thrown for array length", exceptionThrown);
    }

    @Test
    public void testUInt64() throws EventSystemException {
        // First try to set a valid string array
        Event evt = new MapEvent("TestEvent", true, template);
        evt.setUInt64Array("field7", new BigInteger[]{new BigInteger("123456789012"),
                                                      new BigInteger("234567890123")});
        BigInteger[] ar = evt.getUInt64Array("field7");
        assertNotNull("string array was null", ar);
        assertEquals("length was incorrect", 2, ar.length);

        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new MapEvent(serializedEvent, true, template);
        assertNotNull(evt2);
        BigInteger[] a2 = evt2.getUInt64Array("field7");
        assertNotNull(a2);
        assertEquals("deserialized array length was incorrect", 2, a2.length);
        assertEquals("a2[0]", a2[0].longValue(), 123456789012l);
        assertEquals("a2[1]", a2[1].longValue(), 234567890123l);

        // Now try to set an invalid string array and make
        // sure an exception is thrown.
        boolean exceptionThrown = false;
        try {
            evt.setUInt64Array("field7", new long[]{1, 2, 3,
                                                    4, 5, 6,
                                                    7, 8, 9,
                                                    10, 11, 12});
        }
        catch (EventAttributeSizeException e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage());
            }
            exceptionThrown = true;
        }
        assertTrue("No exception was thrown for array length", exceptionThrown);
    }

    @Test
    public void testBooleanArray() throws EventSystemException {
        Event evt = new MapEvent("TestEvent", true, template);
        evt.setBooleanArray("field8", new boolean[] {true, false});
        boolean[] ar = evt.getBooleanArray("field8");
        assertNotNull("boolean array was null", ar);
        assertEquals("length was incorrect", 2, ar.length);

        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new MapEvent(serializedEvent, true, template);
        assertNotNull(evt2);
        boolean[] a2 = evt2.getBooleanArray("field8");
        assertNotNull(a2);
        assertEquals("deserialized array length was incorrect", 2, a2.length);
        assertEquals("a2[0]", a2[0], true);
        assertEquals("a2[1]", a2[1], false);
    }

    @Test
    public void testByteArray() throws EventSystemException {
        Event evt = new MapEvent("TestEvent", true, template);
        evt.setByteArray("field9", new byte[] {(byte) 0x1, (byte) 0x2});
        byte[] ar = evt.getByteArray("field9");
        assertNotNull("boolean array was null", ar);
        assertEquals("length was incorrect", 2, ar.length);

        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new MapEvent(serializedEvent, true, template);
        assertNotNull(evt2);
        byte[] a2 = evt2.getByteArray("field9");
        assertNotNull(a2);
        assertEquals("deserialized array length was incorrect", 2, a2.length);
        assertEquals("a2[0]", a2[0], (byte) 0x1);
        assertEquals("a2[1]", a2[1], (byte) 0x2);
    }

    @Test
    public void testDoubleArray() throws EventSystemException {

        Event evt = new MapEvent("TestEvent", true, template);
        evt.setDoubleArray("field10", new double[] {3.14159, 5.99999});
        double[] ar = evt.getDoubleArray("field10");
        assertNotNull("double array was null", ar);
        assertEquals("number of fields is wrong", 2, ar.length);
        log.debug("EVENT: " + evt.toOneLineString());
        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new MapEvent(serializedEvent, true, template);
        assertNotNull(evt2);
        double[] a2 = evt2.getDoubleArray("field10");
        assertNotNull(a2);
        assertEquals("deserialized array length was incorrect", 2, a2.length);
        log.debug(a2);
        assertEquals("a2[0]", 3.14159, a2[0], .00001);
        assertEquals("a2[1]", 5.99999, a2[1], .00001);
    }

    @Test
    public void testFloatArray() throws EventSystemException {
        Event evt = new MapEvent("TestEvent", true, template);
        evt.setFloatArray("field11", new float[] {new Float(1.11), new Float(2.22)});
        float[] ar = evt.getFloatArray("field11");
        assertNotNull("Float array was null", ar);
        assertEquals("number of fields is wrong ", 2, ar.length);

        byte[] serializedEvent = evt.serialize();
        Event evt2 = new MapEvent(serializedEvent, true, template);
        assertNotNull(evt2);
        float[] a2 = evt2.getFloatArray("field11");
        assertNotNull(a2);
        assertEquals("deserialized array length was incorrect", 2, a2.length);
        assertEquals("a2[0]", (float) 1.11, a2[0], .01);
        assertEquals("a2[1]", (float) 2.22, a2[1], .01);
    }

}
