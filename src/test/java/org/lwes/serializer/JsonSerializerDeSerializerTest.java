package org.lwes.serializer;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lwes.ArrayEvent;
import org.lwes.Event;
import org.lwes.EventFactory;
import org.lwes.EventImplementation;
import org.lwes.FieldType;
import org.lwes.MapEvent;
import org.lwes.util.IPAddress;

public class JsonSerializerDeSerializerTest {

    JsonDeserializer jDeserializer;
    EventFactory factory;
    
    @Before
    public void setup(){
        factory = new EventFactory();
        jDeserializer = JsonDeserializer.getInstance();
    }
    
    @Test
    public void testSimpleSerializeDeserializeMapEvent(){
        Event evt = new MapEvent("json-event");
        setEventProperties(evt);
        Event rebornEvt = factory.createEventFromJson(evt.json(), EventImplementation.MAP_EVENT);
        Assert.assertEquals(evt, rebornEvt);
    }
   
    @Test
    public void testSimpleSerializeDeserializeArrayEvent(){
        Event evt = new ArrayEvent("json-event");
        setEventProperties(evt);
        Event rebornEvt = factory.createEventFromJson(evt.json(), EventImplementation.ARRAY_EVENT);
        Assert.assertEquals(evt, rebornEvt);
    }
    
    @Test
    public void testTypedSerialize(){
        Event evt = new ArrayEvent("json-event");
        setEventProperties(evt);
        System.out.println(evt.json());
    }
    
    @Test
    public void testUntypedSerialize(){
        Event evt = new MapEvent("json-event");
        setEventProperties(evt);
        System.out.println(evt.unTypedJson());
    }
    
    @Test
    public void testExportEventAttributes(){
        Event evt = new ArrayEvent("json-event");
        setEventProperties(evt);
        System.out.println(evt.exportTypedAndUnTypedAttributes());
    }
    
    public void setEventProperties(Event evt){
        
        evt.set("key", FieldType.STRING, "value");
        evt.set("boolean", FieldType.BOOLEAN, true);
        evt.set("byte", FieldType.BYTE, Byte.parseByte("32"));
        evt.set("double", FieldType.DOUBLE, 5.0);
        evt.set("float", FieldType.FLOAT, 1.2f);
        evt.set("int16", FieldType.INT16, (short) 10);
        evt.set("uint16", FieldType.UINT16, 10);
        evt.set("int32", FieldType.INT32, 10);
        evt.set("uint32", FieldType.UINT32, 10l);
        evt.set("int64", FieldType.INT64, 10l);
        evt.set("uint64", FieldType.UINT64, new BigInteger("184467440737095516145"));
        evt.set("ipaddr", FieldType.IPADDR, new IPAddress("127.0.0.1"));
        
        evt.set("int16[]", FieldType.INT16_ARRAY, new short[] {(short) 10});
        evt.set("uint16[]", FieldType.UINT16_ARRAY, new int[] {10});
        evt.set("int32[]", FieldType.INT32_ARRAY, new int[] {10});
        evt.set("uint32[]", FieldType.UINT32_ARRAY, new long[] {10l});
        evt.set("int64[]", FieldType.INT64_ARRAY, new long[] {10l});
        evt.set("uint64[]", FieldType.UINT64_ARRAY, new BigInteger[] {new BigInteger("184467440737095516145")});
        evt.set("boolean[]", FieldType.BOOLEAN_ARRAY, new boolean[] {true});
        evt.set("byte[]", FieldType.BYTE_ARRAY, new byte[] {Byte.parseByte("32")});
        evt.set("double[]", FieldType.DOUBLE_ARRAY, new double[] {5.0});
        evt.set("float[]", FieldType.FLOAT_ARRAY, new float[] {1.2f});
        evt.set("string[]", FieldType.STRING_ARRAY, new String[] {"value with \" '"});
        evt.set("ipaddr[]", FieldType.IP_ADDR_ARRAY, new IPAddress[]{new IPAddress("10.0.13.217")});

        evt.set("nint16[]", FieldType.NINT16_ARRAY, new Short[] {null, null, (short) 10});
        evt.set("nuint16[]", FieldType.NUINT16_ARRAY, new Integer[] {null, 10});
        evt.set("nint32[]", FieldType.NINT32_ARRAY, new Integer[] {10, null, 20});
        evt.set("nuint32[]", FieldType.NUINT32_ARRAY, new Long[] {10l, 502l, null});
        evt.set("nint64[]", FieldType.NINT64_ARRAY, new Long[] {null, 10l,null});
        evt.set("nuint64[]", FieldType.NUINT64_ARRAY, new BigInteger[] {null,new BigInteger("2342322"),  new BigInteger("184467440737095516145")});
        evt.set("nboolean[]", FieldType.NBOOLEAN_ARRAY, new Boolean[] {true,null,false,true});
        evt.set("nbyte[]", FieldType.NBYTE_ARRAY, new Byte[] {null, Byte.parseByte("32")});
        evt.set("nlong[]", FieldType.NINT64_ARRAY, new Long[] { 5000000000l, null, 8675309l });
        evt.set("nshort[]", FieldType.NUINT16_ARRAY, new Integer[] { 5, null, 10 });
        evt.set("ndouble[]", FieldType.NDOUBLE_ARRAY, new Double[] { 1.23, null, 1.26 });
        evt.set("nfloat[]", FieldType.NFLOAT_ARRAY, new Float[] { 1.11f, 1.12f, null });
        
    }
    
}
