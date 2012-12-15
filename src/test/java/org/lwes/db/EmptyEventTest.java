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

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author fmaritato
 */

public class EmptyEventTest {

    private static final String ESF = "EmptyEventTest.esf";

    @Test
    public void testEmptyEvent() {

        EventTemplateDB template = new EventTemplateDB();
        template.setESFInputStream(getClass().getResourceAsStream(ESF));
        template.initialize();

        assertTrue(template.checkForEvent("EmptyEvent"));
    }
}
