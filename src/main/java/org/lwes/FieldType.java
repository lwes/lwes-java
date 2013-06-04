/*======================================================================*
 * Licensed under the New BSD License (the "License"); you may not use  *
 * this file except in compliance with the License.  Unless required    *
 * by applicable law or agreed to in writing, software distributed      *
 * under the License is distributed on an "AS IS" BASIS, WITHOUT        *
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     *
 * See the License for the specific language governing permissions and  *
 * limitations under the License. See accompanying LICENSE file.        *
 *======================================================================*/
package org.lwes;

import java.math.BigInteger;
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
    FLOAT(0x0B, "float", (float) 0.0),
    DOUBLE(0x0C, "double", 0.0),

    // Primitive Arrays
    UINT16_ARRAY(0x81, "[Luint16", new short[0]),
    INT16_ARRAY(0x82, "[Lint16", new int[0]),
    UINT32_ARRAY(0x83, "[Luint32", new int[0]),
    INT32_ARRAY(0x84, "[Lint32", new long[0]),
    STRING_ARRAY(0x85, "[Lstring", new String[0]),
    IP_ADDR_ARRAY(0x86, "[Lip_addr", new IPAddress[0]),
    INT64_ARRAY(0x87, "[Lint64", new long[0]),
    UINT64_ARRAY(0x88, "[Luint64", new BigInteger[0]),
    BOOLEAN_ARRAY(0x89, "[Lboolean", new boolean[0]),
    BYTE_ARRAY(0x8A, "[Lbyte", new byte[0]),
    FLOAT_ARRAY(0x8B, "[Lfloat", new float[0]),
    DOUBLE_ARRAY(0x8C, "[Ldouble", new double[0]),

    // Nullable, object backed arrays
    NUINT16_ARRAY(0x8D, "[LNuint16", new Short[0]),
    NINT16_ARRAY(0x8E, "[LNint16", new Integer[0]),
    NUINT32_ARRAY(0x8F, "[LNuint32", new Integer[0]),
    NINT32_ARRAY(0x90, "[LNint32", new Long[0]),
    NSTRING_ARRAY(0x91, "[LString", new String[0]),
    // N_IP_ADDR_ARRAY not implemented... 0x92
    NINT64_ARRAY(0x93, "[LNint64", new Long[0]),
    NUINT64_ARRAY(0x94, "[LNuint64", new BigInteger[0]),
    NBOOLEAN_ARRAY(0x95, "[LBoolean", new Boolean[0]),
    NBYTE_ARRAY(0x96, "[LByte", new Byte[0]),
    NFLOAT_ARRAY(0x97, "[LFloat", new Float[0]),
    NDOUBLE_ARRAY(0x98, "[LDouble", new Double[0]);

    public final byte token;
    public final String name;
    private Integer constantSize;
    private final boolean array, nullableArray;
    private FieldType componentType, arrayType, nullableArrayType;
    private final Object defaultValue;
    private static final FieldType[] TYPES_BY_TOKEN = new FieldType[256];
    private static final Map<String, FieldType>    TYPES_BY_NAME;

    private FieldType(int token, String name) {
        this(token, name, null);
    }

    private FieldType(int token, String name, Object defaultValue) {
        this.token = (byte) token;
        this.name = name;
        this.array = name.startsWith("[L");
        this.nullableArray = this.array && name().startsWith("N");
        this.defaultValue = defaultValue;
    }

    static {
        TYPES_BY_NAME        = new HashMap<String, FieldType>();
        for (FieldType type : values()) {
            TYPES_BY_TOKEN[type.token & 0xff] = type;
            TYPES_BY_NAME.put(type.name, type);
            
            if (type.isArray()) {
                // This will fail if our naming becomes inconsistent or a type starts with N.
                String name = type.name();
                name = name.replace("_ARRAY", "");
                name = name.replaceFirst("^N", "");
                name = name.replace("IP_ADDR", "IPADDR");  // due to formatting inconsistency
                final FieldType componentType = valueOf(name);
                type.componentType = componentType;
                if (type.isNullableArray()) {
                    componentType.nullableArrayType = type;
                } else {
                    componentType.arrayType = type;
                }
            }
        }
        BOOLEAN.constantSize = 1;
        BYTE.constantSize    = 1;
        INT16.constantSize   = 2;
        UINT16.constantSize  = 2;
        FLOAT.constantSize   = 4;
        INT32.constantSize   = 4;
        IPADDR.constantSize  = 4;
        UINT32.constantSize  = 4;
        INT64.constantSize   = 8;
        UINT64.constantSize  = 8;
        DOUBLE.constantSize  = 8;
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

    public boolean isNullableArray() {
        return nullableArray;
    }

    public boolean isArray() {
        return array;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public FieldType getNullableArrayType() {
        if (nullableArrayType == null) {
            if (isArray()) {
                throw new IllegalStateException(
                    "Multidimensional arrays are not supported; " + this + ".getArrayType() unsupported");
            } else {
                throw new IllegalStateException("Unsupported type: " + this);
            }
        }
        return nullableArrayType;
    }

    public FieldType getArrayType() {
        if (arrayType == null) {
            if (isArray()) {
                throw new IllegalStateException(
                    "Multidimensional arrays are not supported; " + this + ".getArrayType() unsupported");
            } else {
                throw new IllegalStateException("Unsupported type: " + this);
            }
        }
        return arrayType;
    }

    public FieldType getComponentType() {
      if (componentType == null) {
        throw new IllegalStateException(
            "Only array types provide component types " + this + ".getComponentType() unsupported");
      }
      return componentType;
    }

    public boolean isConstantSize() {
        return constantSize != null;
    }

    public int getConstantSize() {
        if (constantSize == null) {
            throw new IllegalStateException("Type "+this+" does not have a constant size");
        } else {
            return constantSize;
        }
    }

    public boolean isCompatibleWith(Object value) {
        if (value == null) {
            return true;
        }
        switch (this) {
            case BOOLEAN:
                return value instanceof Boolean;
            case BYTE:
                return value instanceof Byte;
            case DOUBLE:
                return value instanceof Double;
            case FLOAT:
                return value instanceof Float;
            case INT16:
                return value instanceof Short;
            case INT32:
                return value instanceof Integer;
            case INT64:
                return value instanceof Long;
            case IPADDR:
                return value instanceof IPAddress;
            case STRING:
                return value instanceof String;
            case UINT16:
                return value instanceof Integer;
            case UINT32:
                return value instanceof Long;
            case UINT64:
                return value instanceof BigInteger;
            case BOOLEAN_ARRAY:
                return value instanceof boolean[];
            case BYTE_ARRAY:
                return value instanceof byte[];
            case DOUBLE_ARRAY:
                return value instanceof double[];
            case FLOAT_ARRAY:
                return value instanceof float[];
            case INT16_ARRAY:
                return value instanceof short[];
            case INT32_ARRAY:
                return value instanceof int[];
            case INT64_ARRAY:
                return value instanceof long[];
            case IP_ADDR_ARRAY:
                return value instanceof IPAddress[];
            case STRING_ARRAY:
                return value instanceof String[];
            case UINT16_ARRAY:
                return value instanceof int[];
            case UINT32_ARRAY:
                return value instanceof long[];
            case UINT64_ARRAY:
                return value instanceof BigInteger[];
            case NBOOLEAN_ARRAY:
                return value instanceof Boolean[];
            case NBYTE_ARRAY:
                return value instanceof Byte[];
            case NDOUBLE_ARRAY:
                return value instanceof Double[];
            case NFLOAT_ARRAY:
                return value instanceof Float[];
            case NINT16_ARRAY:
                return value instanceof Short[];
            case NINT32_ARRAY:
                return value instanceof Integer[];
            case NINT64_ARRAY:
                return value instanceof Long[];
            case NSTRING_ARRAY:
                return value instanceof String[];
            case NUINT16_ARRAY:
                return value instanceof Integer[];
            case NUINT32_ARRAY:
                return value instanceof Long[];
            case NUINT64_ARRAY:
                return value instanceof BigInteger[];
        }
        throw new IllegalStateException("Unsupported type: " + this);
    }
}
