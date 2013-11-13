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
    
    public String json(String name, Map<String, BaseType> attrs){
        Map<String, Object> jsonContainer = untypedContainer(name, attrs);
        jsonContainer.put("typed", getTypedAttributes(attrs));
        return gson.toJson(jsonContainer);
    }
    
    public String unTypedJson(String eventName, Map<String,BaseType> attrs){
        return gson.toJson(untypedContainer(eventName, attrs));
    }
    
    public String typedJson(String eventName, Map<String,BaseType> attrs){
        return gson.toJson(typedContainer(eventName, attrs));
    }
    
    private Map<String, Object> basicContainer(String name){
        Map<String, Object> container = new HashMap<String, Object>();
        container.put("name", name);
        return container;
    }
    
    private Map<String, Object> untypedContainer(String name, Map<String, BaseType> attrs){
        Map<String, Object> container = basicContainer(name);
        container.put("attributes", getAttributes(name, attrs));
        return container;
    }
    
    private Map<String, Object> typedContainer(String name, Map<String, BaseType> attrs){
        Map<String, Object> container = basicContainer(name);
        container.put("typed", getTypedAttributes(attrs));
        return container;
    }

    private Object getTypedAttributes(Map<String, BaseType> attrs) {
        Map<String,Object> typedAttrs = new HashMap<String, Object>();
        for(Entry<String, BaseType> attr : attrs.entrySet())
            typedAttrs.put(attr.getKey(), new TypeValue(attr.getValue().getType().name, gson.toJson(attr.getValue().getTypeObject())));
        
        return typedAttrs;
    }

    private Object getAttributes(String name, Map<String, BaseType> attrs) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("EventName", name);
        for(Entry<String, BaseType> attr : attrs.entrySet())
            attributes.put(attr.getKey(),attr.getValue().getTypeObject());
        
        return attributes;
    }
    
}
