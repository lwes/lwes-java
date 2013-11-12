package org.lwes.serializer;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.lwes.Event;
import org.lwes.FieldType;
import org.lwes.MapEvent;
import org.lwes.TypeValue;

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
    
    public Event fromJson(String json, Event e){
        try{
            JsonObject root = parser.parse(json).getAsJsonObject();
            String name = root.getAsJsonPrimitive("name").getAsString();
            if(StringUtils.isEmpty(name))
                return null;
            JsonObject typedContainer = root.getAsJsonObject("typed");
            if(typedContainer == null)
                return null;
            Map<String, TypeValue> typedElems = parseTyped(typedContainer);
            
            e.setEventName(name);
            for(Entry<String, TypeValue> element : typedElems.entrySet()){
                FieldType ft = FieldType.byName(element.getValue().getType());
                e.set(element.getKey(),
                        FieldType.byName(element.getValue().getType()),
                        getObjectForType(ft, element.getValue().getValue()));
            }
            
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
        return e;
    }
    
    Map<String, TypeValue> parseTyped(JsonObject typedContainer){
        Set<Entry<String, JsonElement>> types = typedContainer.entrySet();
        Map<String, TypeValue> typedElements = new HashMap<String, TypeValue>();
        for(Entry<String, JsonElement> type : types){
            TypeValue tv = getTypeValue(type.getValue().getAsJsonObject());
            if(tv != null)
                typedElements.put(type.getKey(), tv);
        }
        return typedElements;
    }
    
    TypeValue getTypeValue(JsonObject typedElement){
        String type = typedElement.getAsJsonPrimitive("type").getAsString();
        String value = typedElement.getAsJsonPrimitive("value").getAsString();
        FieldType fType = FieldType.byName(type);
        if(fType == null)
            return null;
        return new TypeValue(fType.name, value);
    }
    
    Object getObjectForType(FieldType ft, String str){
        switch (ft) {
        case BOOLEAN:
            return gson.fromJson(str, Boolean.class);
        case BYTE:
            return gson.fromJson(str, Byte.class);
        case DOUBLE:
            return gson.fromJson(str, Double.class);
        case FLOAT:
            return gson.fromJson(str, Float.class);
        case INT16:
            return gson.fromJson(str, Short.class);
        case INT32:
            return gson.fromJson(str, Integer.class);
        case INT64:
            return gson.fromJson(str, Long.class);
        case IPADDR:
            return gson.fromJson(str, String.class);
        case STRING:
            return gson.fromJson(str, String.class);
        case UINT16:
            return gson.fromJson(str, Integer.class);
        case UINT32:
            return gson.fromJson(str, Long.class);
        case UINT64:
            return gson.fromJson(str, BigInteger.class);
        case BOOLEAN_ARRAY:
            return gson.fromJson(str, boolean[].class);
        case BYTE_ARRAY:
            return gson.fromJson(str, byte[].class);
        case DOUBLE_ARRAY:
            return gson.fromJson(str, double[].class);
        case FLOAT_ARRAY:
            return gson.fromJson(str, float[].class);
        case INT16_ARRAY:
            return gson.fromJson(str, short[].class);
        case INT32_ARRAY:
            return gson.fromJson(str, int[].class);
        case INT64_ARRAY:
            return gson.fromJson(str, long[].class);
        case IP_ADDR_ARRAY:
            return gson.fromJson(str, String[].class);
        case STRING_ARRAY:
            return gson.fromJson(str, String[].class);
        case UINT16_ARRAY:
            return gson.fromJson(str, int[].class);
        case UINT32_ARRAY:
            return gson.fromJson(str, long[].class);
        case UINT64_ARRAY:
            return gson.fromJson(str, BigInteger[].class);
        case NBOOLEAN_ARRAY:
            return gson.fromJson(str, Boolean[].class);
        case NBYTE_ARRAY:
            return gson.fromJson(str, Byte[].class);
        case NDOUBLE_ARRAY:
            return gson.fromJson(str, Double[].class);
        case NFLOAT_ARRAY:
            return gson.fromJson(str, Float[].class);
        case NINT16_ARRAY:
            return gson.fromJson(str, Short[].class);
        case NINT32_ARRAY:
            return gson.fromJson(str, Integer[].class);
        case NINT64_ARRAY:
            return gson.fromJson(str, Long[].class);
        case NSTRING_ARRAY:
            return gson.fromJson(str, String[].class);
        case NUINT16_ARRAY:
            return gson.fromJson(str, Integer[].class);
        case NUINT32_ARRAY:
            return gson.fromJson(str, Long[].class);
        case NUINT64_ARRAY:
            return gson.fromJson(str, BigInteger[].class);
        default:
            return str;
        }
    }
}
