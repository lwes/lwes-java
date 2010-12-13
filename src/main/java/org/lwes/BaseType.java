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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwes.serializer.StringParser;
import org.lwes.util.EncodedString;
import org.lwes.util.IPAddress;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.List;

/**
 * This class provides a base type for the base types in the event system. acts
 * partially as an interface and partially to provide encapsulation of the
 * TypeIDs used in serialization.
 * <p/>
 * It also provides a sizeof() type method called getByteSize() used to
 * determine how many bytes must be used to serialize an object of the given
 * type.
 *
 * @author Anthony Molinaro
 */
public class BaseType {

    private static transient Log log = LogFactory.getLog(BaseType.class);

    /**
     * The name of this type used in the ESF file
     */
    String typeName = null;

    /**
     * The type token used during serialization
     */
    byte typeToken = TypeID.UNDEFINED_TOKEN;

    /**
     * The object stored in this type
     */
    Object typeObject = null;

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

    public BaseType() {
    }

    public BaseType(String typeName, byte typeToken) {
        this(typeName, typeToken, null, false, -1);
    }

    public BaseType(String typeName, byte typeToken, Object typeObject) {
        this(typeName, typeToken, typeObject, false, -1);
    }

    public BaseType(String typeName,
                    byte typeToken,
                    Object typeObject,
                    boolean required,
                    int sizeRestriction) {
        this(typeName, typeToken, typeObject, required, sizeRestriction, null);
    }

    public BaseType(String typeName,
                    byte typeToken,
                    Object typeObject,
                    boolean required,
                    int sizeRestriction,
                    Object defaultValue) {
        this.required = required;
        this.sizeRestriction = sizeRestriction;
        this.typeName = typeName;
        this.typeObject = typeObject;
        this.typeToken = typeToken;
        this.defaultValue = defaultValue;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeToken(byte typeToken) {
        this.typeToken = typeToken;
    }

    public byte getTypeToken() {
        return typeToken;
    }

    public void setTypeObject(Object typeObject) {
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

    public int getByteSize(short encoding) throws NoSuchAttributeTypeException {
        int size;
        switch (typeToken) {
            case TypeID.UINT16_TOKEN:
                size = 2;
                break;
            case TypeID.INT16_TOKEN:
                size = 2;
                break;
            case TypeID.UINT32_TOKEN:
                size = 4;
                break;
            case TypeID.INT32_TOKEN:
                size = 4;
                break;
            case TypeID.INT64_TOKEN:
                size = 8;
                break;
            case TypeID.UINT64_TOKEN:
                size = 8;
                break;
            case TypeID.FLOAT_TOKEN:
                size = 4;
                break;
            case TypeID.DOUBLE_TOKEN:
                size = 8;
                break;
            case TypeID.STRING_TOKEN:
                String aString = (String) typeObject;
                /* add size of string plus two bytes for the length */
                size = EncodedString.getBytes(aString, Event.ENCODING_STRINGS[encoding]).length + 2;
                break;
            case TypeID.IPADDR_TOKEN:
                size = 4;
                break;
            case TypeID.IPV4_TOKEN:
                size = 4;
                break;
            case TypeID.BOOLEAN_TOKEN:
                size = 1;
                break;
            case TypeID.STRING_ARRAY_TOKEN:
                int count = 2; // start with the length of the array
                List<String> anArray = (List) typeObject;
                for (String s : anArray) {
                    count += EncodedString.getBytes(s, Event.ENCODING_STRINGS[encoding]).length + 2;
                }
                size = count;
                break;
            case TypeID.INT16_ARRAY_TOKEN:
                size = ((List) typeObject).size() * 2 + 2;
                break;
            case TypeID.INT32_ARRAY_TOKEN:
                size = ((List) typeObject).size() * 4 + 2;
                break;
            case TypeID.INT64_ARRAY_TOKEN:
                size = ((List) typeObject).size() * 8 + 2;
                break;
            case TypeID.UINT16_ARRAY_TOKEN:
                size = ((List) typeObject).size() * 4 + 2;
                break;
            case TypeID.UINT32_ARRAY_TOKEN:
                size = ((List) typeObject).size() * 8 + 2;
                break;
            case TypeID.UINT64_ARRAY_TOKEN:
                size = ((List) typeObject).size() * 8 + 2;
                break;
            case TypeID.BOOLEAN_ARRAY_TOKEN:
                size = ((List) typeObject).size() + 2;
                break;
            case TypeID.BYTE_ARRAY_TOKEN:
                size = ((List) typeObject).size() + 2;
                break;
            case TypeID.DOUBLE_ARRAY_TOKEN:
                size = ((List) typeObject).size() * 8 + 2;
                break;
            case TypeID.FLOAT_ARRAY_TOKEN:
                size = ((List) typeObject).size() * 4 + 2;
                break;
            case TypeID.IPV4_ARRAY_TOKEN:
                size = ((List) typeObject).size() * 4 + 2;
                break;
            default:
                throw new NoSuchAttributeTypeException("Unknown size of BaseType " + typeName);
        }
        return size;
    }

    public int bytesStoreSize(short encoding) throws NoSuchAttributeTypeException {
        /* add size of data plus size of token denoting data type */
        return getByteSize(encoding) + 1;
    }

    public Object parseFromString(String string) throws EventSystemException {
        Object toReturn = null;
        switch (typeToken) {
            case TypeID.UINT16_TOKEN:
                toReturn = StringParser.fromStringUINT16(string);
                break;
            case TypeID.INT16_TOKEN:
                toReturn = StringParser.fromStringINT16(string);
                break;
            case TypeID.UINT32_TOKEN:
                toReturn = StringParser.fromStringUINT32(string);
                break;
            case TypeID.INT32_TOKEN:
                toReturn = StringParser.fromStringINT32(string);
                break;
            case TypeID.INT64_TOKEN:
                toReturn = StringParser.fromStringINT64(string);
                break;
            case TypeID.UINT64_TOKEN:
                toReturn = StringParser.fromStringUINT64(string);
                break;
            case TypeID.STRING_TOKEN:
                toReturn = StringParser.fromStringSTRING(string);
                break;
            case TypeID.IPADDR_TOKEN:
                toReturn = StringParser.fromStringIPADDR(string);
                break;
            case TypeID.BOOLEAN_TOKEN:
                toReturn = StringParser.fromStringBOOLEAN(string);
                break;
            default:
                throw new NoSuchAttributeTypeException("Unknown size of BaseType "
                                                       + typeName);
        }
        return toReturn;
    }

    public static BaseType baseTypeFromObject(Object value) {
        if (value instanceof String) {
            return new BaseType(TypeID.STRING_STRING, TypeID.STRING_TOKEN, value);
        }
        if (value instanceof Short) {
            return new BaseType(TypeID.INT16_STRING, TypeID.INT16_TOKEN, value);
        }
        if (value instanceof Integer) {
            return new BaseType(TypeID.INT32_STRING, TypeID.INT32_TOKEN, value);
        }
        if (value instanceof Long) {
            return new BaseType(TypeID.INT64_STRING, TypeID.INT64_TOKEN, value);
        }
        if (value instanceof InetAddress || value instanceof IPAddress) {
            return new BaseType(TypeID.IPADDR_STRING, TypeID.IPADDR_TOKEN, value);
        }
        if (value instanceof BigInteger) {
            return new BaseType(TypeID.UINT64_STRING, TypeID.UINT64_TOKEN, value);
        }
        else {
            log.warn("unaccounted for object class: " + value.getClass().getName());
            return null;
        }
    }

    public BaseType cloneBaseType() {
        BaseType newBaseType = new BaseType(getTypeName(), getTypeToken(),
                                            getTypeObject());
        return newBaseType;
    }

    public String toString() {
        return typeObject.toString();
    }
}
