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
    
    private Map<String, Object> typedContainer(String name, Map<String, BaseType> attrs){
        Map<String, Object> container = new HashMap<String, Object>();
        container.put("name", name);
        container.put("version", JSON_SERIALIZER_VERSION);
        container.put("typed", getTypedAttributes(attrs));
        return container;
    }

    public Object getTypedAttributes(Map<String, BaseType> attrs) {
        Map<String,Object> typedAttrs = new HashMap<String, Object>();
        for(Entry<String, BaseType> attr : attrs.entrySet())
            typedAttrs.put(attr.getKey(), new TypeValue(attr.getValue().getType().name, attr.getValue().stringyfy()));
        
        return typedAttrs;
    }
}
