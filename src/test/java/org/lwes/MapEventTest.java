package org.lwes;
/**
 * User: frank.maritato
 * Date: 5/2/12
 */

import java.math.BigInteger;

import org.junit.Test;

import junit.framework.Assert;

public class MapEventTest extends EventTest {

    @Test
    public void mapEventWithFullNINT32_ARRAY() {
        final MapEvent event = new MapEvent();
        event.set("nint32[]", FieldType.NINT32_ARRAY, new Integer[] { 3 });
        System.out.println(event);
        event.serialize();
    }

    @Test
    public void mapEventWithNINT32_ARRAY() {
        final MapEvent event = new MapEvent();
        event.set("nint32[]", FieldType.NINT32_ARRAY, new Integer[] { null });
        System.out.println(event);
        event.serialize();
    }

    @Test
    public void testNullableArrays() {
        Event evt = new MapEvent("Event");

        evt.set("long[]", FieldType.NINT64_ARRAY, new Long[] { 5000000000l, null, 8675309l });
        evt.set("str", FieldType.STRING, "testing");
        Long[] retrievedArray = evt.getLongObjArray("long[]");
        Assert.assertEquals(5000000000l, retrievedArray[0].longValue());
        Assert.assertNull(evt.getLongObjArray("long[]")[1]);
        Assert.assertEquals("testing", evt.getString("str"));

        evt.set("Double[]", FieldType.NDOUBLE_ARRAY, new Double[] { 1.23, null, 1.26 });
        Assert.assertEquals(1.23, evt.getDoubleObjArray("Double[]")[0]);
        Assert.assertNull(evt.getDoubleObjArray("Double[]")[1]);

        evt.set("Float[]", FieldType.NFLOAT_ARRAY, new Float[] { 1.11f, 1.12f, null });
        Assert.assertEquals(1.11f, evt.getFloatObjArray("Float[]")[0]);
        Assert.assertNull(evt.getFloatObjArray("Float[]")[2]);

        evt.set("Long[]", FieldType.NUINT32_ARRAY, new Long[] { 5000L, null, 12345L });
        Assert.assertEquals(5000, evt.getLongObjArray("Long[]")[0].longValue());
        Assert.assertNull(evt.getLongObjArray("Long[]")[1]);

        evt.set("Integer[]", FieldType.NUINT16_ARRAY, new Integer[] { 5, null, 10 });
        Assert.assertEquals(5, evt.getIntegerObjArray("Integer[]")[0].shortValue());
        Assert.assertNull(evt.getIntegerObjArray("Integer[]")[1]);

    }

    @Test
    public void testSerializeDeserialize() {
      Event evt = new MapEvent("Event_Name");

      evt.set("str", FieldType.STRING, "testing");
      evt.set("str2", FieldType.STRING, new String("A" + "\u00ea" + "\u00f1" + "\u00fc" + "C"));
      evt.set("boolean", FieldType.BOOLEAN, true);
      evt.set("byte", FieldType.BYTE, Byte.parseByte("32"));
      evt.set("double", FieldType.DOUBLE, 5.0);
      evt.set("float", FieldType.FLOAT, 1.2f);
      evt.set("int16", FieldType.INT16, (short) 10);
      evt.set("uint16", FieldType.UINT16, 20);
      evt.set("int32", FieldType.INT32, 30);
      evt.set("long[]", FieldType.NINT64_ARRAY, new Long[] { 5000000000l, null, 8675309l });
      evt.set("Double[]", FieldType.NDOUBLE_ARRAY, new Double[] { 1.23, null, 1.26 });
      evt.set("Float[]", FieldType.NFLOAT_ARRAY, new Float[] { 1.11f, 1.12f, null });
      evt.set("Long[]", FieldType.NUINT32_ARRAY, new Long[] { 5000L, null, 12345L });
      evt.set("Integer[]", FieldType.NUINT16_ARRAY, new Integer[] { 5, null, 10 });
      evt.set("string[]", FieldType.STRING_ARRAY, new String[] {"value1", "value2"});
      evt.set("nstring[]", FieldType.NSTRING_ARRAY, new String[] {null, "value3"});

      byte[] serialize = new byte[ 64 * 1024 ];
      int len = evt.serialize(serialize, 0);
      Event evt2 = new MapEvent();
      evt2.deserialize(serialize, 0, len);

      // make sure 2 events are identical
      Assert.assertEquals("Event_Name", evt2.getEventName());
      Assert.assertEquals("testing", evt2.getString("str"));
      Assert.assertEquals(new String("A" + "\u00ea" + "\u00f1" + "\u00fc" + "C"), evt2.getString("str2"));
      Assert.assertTrue(evt2.getBoolean("boolean"));
      Assert.assertEquals(32, (byte) evt2.getByte("byte"));
      Assert.assertEquals(5.0, evt2.getDouble("double"));
      Assert.assertEquals(1.2f, evt2.getFloat("float"));
      Assert.assertEquals(10, (short) evt2.getInt16("int16"));
      Assert.assertEquals(20, (int) evt2.getUInt16("uint16"));
      Assert.assertEquals(30, (int) evt.getInt32("int32"));
      Assert.assertEquals(5000000000l, evt2.getLongObjArray("long[]")[0].longValue());
      Assert.assertNull(evt2.getLongObjArray("long[]")[1]);
      Assert.assertEquals(8675309l, evt2.getLongObjArray("long[]")[2].longValue());
      Assert.assertEquals(1.23, evt2.getDoubleObjArray("Double[]")[0]);
      Assert.assertNull(evt2.getDoubleObjArray("Double[]")[1]);
      Assert.assertEquals(1.26, evt2.getDoubleObjArray("Double[]")[2]);
      Assert.assertEquals(1.11f, evt2.getFloatObjArray("Float[]")[0]);
      Assert.assertEquals(1.12f, evt2.getFloatObjArray("Float[]")[1]);
      Assert.assertNull(evt2.getFloatObjArray("Float[]")[2]);
      Assert.assertEquals(5000, evt2.getLongObjArray("Long[]")[0].longValue());
      Assert.assertNull( evt2.getLongObjArray("Long[]")[1]);
      Assert.assertEquals(12345, evt2.getLongObjArray("Long[]")[2].longValue());
      Assert.assertEquals(5, evt2.getIntegerObjArray("Integer[]")[0].shortValue());
      Assert.assertNull(evt2.getIntegerObjArray("Integer[]")[1]);
      Assert.assertEquals(10, evt2.getIntegerObjArray("Integer[]")[2].shortValue());
      Assert.assertEquals("value1", evt2.getStringArray("string[]")[0]);
      Assert.assertEquals("value2", evt2.getStringArray("string[]")[1]);
      Assert.assertNull(evt2.getStringArray("nstring[]")[0]);
      Assert.assertEquals("value3", evt2.getStringArray("nstring[]")[1]);

      byte[] serialize2 = new byte[ 64 * 1024 ];
      int len2 = evt2.serialize(serialize2, 0);
      Assert.assertEquals(len, len2);

    }

    @Test
    public void testGettersSetters() {
        Event evt = new MapEvent("Event");

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
        Event evt = new MapEvent("Event");

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

        Event evt = new MapEvent("Event");

        evt.set("string[]", FieldType.STRING_ARRAY, new String[] {"value"});
        Assert.assertEquals("value", evt.getStringArray("string[]")[0]);

        evt.set("int64[]", FieldType.INT64_ARRAY, new long[] {10l});
        Assert.assertEquals(10, evt.getInt64Array("int64[]")[0]);
    }

    @Override
    protected MapEvent createEvent() {
        return new MapEvent();
    }

}
