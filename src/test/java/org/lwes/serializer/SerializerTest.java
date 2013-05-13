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

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Random;

import org.junit.Test;
import org.lwes.FieldType;

import junit.framework.Assert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author fmaritato
 */

public class SerializerTest {
    private static final int N = 100;

    @Test
    public void testSerializeBitSetOneNullMember() {
        BitSet bitSet = new BitSet(1);

        DeserializerState ds = new DeserializerState();
        int offset = 0;
        byte[] bytes = new byte[25];
        Serializer.serializeBitSet(bitSet, 1, bytes, offset);
        BitSet dsbs = Deserializer.deserializeBitSet(ds, bytes);
        Assert.assertFalse(dsbs.get(0));
    }

    @Test
    public void testSerializeBitSetOneByte() {
        BitSet bitSet = new BitSet(5);
        bitSet.set(1);
        bitSet.set(2);

        DeserializerState ds = new DeserializerState();
        int offset = 0;
        byte[] bytes = new byte[25];
        Serializer.serializeBitSet(bitSet, 5, bytes, offset);
        BitSet dsbs = Deserializer.deserializeBitSet(ds, bytes);
        Assert.assertEquals(2, dsbs.cardinality());
        Assert.assertFalse(dsbs.get(0));
        Assert.assertTrue(dsbs.get(1));
        Assert.assertTrue(dsbs.get(2));
        Assert.assertFalse(dsbs.get(3));
    }

    @Test
    public void testSerializeBitSetTwoBytes() {
        BitSet bitSet = new BitSet(15);
        bitSet.set(0);
        bitSet.set(2);
        bitSet.set(14);

        DeserializerState ds = new DeserializerState();
        int offset = 0;
        byte[] bytes = new byte[25];
        Serializer.serializeBitSet(bitSet, 15, bytes, offset);

        BitSet dsbs = Deserializer.deserializeBitSet(ds, bytes);
        Assert.assertEquals(3, dsbs.cardinality());
        Assert.assertTrue(dsbs.get(0));
        Assert.assertFalse(dsbs.get(1));
        Assert.assertTrue(dsbs.get(2));
        Assert.assertFalse(dsbs.get(3));
        Assert.assertTrue(dsbs.get(14));
    }

    @Test
    public void testSerializeOneInt32NullArray() {
        Integer[] array = new Integer[]{
                null
        };

        byte[] bytes = new byte[2 + (4 * array.length) + 2 + (2 * array.length)];
        int offset = 0;
        Serializer.serializeNInt32Array(array, bytes, offset);
        DeserializerState state = new DeserializerState();
        Integer[] dsIntegers = Deserializer.deserializeNInt32Array(state, bytes);
        Assert.assertNotNull(dsIntegers);
        Assert.assertEquals(1, dsIntegers.length);
        Assert.assertNull(dsIntegers[0]);
    }

    @Test
    public void testSerializeNBigIntegerArray() {
        BigInteger[] array = new BigInteger[]{
                new BigInteger("888888"),
                new BigInteger("0980398012830"),
                null,
                new BigInteger("90238109283"),
                null,
                new BigInteger("9812398123")
        };
        byte[] bytes = new byte[64];
        int num = Serializer.serializeValue(FieldType.NUINT64_ARRAY, array, (short) 0, bytes, 0);

        // length + bitSet_len + bitSet + values
        // 2 + 2 + 1 + (8*4)
        Assert.assertEquals(37, num);
        DeserializerState state = new DeserializerState();
        BigInteger[] rtn = Deserializer.deserializeNUInt64Array(state, bytes);
        Assert.assertNotNull(rtn);
        Assert.assertEquals(new BigInteger("888888"), rtn[0]);
        Assert.assertEquals(new BigInteger("0980398012830"), rtn[1]);
        Assert.assertNull(rtn[2]);
        Assert.assertEquals(new BigInteger("90238109283"), rtn[3]);
        Assert.assertNull(rtn[4]);
        Assert.assertEquals(new BigInteger("9812398123"), rtn[5]);
    }

    @Test
    public void testSerializeNBooleanArray() {
        Boolean[] array = new Boolean[]{
                true, false, null, false, null, true
        };
        byte[] bytes = new byte[64];
        int num = Serializer.serializeNBooleanArray(array, bytes, 0);
        // length + bitSet_len + bitSet + values
        // 2 + 2 + 1 + 4
        Assert.assertEquals(9, num);
        DeserializerState state = new DeserializerState();
        Boolean[] rtn = Deserializer.deserializeNBooleanArray(state, bytes);
        Assert.assertNotNull(rtn);
        Assert.assertTrue(rtn[0]);
        Assert.assertFalse(rtn[1]);
        Assert.assertNull(rtn[2]);
        Assert.assertFalse(rtn[3]);
        Assert.assertNull(rtn[4]);
        Assert.assertTrue(rtn[5]);
    }

    @Test
    public void testSerializeValueNArray() {
        Float[] array = new Float[]{
                1.2f, null, 1.3f, 3.1f
        };

        byte[] bytes = new byte[64];
        int num = Serializer.serializeValue(FieldType.NFLOAT_ARRAY, array, (short) 1, bytes, 0);
        Assert.assertEquals(17, num);
        DeserializerState state = new DeserializerState();
        Float[] rtn = (Float[]) Deserializer.deserializeValue(state, bytes, FieldType.NFLOAT_ARRAY, (short) 1);
        Assert.assertNotNull(rtn);
        Assert.assertEquals(1.2f, rtn[0]);
        Assert.assertNull(rtn[1]);
        Assert.assertEquals(1.3f, rtn[2]);
        Assert.assertEquals(3.1f, rtn[3]);
    }

    @Test
    public void testSerializeNFloatArray() {
        Float[] array = new Float[]{
                1.2f, null, 1.3f, 3.1f
        };

        byte[] bytes = new byte[2 + (4 * array.length) + 2 + (2 * array.length)];
        int offset = 0;
        Serializer.serializeNFloatArray(array, bytes, offset);
        DeserializerState state = new DeserializerState();
        Float[] dsFloats = Deserializer.deserializeNFloatArray(state, bytes);
        Assert.assertNotNull(dsFloats);
        Assert.assertEquals(4, dsFloats.length);
        Assert.assertEquals(1.2f, dsFloats[0]);
        Assert.assertNull(dsFloats[1]);
        Assert.assertEquals(1.3f, dsFloats[2]);
        Assert.assertEquals(3.1f, dsFloats[3]);
    }

    @Test
    public void testSerializeNDoubleArray() {
        Double[] array = new Double[]{
                1.2, null, 1.3, 3.1
        };

        byte[] bytes = new byte[64];
        int offset = 0;
        Serializer.serializeNDoubleArray(array, bytes, offset);
        DeserializerState state = new DeserializerState();
        Double[] dsDoubles = Deserializer.deserializeNDoubleArray(state, bytes);
        Assert.assertNotNull(dsDoubles);
        Assert.assertEquals(4, dsDoubles.length);
        Assert.assertEquals(1.2, dsDoubles[0]);
        Assert.assertNull(dsDoubles[1]);
        Assert.assertEquals(1.3, dsDoubles[2]);
        Assert.assertEquals(3.1, dsDoubles[3]);
    }

    @Test
    public void testSerializeNLongArray() {
        Long[] array = new Long[]{
                123456l, null, 13579l, 3897982734982l
        };

        byte[] bytes = new byte[64];
        int offset = 0;
        Serializer.serializeNInt64Array(array, bytes, offset);
        DeserializerState state = new DeserializerState();
        Long[] dsLongs = Deserializer.deserializeNInt64Array(state, bytes);
        Assert.assertNotNull(dsLongs);
        Assert.assertEquals(4, dsLongs.length);
        Assert.assertEquals(123456l, (long) dsLongs[0]);
        Assert.assertNull(dsLongs[1]);
        Assert.assertEquals(13579l, (long) dsLongs[2]);
        Assert.assertEquals(3897982734982l, (long) dsLongs[3]);
    }

    @Test
    public void testSerializeNIntegerArray() {
        Integer[] array = new Integer[]{
                12, null, 13, 31
        };

        byte[] bytes = new byte[2 + (4 * array.length) + 2 + (2 * array.length)];
        int offset = 0;
        Serializer.serializeNInt32Array(array, bytes, offset);
        DeserializerState state = new DeserializerState();
        Integer[] dsIntegers = Deserializer.deserializeNInt32Array(state, bytes);
        Assert.assertNotNull(dsIntegers);
        Assert.assertEquals(4, dsIntegers.length);
        Assert.assertEquals(12, (int) dsIntegers[0]);
        Assert.assertNull(dsIntegers[1]);
        Assert.assertEquals(13, (int) dsIntegers[2]);
        Assert.assertEquals(31, (int) dsIntegers[3]);
    }

    @Test
    public void testSerializeNShortArray() {
        Short[] array = new Short[]{
                12, null, 13, 31
        };

        byte[] bytes = new byte[2 + (4 * array.length) + 2 + (2 * array.length)];
        int offset = 0;
        Serializer.serializeNInt16Array(array, bytes, offset);
        DeserializerState state = new DeserializerState();
        Short[] dsShorts = Deserializer.deserializeNInt16Array(state, bytes);
        Assert.assertNotNull(dsShorts);
        Assert.assertEquals(4, dsShorts.length);
        Assert.assertEquals(12, (short) dsShorts[0]);
        Assert.assertNull(dsShorts[1]);
        Assert.assertEquals(13, (short) dsShorts[2]);
        Assert.assertEquals(31, (short) dsShorts[3]);
    }

    @Test
    public void testSerializeNStringArray() {
        String[] array = new String[]{"test", null, "two", "three"};

        byte[] bytes = new byte[30];
        int offset = 0;
        short encoding = 1;
        int numbytes = Serializer.serializeNStringArray(array,
                                                        bytes,
                                                        offset,
                                                        encoding);
        assertEquals("Number of bytes serialized incorrect", 23, numbytes);
        DeserializerState state = new DeserializerState();
        String[] a = Deserializer.deserializeNStringArray(state, bytes, encoding);
        assertNotNull(a);
        assertEquals("wrong number of elements", 4, a.length);
        int index = 0;
        for (String s : a) {
            assertEquals("String array element wrong", array[index++], s);
        }
    }

    @Test
    public void testSerializeStringArray() {
        String[] array = new String[]{"test", "one", "two", "three"};

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
        for (short x = 0; x < 256; ++x) {
            final byte[] bytes = new byte[1];
            Serializer.serializeUBYTE(x, bytes, 0);
            assertEquals(x, Deserializer.deserializeUBYTE(new DeserializerState(), bytes));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeUBYTE() throws IllegalArgumentException {
        Serializer.serializeUBYTE((short) -1, new byte[1], 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOverflowUBYTE() throws IllegalArgumentException {
        Serializer.serializeUBYTE((short) 256, new byte[1], 0);
    }

    @Test
    public void testSerializeInt16() {
        final DeserializerState state = new DeserializerState();
        final byte[] bytes = new byte[2];
        for (short x : new short[]{Short.MIN_VALUE,
                                   Short.MIN_VALUE + 1,
                                   -1,
                                   0,
                                   1,
                                   Short.MAX_VALUE - 1,
                                   Short.MAX_VALUE}) {
            testSerializeInt16(x, bytes, state);
        }
        final Random random = new Random(0);
        for (int i = 0; i < N; ++i) {
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
        for (int x : new int[]{Integer.MIN_VALUE,
                               Integer.MIN_VALUE + 1,
                               -1,
                               0,
                               1,
                               Integer.MAX_VALUE - 1,
                               Integer.MAX_VALUE}) {
            testSerializeInt32(x, bytes, state);
        }
        final Random random = new Random(0);
        for (int i = 0; i < N; ++i) {
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
        for (long x : new long[]{Long.MIN_VALUE, Long.MIN_VALUE + 1, -1, 0, 1, Long.MAX_VALUE - 1, Long.MAX_VALUE}) {
            testSerializeInt64(x, bytes, state);
        }
        final Random random = new Random(0);
        for (int i = 0; i < N; ++i) {
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
        for (int x : new int[]{0, 1, 0xfffe, 0xffff}) {
            testSerializeUInt16(x, bytes, state);
        }
        final Random random = new Random(0);
        for (int i = 0; i < N; ++i) {
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
        for (long x : new long[]{0L, 1L, 0xfffffffeL, 0xffffffffL}) {
            testSerializeUInt32(x, bytes, state);
        }
        final Random random = new Random(0);
        for (int i = 0; i < N; ++i) {
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
        for (long x : new long[]{0L, 1L, Long.MAX_VALUE - 1, Long.MAX_VALUE}) {
            testSerializeUInt64(x, bytes, state);
        }
        final Random random = new Random(0);
        for (int i = 0; i < N; ++i) {
            testSerializeUInt64(random.nextLong() & 0xffffffffffffffffL, bytes, state);
        }
    }

    private void testSerializeUInt64(long x, byte[] bytes, DeserializerState state) {
        state.reset();
        Serializer.serializeUINT64(x, bytes, 0);
        assertEquals(x, Deserializer.deserializeUINT64(state, bytes));
    }
}
