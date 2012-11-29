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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.lwes.BaseType;
import org.lwes.FieldType;
import org.lwes.NoSuchAttributeTypeException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author fmaritato
 */
public class EventTemplateDBTest {

    private static final String ESF = "src/test/java/org/lwes/db/EventTemplateDBTest.esf";
    private static final String TEST_EVENT = "TestEvent";

    @Test
    public void testTemplateFromFile() throws NoSuchAttributeTypeException {
        EventTemplateDB template = new EventTemplateDB();
        template.setESFFile(new File(ESF));
        assertTrue("Template did not initialize", template.initialize());

        Enumeration<String> eventNames = template.getEventNames();
        assertNotNull("Event names enum was null", eventNames);

        assertTrue("TestEvent was not known to the template",
                   template.checkForEvent(TEST_EVENT));

        assertEquals("# Event comment\n# spans 2 lines\n", template.getEventComment(TEST_EVENT));

        assertEquals("# metaevent comment\n", template.getMetaComment());

        assertTrue("field1 attribute not known to the template",
                   template.checkForAttribute(TEST_EVENT, "field1"));

        // Check a metadata attribute
        assertTrue("SenderIP attribute not known to the template",
                   template.checkForAttribute(TEST_EVENT, "SenderIP"));

        BaseType bt = template.getBaseTypeForObjectAttribute(TEST_EVENT, "field2", 100l);
        assertNotNull(bt);
        assertEquals("Wrong BaseType returned", FieldType.INT64, bt.getType());
        assertEquals("# this is a comment\n", bt.getComment());

        assertTrue("Wrong type for attribute field2",
                   template.checkTypeForAttribute(TEST_EVENT, "field2", bt));

        assertFalse("template allowed string for a numeric type",
                    template.checkTypeForAttribute(TEST_EVENT, "field2", FieldType.STRING));

        Map<String, BaseType> bts = template.getBaseTypesForEvent(TEST_EVENT);
        assertNotNull(bts);
        assertEquals("Number of types in the map is wrong", 7, bts.size());
        BaseType field2BT = bts.get("field2");
        assertNotNull(field2BT);
        assertEquals(FieldType.INT64, field2BT.getType());

        Object obj = template.parseAttribute(TEST_EVENT, "field2", "100");
        assertNotNull(obj);

        String testHtmlString =
                new StringBuilder().append("<table>\n")
                                   .append("<tr><th>MetaEventInfo</th><th>Type</th><th>Name</th></tr>\n")
                                   .append("<tr><td></td><td>uint16</td><td>SiteID</td></tr>\n")
                                   .append("<tr><td></td><td>int16</td><td>enc</td></tr>\n")
                                   .append("<tr><td></td><td>ip_addr</td><td>SenderIP</td></tr>\n")
                                   .append("<tr><td></td><td>int64</td><td>ReceiptTime</td></tr>\n")
                                   .append("<tr><td></td><td>uint16</td><td>SenderPort</td></tr>\n")
                                   .append("<tr><th>TestEvent</th><th>Type</th><th>Name</th></tr>\n")
                                   .append("<tr><td></td><td>uint16</td><td>SiteID</td></tr>\n")
                                   .append("<tr><td></td><td>int16</td><td>enc</td></tr>\n")
                                   .append("<tr><td></td><td>string</td><td>field1</td></tr>\n")
                                   .append("<tr><td></td><td>int64</td><td>field2</td></tr>\n")
                                   .append("<tr><td></td><td>ip_addr</td><td>SenderIP</td></tr>\n")
                                   .append("<tr><td></td><td>int64</td><td>ReceiptTime</td></tr>\n")
                                   .append("<tr><td></td><td>uint16</td><td>SenderPort</td></tr>\n")
                                   .append("</table>\n")
                                   .toString();
        String htmlString = template.toHtmlString();
        assertNotNull("html string was null", htmlString);
        assertEquals("html string did not match", testHtmlString, htmlString);

        String testString =
                new StringBuilder().append("\nMetaEventInfo\n")
                                   .append("{\n")
                                   .append("\tint64 ReceiptTime;\n")
                                   .append("\tip_addr SenderIP;\n")
                                   .append("\tuint16 SenderPort;\n")
                                   .append("\tuint16 SiteID;\n")
                                   .append("\tint16 enc;\n")
                                   .append("}\n")
                                   .append("TestEvent\n")
                                   .append("{\n")
                                   .append("\tint64 ReceiptTime;\n")
                                   .append("\tip_addr SenderIP;\n")
                                   .append("\tuint16 SenderPort;\n")
                                   .append("\tuint16 SiteID;\n")
                                   .append("\tint16 enc;\n")
                                   .append("\tstring field1;\n")
                                   .append("\tint64 field2;\n")
                                   .append("}\n")
                                   .toString();
        String toString = template.toString();
        assertNotNull("toString was null", toString);
        assertEquals("test string did not match", testString, toString);
    }

    @Test
    public void testTemplateFromStream() {
        EventTemplateDB template = new EventTemplateDB();
        try {
            template.setESFInputStream(new FileInputStream(ESF));
        }
        catch (FileNotFoundException e) {
            fail(e.getMessage());
        }
        assertTrue(template.initialize());
    }
}
