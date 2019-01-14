/*======================================================================*
 * Copyright (c) 2008, Yahoo! Inc. All rights reserved.                 *
 *                                                                      *
 * Licensed under the New BSD License (the "License"); you may not use  *
 * this file except in compliance with the License.  Unless required    *
 * by applicable law or agreed to in writing, software distributed      *
 * under the License is distributed on an "AS IS" BASIS, WITHOUT        *
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     *
 * See the License for the specific language governing permissions and  *
 * limitations under the License. See accompanying LICENSE file.        *
 *======================================================================*/

package org.lwes;

import java.lang.reflect.Array;

import org.lwes.serializer.StringParser;
import org.lwes.util.EncodedString;

/**
 * <p>This class provides a base type for the base types in the event system. acts
 * partially as an interface and partially to provide encapsulation of the
 * TypeIDs used in serialization.
 * </p><p>
 * It also provides a sizeof() type method called getByteSize() used to
 * determine how many bytes must be used to serialize an object of the given
 * type.
 * </p>
 *
 * @author Anthony Molinaro
 */
public class BaseType {

    /**
     * The FieldType of this field, which provides both ESF name and
     * serialization token.
     */
    private FieldType type = null;

    /**
     * The object stored in this type
     */
    private Object typeObject = null;

    /**
     * Is this guy required
     */
    private boolean required;

    /**
     * What is the size restriction on this guy
     */
    private int sizeRestriction;

    /**
     * What the default value for this guy should be.
     */
    private Object defaultValue;

    /**
     * comment describing attribute
     */
    private String comment;

    public BaseType() {
    }

    @Deprecated
    public BaseType(String typeName, byte typeToken) {
        this(typeName, typeToken, null, false, -1);
    }

    @Deprecated
    public BaseType(String typeName, byte typeToken, Object typeObject) {
        this(typeName, typeToken, typeObject, false, -1);
    }

    @Deprecated
    public BaseType(String typeName,
                    byte typeToken,
                    Object typeObject,
                    boolean required,
                    int sizeRestriction) {
        this(typeName, typeToken, typeObject, required, sizeRestriction, null);
    }

    @Deprecated
    public BaseType(String typeName,
                    byte typeToken,
                    Object typeObject,
                    boolean required,
                    int sizeRestriction,
                    Object defaultValue) {
        final FieldType newType = FieldType.byToken(typeToken);
        newType.checkCompatibilityWith(typeObject);
        this.required = required;
        this.sizeRestriction = sizeRestriction;
        this.typeObject = typeObject;
        this.type = newType;
        this.defaultValue = defaultValue;
        if (!typeName.equals(type.name)) {
            throw new IllegalStateException("Inconsistent type name and token: " + typeName + " vs. " + type.name);
        }
    }

    public BaseType(FieldType type) {
        this(type, null, false, -1);
    }

    public BaseType(FieldType type, Object typeObject) {
        this(type, typeObject, false, -1);
    }

    public BaseType(FieldType type,
                    Object typeObject,
                    boolean required,
                    int sizeRestriction) {
        this(type, typeObject, required, sizeRestriction, null);
    }

    public BaseType(FieldType type,
                    Object typeObject,
                    boolean required,
                    int sizeRestriction,
                    Object defaultValue) {
        this(type, typeObject, required, sizeRestriction, defaultValue, null);
    }

    public BaseType(FieldType type,
                    Object typeObject,
                    boolean required,
                    int sizeRestriction,
                    Object defaultValue,
                    String comment) {
        type.checkCompatibilityWith(typeObject);
        this.required = required;
        this.sizeRestriction = sizeRestriction;
        this.type = type;
        this.typeObject = typeObject;
        this.defaultValue = defaultValue;
        this.comment = comment;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    /**
     * use {@link #setType(FieldType)}
     *
     * @param typeName a string that is a valid FieldType name.
     */
    @Deprecated
    public void setTypeName(String typeName) {
        this.type = FieldType.byName(typeName);
    }

    /**
     * use {@link #getType()}
     *
     * @return the name of the FieldType
     */
    @Deprecated
    public String getTypeName() {
        return type.name;
    }

    /**
     * use {@link #setType(FieldType)}
     *
     * @param typeToken byte-code for this FieldType.
     */
    @Deprecated
    public void setTypeToken(byte typeToken) {
        this.type = FieldType.byToken(typeToken);
    }

    public byte getTypeToken() {
        return type.token;
    }

    public void setTypeObject(Object typeObject) throws NoSuchAttributeTypeException {
        type.checkCompatibilityWith(typeObject);
        this.typeObject = typeObject;
    }

    public Object getTypeObject() {
        return typeObject;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public Integer getSizeRestriction() {
        return sizeRestriction;
    }

    public void setSizeRestriction(Integer sizeRestriction) {
        this.sizeRestriction = sizeRestriction;
    }

    public int getNullableArrayByteSize(short encoding) {
        // Size of the array (* n bytes) + 2 bytes for the length number
        int count = 2 + 2; // start with the length of the array + length of bitset
        int arrayLen = Array.getLength(typeObject);
        Object[] objArray = (Object[]) typeObject;
        count += (int) Math.ceil(arrayLen / 8.0); // number of bytes in the bitset
        for (Object o : objArray) {
            if (o != null) {
                switch (type.getComponentType()) {
                    case STRING:
                        // length of each string in bytes + 2 for the length number
                        count += EncodedString.getBytes((String) o, Event.ENCODING_STRINGS[encoding]).length + 2;
                        break;
                    default:
                        count += type.getComponentType().getConstantSize();
                        break;

                }
            }
        }
        return count;
    }

    public int getByteSize(short encoding) {
        if (type.isNullableArray()) {
            return getNullableArrayByteSize(encoding);
        }
        else if (type.isConstantSize()) {
            return type.getConstantSize();
        }
        else {
            switch (type) {
                case STRING:
                /* add size of string plus two bytes for the length */
                    return EncodedString.getBytes((String) typeObject,
                                                  Event.ENCODING_STRINGS[encoding]).length + 2;
                case STRING_ARRAY: {
                    int count = 2; // start with the length of the array
                    String[] anArray = (String[]) typeObject;
                    for (String s : anArray) {
                        if (s != null) {
                            count += EncodedString.getBytes(s, Event.ENCODING_STRINGS[encoding]).length + 2;
                        }
                    }
                    return count;
                }
                case BOOLEAN_ARRAY:
                case BYTE_ARRAY:
                    return Array.getLength(typeObject) + 2;
                case INT16_ARRAY:
                case UINT16_ARRAY:
                    return Array.getLength(typeObject) * 2 + 2;
                case INT32_ARRAY:
                case UINT32_ARRAY:
                case FLOAT_ARRAY:
                case IP_ADDR_ARRAY:
                    return Array.getLength(typeObject) * 4 + 2;
                case INT64_ARRAY:
                case UINT64_ARRAY:
                case DOUBLE_ARRAY:
                    return Array.getLength(typeObject) * 8 + 2;
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
                    throw new IllegalStateException("Type should be handled in the constant-size condition: "+type);
                case NBOOLEAN_ARRAY:
                case NBYTE_ARRAY:
                case NDOUBLE_ARRAY:
                case NFLOAT_ARRAY:
                case NINT16_ARRAY:
                case NINT32_ARRAY:
                case NINT64_ARRAY:
                case NSTRING_ARRAY:
                case NUINT16_ARRAY:
                case NUINT32_ARRAY:
                case NUINT64_ARRAY:
                    throw new IllegalStateException("Type should be handled in the array-type condition: "+type);
            }
        }
        throw new IllegalArgumentException("Unknown size of BaseType " + type.name);
    }

    public int bytesStoreSize(short encoding) {
        /* add size of data plus size of token denoting data type */
        return getByteSize(encoding) + 1;
    }

    public Object parseFromString(String string) throws EventSystemException {
        switch (type) {
            case UINT16:
                return StringParser.fromStringUINT16(string);
            case INT16:
                return StringParser.fromStringINT16(string);
            case UINT32:
                return StringParser.fromStringUINT32(string);
            case INT32:
                return StringParser.fromStringINT32(string);
            case INT64:
                return StringParser.fromStringINT64(string);
            case UINT64:
                return StringParser.fromStringUINT64(string);
            case STRING:
                return StringParser.fromStringSTRING(string);
            case IPADDR:
                return StringParser.fromStringIPADDR(string);
            case BOOLEAN:
                return StringParser.fromStringBOOLEAN(string);
            case BYTE:
                return StringParser.fromStringBYTE(string);
            case DOUBLE:
                return StringParser.fromStringDOUBLE(string);
            case FLOAT:
                return StringParser.fromStringFLOAT(string);
            case BOOLEAN_ARRAY:
            case INT16_ARRAY:
            case INT32_ARRAY:
            case INT64_ARRAY:
            case FLOAT_ARRAY:
            case DOUBLE_ARRAY:
            case IP_ADDR_ARRAY:
            case STRING_ARRAY:
            case UINT16_ARRAY:
            case UINT32_ARRAY:
            case UINT64_ARRAY:
            case BYTE_ARRAY:
            case NBOOLEAN_ARRAY:
            case NBYTE_ARRAY:
            case NDOUBLE_ARRAY:
            case NFLOAT_ARRAY:
            case NINT16_ARRAY:
            case NINT32_ARRAY:
            case NINT64_ARRAY:
            case NSTRING_ARRAY:
            case NUINT16_ARRAY:
            case NUINT32_ARRAY:
            case NUINT64_ARRAY:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        throw new NoSuchAttributeTypeException("Unknown size of BaseType "
                                               + type.name);
    }

    public BaseType cloneBaseType() {
        return new BaseType(type, typeObject, required, sizeRestriction, defaultValue, comment);
    }

    @Override
    public String toString() {
        return typeObject.toString();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


}
