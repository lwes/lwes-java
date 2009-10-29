package org.lwes.db;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;

import java.io.File;
import java.util.Enumeration;

/**
 * @author fmaritato
 */

@Ignore
public class ArrayTest {

    private static final String ESF = "tests/org/lwes/db/ArrayTest.esf";
    private static final String TEST_EVENT = "TestEvent";

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
}
