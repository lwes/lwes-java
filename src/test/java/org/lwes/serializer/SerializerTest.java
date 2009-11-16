package org.lwes.serializer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 * @author fmaritato
 */

public class SerializerTest {

    @Test
    public void testSerializeStringArray() {
        String[] array = new String[]{
                "test", "one", "two", "three"
        };

        byte[] bytes = new byte[30];
        int offset = 0;
        short encoding = 1;
        int numbytes = Serializer.serializeStringArray(array,
                                                       bytes,
                                                       offset,
                                                       encoding);
        assertEquals("Number of bytes serialized incorrect", 25, numbytes);
        DeserializerState state = new DeserializerState();
        String[] a = Deserializer.deserializeStringArray(state, bytes, encoding);
        assertNotNull(a);
        assertEquals("wrong number of elements", 4, a.length);
        for (int i = 0; i < a.length; i++) {
            assertEquals("Element "+i+" incorrect", array[i], a[i]);
        }
    }
}
