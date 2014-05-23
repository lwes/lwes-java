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

package org.lwes;
/**
 * @author fmaritato
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EventFactoryTest {

    @Test
    public void testEventFactoryInitializeFile() throws EventSystemException {
        EventFactory fact = new EventFactory();
        fact.setESFFile(new File(getClass().getResource(getClass().getSimpleName()+".esf").getPath()));
        fact.initialize();
    }

    @Test
    public void testEventFactoryInitializePath() throws EventSystemException {
        EventFactory fact = new EventFactory();
        fact.setESFFilePath(getClass().getResource(getClass().getSimpleName()+".esf").getPath());
        fact.initialize();
    }

    @Test
    public void testEventFactoryInitializeStream()
            throws EventSystemException, IOException {
        final FileInputStream stream = new FileInputStream(getClass().getResource(getClass().getSimpleName()+".esf").getPath());
        try {
            EventFactory fact = new EventFactory();
            fact.setESFInputStream(stream);
            fact.initialize();
        } finally {
          stream.close();
        }
    }

    @Test
    public void testEventFactoryCreateEvent() throws EventSystemException {
        EventFactory fact = new EventFactory();
        fact.setESFFilePath(getClass().getResource(getClass().getSimpleName()+".esf").getPath());
        fact.initialize();

        Event evt = fact.createEvent("TestEvent");
        assertNotNull(evt);

        // Verify the default values are set.
        assertEquals("yeah", evt.get("field1"));
        assertEquals(10, evt.get("field3"));

        // Verify that setting a field that had a default value, overrides it.
        evt.setInt32("field3", 42);
        assertEquals(42, (long) evt.getInt32("field3"));

        evt = fact.createEvent("TestEvent", (short) 1);
        assertNotNull(evt);

        evt = fact.createEvent("TestEvent", true);
        assertNotNull(evt);

        evt = fact.createEvent("TestEvent", false, (short) 0);
        assertNotNull(evt);
    }
}
