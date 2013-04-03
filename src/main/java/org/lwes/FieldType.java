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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
    private final boolean array;
    private final Object defaultValue;
    private static final FieldType[] TYPES_BY_TOKEN = new FieldType[256];
    private static final Map<String, FieldType> TYPES_BY_NAME = new HashMap<String, FieldType>();
    private static final Map<FieldType, FieldType> COMPONENTS = new EnumMap<FieldType, FieldType>(FieldType.class);

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
            
            if (type.name().endsWith("_ARRAY")) {
              // This will fail if our naming becomes inconsistent or a type starts with N.
              String name = type.name();
              name = name.replace("_ARRAY", "");
              name = name.replaceFirst("^N", "");
              name = name.replace("IP_ADDR", "IPADDR");  // due to formatting inconsistency
              COMPONENTS.put(type, valueOf(name));
            }
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
        return (this == NUINT16_ARRAY || this == NDOUBLE_ARRAY || this == NFLOAT_ARRAY ||
                this == NUINT64_ARRAY || this == NBOOLEAN_ARRAY || this == NBYTE_ARRAY ||
                this == NUINT32_ARRAY || this == NINT64_ARRAY || this == NSTRING_ARRAY ||
                this == NINT16_ARRAY || this == NINT32_ARRAY);
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
                return NUINT16_ARRAY;
            case INT32:
                return NUINT32_ARRAY;
            case INT64:
                return NINT64_ARRAY;
            case UINT16:
                return NUINT32_ARRAY;
            case UINT32:
                return NINT64_ARRAY;
            case UINT64:
                return NUINT64_ARRAY;
            case STRING:
                return NSTRING_ARRAY;
        }
        throw new IllegalStateException("Unsupported type: " + this);
    }

    public FieldType getArrayType() {
        for (Entry<FieldType,FieldType> entry : COMPONENTS.entrySet()) {
          if (this == entry.getValue()) {
            return entry.getKey();
          }
        }
        if (COMPONENTS.containsKey(this)) {
          throw new IllegalStateException(
              "Multidimensional arrays are not supported; " + this + ".getArrayType() unsupported");
        } else {
          throw new IllegalStateException("Unsupported type: " + this);
        }
    }

    public FieldType getComponentType() {
      final FieldType component = COMPONENTS.get(this);
      if (component == null) {
        throw new IllegalStateException(
            "Only array types provide component types " + this + ".getComponentType() unsupported");
      }
      return component;
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
