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
