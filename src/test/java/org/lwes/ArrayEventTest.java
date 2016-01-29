/*======================================================================*
 * Licensed under the New BSD License (the "License"); you may not use  *
 * this file except in compliance with the License.  Unless required    *
 * by applicable law or agreed to in writing, software distributed      *
 * under the License is distributed on an "AS IS" BASIS, WITHOUT        *
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     *
 * See the License for the specific language governing permissions and  *
 * limitations under the License. See accompanying LICENSE file.        *
 *======================================================================*/
package org.lwes;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Enumeration;

import org.junit.Test;
import org.lwes.ArrayEvent.ArrayEventStats;

import static org.lwes.Event.UTF_8;

import junit.framework.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public final class ArrayEventTest extends EventTest {

    final byte[] testBytes = new byte[]{4, 'T', 'e', 's', 't', 0, 1, 2, 'a', 'b', FieldType.INT16.token, -10, 12};

    @Test
    public void testBasicFunctions() throws EventSystemException {

        final ArrayEvent e1 = new ArrayEvent(testBytes);

        assertTrue(Arrays.equals(testBytes, e1.serialize()));
        assertEquals("Test { \tab = -2548; }", e1.toOneLineString());
        assertEquals("Test { \tab = -2548; }", e1.copy().toOneLineString());
        assertTrue(e1.isSet("ab"));

        final Event e2 = e1.copy();
        e2.clear("ab");
        assertEquals("Test { }", e2.toOneLineString());
        assertTrue(e1.isSet("ab"));
        assertFalse(e2.isSet("ab"));

        final Event e3 = e1.copy();
        e3.setEventName("Different");
        assertEquals("Different { \tab = -2548; }", e3.toOneLineString());
        e3.set("cd", FieldType.STRING, "value");
        assertEquals("Different { \tab = -2548; \tcd = value; \tenc = 1; }", e3.toOneLineString());
        e3.set("ab", FieldType.INT16, (short) -1234);
        assertEquals("Different { \tab = -1234; \tcd = value; \tenc = 1; }", e3.toOneLineString());

        final Enumeration<String> names = e3.getEventAttributeNames();
        assertEquals("ab", names.nextElement());
        assertEquals("cd", names.nextElement());
        assertEquals("enc", names.nextElement());
        assertFalse(names.hasMoreElements());

        assertEquals(FieldType.INT16, e3.getType("ab"));
        assertEquals(FieldType.STRING, e3.getType("cd"));
        assertEquals(FieldType.INT16, e3.getType("enc"));
        assertNull(e3.getType("zzz"));

        final Event e4 = new MapEvent();
        e4.copyFrom(e3);
        assertEquals("Different { \tab = -1234; \tcd = value; \tenc = 1; }", e4.toOneLineString());

        final Event e5 = new ArrayEvent();
        e5.copyFrom(e3);
        assertEquals("Different { \tab = -1234; \tcd = value; \tenc = 1; }", e5.toOneLineString());
        e5.copyFrom(e4);
        assertEquals("Different { \tab = -1234; \tcd = value; \tenc = 1; }", e5.toOneLineString());

        e1.reset();
        assertEquals("", e1.toOneLineString());
        assertFalse(e1.isSet("ab"));

        System.gc();
    }

    @Test
    public void testReadOnly() {
        ArrayEvent.resetStats();
        final ArrayEvent e1 = new ArrayEvent(testBytes); // copy
        assertEquals(testBytes.length, e1.getBytesSize());
        assertEquals(Event.MAX_MESSAGE_SIZE, e1.getCapacity());
        final ArrayEvent e2 = new ArrayEvent(testBytes, false); // no copy
        assertEquals(e1, e2);
        assertEquals(testBytes.length, e2.getBytesSize());
        assertEquals(testBytes.length, e2.getCapacity());
        final ArrayEvent e3 = new ArrayEvent(testBytes, false); // no copy
        assertEquals(testBytes.length, e3.getCapacity());
        assertEquals(e2, e3);
        final ArrayEvent e4 = new ArrayEvent(testBytes, true); // copy
        assertEquals(e2, e4);
        assertEquals(testBytes.length, e4.getBytesSize());
        assertEquals(Event.MAX_MESSAGE_SIZE, e4.getCapacity());

        final int bigSize = testBytes.length * 3;
        byte[] big = new byte[bigSize];
        System.arraycopy(testBytes, 0, big, 0, testBytes.length);
        final ArrayEvent e5 = new ArrayEvent(big, testBytes.length, false); // no copy
        assertEquals(e5, e1);
        assertEquals(testBytes.length, e5.getBytesSize());
        assertEquals(big.length, e5.getCapacity());

        assertEquals(3, ArrayEvent.getStats().get(ArrayEventStats.WRAPS).intValue());
        assertEquals(5, ArrayEvent.getStats().get(ArrayEventStats.CREATIONS).intValue());
        // System.out.print(e5.toStringDetailed());
    }

    @Test
    public void testGettersSetters() {
        Event evt = new ArrayEvent("Event");

        // Test the various getters/setters
        evt.set("key", FieldType.STRING, "value");
        Assert.assertEquals("value", evt.get("key"));
        evt.set("boolean", FieldType.BOOLEAN, true);
        Assert.assertTrue(evt.getBoolean("boolean"));
        evt.set("byte", FieldType.BYTE, Byte.parseByte("32"));
        Assert.assertEquals(32, (byte) evt.getByte("byte"));
        evt.set("double", FieldType.DOUBLE, 5.0);
        Assert.assertEquals(5.0, evt.getDouble("double"));
        evt.set("float", FieldType.FLOAT, 1.2f);
        Assert.assertEquals(1.2f, evt.getFloat("float"));
        evt.set("int16", FieldType.INT16, (short) 10);
        Assert.assertEquals(10, (short) evt.getInt16("int16"));
        evt.set("uint16", FieldType.UINT16, 10);
        Assert.assertEquals(10, (int) evt.getUInt16("uint16"));

        evt.set("int32", FieldType.INT32, 10);
        Assert.assertEquals(10, (int) evt.getInt32("int32"));
        evt.set("uint32", FieldType.UINT32, 10l);
        Assert.assertEquals(10, (long) evt.getUInt32("uint32"));

        evt.set("int64", FieldType.INT64, 10l);
        Assert.assertEquals(10, (long) evt.getInt64("int64"));

        evt.set("uint64", FieldType.UINT64, new BigInteger("10000000000000"));
        Assert.assertEquals(new BigInteger("10000000000000"), evt.getUInt64("uint64"));
    }

    @Test
    public void testArrayGettersSetters() {
        Event evt = new ArrayEvent("Event");

        evt.set("int32[]", FieldType.INT32_ARRAY, new int[]{10});
        Assert.assertEquals(10, evt.getInt32Array("int32[]")[0]);

        evt.set("boolean[]", FieldType.BOOLEAN_ARRAY, new boolean[]{true});
        Assert.assertTrue(evt.getBooleanArray("boolean[]")[0]);

        evt.set("byte[]", FieldType.BYTE_ARRAY, new byte[]{Byte.parseByte("32")});
        Assert.assertEquals(32, evt.getByteArray("byte[]")[0]);

        evt.set("double[]", FieldType.DOUBLE_ARRAY, new double[]{5.0});
        Assert.assertEquals(5.0, evt.getDoubleArray("double[]")[0]);

        evt.set("float[]", FieldType.FLOAT_ARRAY, new float[]{1.2f});
        Assert.assertEquals(1.2f, evt.getFloatArray("float[]")[0]);

        evt.set("int16[]", FieldType.INT16_ARRAY, new short[]{(short) 10});
        Assert.assertEquals(10, evt.getInt16Array("int16[]")[0]);

        evt.set("uint16[]", FieldType.UINT16_ARRAY, new int[]{10});
        Assert.assertEquals(10, evt.getUInt16Array("uint16[]")[0]);

        evt.set("uint32[]", FieldType.UINT32_ARRAY, new long[]{10l});
        Assert.assertEquals(10, evt.getUInt32Array("uint32[]")[0]);

        evt.set("int64[]", FieldType.INT64_ARRAY, new long[]{10l});
        Assert.assertEquals(10, evt.getInt64Array("int64[]")[0]);

        evt.set("uint64[]", FieldType.UINT64_ARRAY, new BigInteger[]{new BigInteger("10000000000000")});
        Assert.assertEquals(new BigInteger("10000000000000"), evt.getUInt64Array("uint64[]")[0]);
    }

    @Test
    public void testStringArray() {
        Event evt = new ArrayEvent("Event");

        evt.set("string[]", FieldType.STRING_ARRAY, new String[]{"value"});
        Assert.assertEquals("value", evt.getStringArray("string[]")[0]);

        evt.set("int64[]", FieldType.INT64_ARRAY, new long[]{10l});
        Assert.assertEquals(10, evt.getInt64Array("int64[]")[0]);
    }

    @Test(expected = EventSystemException.class)
    public void testInvalidEncodingType() throws EventSystemException {
        final ArrayEvent event = new ArrayEvent("Event");
        event.set("enc", FieldType.INT32, UTF_8);
    }

    @Test
    public void testNInt64() {
        Event evt = new ArrayEvent("Event");
        evt.set("long[]", FieldType.NINT64_ARRAY, new Long[]{5000000000l, null, 8675309l});
        Long[] retrievedArray = evt.getLongObjArray("long[]");
        Assert.assertEquals(5000000000l, retrievedArray[0].longValue());
        Assert.assertNull(evt.getLongObjArray("long[]")[1]);
    }

    @Test
    public void testNDouble() {
        Event evt = new ArrayEvent("Event");
        evt.set("double[]", FieldType.NDOUBLE_ARRAY, new Double[]{1.23, 1.26, null});
        Assert.assertEquals(1.23, evt.getDoubleObjArray("double[]")[0]);
        Assert.assertNull(evt.getDoubleObjArray("double[]")[2]);
    }

    @Test
    public void testNFloat() {
        Event evt = new ArrayEvent("Event");
        evt.set("float[]", FieldType.NFLOAT_ARRAY, new Float[]{1.11f, null, 1.12f});
        Assert.assertEquals(1.11f, evt.getFloatObjArray("float[]")[0]);
        Assert.assertNull(evt.getFloatObjArray("float[]")[1]);
    }

    @Test
    public void testNInt16() {
        Event evt = new ArrayEvent("Event");
        evt.set("short[]", FieldType.NINT16_ARRAY, new Short[]{5, null, 10});
        Assert.assertEquals(5, evt.getShortObjArray("short[]")[0].shortValue());
        Assert.assertNull(evt.getShortObjArray("short[]")[1]);
    }

    @Test
    public void testNUint16() {
        Event evt = new ArrayEvent("Event");
        evt.set("int[]", FieldType.NUINT16_ARRAY, new Integer[]{5, null, 10});
        Assert.assertEquals(5, evt.getIntegerObjArray("int[]")[0].intValue());
        Assert.assertNull(evt.getIntegerObjArray("int[]")[1]);
    }

    @Test
    public void testNInt32() {
        Event evt = new ArrayEvent("Event");
        evt.set("int[]", FieldType.NINT32_ARRAY, new Integer[]{5, null, 10});
        Assert.assertEquals(5, evt.getIntegerObjArray("int[]")[0].intValue());
        Assert.assertNull(evt.getIntegerObjArray("int[]")[1]);
    }

    @Test
    public void testNUint32() {
        Event evt = new ArrayEvent("Event");
        evt.set("long[]", FieldType.NUINT32_ARRAY, new Long[]{5000l, null, 12345l});
        Assert.assertEquals(5000, evt.getLongObjArray("long[]")[0].longValue());
        Assert.assertNull(evt.getLongObjArray("long[]")[1]);
    }

    @Test
    public void testNUint64() {
        Event evt = new ArrayEvent("Event");
        evt.set("biginteger[]", FieldType.NUINT64_ARRAY,
                new BigInteger[]{
                        new BigInteger("5"), null, new BigInteger("10")
                });
        Assert.assertEquals(5, evt.getBigIntegerObjArray("biginteger[]")[0].intValue());
        Assert.assertNull(evt.getBigIntegerObjArray("biginteger[]")[1]);
    }

    @Test
    public void testNBoolean() {
        Event evt = new ArrayEvent("Event");
        evt.set("boolean[]", FieldType.NBOOLEAN_ARRAY, new Boolean[]{true, null, false});
        Assert.assertTrue(evt.getBooleanObjArray("boolean[]")[0]);
        Assert.assertNull(evt.getBooleanObjArray("boolean[]")[1]);
        Assert.assertFalse(evt.getBooleanObjArray("boolean[]")[2]);
    }

    @Test
    public void testNString() {
        Event evt = new ArrayEvent("Event");
        evt.set("string[]", FieldType.NSTRING_ARRAY, new String[]{"a", null, "bc"});
        Assert.assertEquals("a", evt.getStringObjArray("string[]")[0]);
        Assert.assertNull(evt.getStringObjArray("string[]")[1]);
        Assert.assertEquals("bc", evt.getStringObjArray("string[]")[2]);
    }

    @Test
    public void testNByte() {
        Event evt = new ArrayEvent("Event");
        evt.set("byte[]", FieldType.NBYTE_ARRAY, new Byte[]{0x32, null, 0x33});
        Assert.assertEquals(0x32, evt.getByteObjArray("byte[]")[0].byteValue());
        Assert.assertNull(evt.getByteObjArray("byte[]")[1]);
        Assert.assertEquals(0x33, evt.getByteObjArray("byte[]")[2].byteValue());
    }

    @Test
    public void testNIntegrated() {
        Event evt = new ArrayEvent("Event");

        evt.set("nint64[]", FieldType.NINT64_ARRAY, new Long[]{5000000000l, null, 8675309l});
        Long[] retrievedArray = evt.getLongObjArray("nint64[]");
        Assert.assertEquals(5000000000l, retrievedArray[0].longValue());
        Assert.assertNull(evt.getLongObjArray("nint64[]")[1]);

        evt.set("nuint16[]", FieldType.NUINT16_ARRAY, new Integer[]{5000, null, 8675});
        Assert.assertEquals(5000, evt.getIntegerObjArray("nuint16[]")[0].intValue());
        Assert.assertNull(evt.getIntegerObjArray("nuint16[]")[1]);

        evt.set("nint16[]", FieldType.NINT16_ARRAY, new Short[]{5000, null, 8675});
        Assert.assertEquals(5000, evt.getShortObjArray("nint16[]")[0].shortValue());
        Assert.assertNull(evt.getShortObjArray("nint16[]")[1]);

        evt.set("nuint32[]", FieldType.NUINT32_ARRAY, new Long[]{5000l, null, 12345l});
        Assert.assertEquals(5000, evt.getLongObjArray("nuint32[]")[0].longValue());
        Assert.assertNull(evt.getLongObjArray("nuint32[]")[1]);

        evt.set("byte[]", FieldType.NBYTE_ARRAY, new Byte[]{0x32, null, 0x33});
        Assert.assertEquals(0x32, evt.getByteObjArray("byte[]")[0].byteValue());
        Assert.assertNull(evt.getByteObjArray("byte[]")[1]);
        Assert.assertEquals(0x33, evt.getByteObjArray("byte[]")[2].byteValue());

        evt.set("Double[]", FieldType.NDOUBLE_ARRAY, new Double[]{
                0.15392470038762496, null, 0.8454572482640883, 0.4266316445138164, 0.9235260958754714
        });
        Assert.assertEquals(0.15392470038762496, evt.getDoubleObjArray("Double[]")[0]);
        Assert.assertNull(evt.getDoubleObjArray("Double[]")[1]);

        evt.set("revjhdttokuc", FieldType.STRING,
                "kzukphbcbghywpklojzauzmyapwdmmqctcxeoqbvzwzltdzanksdxzfkrgvkemsbiqxnjqdivsszxetvytxocrukyqiu");
        Assert.assertEquals(
                "kzukphbcbghywpklojzauzmyapwdmmqctcxeoqbvzwzltdzanksdxzfkrgvkemsbiqxnjqdivsszxetvytxocrukyqiu",
                evt.getString("revjhdttokuc"));

        evt.set("float[]", FieldType.NFLOAT_ARRAY, new Float[]{1.11f, null, 1.12f});
        Assert.assertEquals(1.11f, evt.getFloatObjArray("float[]")[0]);
        Assert.assertNull(evt.getFloatObjArray("float[]")[1]);

        evt.serialize();
    }

    @Test
    public void testResettingField() {
        ArrayEvent evt = new ArrayEvent("Event");
        Short[] ar = new Short[]{1, null, 2};

        evt.set("nint16[]", FieldType.NINT16_ARRAY, ar);
        Assert.assertEquals(1, evt.getShortObjArray("nint16[]")[0].shortValue());
        Assert.assertNull(evt.getShortObjArray("nint16[]")[1]);

        evt.set("nint16[]", FieldType.NINT16_ARRAY, new Short[]{null});
        Assert.assertNull(evt.getShortObjArray("nint16[]")[0]);

        evt.serialize();
    }

    @Test
    public void testStringReset() {
        ArrayEvent evt = new ArrayEvent("Event");
        evt.set("a", FieldType.STRING, "zzzzzz");
        Assert.assertEquals("zzzzzz", evt.get("a"));
        evt.set("a", FieldType.STRING, "ab");
        Assert.assertEquals("ab", evt.get("a"));

        evt.set("b", FieldType.STRING_ARRAY, new String[]{"a", "b"});
        Assert.assertEquals("b", evt.getStringArray("b")[1]);
        evt.set("b", FieldType.STRING_ARRAY, new String[]{"a", "b", "c"});
        Assert.assertEquals("c", evt.getStringArray("b")[2]);

        evt.serialize();
    }

    @Override
    protected ArrayEvent createEvent() {
        return new ArrayEvent();
    }
}
