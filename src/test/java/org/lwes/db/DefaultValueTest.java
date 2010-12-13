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
/**
 * @author fmaritato
 */

import org.junit.Test;
import org.lwes.Event;
import org.lwes.EventSystemException;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DefaultValueTest {

    private static final String ESF = "src/test/java/org/lwes/db/DefaultValueTest.esf";

    @Test
    public void testDefaultValue() throws EventSystemException {

        EventTemplateDB template = new EventTemplateDB();
        template.setESFFile(new File(ESF));
        template.initialize();

        assertTrue(template.checkForEvent("DefaultValueEvent"));

        Event evt = new Event("DefaultValueEvent", true, template);


        // numeric values
        assertNotNull("anInt not set", evt.get("anInt"));
        assertEquals("anInt value wrong", 5, evt.get("anInt"));

        assertNotNull("pi not set", evt.get("pi"));
        assertEquals("pi value wrong", 3.141594, evt.get("pi"));

        // booleans
        assertNotNull("aBool not set", evt.get("aBool"));
        assertEquals("aBool value wrong", true, evt.get("aBool"));

        // Strings
        assertNotNull("version not set", evt.get("version"));
        assertEquals("version value wrong", "1.0.0", evt.get("version"));
    }
}
