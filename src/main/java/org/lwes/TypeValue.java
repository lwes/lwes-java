package org.lwes;

public class TypeValue {

    private String type;
    private Object value;
    public String getType() {
        return type;
    }
    public Object getValue() {
        return value;
    }
    public TypeValue(String type, Object value) {
        super();
        this.type = type;
        this.value = value;
    }
    
}
