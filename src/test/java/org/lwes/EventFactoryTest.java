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

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Test;

public class EventFactoryTest {

    @Test
    public void testEventFactoryInitializeFile() throws EventSystemException {
        EventFactory fact = new EventFactory();
        fact.setESFFile(new File("src/test/java/org/lwes/EventFactoryTest.esf"));
        fact.initialize();
    }

    @Test
    public void testEventFactoryInitializePath() throws EventSystemException {
        EventFactory fact = new EventFactory();
        fact.setESFFilePath("src/test/java/org/lwes/EventFactoryTest.esf");
        fact.initialize();
    }

    @Test
    public void testEventFactoryInitializeStream()
            throws EventSystemException, FileNotFoundException {
        EventFactory fact = new EventFactory();
        fact.setESFInputStream(new FileInputStream("src/test/java/org/lwes/EventFactoryTest.esf"));
        fact.initialize();
    }

    @Test
    public void testEventFactoryCreateEvent() throws EventSystemException {
        EventFactory fact = new EventFactory();
        fact.setESFFile(new File("src/test/java/org/lwes/EventFactoryTest.esf"));
        fact.initialize();

        Event evt = fact.createEvent("TestEvent");
        assertNotNull(evt);

        evt = fact.createEvent("TestEvent", (short) 1);
        assertNotNull(evt);

        evt = fact.createEvent("TestEvent", true);
        assertNotNull(evt);

        evt = fact.createEvent("TestEvent", false, (short) 0);
        assertNotNull(evt);
    }
}
