package org.lwes.serializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwes.BaseType;
import org.lwes.TypeValue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonSerializer {

    private static JsonSerializer instance;
    
    private Gson gson;
    
    public static JsonSerializer getInstance(){
        if(instance == null){
            synchronized (JsonSerializer.class) {
                //Double-checked locking
                if(instance == null)
                    instance = new JsonSerializer();
            }
        }
        return instance;
    }
    
    private JsonSerializer(){
        GsonBuilder bldr = new GsonBuilder();
        gson = bldr.create();
    }
    
    public String toJson(String eventName, Map<String,BaseType> attrs){
        Map<String, Object> jsonContainer = jsonRepresentation(eventName, attrs);
        return gson.toJson(jsonContainer);
    }
    
    public String toTypedJson(String eventName, Map<String,BaseType> attrs){
        Map<String, Object> jsonContainer = typedJsonRepresentation(eventName, attrs);
        return gson.toJson(jsonContainer);
    }
    
    public Map<String, Object> jsonRepresentation(String name, Map<String, BaseType> attrs){
        Map<String, Object> container = new HashMap<String, Object>();
        container.put("name", name);
        container.put("attributes", getAttributes(name, attrs));
        return container;
    }
    
    public Map<String, Object> typedJsonRepresentation(String name, Map<String, BaseType> attrs){
        Map<String, Object> container = new HashMap<String, Object>();
        container.put("name", name);
        container.put("typed", getTypedAttributes(attrs));
        return container;
    }

    private Object getTypedAttributes(Map<String, BaseType> attrs) {
        Map<String,Object> typedAttrs = new HashMap<String, Object>();
        for(Entry<String, BaseType> attr : attrs.entrySet()){
            typedAttrs.put(attr.getKey(), new TypeValue(attr.getValue().getType().name, gson.toJson(attr.getValue().getTypeObject())));
        }
        return typedAttrs;
    }

    private Object getAttributes(String name, Map<String, BaseType> attrs) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("EventName", name);
        for(Entry<String, BaseType> attr : attrs.entrySet()){
            attributes.put(attr.getKey(),attr.getValue().getTypeObject());
        }
        return attributes;
    }
    
}
