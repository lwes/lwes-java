package org.lwes.serializer;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.lwes.Event;
import org.lwes.FieldType;
import org.lwes.TypeValue;
import org.lwes.util.IPAddress;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonDeserializer {

private static JsonDeserializer instance;
    
    private Gson gson;
    private JsonParser parser;
    
    public static JsonDeserializer getInstance(){
        if(instance == null){
            synchronized (JsonSerializer.class) {
                //Double-checked locking
                if(instance == null)
                    instance = new JsonDeserializer();
            }
        }
        return instance;
    }
    
    private JsonDeserializer(){
        parser = new JsonParser();
        GsonBuilder bldr = new GsonBuilder();
        gson = bldr.create();
    }
    
    public Event fromJson(String json, Event e) throws UnsupportedOperationException{
        try{
            Map<String, TypeValue> typedElems;
            JsonObject root = parser.parse(json).getAsJsonObject();
            String name = root.getAsJsonPrimitive("name").getAsString();
            e.setEventName(name);
            if(StringUtils.isEmpty(name))
                return null;
            JsonObject typedContainer = root.getAsJsonObject("typed");
            JsonObject attributeContainer = root.getAsJsonObject("attributes");
            if(attributeContainer == null && typedContainer == null)
                return null;
            else if(typedContainer != null){
               typedElems = parseTyped(typedContainer);
               for(Entry<String, TypeValue> element : typedElems.entrySet()){
                   FieldType ft = FieldType.byName(element.getValue().getType());
                   e.set(element.getKey(),
                        FieldType.byName(element.getValue().getType()),
                        getObjectForType(ft, element.getValue().getValue()));
               }
            }else
                throw new UnsupportedOperationException("Cannot construct the event without the type information for attributes.");
            
        }catch(Exception ex){
            if (ex instanceof UnsupportedOperationException)
                throw (UnsupportedOperationException)ex;
            else   
                return null;
        }
        return e;
    }
    
    Map<String, TypeValue> parseTyped(JsonObject typedContainer){
        Set<Entry<String, JsonElement>> types = typedContainer.entrySet();
        Map<String, TypeValue> typedElements = new HashMap<String, TypeValue>();
        for(Entry<String, JsonElement> type : types){
            TypeValue tv = getTypedValue(type.getValue().getAsJsonObject());
            if(tv != null)
                typedElements.put(type.getKey(), tv);
        }
        return typedElements;
    }
    
    
    TypeValue getTypedValue(JsonObject typedElement){
        String type = typedElement.getAsJsonPrimitive("type").getAsString();
        String value = typedElement.getAsJsonPrimitive("value").getAsString();
        FieldType fType = FieldType.byName(type);
        if(fType == null)
            return null;
        return new TypeValue(fType.name, value);
    }
    
    Object getObjectForType(FieldType ft, String str) {
        //Giant switch statement dervied from Serializer.serializeValue's
        //type system definition
        switch (ft) {
            case BOOLEAN:
                return getObjectForType(str, Boolean.class);
            case BYTE:
                return getObjectForType(str, Byte.class);
            case DOUBLE:
                return getObjectForType(str, Double.class);
            case FLOAT:
                return getObjectForType(str, Float.class);
            case INT16:
                return getObjectForType(str, Short.class);
            case INT32:
                return getObjectForType(str, Integer.class);
            case INT64:
                return getObjectForType(str, Long.class);
            case IPADDR:
                return getObjectForType(str, IPAddress.class);
            case STRING:
                return getObjectForType(str, String.class);
            case UINT16:
                return getObjectForType(str, Integer.class);
            case UINT32:
                return getObjectForType(str, Long.class);
            case UINT64:
                return getObjectForType(str, BigInteger.class);
            case BOOLEAN_ARRAY:
                return getObjectForType(str, boolean[].class);
            case BYTE_ARRAY:
                return getObjectForType(str, byte[].class);
            case DOUBLE_ARRAY:
                return getObjectForType(str, double[].class);
            case FLOAT_ARRAY:
                return getObjectForType(str, float[].class);
            case INT16_ARRAY:
                return getObjectForType(str, short[].class);
            case INT32_ARRAY:
                return getObjectForType(str, int[].class);
            case INT64_ARRAY:
                return getObjectForType(str, long[].class);
            case IP_ADDR_ARRAY:
                return getObjectForType(str, IPAddress[].class);
            case STRING_ARRAY:
                return getObjectForType(str, String[].class);
            case UINT16_ARRAY:
                return getObjectForType(str, int[].class);
            case UINT32_ARRAY:
                return getObjectForType(str, long[].class);
            case UINT64_ARRAY:
                return getObjectForType(str, BigInteger[].class);
            case NBOOLEAN_ARRAY:
                return getObjectForType(str, Boolean[].class);
            case NBYTE_ARRAY:
                return getObjectForType(str, Byte[].class);
            case NDOUBLE_ARRAY:
                return getObjectForType(str, Double[].class);
            case NFLOAT_ARRAY:
                return getObjectForType(str, Float[].class);
            case NINT16_ARRAY:
                return getObjectForType(str, Short[].class);
            case NINT32_ARRAY:
                return getObjectForType(str, Integer[].class);
            case NINT64_ARRAY:
                return getObjectForType(str, Long[].class);
            case NSTRING_ARRAY:
                return getObjectForType(str, String[].class);
            case NUINT16_ARRAY:
                return getObjectForType(str, Integer[].class);
            case NUINT32_ARRAY:
                return getObjectForType(str, Long[].class);
            case NUINT64_ARRAY:
                return getObjectForType(str, BigInteger[].class);
            default:
                return str;
        }
    }
    
    Object getObjectForType(String str, Class clz){
        return gson.fromJson(str, clz);
    }
}
