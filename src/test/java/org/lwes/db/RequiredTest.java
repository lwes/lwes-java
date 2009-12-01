package org.lwes.db;
/**
 * @author fmaritato
 */

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.lwes.AttributeRequiredException;
import org.lwes.Event;
import org.lwes.EventSystemException;
import org.lwes.util.Log;

import java.io.File;
import java.util.Enumeration;

public class RequiredTest {

    private static final String ESF = "src/test/java/org/lwes/db/RequiredTest.esf";
    private static final String TEST_EVENT = "TestEvent";

    @Test
    public void testRequired() throws EventSystemException {
        EventTemplateDB template = new EventTemplateDB();
        template.setESFFile(new File(ESF));
        assertTrue("Template did not initialize", template.initialize());
        Enumeration<String> eventNames = template.getEventNames();
        assertNotNull("Event names enum was null", eventNames);

        assertTrue("TestEvent was not known to the template",
                   template.checkForEvent(TEST_EVENT));

        assertTrue("field1 attribute not known to the template",
                   template.checkForAttribute(TEST_EVENT, "field1"));

        boolean exceptionThrown = false;

        // Verify that an exception is thrown when a required field is not present.
        Event evt = new Event("TestEvent", true, template);
        try {
            evt.validate();
        }
        catch (AttributeRequiredException e) {
            if (Log.isLogDebug()) {
                Log.debug(e.getMessage());
            }
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        exceptionThrown = false;
        // Verify no exception when all required fields are set.
        evt.setString("field1", "value");
        if (Log.isLogDebug()) {
            Log.debug(evt.toString());
        }
        try {
            evt.validate();
        }
        catch (AttributeRequiredException e) {
            if (Log.isLogDebug()) {
                Log.debug(e.getMessage());
            }
            exceptionThrown = true;
        }
        assertFalse(exceptionThrown);

    }
}
