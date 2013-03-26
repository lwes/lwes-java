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
    NSHORT(0x0D, "Short", (short) 0),
    NINTEGER(0x0E, "Integer", 0),
    NLONG(0x0F, "Long", 0),
    NBIGINT(0x10, "BigInt", 0),
    NDOUBLE(0x11, "Double", 0.0),
    NFLOAT(0x12, "Float", 0.0),
    NBYTE(0x13, "Byte", 0),
    NBOOLEAN(0x14, "Boolean", true),

    // Primitive Arrays
    UINT16_ARRAY(0x81, "[Luint16", new short[0]),
    INT16_ARRAY(0x82, "[Lint16", new int[0]),
    UINT32_ARRAY(0x83, "[Luint32", new int[0]),
    INT32_ARRAY(0x84, "[Lint32", new long[0]),
    STRING_ARRAY(0x85, "[Lstring", new String[0]),
    IP_ADDR_ARRAY(0x86, "[Lip_addr", new IPAddress[0]),
    INT64_ARRAY(0x87, "[Lint64", new BigInteger[0]),
    UINT64_ARRAY(0x88, "[Luint64", new long[0]),
    BOOLEAN_ARRAY(0x89, "[Lboolean", new boolean[0]),
    BYTE_ARRAY(0x8A, "[Lbyte", new byte[0]),
    FLOAT_ARRAY(0x8B, "[Lfloat", new float[0]),
    DOUBLE_ARRAY(0x8C, "[Ldouble", new double[0]),

    // Nullable, object backed arrays
    NSHORT_ARRAY(0x8D, "[LShort", new Short[0]),
    NINTEGER_ARRAY(0x8E, "[LInteger", new Integer[0]),
    NLONG_ARRAY(0x8F, "[LLong", new Long[0]),
    NBIGINT_ARRAY(0x90, "[LBigInt", new BigInteger[0]),
    NBOOLEAN_ARRAY(0x91, "[LBoolean", new Boolean[0]),
    NBYTE_ARRAY(0x92, "[LByte", new Byte[0]),
    NFLOAT_ARRAY(0x93, "[LFloat", new Float[0]),
    NDOUBLE_ARRAY(0x94, "[LDouble", new Double[0]),
    NSTRING_ARRAY(0x95, "[LString", new String[0]);

    public final byte token;
    public final String name;
    private final boolean array;
    private final Object defaultValue;
    private static final FieldType[] TYPES_BY_TOKEN = new FieldType[256];
    private static final Map<String, FieldType> TYPES_BY_NAME = new HashMap<String, FieldType>();

    private FieldType(int token, String name) {
        this(token, name, null);
    }

    private FieldType(int token, String name, Object defaultValue) {
        this.token = (byte) token;
        this.name = name;
        this.array = name.startsWith("[L");
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

    public boolean isNullableArray() {
        return (this == NSHORT_ARRAY || this == NDOUBLE_ARRAY || this == NFLOAT_ARRAY ||
                this == NBIGINT_ARRAY || this == NBOOLEAN_ARRAY || this == NBYTE_ARRAY ||
                this == NINTEGER_ARRAY || this == NLONG_ARRAY);
    }

    public boolean isArray() {
        return array;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public FieldType getNullableArrayType() {
        switch (this) {
            case BOOLEAN:
                return NBOOLEAN_ARRAY;
            case BYTE:
                return NBYTE_ARRAY;
            case DOUBLE:
                return NDOUBLE_ARRAY;
            case FLOAT:
                return NFLOAT_ARRAY;
            case INT16:
                return NSHORT_ARRAY;
            case INT32:
                return NINTEGER_ARRAY;
            case INT64:
                return NLONG_ARRAY;
            case UINT16:
                return NINTEGER_ARRAY;
            case UINT32:
                return NLONG_ARRAY;
            case UINT64:
                return NBIGINT_ARRAY;
            case STRING:
                return NSTRING_ARRAY;
        }
        throw new IllegalStateException("Unsupported type: " + this);
    }

    public FieldType getArrayType() {
        switch (this) {
            case BOOLEAN:
                return BOOLEAN_ARRAY;
            case BYTE:
                return BYTE_ARRAY;
            case DOUBLE:
                return DOUBLE_ARRAY;
            case FLOAT:
                return FLOAT_ARRAY;
            case INT16:
                return INT16_ARRAY;
            case INT32:
                return INT32_ARRAY;
            case INT64:
                return INT64_ARRAY;
            case IPADDR:
                return IP_ADDR_ARRAY;
            case STRING:
                return STRING_ARRAY;
            case UINT16:
                return UINT16_ARRAY;
            case UINT32:
                return UINT32_ARRAY;
            case UINT64:
                return UINT64_ARRAY;
            case NINTEGER:
                return NINTEGER_ARRAY;
            case NLONG:
                return NLONG_ARRAY;
            case NDOUBLE:
                return NDOUBLE_ARRAY;
            case NFLOAT:
                return NFLOAT_ARRAY;
            case NSHORT:
                return NSHORT_ARRAY;
            case NBOOLEAN:
                return NBOOLEAN_ARRAY;
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
                throw new IllegalStateException(
                        "Multidimensional arrays are not supported; " + this + ".getArrayType() unsupported");
        }
        throw new IllegalStateException("Unsupported type: " + this);
    }

    public FieldType getComponentType() {
        switch (this) {
            case BOOLEAN:
            case BYTE:
            case DOUBLE:
            case FLOAT:
            case INT16:
            case INT32:
            case INT64:
            case IPADDR:
            case STRING:
            case UINT16:
            case UINT32:
            case UINT64:
            case NBOOLEAN:
            case NBYTE:
            case NDOUBLE:
            case NFLOAT:
            case NINTEGER:
            case NLONG:
                throw new IllegalStateException(
                        "Only array types provide component types " + this + ".getComponentType() unsupported");

            case NBOOLEAN_ARRAY:
                return NBOOLEAN;
            case BOOLEAN_ARRAY:
                return BOOLEAN;
            case NBYTE_ARRAY:
                return NBYTE;
            case BYTE_ARRAY:
                return BYTE;
            case NDOUBLE_ARRAY:
                return NDOUBLE;
            case DOUBLE_ARRAY:
                return DOUBLE;
            case NFLOAT_ARRAY:
                return NFLOAT;
            case FLOAT_ARRAY:
                return FLOAT;
            case NSHORT_ARRAY:
                return NSHORT;
            case INT16_ARRAY:
                return INT16;
            case NINTEGER_ARRAY:
                return NINTEGER;
            case INT32_ARRAY:
                return INT32;
            case NLONG_ARRAY:
                return NLONG;
            case INT64_ARRAY:
                return INT64;
            case IP_ADDR_ARRAY:
                return IPADDR;
            case STRING_ARRAY:
                return STRING;
            case UINT16_ARRAY:
                return UINT16;
            case UINT32_ARRAY:
                return UINT32;
            case UINT64_ARRAY:
                return UINT64;
            case NBIGINT_ARRAY:
                return NBIGINT;

        }
        throw new IllegalStateException("Unsupported type: " + this);
    }

    public boolean isConstantSize() {
        switch (this) {
            case BOOLEAN:
            case BYTE:
            case DOUBLE:
            case FLOAT:
            case INT16:
            case INT32:
            case INT64:
            case IPADDR:
            case UINT16:
            case UINT32:
            case UINT64:
                return true;
            default:
                return false;
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
        }
        throw new IllegalStateException("Unsupported type: " + this);
    }
}
