package org.lwes;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.lwes.util.IPAddress;

public enum FieldType {
    UINT16(0x01, "uint16", 0),
    INT16(0x02, "int16", (short) 0),
    UINT32(0x03, "uint32", 0L),
    INT32(0x04, "int32", 0),
    STRING(0x05, "string", ""),
    IPADDR(0x06, "ip_addr", new IPAddress()),
    INT64(0x07, "int64", 0L),
    UINT64(0x08, "uint64", BigInteger.ZERO),
    BOOLEAN(0x09, "boolean", true),
    BYTE(0x0A, "byte", (byte) 0),
    FLOAT(0x0B, "float"),
    DOUBLE(0x0C, "double"),
    UINT16_ARRAY(0x81, "[Luint16"),
    INT16_ARRAY(0x82, "[Lint16"),
    UINT32_ARRAY(0x83, "[Luint32"),
    INT32_ARRAY(0x84, "[Lint32"),
    STRING_ARRAY(0x85, "[Lstring"),
    IP_ADDR_ARRAY(0x86, "[Lip_addr"),
    INT64_ARRAY(0x87, "[Lint64"),
    UINT64_ARRAY(0x88, "[Luint64"),
    BOOLEAN_ARRAY(0x89, "[Lboolean"),
    BYTE_ARRAY(0x8A, "[Lbyte"),
    FLOAT_ARRAY(0x8B, "[Lfloat"),
    DOUBLE_ARRAY(0x8C, "[Ldouble");

    public final byte                           token;
    public final String                         name;
    private final boolean                       array;
    private final Object                        defaultValue;
    private static final FieldType[]            TYPES_BY_TOKEN = new FieldType[256];
    private static final Map<String, FieldType> TYPES_BY_NAME  = new HashMap<String, FieldType>();

    private FieldType(int token, String name) {
        this(token, name, null);
    }
    
    private FieldType(int token, String name, Object defaultValue) {
        this.token        = (byte) token;
        this.name         = name;
        this.array        = name.startsWith("[L");
        this.defaultValue = defaultValue;
    }

    static {
        for (FieldType type : values()) {
            TYPES_BY_TOKEN[type.token & 0xff] = type;
            TYPES_BY_NAME.put(type.name, type);
        }
    }

    public static FieldType byToken(byte token) {
        final FieldType type = TYPES_BY_TOKEN[token & 0xff];
        if (type == null) {
            throw new IllegalArgumentException("Bad token: " + token);
        }
        return type;
    }

    public static FieldType byName(String name) {
        final FieldType type = TYPES_BY_NAME.get(name);
        if (type == null) {
            throw new IllegalArgumentException("Bad field name: " + name);
        }
        return type;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isArray() {
        return array;
    }
    
    public Object getDefaultValue() {
        return defaultValue;
    }

    public FieldType getArrayType() {
        switch (this) {
            case BOOLEAN: return BOOLEAN_ARRAY;
            case BYTE:    return BYTE_ARRAY;
            case DOUBLE:  return DOUBLE_ARRAY;
            case FLOAT:   return FLOAT_ARRAY;
            case INT16:   return INT16_ARRAY;
            case INT32:   return INT32_ARRAY;
            case INT64:   return INT64_ARRAY;
            case IPADDR:  return IP_ADDR_ARRAY;
            case STRING:  return STRING_ARRAY;
            case UINT16:  return UINT16_ARRAY;
            case UINT32:  return UINT32_ARRAY;
            case UINT64:  return UINT64_ARRAY;
            case BOOLEAN_ARRAY:
            case BYTE_ARRAY:
            case DOUBLE_ARRAY:
            case FLOAT_ARRAY:
            case INT16_ARRAY:
            case INT32_ARRAY:
            case INT64_ARRAY:
            case IP_ADDR_ARRAY:
            case STRING_ARRAY:
            case UINT16_ARRAY:
            case UINT32_ARRAY:
            case UINT64_ARRAY:
                throw new IllegalStateException("Multidimensional arrays are not supported; "+this+".getArrayType() unsupported");
        }
        throw new IllegalStateException("Unsupported type: "+this);
    }

    public boolean isCompatibleWith(Object value) {
        if (value == null) return true;
        switch (this) {
            case BOOLEAN:       return value instanceof Boolean;
            case BYTE:          return value instanceof Byte;
            case DOUBLE:        return value instanceof Double;
            case FLOAT:         return value instanceof Float;
            case INT16:         return value instanceof Short;
            case INT32:         return value instanceof Integer;
            case INT64:         return value instanceof Long;
            case IPADDR:        return value instanceof IPAddress;
            case STRING:        return value instanceof String;
            case UINT16:        return value instanceof Integer;
            case UINT32:        return value instanceof Long;
            case UINT64:        return value instanceof BigInteger;
            case BOOLEAN_ARRAY: return value instanceof boolean[];
            case BYTE_ARRAY:    return value instanceof byte[];
            case DOUBLE_ARRAY:  return value instanceof double[];
            case FLOAT_ARRAY:   return value instanceof float[];
            case INT16_ARRAY:   return value instanceof short[];
            case INT32_ARRAY:   return value instanceof int[];
            case INT64_ARRAY:   return value instanceof long[];
            case IP_ADDR_ARRAY: return value instanceof IPAddress[];
            case STRING_ARRAY:  return value instanceof String[];
            case UINT16_ARRAY:  return value instanceof int[];
            case UINT32_ARRAY:  return value instanceof long[];
            case UINT64_ARRAY:  return value instanceof BigInteger[];
        }
        throw new IllegalStateException("Unsupported type: "+this);
    }
}
