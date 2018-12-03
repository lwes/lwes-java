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
import java.util.Arrays;

/**
 * @author fmaritato
 */
public class EventTemplateDBTest {

    private static final String TEST_EVENT = "TestEvent";

    static boolean equalsUpToPermutation(String[] a, String[] b) {
      Arrays.sort(a);
      Arrays.sort(b);
      return Arrays.equals(a,b);
    }

    @Test
    public void testTemplateFromFile() throws NoSuchAttributeTypeException {
        EventTemplateDB template = new EventTemplateDB();
        template.setESFFile(new File(getClass().getResource(getClass().getSimpleName()+".esf").getPath()));
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

        String[] htmlStringLines = template.toHtmlString().split("\n");
        assert htmlStringLines[0].equals("<table>");
        assert htmlStringLines[1].equals("<tr><th>MetaEventInfo</th><th>Type</th><th>Name</th></tr>");
        assert equalsUpToPermutation(Arrays.copyOfRange(htmlStringLines, 2, 7),
                                     new String[]{
                                           "<tr><td></td><td>ip_addr</td><td>SenderIP</td></tr>",
                                           "<tr><td></td><td>uint16</td><td>SenderPort</td></tr>",
                                           "<tr><td></td><td>int64</td><td>ReceiptTime</td></tr>",
                                           "<tr><td></td><td>int16</td><td>enc</td></tr>",
                                           "<tr><td></td><td>uint16</td><td>SiteID</td></tr>"
                                     });
        assert htmlStringLines[7].equals("<tr><th>TestEvent</th><th>Type</th><th>Name</th></tr>");
        assert equalsUpToPermutation(Arrays.copyOfRange(htmlStringLines, 8, 15),
                                     new String[]{
                                          "<tr><td></td><td>ip_addr</td><td>SenderIP</td></tr>",
                                          "<tr><td></td><td>uint16</td><td>SenderPort</td></tr>",
                                          "<tr><td></td><td>int64</td><td>ReceiptTime</td></tr>",
                                          "<tr><td></td><td>int16</td><td>enc</td></tr>",
                                          "<tr><td></td><td>uint16</td><td>SiteID</td></tr>",
                                          "<tr><td></td><td>string</td><td>field1</td></tr>",
                                          "<tr><td></td><td>int64</td><td>field2</td></tr>"
                                     });
        assert htmlStringLines[15].equals("</table>");

        String[] esfLines = template.toString().split("\n");
        assert esfLines[0].equals("");
        assert esfLines[1].equals("MetaEventInfo");
        assert esfLines[2].equals("{");
        assert equalsUpToPermutation(Arrays.copyOfRange(esfLines, 3, 8),
                                     new String[]{
                                         "\tint64 ReceiptTime;",
                                         "\tip_addr SenderIP;",
                                         "\tuint16 SenderPort;",
                                         "\tuint16 SiteID;",
                                         "\tint16 enc;"
                                     });
        assert esfLines[8].equals("}");
        assert esfLines[9].equals("TestEvent");
        assert esfLines[10].equals("{");
        assert equalsUpToPermutation(Arrays.copyOfRange(esfLines, 11, 18),
                                     new String[]{"\tint64 ReceiptTime;",
                                       "\tip_addr SenderIP;",
                                       "\tuint16 SenderPort;",
                                       "\tuint16 SiteID;",
                                       "\tint16 enc;",
                                       "\tstring field1;",
                                       "\tint64 field2;"
                                     });
        assert esfLines[18].equals("}");
    }

    @Test
    public void testTemplateFromStream() {
        EventTemplateDB template = new EventTemplateDB();
        try {
            template.setESFInputStream(new FileInputStream(getClass().getResource(getClass().getSimpleName()+".esf").getPath()));
        }
        catch (FileNotFoundException e) {
            fail(e.getMessage());
        }
        assertTrue(template.initialize());
    }
}
