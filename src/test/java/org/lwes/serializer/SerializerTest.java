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

package org.lwes.serializer;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author fmaritato
 */

public class SerializerTest {

    @Test
    public void testSerializeStringArray() {
        List array = new ArrayList(Arrays.asList(
                "test", "one", "two", "three"
        ));

        byte[] bytes = new byte[30];
        int offset = 0;
        short encoding = 1;
        int numbytes = Serializer.serializeStringArray(array,
                                                       bytes,
                                                       offset,
                                                       encoding);
        assertEquals("Number of bytes serialized incorrect", 25, numbytes);
        DeserializerState state = new DeserializerState();
        List<String> a = Deserializer.deserializeStringArray(state, bytes, encoding);
        assertNotNull(a);
        assertEquals("wrong number of elements", 4, a.size());
        int index = 0;
        for (String s : a) {
            assertEquals("String array element wrong", array.get(index++), s);
        }
    }
}
