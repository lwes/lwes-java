package org.lwes.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.lwes.Event;
import org.lwes.EventAttributeSizeException;
import org.lwes.EventSystemException;
import org.lwes.util.Log;

import java.io.File;
import java.util.Enumeration;


/**
 * @author fmaritato
 */

public class ArrayTest {

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
        evt.setStringArray("field1", new String[]{"1", "2", "3"});
        String[] ar = evt.getStringArray("field1");
        assertNotNull("string array was null", ar);
        assertEquals("length was incorrect", 3, ar.length);

        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new Event(serializedEvent, true, template);
        assertNotNull(evt2);
        String[] a2 = evt2.getStringArray("field1");
        assertNotNull(a2);
        assertEquals("deserialized array length was incorrect", 3, a2.length);

        // Now try to set an invalid string array and make
        // sure an exception is thrown.
        boolean exceptionThrown = false;
        try {
            evt.setStringArray("field1", new String[]{"1", "2", "3",
                                                      "4", "5", "6",
                                                      "7", "8", "9",
                                                      "10", "11", "12"});
        }
        catch (EventAttributeSizeException e) {
            if (Log.isLogDebug()) {
                Log.debug(e.getMessage());
            }
            exceptionThrown = true;
        }
        assertTrue("No exception was thrown for array length", exceptionThrown);
    }

    @Test
    public void testInt16() throws EventSystemException {
        // First try to set a valid string array
        Event evt = new Event("TestEvent", true, template);
        evt.setInt16Array("field2", new short[]{1, 2, 3});
        short[] ar = evt.getInt16Array("field2");
        assertNotNull("string array was null", ar);
        assertEquals("length was incorrect", 3, ar.length);

        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new Event(serializedEvent, true, template);
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
            if (Log.isLogDebug()) {
                Log.debug(e.getMessage());
            }
            exceptionThrown = true;
        }
        assertTrue("No exception was thrown for array length", exceptionThrown);
    }

    @Test
    public void testInt32() throws EventSystemException {
        // First try to set a valid string array
        Event evt = new Event("TestEvent", true, template);
        evt.setInt32Array("field3", new int[]{1234567890, 234567890, 345678901});
        int[] ar = evt.getInt32Array("field3");
        assertNotNull("string array was null", ar);
        assertEquals("length was incorrect", 3, ar.length);

        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new Event(serializedEvent, true, template);
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
            if (Log.isLogDebug()) {
                Log.debug(e.getMessage());
            }
            exceptionThrown = true;
        }
        assertTrue("No exception was thrown for array length", exceptionThrown);
    }

    @Test
    public void testInt64() throws EventSystemException {
        // First try to set a valid string array
        Event evt = new Event("TestEvent", true, template);
        evt.setInt64Array("field4", new long[]{123456789012l, 234567890123l});
        long[] ar = evt.getInt64Array("field4");
        assertNotNull("string array was null", ar);
        assertEquals("length was incorrect", 2, ar.length);

        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new Event(serializedEvent, true, template);
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
            if (Log.isLogDebug()) {
                Log.debug(e.getMessage());
            }
            exceptionThrown = true;
        }
        assertTrue("No exception was thrown for array length", exceptionThrown);
    }

    @Test
    public void testUInt16() throws EventSystemException {
        // First try to set a valid string array
        Event evt = new Event("TestEvent", true, template);
        evt.setUInt16Array("field5", new int[]{1, 2, 3});
        int[] ar = evt.getUInt16Array("field5");
        assertNotNull("string array was null", ar);
        assertEquals("length was incorrect", 3, ar.length);

        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new Event(serializedEvent, true, template);
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
            if (Log.isLogDebug()) {
                Log.debug(e.getMessage());
            }
            exceptionThrown = true;
        }
        assertTrue("No exception was thrown for array length", exceptionThrown);
    }

    @Test
    public void testUInt32() throws EventSystemException {
        // First try to set a valid string array
        Event evt = new Event("TestEvent", true, template);
        evt.setUInt32Array("field6", new long[]{1234567890, 234567890, 345678901});
        long[] ar = evt.getUInt32Array("field6");
        assertNotNull("string array was null", ar);
        assertEquals("length was incorrect", 3, ar.length);

        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new Event(serializedEvent, true, template);
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
            if (Log.isLogDebug()) {
                Log.debug(e.getMessage());
            }
            exceptionThrown = true;
        }
        assertTrue("No exception was thrown for array length", exceptionThrown);
    }

    @Test
    public void testUInt64() throws EventSystemException {
        // First try to set a valid string array
        Event evt = new Event("TestEvent", true, template);
        evt.setUInt64Array("field7", new long[]{123456789012l, 234567890123l});
        long[] ar = evt.getUInt64Array("field7");
        assertNotNull("string array was null", ar);
        assertEquals("length was incorrect", 2, ar.length);

        // Make sure we can serialize/deserialize it
        byte[] serializedEvent = evt.serialize();
        Event evt2 = new Event(serializedEvent, true, template);
        assertNotNull(evt2);
        long[] a2 = evt2.getUInt64Array("field7");
        assertNotNull(a2);
        assertEquals("deserialized array length was incorrect", 2, a2.length);
        assertEquals("a2[0]", a2[0], 123456789012l);
        assertEquals("a2[1]", a2[1], 234567890123l);

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
            if (Log.isLogDebug()) {
                Log.debug(e.getMessage());
            }
            exceptionThrown = true;
        }
        assertTrue("No exception was thrown for array length", exceptionThrown);
    }
}
