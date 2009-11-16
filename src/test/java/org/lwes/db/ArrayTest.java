package org.lwes.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
    public void testSetArray() throws EventSystemException {
        EventTemplateDB template = new EventTemplateDB();
        template.setESFFile(new File(ESF));
        template.initialize();

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
}
