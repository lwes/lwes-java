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

import org.apache.log4j.Logger;
import org.junit.Test;

import junit.framework.Assert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public final class ArrayEventTest extends EventTest {

    private static final Logger log = Logger.getLogger(ArrayEventTest.class);

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEncoding() {
        ArrayEvent evt = new ArrayEvent("Event");
        evt.setEncoding((short) 10);
    }

    @Test
    public void testValidEncoding() {
        ArrayEvent evt = new ArrayEvent("Event");
        evt.setEncoding(Event.UTF_8);
        Assert.assertEquals(Event.UTF_8, evt.getEncoding());
    }

    @Test
    public void testBasicFunctions() throws EventSystemException {
        final byte[] bytes = new byte[]{4, 'T', 'e', 's', 't', 0, 1, 2, 'a', 'b', FieldType.INT16.token, -10, 12};

        final ArrayEvent e1 = new ArrayEvent(bytes);

        assertTrue(Arrays.equals(bytes, e1.serialize()));
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
        e3.setEncoding(Event.ISO_8859_1);
        assertEquals("Different { \tab = -2548; \tcd = value; \tenc = 0; }", e3.toOneLineString());
        e3.set("enc", FieldType.INT16, Event.DEFAULT_ENCODING);
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

        evt.set("int32[]", FieldType.INT32_ARRAY, new int[] {10});
        Assert.assertEquals(10, evt.getInt32Array("int32[]")[0]);

        evt.set("boolean[]", FieldType.BOOLEAN_ARRAY, new boolean[] {true});
        Assert.assertTrue(evt.getBooleanArray("boolean[]")[0]);

        evt.set("byte[]", FieldType.BYTE_ARRAY, new byte[] {Byte.parseByte("32")});
        Assert.assertEquals(32, evt.getByteArray("byte[]")[0]);

        evt.set("double[]", FieldType.DOUBLE_ARRAY, new double[] {5.0});
        Assert.assertEquals(5.0, evt.getDoubleArray("double[]")[0]);

        evt.set("float[]", FieldType.FLOAT_ARRAY, new float[] {1.2f});
        Assert.assertEquals(1.2f, evt.getFloatArray("float[]")[0]);

        evt.set("int16[]", FieldType.INT16_ARRAY, new short[] {(short) 10});
        Assert.assertEquals(10, evt.getInt16Array("int16[]")[0]);

        evt.set("uint16[]", FieldType.UINT16_ARRAY, new int[] {10});
        Assert.assertEquals(10, evt.getUInt16Array("uint16[]")[0]);

        evt.set("uint32[]", FieldType.UINT32_ARRAY, new long[] {10l});
        Assert.assertEquals(10, evt.getUInt32Array("uint32[]")[0]);

        evt.set("int64[]", FieldType.INT64_ARRAY, new long[] {10l});
        Assert.assertEquals(10, evt.getInt64Array("int64[]")[0]);

        evt.set("uint64[]", FieldType.UINT64_ARRAY, new BigInteger[] {new BigInteger("10000000000000")});
        Assert.assertEquals(new BigInteger("10000000000000"), evt.getUInt64Array("uint64[]")[0]);
    }

    @Test
    public void testStringArray() {
        Event evt = new ArrayEvent("Event");

        evt.set("string[]", FieldType.STRING_ARRAY, new String[] {"value"});
        Assert.assertEquals("value", evt.getStringArray("string[]")[0]);

        evt.set("int64[]", FieldType.INT64_ARRAY, new long[] {10l});
        Assert.assertEquals(10, evt.getInt64Array("int64[]")[0]);
    }

    @Test(expected = EventSystemException.class)
    public void testInvalidEncodingType() throws EventSystemException {
        final ArrayEvent event = new ArrayEvent("Event");
        event.set("enc", FieldType.INT32, Event.DEFAULT_ENCODING);
    }

    @Test
    public void testNullableEvents() {
        Event evt = new ArrayEvent("Event");
        evt.set("long[]", FieldType.NLONG_ARRAY, new Long[] { 5000000000l, null, 8675309l });
        Long[] retrievedArray = evt.getLongObjArray("long[]");
        for (Long l : retrievedArray) {
            log.debug("retrieved item: "+l);
        }
        evt.set("str", FieldType.STRING, "testing");
        Assert.assertEquals(5000000000l, retrievedArray[0].longValue());
        Assert.assertNull(evt.getLongObjArray("long[]")[1]);
        Assert.assertEquals("testing", evt.getString("str"));

        evt.set("double[]", FieldType.NDOUBLE_ARRAY, new Double[] { 1.23, 1.26, null });
        Assert.assertEquals(1.23, evt.getDoubleObjArray("double[]")[0]);
        Assert.assertNull(evt.getDoubleObjArray("double[]")[2]);

        evt.set("float[]", FieldType.NFLOAT_ARRAY, new Float[] { 1.11f, null, 1.12f });
        Assert.assertEquals(1.11f, evt.getFloatObjArray("float[]")[0]);
        Assert.assertNull(evt.getFloatObjArray("float[]")[1]);

        evt.set("integer[]", FieldType.NINTEGER_ARRAY, new Integer[] { 5000, null, 12345 });
        Assert.assertEquals(5000, evt.getIntegerObjArray("integer[]")[0].intValue());
        Assert.assertNull(evt.getIntegerObjArray("integer[]")[1]);

        evt.set("short[]", FieldType.NSHORT_ARRAY, new Short[] { 5, null, 10 });
        Assert.assertEquals(5, evt.getShortObjArray("short[]")[0].shortValue());
        Assert.assertNull(evt.getShortObjArray("short[]")[1]);

        evt.set("boolean[]", FieldType.NBOOLEAN_ARRAY, new Boolean[] { true, null, false });
        Assert.assertTrue(evt.getBooleanObjArray("boolean[]")[0]);
        Assert.assertNull(evt.getBooleanObjArray("boolean[]")[1]);
        Assert.assertFalse(evt.getBooleanObjArray("boolean[]")[2]);

        evt.set("string[]", FieldType.NSTRING_ARRAY, new String[] { "a", null, "bc"});
        Assert.assertEquals("a", evt.getStringObjArray("string[]")[0]);
        Assert.assertNull(evt.getStringObjArray("string[]")[1]);
        Assert.assertEquals("bc", evt.getStringObjArray("string[]")[2]);
    }

    @Override
    protected ArrayEvent createEvent() {
        return new ArrayEvent();
    }
}
