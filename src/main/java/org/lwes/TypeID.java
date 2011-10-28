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

/**
 * This class contains some global variables used in various parts of
 * the event system.
 *
 * @author Anthony Molinaro
 * @author Michael P. Lum
 */
public class TypeID {
    /**
     * The token used for <tt>undefined</tt> types in LWES
     */
    public final static byte UNDEFINED_TOKEN = (byte) 0xff;

    /**
     * The token used by <tt>uint16</tt> in the Event Serialization Protocol
     */
    public final static byte UINT16_TOKEN = (byte) 0x01;

    /**
     * The token used by <tt>int16</tt> in the Event Serialization Protocol
     */
    public final static byte INT16_TOKEN = (byte) 0x02;
    /**
     * The token used by <tt>uint32</tt> in the Event Serialization Protocol
     */
    public final static byte UINT32_TOKEN = (byte) 0x03;
    /**
     * The token used by <tt>int32</tt> in the Event Serialization Protocol
     */
    public final static byte INT32_TOKEN = (byte) 0x04;
    /**
     * The token used by <tt>string</tt> in the Event Serialization Protocol
     */
    public final static byte STRING_TOKEN = (byte) 0x05;
    /**
     * The token used by <tt>ip_addr</tt> in the Event Serialization Protocol
     */
    public final static byte IPADDR_TOKEN = (byte) 0x06;
    /**
     * The token used by <tt>int64</tt> in the Event Serialization Protocol
     */
    public final static byte INT64_TOKEN = (byte) 0x07;
    /**
     * The token used by <tt>uint64</tt> in the Event Serialization Protocol
     */
    public final static byte UINT64_TOKEN = (byte) 0x08;
    /**
     * The token used by <tt>boolean</tt> in the Event Serialization Protocol
     */
    public final static byte BOOLEAN_TOKEN = (byte) 0x09;

    public final static byte BYTE_TOKEN = (byte) 0x0A;
    public final static byte FLOAT_TOKEN = (byte) 0x0B;
    public final static byte DOUBLE_TOKEN = (byte) 0x0C;

    /**
     * The token used by <tt>uint16[]</tt> in the Event Serialization Protocol
     */
    public final static byte UINT16_ARRAY_TOKEN = (byte) 0x81;
    /**
     * The token used by <tt>int16[]</tt> in the Event Serialization Protocol
     */
    public final static byte INT16_ARRAY_TOKEN = (byte) 0x82;
    /**
     * The token used by <tt>uint32[]</tt> in the Event Serialization Protocol
     */
    public final static byte UINT32_ARRAY_TOKEN = (byte) 0x83;
    /**
     * The token used by <tt>int32[]</tt> in the Event Serialization Protocol
     */
    public final static byte INT32_ARRAY_TOKEN = (byte) 0x84;

    /**
     * The token used by <tt>string[]</tt> in the Event Serialization Protocol
     */
    public final static byte STRING_ARRAY_TOKEN = (byte) 0x85;

    /**
     * The token used by <tt>ip_addr[]</tt> in the Event Serialization Protocol
     */
    public final static byte IP_ADDR_ARRAY_TOKEN = (byte) 0x86;

    /**
     * The token used by <tt>int64[]</tt> in the Event Serialization Protocol
     */
    public final static byte INT64_ARRAY_TOKEN = (byte) 0x87;

    /**
     * The token used by <tt>uint64[]</tt> in the Event Serialization Protocol
     */
    public final static byte UINT64_ARRAY_TOKEN = (byte) 0x88;

    /**
     * The token used by <tt>boolean[]</tt> in the Event Serialization Protocol
     */
    public final static byte BOOLEAN_ARRAY_TOKEN = (byte) 0x89;
    /**
     * The token used by <tt>byte[]</tt> in the Event Serialization Protocol
     */
    public final static byte BYTE_ARRAY_TOKEN = (byte) 0x8A;

    public final static byte FLOAT_ARRAY_TOKEN = (byte) 0x8B;

    public final static byte DOUBLE_ARRAY_TOKEN = (byte) 0x8C;

    /**
     * The  string used by <tt>uint16</tt> in the Event Serialization Protocol
     */
    public final static String UINT16_STRING = "uint16";
    /**
     * The  string used by <tt>int16</tt> in the Event Serialization Protocol
     */
    public final static String INT16_STRING = "int16";
    /**
     * The  string used by <tt>uint32</tt> in the Event Serialization Protocol
     */
    public final static String UINT32_STRING = "uint32";
    /**
     * The  string used by <tt>int32</tt> in the Event Serialization Protocol
     */
    public final static String INT32_STRING = "int32";
    /**
     * The  string used by <tt>string</tt> in the Event Serialization Protocol
     */
    public final static String STRING_STRING = "string";
    /**
     * The  string used by <tt>ip_addr</tt> in the Event Serialization Protocol
     */
    public final static String IPADDR_STRING = "ip_addr";
    /**
     * The  string used by <tt>int64</tt> in the Event Serialization Protocol
     */
    public final static String INT64_STRING = "int64";
    /**
     * The  string used by <tt>uint64</tt> in the Event Serialization Protocol
     */
    public final static String UINT64_STRING = "uint64";
    /**
     * The  string used by <tt>boolean</tt> in the Event Serialization Protocol
     */
    public final static String BOOLEAN_STRING = "boolean";

    public final static String BYTE_STRING = "byte";
    public final static String DOUBLE_STRING = "double";
    public final static String FLOAT_STRING = "float";

    public final static String STRING_ARRAY_STRING = "[Lstring";
    public final static String UINT16_ARRAY_STRING = "[Luint16";
    public final static String INT16_ARRAY_STRING = "[Lint16";
    public final static String UINT32_ARRAY_STRING = "[Luint32";
    public final static String INT32_ARRAY_STRING = "[Lint32";
    public final static String UINT64_ARRAY_STRING = "[Luint64";
    public final static String INT64_ARRAY_STRING = "[Lint64";
    public final static String BOOLEAN_ARRAY_STRING = "[Lboolean";
    public final static String BYTE_ARRAY_STRING = "[Lbyte";
    public final static String DOUBLE_ARRAY_STRING = "[Ldouble";
    public final static String FLOAT_ARRAY_STRING = "[Lfloat";

    /**
     * This is a regular expression for parsing an integer number from a string
     */
    public final static String SIGNED_INTEGER_REGEX = "-?\\d+";
    /**
     * This is a regular expression for parsing an unsigned integer number
     * from a string
     */
    public final static String UNSIGNED_INTEGER_REGEX = "\\d+(?=\\s|$)";
    /**
     * This is a regular expression for matching a hexadecimal short from a string
     */
    public final static String HEX_SHORT_REGEX = "0x[0-9a-fA-F]{1,4}(?=\\s|$)";
    /**
     * This is a regular expression for matching a hexadecimal int from a string
     */
    public final static String HEX_INT_REGEX = "0x[0-9a-fA-F]{5,8}(?=\\s|$)";
    /**
     * This is a regular expression for matching a hexadecimal long from a string
     */
    public final static String HEX_LONG_REGEX = "0x[0-9a-fA-F]{9,16}(?=\\s|$)";
    /**
     * This is a regular expression for matching an ip address from a string
     */
    public final static String IP_ADDR_REGEX
            =
            "\\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b";
    /**
     * This is a regular expression for matching a boolean from a string
     */
    public final static String BOOLEAN_REGEX = "true|false";

    /**
     * Simple conversion utility
     *
     * @param id The id token to translate
     * @return String representation of this id
     */
    public static String byteIDToString(byte id) {
        switch (id) {
            case UINT16_TOKEN:
                return UINT16_STRING;
            case INT16_TOKEN:
                return INT16_STRING;
            case UINT32_TOKEN:
                return UINT32_STRING;
            case INT32_TOKEN:
                return INT32_STRING;
            case STRING_TOKEN:
                return STRING_STRING;
            case IPADDR_TOKEN:
                return IPADDR_STRING;
            case INT64_TOKEN:
                return INT64_STRING;
            case UINT64_TOKEN:
                return UINT64_STRING;
            case BOOLEAN_TOKEN:
                return BOOLEAN_STRING;
            case BYTE_TOKEN:
                return BYTE_STRING;
            case STRING_ARRAY_TOKEN:
                return STRING_ARRAY_STRING;
            case UINT16_ARRAY_TOKEN:
                return UINT16_ARRAY_STRING;
            case INT16_ARRAY_TOKEN:
                return INT16_ARRAY_STRING;
            case UINT32_ARRAY_TOKEN:
                return UINT32_ARRAY_STRING;
            case INT32_ARRAY_TOKEN:
                return INT32_ARRAY_STRING;
            case UINT64_ARRAY_TOKEN:
                return UINT64_ARRAY_STRING;
            case INT64_ARRAY_TOKEN:
                return INT64_ARRAY_STRING;
            case BOOLEAN_ARRAY_TOKEN:
                return BOOLEAN_ARRAY_STRING;
            case BYTE_ARRAY_TOKEN:
                return BYTE_ARRAY_STRING;
            default:
                return null;
        }
    }

    /**
     * Another conversion utility
     *
     * @param id String representation of the type id
     * @return byte token representation of the type id
     */
    public static byte stringToByteID(String id) {
        if (id.equals(UINT16_STRING)) {
            return UINT16_TOKEN;
        }
        else if (id.equals(INT16_STRING)) {
            return INT16_TOKEN;
        }
        else if (id.equals(UINT32_STRING)) {
            return UINT32_TOKEN;
        }
        else if (id.equals(INT32_STRING)) {
            return INT32_TOKEN;
        }
        else if (id.equals(STRING_STRING)) {
            return STRING_TOKEN;
        }
        else if (id.equals(IPADDR_STRING)) {
            return IPADDR_TOKEN;
        }
        else if (id.equals(INT64_STRING)) {
            return INT64_TOKEN;
        }
        else if (id.equals(UINT64_STRING)) {
            return UINT64_TOKEN;
        }
        else if (id.equals(BOOLEAN_STRING)) {
            return BOOLEAN_TOKEN;
        }
        else if (id.equals(BYTE_STRING)) {
            return BYTE_TOKEN;
        }
        else if (id.equals(STRING_ARRAY_STRING)) {
            return STRING_ARRAY_TOKEN;
        }
        else if (id.equals(UINT16_ARRAY_STRING)) {
            return UINT16_ARRAY_TOKEN;
        }
        else if (id.equals(INT16_ARRAY_STRING)) {
            return INT16_ARRAY_TOKEN;
        }
        else if (id.equals(UINT32_ARRAY_STRING)) {
            return UINT32_ARRAY_TOKEN;
        }
        else if (id.equals(INT32_ARRAY_STRING)) {
            return INT32_ARRAY_TOKEN;
        }
        else if (id.equals(STRING_ARRAY_STRING)) {
            return STRING_ARRAY_TOKEN;
        }
        else if (id.equals(INT64_ARRAY_STRING)) {
            return INT64_ARRAY_TOKEN;
        }
        else if (id.equals(UINT64_ARRAY_STRING)) {
            return UINT64_ARRAY_TOKEN;
        }
        else if (id.equals(BOOLEAN_ARRAY_STRING)) {
            return BOOLEAN_ARRAY_TOKEN;
        }
        else if (id.equals(BYTE_ARRAY_STRING)) {
            return BYTE_ARRAY_TOKEN;
        }
        else {
            return UNDEFINED_TOKEN;
        }
    }
}
