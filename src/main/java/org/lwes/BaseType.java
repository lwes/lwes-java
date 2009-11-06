package org.lwes;

import org.lwes.serializer.StringParser;
import org.lwes.util.EncodedString;
import org.lwes.util.IPAddress;
import org.lwes.util.Log;

import java.math.BigInteger;
import java.net.InetAddress;

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

    public BaseType() {
    }

    public BaseType(String typeName, byte typeToken) {
        this.typeName = typeName;
        this.typeToken = typeToken;
    }

    public BaseType(String typeName, byte typeToken, Object typeObject) {
        this.typeName = typeName;
        this.typeToken = typeToken;
        this.typeObject = typeObject;
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
            case TypeID.STRING_TOKEN:
                String aString = (String) typeObject;
                /* add size of string plus two bytes for the length */
                size = EncodedString.getBytes(aString, Event.ENCODING_STRINGS[encoding]).length + 2;
                break;
            case TypeID.IPADDR_TOKEN:
                size = 4;
                break;
            case TypeID.BOOLEAN_TOKEN:
                size = 1;
                break;
            default:
                throw new NoSuchAttributeTypeException("Unknown size of BaseType " + typeName);
        }
        return size;
    }

    public int bytesStoreSize(short encoding) {
        /* add size of data plus size of token denoting data type */
        try {
            return getByteSize(encoding) + 1;
        }
        catch (NoSuchAttributeTypeException e) {
            return 0;
        }
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
            Log.warning("unaccounted for object class: "+value.getClass().getName());
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
