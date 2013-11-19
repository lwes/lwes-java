package org.lwes.serializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.lwes.Event;
import org.lwes.FieldType;
import org.lwes.TypeValue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
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
                   TypeValue tv = element.getValue();
                   FieldType ft = FieldType.byName(tv.getType());
                   if (ft==null)
                       continue;
                   
                   if(tv.getValue() instanceof String)
                       e.set(element.getKey(),
                               ft,getObjectForType(ft, (String)tv.getValue(), null));
                   else if (tv.getValue() instanceof String[])
                       e.set(element.getKey(), ft, getObjectForType(ft, null, (String[])tv.getValue()));
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
        Set<Entry<String, JsonElement>> typedElements = typedContainer.entrySet();
        Map<String, TypeValue> typedValues = new HashMap<String, TypeValue>();
        for(Entry<String, JsonElement> element : typedElements){
            String key = element.getKey();
            JsonObject tv = (JsonObject)element.getValue();
            String type = tv.get("type").getAsString();
            JsonElement jsonValue = tv.get("value");
            if(key==null || element.getValue()==null)
                continue;
            if(jsonValue instanceof JsonArray){
                typedValues.put(element.getKey(), new TypeValue(type, parseJSONArray((JsonArray)jsonValue)));
            }else{
                typedValues.put(element.getKey(), new TypeValue(type, jsonValue.getAsString()));
            }
        }
        return typedValues;
    }
    
    String[] parseJSONArray(JsonArray json){
        String[] strArr = new String[json.size()];
        for(int i=0;i<json.size();i++)
            if(json.get(i) instanceof JsonNull)
                strArr[i] = null;
            else
                strArr[i] = json.get(i).getAsString();
        
        return strArr;
    }
    
    Object getObjectForType(FieldType ft, String str, String[] strArr) {
        
        switch (ft) {
            case BOOLEAN:
                return StringParser.fromStringBOOLEAN(str);
            case BYTE:
                return StringParser.fromStringBYTE(str);
            case DOUBLE:
                return StringParser.fromStringDOUBLE(str);
            case FLOAT:
                return StringParser.fromStringFLOAT(str);
            case INT16:
                return StringParser.fromStringINT16(str);
            case INT32:
                return StringParser.fromStringINT32(str);
            case INT64:
                return StringParser.fromStringINT64(str);
            case IPADDR:
                return StringParser.fromStringIPADDR(str);
            case STRING:
                return StringParser.fromStringSTRING(str);
            case UINT16:
                return StringParser.fromStringUINT16(str);
            case UINT32:
                return StringParser.fromStringUINT32(str);
            case UINT64:
                return StringParser.fromStringUINT64(str);
            case BOOLEAN_ARRAY:
                return StringParser.fromStringBOOLEANArray(strArr);
            case BYTE_ARRAY:
                return StringParser.fromStringBYTEArray(strArr);
            case DOUBLE_ARRAY:
                return StringParser.fromStringDOUBLEArray(strArr);
            case FLOAT_ARRAY:
                return StringParser.fromStringFLOATArray(strArr);
            case INT16_ARRAY:
                return StringParser.fromStringINT16Array(strArr);
            case INT32_ARRAY:
                return StringParser.fromStringINT32Array(strArr);
            case INT64_ARRAY:
                return StringParser.fromStringINT64Array(strArr);
            case IP_ADDR_ARRAY:
                return StringParser.fromStringIPADDRArray(strArr);
            case STRING_ARRAY:
                return StringParser.fromStringSTRINGArray(strArr);
            case UINT16_ARRAY:
                return StringParser.fromStringUINT16Array(strArr);
            case UINT32_ARRAY:
                return StringParser.fromStringUINT32Array(strArr);
            case UINT64_ARRAY:
                return StringParser.fromStringUINT64Array(strArr);
            case NBOOLEAN_ARRAY:
                return StringParser.fromStringBOOLEANNArray(strArr);
            case NBYTE_ARRAY:
                return StringParser.fromStringBYTENArray(strArr);
            case NDOUBLE_ARRAY:
                return StringParser.fromStringDOUBLENArray(strArr);
            case NFLOAT_ARRAY:
                return StringParser.fromStringFLOATNArray(strArr);
            case NINT16_ARRAY:
                return StringParser.fromStringINT16NArray(strArr);
            case NINT32_ARRAY:
                return StringParser.fromStringINT32NArray(strArr);
            case NINT64_ARRAY:
                return StringParser.fromStringINT64NArray(strArr);
            case NSTRING_ARRAY:
                return StringParser.fromStringSTRINGNArray(strArr);
            case NUINT16_ARRAY:
                return StringParser.fromStringUINT16NArray(strArr);
            case NUINT32_ARRAY:
                return StringParser.fromStringUINT32NArray(strArr);
            case NUINT64_ARRAY:
                return StringParser.fromStringUINT64NArray(strArr);
            default:
                return str;
        }
    }
    
}
