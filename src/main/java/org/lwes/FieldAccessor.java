package org.lwes;

public interface FieldAccessor {
    String    getName();
    FieldType getType();
    Object    getValue();
}
