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
import org.lwes.MapEvent;
import org.lwes.util.IPAddress;

import java.io.File;
import java.math.BigInteger;

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

        Event evt = new MapEvent("DefaultValueEvent", true, template);

        assertEquals("aBool value wrong", true, evt.get("aBool"));
        assertEquals("aBoolOpt value wrong", false, evt.get("aBoolOpt"));
        assertEquals("anInt value wrong", 5, evt.get("anInt"));
        assertEquals("anIntOpt value wrong", 6, evt.get("anIntOpt"));
        assertEquals("aLong value wrong", 5000000000L, evt.get("aLong"));
        assertEquals("pi value wrong", 3.141594, evt.get("pi"));
        assertEquals("piOpt value wrong", -3.141594, evt.get("piOpt"));
        assertEquals("ip value wrong", new IPAddress("255.255.255.255"), evt.get("ip"));
        assertEquals("aByte value wrong", (byte) -128, evt.get("aByte"));
        assertEquals("aDouble value wrong", 6.8, evt.get("aDouble"));
        assertEquals("anInt16 value wrong", (short)32767, evt.get("anInt16"));
        assertEquals("anInt32 value wrong", -2147483648, evt.get("anInt32"));
        assertEquals("aUInt16 value wrong", 65535, evt.get("aUInt16"));
        assertEquals("aUInt32 value wrong", 4294967295L, evt.get("aUInt32"));
        assertEquals("aUInt64 value wrong", new BigInteger("18446744073709551615"), evt.get("aUInt64"));
        assertEquals("aSmallUInt64 value wrong", BigInteger.valueOf(1), evt.get("aSmallUInt64"));

        // Strings
        assertNotNull("version not set", evt.get("version"));
        assertEquals("version value wrong", "1.0.0", evt.get("version"));
    }
}
