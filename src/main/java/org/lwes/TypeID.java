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
 * @use {@link FieldType}
 */
@Deprecated
public class TypeID {
    public final static byte UNDEFINED_TOKEN = (byte) 0xff;
    public final static byte UINT16_TOKEN = (byte) 0x01;
    public final static byte INT16_TOKEN = (byte) 0x02;
    public final static byte UINT32_TOKEN = (byte) 0x03;
    public final static byte INT32_TOKEN = (byte) 0x04;
    public final static byte STRING_TOKEN = (byte) 0x05;
    public final static byte IPADDR_TOKEN = (byte) 0x06;
    public final static byte INT64_TOKEN = (byte) 0x07;
    public final static byte UINT64_TOKEN = (byte) 0x08;
    public final static byte BOOLEAN_TOKEN = (byte) 0x09;
    public final static byte BYTE_TOKEN = (byte) 0x0A;
    public final static byte FLOAT_TOKEN = (byte) 0x0B;
    public final static byte DOUBLE_TOKEN = (byte) 0x0C;
    public final static byte UINT16_ARRAY_TOKEN = (byte) 0x81;
    public final static byte INT16_ARRAY_TOKEN = (byte) 0x82;
    public final static byte UINT32_ARRAY_TOKEN = (byte) 0x83;
    public final static byte INT32_ARRAY_TOKEN = (byte) 0x84;
    public final static byte STRING_ARRAY_TOKEN = (byte) 0x85;
    public final static byte IP_ADDR_ARRAY_TOKEN = (byte) 0x86;
    public final static byte INT64_ARRAY_TOKEN = (byte) 0x87;
    public final static byte UINT64_ARRAY_TOKEN = (byte) 0x88;
    public final static byte BOOLEAN_ARRAY_TOKEN = (byte) 0x89;
    public final static byte BYTE_ARRAY_TOKEN = (byte) 0x8A;
    public final static byte FLOAT_ARRAY_TOKEN = (byte) 0x8B;
    public final static byte DOUBLE_ARRAY_TOKEN = (byte) 0x8C;

    public final static String UINT16_STRING = "uint16";
    public final static String INT16_STRING = "int16";
    public final static String UINT32_STRING = "uint32";
    public final static String INT32_STRING = "int32";
    public final static String STRING_STRING = "string";
    public final static String IPADDR_STRING = "ip_addr";
    public final static String INT64_STRING = "int64";
    public final static String UINT64_STRING = "uint64";
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

    public final static String SIGNED_INTEGER_REGEX = "-?\\d+";
    public final static String UNSIGNED_INTEGER_REGEX = "\\d+(?=\\s|$)";
    public final static String HEX_SHORT_REGEX = "0x[0-9a-fA-F]{1,4}(?=\\s|$)";
    public final static String HEX_INT_REGEX = "0x[0-9a-fA-F]{5,8}(?=\\s|$)";
    public final static String HEX_LONG_REGEX = "0x[0-9a-fA-F]{9,16}(?=\\s|$)";
    public final static String IP_ADDR_REGEX
            =
            "\\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b";
    public final static String BOOLEAN_REGEX = "true|false";

    /**
     * Simple conversion utility
     *
     * @param id The id token to translate
     * @return String representation of this id
     */
    public static String byteIDToString(byte token) {
        return FieldType.byToken(token).name;
    }

    /**
     * Another conversion utility
     *
     * @param id String representation of the type id
     * @return byte token representation of the type id
     */
    public static byte stringToByteID(String name) {
        return FieldType.byName(name).token;
    }
}
