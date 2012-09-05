package org.lwes;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class DefaultFieldAccessor implements FieldAccessor {
    private String    name;
    private FieldType type;
    private Object    value;
    
    public DefaultFieldAccessor() { }
    
    public DefaultFieldAccessor(String name, FieldType type, Object value) {
        setName(name);
        setType(type);
        setValue(value);
    }
    
    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public FieldType getType() {
        return type;
    }

    protected void setType(FieldType type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    protected void setValue(Object value) {
        this.value = value;
    }
    
    @Override
    public boolean equals(Object obj) {
      return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
      return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
      return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }
}
