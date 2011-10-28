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

import java.util.Random;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author fmaritato
 */

public class SerializerTest {
    private static final int N = 100;

    @Test
    public void testSerializeStringArray() {
        String[] array = new String[] {"test", "one", "two", "three"};

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
        int index = 0;
        for (String s : a) {
            assertEquals("String array element wrong", array[index++], s);
        }
    }

    @Test
    public void testSerializeValidUBYTEs() {
        for (short x=0; x<256; ++x) {
            final byte[] bytes = new byte[1];
            Serializer.serializeUBYTE(x, bytes, 0);
            assertEquals(x, Deserializer.deserializeUBYTE(new DeserializerState(), bytes));
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNegativeUBYTE() throws IllegalArgumentException {
        Serializer.serializeUBYTE((short)-1, new byte[1], 0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testOverflowUBYTE() throws IllegalArgumentException {
        Serializer.serializeUBYTE((short)256, new byte[1], 0);
    }
    
    @Test
    public void testSerializeInt16() {
        final DeserializerState state = new DeserializerState();
        final byte[] bytes = new byte[2];
        for (short x : new short[] { Short.MIN_VALUE, Short.MIN_VALUE+1, -1, 0, 1, Short.MAX_VALUE-1, Short.MAX_VALUE }) {
            testSerializeInt16(x, bytes, state);
        }
        final Random random = new Random(0);
        for (int i=0; i<N; ++i) {
            testSerializeInt16((short) random.nextInt(), bytes, state);
        }
    }
    
    private void testSerializeInt16(short x, byte[] bytes, DeserializerState state) {
        state.reset();
        Serializer.serializeINT16(x, bytes, 0);
        assertEquals(x, Deserializer.deserializeINT16(state, bytes));
    }

    @Test
    public void testSerializeInt32s() {
        final DeserializerState state = new DeserializerState();
        final byte[] bytes = new byte[4];
        for (int x : new int[] { Integer.MIN_VALUE, Integer.MIN_VALUE+1, -1, 0, 1, Integer.MAX_VALUE-1, Integer.MAX_VALUE }) {
            testSerializeInt32(x, bytes, state);
        }
        final Random random = new Random(0);
        for (int i=0; i<N; ++i) {
            testSerializeInt32(random.nextInt(), bytes, state);
        }
    }

    private void testSerializeInt32(int x, byte[] bytes, DeserializerState state) {
        state.reset();
        Serializer.serializeINT32(x, bytes, 0);
        assertEquals(x, Deserializer.deserializeINT32(state, bytes));
    }

    @Test
    public void testSerializeInt64s() {
        final DeserializerState state = new DeserializerState();
        final byte[] bytes = new byte[8];
        for (long x : new long[] { Long.MIN_VALUE, Long.MIN_VALUE+1, -1, 0, 1, Long.MAX_VALUE-1, Long.MAX_VALUE }) {
            testSerializeInt64(x, bytes, state);
        }
        final Random random = new Random(0);
        for (int i=0; i<N; ++i) {
            testSerializeInt64(random.nextLong(), bytes, state);
        }
    }

    private void testSerializeInt64(long x, byte[] bytes, DeserializerState state) {
        state.reset();
        Serializer.serializeINT64(x, bytes, 0);
        assertEquals(x, Deserializer.deserializeINT64(state, bytes));
    }
    
    @Test
    public void testSerializeUInt16() {
        final DeserializerState state = new DeserializerState();
        final byte[] bytes = new byte[2];
        for (int x : new int[] { 0, 1, 0xfffe, 0xffff }) {
            testSerializeUInt16(x, bytes, state);
        }
        final Random random = new Random(0);
        for (int i=0; i<N; ++i) {
            testSerializeUInt16(random.nextInt() & 0xffff, bytes, state);
        }
    }
    
    private void testSerializeUInt16(int x, byte[] bytes, DeserializerState state) {
        state.reset();
        Serializer.serializeUINT16(x, bytes, 0);
        assertEquals(x, Deserializer.deserializeUINT16(state, bytes));
    }

    @Test
    public void testSerializeUInt32s() {
        final DeserializerState state = new DeserializerState();
        final byte[] bytes = new byte[4];
        for (long x : new long[] { 0L, 1L, 0xfffffffeL, 0xffffffffL }) {
            testSerializeUInt32(x, bytes, state);
        }
        final Random random = new Random(0);
        for (int i=0; i<N; ++i) {
            testSerializeUInt32(random.nextLong() & 0xffffffffL, bytes, state);
        }
    }

    private void testSerializeUInt32(long x, byte[] bytes, DeserializerState state) {
        state.reset();
        Serializer.serializeUINT32(x, bytes, 0);
        assertEquals(x, Deserializer.deserializeUINT32(state, bytes));
    }

    @Test
    public void testSerializeUInt64s() {
        final DeserializerState state = new DeserializerState();
        final byte[] bytes = new byte[8];
        for (long x : new long[] { 0L, 1L, Long.MAX_VALUE-1, Long.MAX_VALUE }) {
            testSerializeUInt64(x, bytes, state);
        }
        final Random random = new Random(0);
        for (int i=0; i<N; ++i) {
            testSerializeUInt64(random.nextLong() & 0xffffffffffffffffL, bytes, state);
        }
    }

    private void testSerializeUInt64(long x, byte[] bytes, DeserializerState state) {
        state.reset();
        Serializer.serializeUINT64(x, bytes, 0);
        assertEquals(x, Deserializer.deserializeUINT64(state, bytes));
    }
}
