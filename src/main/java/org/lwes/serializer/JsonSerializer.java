package org.lwes.serializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwes.BaseType;
import org.lwes.TypeValue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonSerializer {

    private static final int JSON_SERIALIZER_VERSION = 1;
    
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
        gson = new GsonBuilder().disableHtmlEscaping().create();
    }
    
    public String json(String eventName, Map<String,BaseType> attrs){
        return gson.toJson(typedContainer(eventName, attrs));
    }
    
    public String unTypedJson(String eventName, Map<String, BaseType> attrs){
        return gson.toJson(unTypedContainer(eventName, attrs));
    }
    
    private Map<String, Object> basicContainer(String eventName){
        Map<String, Object> container = new HashMap<String, Object>();
        container.put("name", eventName);
        container.put("version", JSON_SERIALIZER_VERSION);
        return container;
    }
    
    private Map<String, Object> unTypedContainer(String eventName, Map<String, BaseType> attrs){
        Map<String, Object> container = basicContainer(eventName);
        container.put("attributes", getUnTypedAttributes(eventName, attrs));
        return container;
    }
    
    private Map<String, Object> typedContainer(String eventName, Map<String, BaseType> attrs){
        Map<String, Object> container = basicContainer(eventName);
        container.put("typed", getTypedAttributes(attrs));
        return container;
    }

    public Object getTypedAttributes(Map<String, BaseType> attrs) {
        Map<String,Object> typedAttrs = new HashMap<String, Object>();
        for(Entry<String, BaseType> attr : attrs.entrySet())
            typedAttrs.put(attr.getKey(), new TypeValue(attr.getValue().getType().typeDesc, attr.getValue().stringyfy()));
        
        return typedAttrs;
    }
    
    public Object getUnTypedAttributes(String eventName, Map<String, BaseType> attributes) {
        Map<String, Object> unTypedAttributes = new HashMap<String, Object>();
        unTypedAttributes.put("EventName", eventName);
        for(Entry<String, BaseType> attr : attributes.entrySet())
            unTypedAttributes.put(attr.getKey(),attr.getValue().getTypeObject());
        
        return unTypedAttributes;
    }
}
