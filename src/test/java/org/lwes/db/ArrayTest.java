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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.lwes.Event;
import org.lwes.EventAttributeSizeException;
import org.lwes.EventSystemException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
        Event evt = new Event("TestEvent", true, template);
        List<String> data = new ArrayList<String>(Arrays.asList("1", "2", "3"));
        evt.setStringArray("field1", data);
        List<String> ar = evt.getStringArray("field1");
        assertNotNull("string array was null", ar);
        assertEquals("length was incorrect", 3, ar.size());

        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new Event(serializedEvent, true, template);
        assertNotNull(evt2);
        List a2 = evt2.getStringArray("field1");
        assertNotNull(a2);
        assertEquals("deserialized array length was incorrect", 3, a2.size());

        // Now try to set an invalid string array and make
        // sure an exception is thrown.
        boolean exceptionThrown = false;
        try {
            evt.setStringArray("field1",
                               new ArrayList(Arrays.asList("1", "2", "3",
                                                           "4", "5", "6",
                                                           "7", "8", "9",
                                                           "10", "11", "12")));
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
        Event evt = new Event("TestEvent", true, template);
        evt.setInt16Array("field2", new ArrayList(Arrays.asList((short) 1, (short) 2, (short) 3)));
        List<Short> ar = evt.getInt16Array("field2");
        assertNotNull("string array was null", ar);
        assertEquals("length was incorrect", 3, ar.size());

        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new Event(serializedEvent, true, template);
        assertNotNull(evt2);
        List<Short> a2 = evt2.getInt16Array("field2");
        assertNotNull(a2);
        assertEquals("deserialized array length was incorrect", 3, a2.size());

        // Now try to set an invalid string array and make
        // sure an exception is thrown.
        boolean exceptionThrown = false;
        try {
            evt.setInt16Array("field2", new ArrayList(Arrays.asList(1, 2, 3,
                                                                    4, 5, 6,
                                                                    7, 8, 9,
                                                                    10, 11, 12)));
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
        Event evt = new Event("TestEvent", true, template);
        evt.setInt32Array("field3", new ArrayList(Arrays.asList(1234567890, 234567890, 345678901)));
        List<Integer> ar = evt.getInt32Array("field3");
        assertNotNull("string array was null", ar);
        assertEquals("length was incorrect", 3, ar.size());

        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new Event(serializedEvent, true, template);
        assertNotNull(evt2);
        List<Integer> a2 = evt2.getInt32Array("field3");
        assertNotNull(a2);
        assertEquals("deserialized array length was incorrect", 3, a2.size());
        assertEquals("a2[0]", (int) a2.get(0), 1234567890);
        assertEquals("a2[1]", (int) a2.get(1), 234567890);
        assertEquals("a2[2]", (int) a2.get(2), 345678901);

        // Now try to set an invalid string array and make
        // sure an exception is thrown.
        boolean exceptionThrown = false;
        try {
            evt.setInt32Array("field3", new ArrayList(Arrays.asList(1, 2, 3,
                                                                    4, 5, 6,
                                                                    7, 8, 9,
                                                                    10, 11, 12)));
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
        Event evt = new Event("TestEvent", true, template);
        evt.setInt64Array("field4", new ArrayList(Arrays.asList(123456789012l, 234567890123l)));
        List<Long> ar = evt.getInt64Array("field4");
        assertNotNull("string array was null", ar);
        assertEquals("length was incorrect", 2, ar.size());

        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new Event(serializedEvent, true, template);
        assertNotNull(evt2);
        List<Long> a2 = evt2.getInt64Array("field4");
        assertNotNull(a2);
        assertEquals("deserialized array length was incorrect", 2, a2.size());
        assertEquals("a2[0]", (long) a2.get(0), 123456789012l);
        assertEquals("a2[1]", (long) a2.get(1), 234567890123l);

        // Now try to set an invalid string array and make
        // sure an exception is thrown.
        boolean exceptionThrown = false;
        try {
            evt.setInt64Array("field4", new ArrayList(Arrays.asList(1, 2, 3,
                                                                    4, 5, 6,
                                                                    7, 8, 9,
                                                                    10, 11, 12)));
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
        Event evt = new Event("TestEvent", true, template);
        evt.setUInt16Array("field5", new ArrayList(Arrays.asList(1, 2, 3)));
        List<Integer> ar = evt.getUInt16Array("field5");
        assertNotNull("string array was null", ar);
        assertEquals("length was incorrect", 3, ar.size());

        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new Event(serializedEvent, true, template);
        assertNotNull(evt2);
        List<Integer> a2 = evt2.getUInt16Array("field5");
        assertNotNull(a2);
        assertEquals("deserialized array length was incorrect", 3, a2.size());

        // Now try to set an invalid string array and make
        // sure an exception is thrown.
        boolean exceptionThrown = false;
        try {
            evt.setUInt16Array("field5", new ArrayList(Arrays.asList(1, 2, 3,
                                                                     4, 5, 6,
                                                                     7, 8, 9,
                                                                     10, 11, 12)));
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
        Event evt = new Event("TestEvent", true, template);
        evt.setUInt32Array("field6", new ArrayList(Arrays.asList(1234567890l, 234567890l, 345678901l)));
        List<Long> ar = evt.getUInt32Array("field6");
        assertNotNull("string array was null", ar);
        assertEquals("length was incorrect", 3, ar.size());

        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new Event(serializedEvent, true, template);
        assertNotNull(evt2);
        List<Long> a2 = evt2.getUInt32Array("field6");
        assertNotNull(a2);
        assertEquals("deserialized array length was incorrect", 3, a2.size());
        assertEquals("a2[0]", (long) a2.get(0), 1234567890);
        assertEquals("a2[1]", (long) a2.get(1), 234567890);
        assertEquals("a2[2]", (long) a2.get(2), 345678901);

        // Now try to set an invalid string array and make
        // sure an exception is thrown.
        boolean exceptionThrown = false;
        try {
            evt.setUInt32Array("field6", new ArrayList(Arrays.asList(1, 2, 3,
                                                                     4, 5, 6,
                                                                     7, 8, 9,
                                                                     10, 11, 12)));
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
        Event evt = new Event("TestEvent", true, template);
        evt.setUInt64Array("field7", new ArrayList(Arrays.asList(123456789012l, 234567890123l)));
        List ar = evt.getUInt64Array("field7");
        assertNotNull("string array was null", ar);
        assertEquals("length was incorrect", 2, ar.size());

        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new Event(serializedEvent, true, template);
        assertNotNull(evt2);
        List a2 = evt2.getUInt64Array("field7");
        assertNotNull(a2);
        assertEquals("deserialized array length was incorrect", 2, a2.size());
        assertEquals("a2.get(0)", a2.get(0), 123456789012l);
        assertEquals("a2.get(1)", a2.get(1), 234567890123l);

        // Now try to set an invalid string array and make
        // sure an exception is thrown.
        boolean exceptionThrown = false;
        try {
            evt.setUInt64Array("field7", new ArrayList(Arrays.asList(1, 2, 3,
                                                                     4, 5, 6,
                                                                     7, 8, 9,
                                                                     10, 11, 12)));
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
        Event evt = new Event("TestEvent", true, template);
        evt.setBooleanArray("field8", new ArrayList(Arrays.asList(true, false)));
        List ar = evt.getBooleanArray("field8");
        assertNotNull("boolean array was null", ar);
        assertEquals("length was incorrect", 2, ar.size());

        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new Event(serializedEvent, true, template);
        assertNotNull(evt2);
        List a2 = evt2.getBooleanArray("field8");
        assertNotNull(a2);
        assertEquals("deserialized array length was incorrect", 2, a2.size());
        assertEquals("a2.get(0)", a2.get(0), true);
        assertEquals("a2.get(1)", a2.get(1), false);
    }

    @Test
    public void testByteArray() throws EventSystemException {
        Event evt = new Event("TestEvent", true, template);
        evt.setByteArray("field9", new ArrayList(Arrays.asList((byte) 0x1, (byte) 0x2)));
        List ar = evt.getByteArray("field9");
        assertNotNull("boolean array was null", ar);
        assertEquals("length was incorrect", 2, ar.size());

        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new Event(serializedEvent, true, template);
        assertNotNull(evt2);
        List a2 = evt2.getByteArray("field9");
        assertNotNull(a2);
        assertEquals("deserialized array length was incorrect", 2, a2.size());
        assertEquals("a2.get(0)", a2.get(0), (byte) 0x1);
        assertEquals("a2.get(1)", a2.get(1), (byte) 0x2);
    }

    @Test
    public void testDoubleArray() throws EventSystemException {

        Event evt = new Event("TestEvent", true, template);
        evt.setDoubleArray("field10", new ArrayList(Arrays.asList(3.14159, 5.99999)));
        List ar = evt.getDoubleArray("field10");
        assertNotNull("double array was null", ar);
        assertEquals("number of fields is wrong", 2, ar.size());
        log.debug("EVENT: " + evt.toOneLineString());
        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new Event(serializedEvent, true, template);
        assertNotNull(evt2);
        List<Double> a2 = evt2.getDoubleArray("field10");
        assertNotNull(a2);
        assertEquals("deserialized array length was incorrect", 2, a2.size());
        log.debug(a2);
        assertEquals("a2.get(0)", 3.14159, a2.get(0), .00001);
        assertEquals("a2.get(1)", 5.99999, a2.get(1), .00001);
    }

    @Test
    public void testFloatArray() throws EventSystemException {
        Event evt = new Event("TestEvent", true, template);
        evt.setFloatArray("field11", new ArrayList(Arrays.asList(new Float(1.11), new Float(2.22))));
        List ar = evt.getFloatArray("field11");
        assertNotNull("Float array was null", ar);
        assertEquals("number of fields is wrong ", 2, ar.size());

        byte[] serializedEvent = evt.serialize();
        Event evt2 = new Event(serializedEvent, true, template);
        assertNotNull(evt2);
        List<Float> a2 = evt2.getFloatArray("field11");
        assertNotNull(a2);
        assertEquals("deserialized array length was incorrect", 2, a2.size());
        assertEquals("a2.get(0)", (float) 1.11, a2.get(0), .01);
        assertEquals("a2.get(1)", (float) 2.22, a2.get(1), .01);
    }

}
