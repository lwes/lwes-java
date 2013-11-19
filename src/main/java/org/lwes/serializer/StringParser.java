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

package org.lwes.serializer;

import java.math.BigInteger;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwes.EventSystemException;
import org.lwes.util.IPAddress;
import org.lwes.util.NumberCodec;

/**
 * This contains low level type serialization used by the rest of the system.
 *
 * @author Anthony Molinaro
 * @author Michael P. Lum
 */
public class StringParser {
    private final static String HEX_SHORT_REGEX = "0x[0-9a-fA-F]{1,4}(?=\\s|$)";
    private final static String HEX_INT_REGEX = "0x[0-9a-fA-F]{5,8}(?=\\s|$)";
    private final static String HEX_LONG_REGEX = "0x[0-9a-fA-F]{9,16}(?=\\s|$)";
    private final static String IP_ADDR_REGEX
            =
            "\\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b";
    private static transient Log log = LogFactory.getLog(StringParser.class);

    public static byte fromStringBYTE(String string) {
        return Byte.parseByte(string);
    }

    public static float fromStringFLOAT(String string) {
        return Float.parseFloat(string);
    }

    public static double fromStringDOUBLE(String string) {
        return Double.parseDouble(string);
    }

    public static Object fromStringBOOLEAN(String string)
            throws EventSystemException {
        log.trace("Parsing boolean");
        final Boolean toReturn;
        if (string.equalsIgnoreCase("true")) {
            toReturn = Boolean.TRUE;
        }
        else if (string.equalsIgnoreCase("false")) {
            toReturn = Boolean.FALSE;
        }
        else {
            throw new EventSystemException("Unable to parse '" + string + "' as a boolean value.");
        }
        log.trace("Got '" + toReturn + "'");
        return toReturn;
    }

    public static Object fromStringUINT16(String string)
            throws EventSystemException {
        Object toReturn = null;

        log.trace("Parsing uint16");
        if (Pattern.matches(HEX_SHORT_REGEX, string)) {
            if (string.startsWith("0x")) {
                string = string.substring(2);
            }

			/* pad with zeros since NumberCodec.decodeInt expects a length of 8 */
            string = "0000" + string;
            byte[] bytes = NumberCodec.hexStringToByteArray(string);
            toReturn = NumberCodec.decodeInt(bytes, 0, bytes.length);
        }
        else {
            try {
                toReturn = Integer.valueOf(string);
            }
            catch (NumberFormatException nfe) {
                throw new EventSystemException(nfe);
            }
            int intValue = (Integer) toReturn;
            if (intValue < 0 || intValue > 65535) {
                throw new EventSystemException("Unsigned Short must be in the "
                                               + "range [0-65535] ");
            }
        }
        log.trace("received '" + toReturn + "'");

        return toReturn;
    }

    public static Object fromStringINT16(String string)
            throws EventSystemException {
        Object toReturn = null;

        log.trace("Parsing int16");
        if (Pattern.matches(HEX_SHORT_REGEX, string)) {
            if (string.startsWith("0x")) {
                string = string.substring(2);
            }

            byte[] bytes = NumberCodec.hexStringToByteArray(string);
            toReturn = NumberCodec.decodeShort(bytes, 0, bytes.length);
        }
        else {
            try {
                toReturn = Short.valueOf(string);
            }
            catch (NumberFormatException nfe) {
                throw new EventSystemException("Probably not a short, "
                                               + "got exception " + nfe);
            }
            short shortValue = (Short) toReturn;
            if (shortValue < -32768 || shortValue > 32767) {
                throw new EventSystemException("Signed Short must be in the "
                                               + "range [-32768 - 32767] ");
            }
        }
        log.trace("received '" + toReturn + "'");

        return toReturn;
    }

    public static Object fromStringUINT32(String string)
            throws EventSystemException {
        Object toReturn = null;

        log.trace("Parsing uint32");
        if (Pattern.matches(HEX_INT_REGEX, string)) {
            if (string.startsWith("0x")) {
                string = string.substring(2);
            }

			/* pad with zeros since NumberCodec.decodeLong expects a length of 8 */
            string = "00000000" + string;
            byte[] bytes = NumberCodec.hexStringToByteArray(string);
            toReturn = NumberCodec.decodeLong(bytes, 0, bytes.length);
        }
        else {
            try {
                toReturn = Long.valueOf(string);
            }
            catch (NumberFormatException nfe) {
                throw new EventSystemException(nfe);
            }
            long longValue = (Long) toReturn;
            if (longValue < 0
                || longValue > ((long) Integer.MAX_VALUE - ((long) Integer.MIN_VALUE))) {
                throw new EventSystemException("Unsigned Int must be in the "
                                               + "range [0-"
                                               + ((long) Integer.MAX_VALUE - (long) Integer.MIN_VALUE)
                                               + "] ");
            }
        }
        log.trace("received '" + toReturn + "'");

        return toReturn;
    }

    public static Object fromStringINT32(String string)
            throws EventSystemException {
        Object toReturn = null;

        log.trace("Parsing int32");
        if (Pattern.matches(HEX_INT_REGEX, string)) {
            if (string.startsWith("0x")) {
                string = string.substring(2);
            }

            byte[] bytes = NumberCodec.hexStringToByteArray(string);
            toReturn = NumberCodec.decodeInt(bytes, 0, bytes.length);
        }
        else {
            try {
                toReturn = Integer.valueOf(string);
            }
            catch (NumberFormatException nfe) {
                throw new EventSystemException(nfe);
            }
        }
        log.trace("received '" + toReturn + "'");

        return toReturn;
    }

    public static Object fromStringUINT64(String string)
            throws EventSystemException {
        Object toReturn = null;

        log.trace("Parsing uint64");
        if (Pattern.matches(HEX_LONG_REGEX, string)) {
            if (string.startsWith("0x")) {
                string = string.substring(2);
            }

            byte[] bytes = NumberCodec.hexStringToByteArray(string);
            toReturn = BigInteger.valueOf(NumberCodec.decodeLong(bytes, 0, bytes.length));
        }
        else {
            try {
                toReturn = new BigInteger(string);
            }
            catch (NumberFormatException nfe) {
                throw new EventSystemException("Got Exception " + nfe);
            }
        }
        log.trace("received '" + toReturn + "'");

        return toReturn;
    }

    public static Object fromStringINT64(String string)
            throws EventSystemException {
        Object toReturn = null;

        log.trace("Parsing int64");
        if (Pattern.matches(HEX_LONG_REGEX, string)) {
            if (string.startsWith("0x")) {
                string = string.substring(2);
            }

            byte[] bytes = NumberCodec.hexStringToByteArray(string);
            toReturn = NumberCodec.decodeLong(bytes, 0, bytes.length);
        }
        else {
            try {
                toReturn = Long.valueOf(string);
            }
            catch (NumberFormatException nfe) {
                throw new EventSystemException(nfe);
            }
        }
        log.trace("received '" + toReturn + "'");

        return toReturn;
    }

    public static Object fromStringSTRING(String string) {
        log.trace("Parsing string '" + string + "'");
        return string;
    }

    public static Object fromStringIPADDR(String string)
            throws EventSystemException {
        Object toReturn = null;

        log.trace("Parsing IPAddress");

        if (Pattern.matches(IP_ADDR_REGEX, string)) {
            toReturn = new IPAddress(string);
            if (((IPAddress) toReturn).toInt() == 0) {
                throw new EventSystemException("Possible Bad IP Address "
                                               + string);
            }
        }
        else {
            throw new EventSystemException("Invalid IP Address");
        }
        log.trace("received '" + toReturn + "'");

        return toReturn;
    }
    
    public static byte[] fromStringBYTEArray(String[] arr){
        byte[] bArr = new byte[arr.length];
        for(int i=0;i<arr.length;i++)
            bArr[i] = fromStringBYTE(arr[i]);
        
        return bArr;
    }
    
    public static Byte[] fromStringBYTENArray(String[] arr){
        Byte[] bArr = new Byte[arr.length];
        for(int i=0;i<arr.length;i++)
            bArr[i] = arr[i]==null?null:fromStringBYTE(arr[i]);
        
        return bArr;
    }
    
    public static boolean[] fromStringBOOLEANArray(String[] arr){
        boolean[] bArr = new boolean[arr.length];
        for(int i=0;i<arr.length;i++)
            bArr[i] = (Boolean)fromStringBOOLEAN(arr[i]);
        
        return bArr;
    }
    
    public static Boolean[] fromStringBOOLEANNArray(String[] arr){
        Boolean[] bArr = new Boolean[arr.length];
        for(int i=0;i<arr.length;i++)
            bArr[i] = arr[i]==null?null:(Boolean)fromStringBOOLEAN(arr[i]);
        
        return bArr;
    }
    
    public static short[] fromStringINT16Array(String[] arr){
        short[] bArr = new short[arr.length];
        for(int i=0;i<arr.length;i++)
            bArr[i] = (Short)fromStringINT16(arr[i]);
        
        return bArr;
    }
    
    public static int[] fromStringUINT16Array(String[] arr){
        int[] bArr = new int[arr.length];
        for(int i=0;i<arr.length;i++)
            bArr[i] = (Integer)fromStringUINT16(arr[i]);
        
        return bArr;
    }
    
    public static Short[] fromStringINT16NArray(String[] arr){
        Short[] bArr = new Short[arr.length];
        for(int i=0;i<arr.length;i++)
            bArr[i] = arr[i]==null?null:(Short)fromStringINT16(arr[i]);
        
        return bArr;
    }
    
    public static Integer[] fromStringUINT16NArray(String[] arr){
        Integer[] bArr = new Integer[arr.length];
        for(int i=0;i<arr.length;i++)
            bArr[i] = arr[i]==null?null:(Integer)fromStringUINT16(arr[i]);
        
        return bArr;
    }
    
    public static int[] fromStringINT32Array(String[] arr){
        int[] bArr = new int[arr.length];
        for(int i=0;i<arr.length;i++)
            bArr[i] = (Integer)fromStringINT32(arr[i]);
        
        return bArr;
    }
    
    public static long[] fromStringUINT32Array(String[] arr){
        long[] bArr = new long[arr.length];
        for(int i=0;i<arr.length;i++)
            bArr[i] = (Long)fromStringUINT32(arr[i]);
        
        return bArr;
    }
    
    public static Integer[] fromStringINT32NArray(String[] arr){
        Integer[] bArr = new Integer[arr.length];
        for(int i=0;i<arr.length;i++)
            bArr[i] = arr[i]==null?null:(Integer)fromStringINT32(arr[i]);
        
        return bArr;
    }
    
    public static Long[] fromStringUINT32NArray(String[] arr){
        Long[] bArr = new Long[arr.length];
        for(int i=0;i<arr.length;i++)
            bArr[i] = arr[i]==null?null:(Long)fromStringUINT32(arr[i]);
        
        return bArr;
    }
    
    public static long[] fromStringINT64Array(String[] arr){
        long[] bArr = new long[arr.length];
        for(int i=0;i<arr.length;i++)
            bArr[i] = (Long)fromStringINT64(arr[i]);
        
        return bArr;
    }
    
    public static BigInteger[] fromStringUINT64Array(String[] arr){
        BigInteger[] bArr = new BigInteger[arr.length];
        for(int i=0;i<arr.length;i++)
            bArr[i] = (BigInteger)fromStringUINT64(arr[i]);
        
        return bArr;
    }
    
    public static Long[] fromStringINT64NArray(String[] arr){
        Long[] bArr = new Long[arr.length];
        for(int i=0;i<arr.length;i++)
            bArr[i] = arr[i]==null?null:(Long)fromStringINT64(arr[i]);
        
        return bArr;
    }
    
    public static BigInteger[] fromStringUINT64NArray(String[] arr){
        BigInteger[] bArr = new BigInteger[arr.length];
        for(int i=0;i<arr.length;i++)
            bArr[i] = arr[i]==null?null:(BigInteger)fromStringUINT64(arr[i]);
        
        return bArr;
    }
    
    public static float[] fromStringFLOATArray(String[] arr){
        float[] bArr = new float[arr.length];
        for(int i=0;i<arr.length;i++)
            bArr[i] = (float)fromStringFLOAT(arr[i]);
        
        return bArr;
    }
    
    public static Float[] fromStringFLOATNArray(String[] arr){
        Float[] bArr = new Float[arr.length];
        for(int i=0;i<arr.length;i++)
            bArr[i] = arr[i]==null? null:(Float)fromStringFLOAT(arr[i]);
        
        return bArr;
    }
    
    public static double[] fromStringDOUBLEArray(String[] arr){
        double[] bArr = new double[arr.length];
        for(int i=0;i<arr.length;i++)
            bArr[i] = (Double)fromStringDOUBLE(arr[i]);
        
        return bArr;
    }

    public static Double[] fromStringDOUBLENArray(String[] arr){
        Double[] bArr = new Double[arr.length];
        for(int i=0;i<arr.length;i++)
            bArr[i] = arr[i]==null? null:(Double)fromStringDOUBLE(arr[i]);
        
        return bArr;
    }
    
    public static String[] fromStringSTRINGArray(String[] arr){
        String[] bArr = new String[arr.length];
        for(int i=0;i<arr.length;i++)
            bArr[i] = (String)fromStringSTRING(arr[i]);
        
        return bArr;
    }
    
    public static String[] fromStringSTRINGNArray(String[] arr){
        String[] bArr = new String[arr.length];
        for(int i=0;i<arr.length;i++)
            bArr[i] = arr[i]==null? null:(String)fromStringSTRING(arr[i]);
        
        return bArr;
    }
    
    public static IPAddress[] fromStringIPADDRArray(String[] arr){
        IPAddress[] bArr = new IPAddress[arr.length];
        for(int i=0;i<arr.length;i++)
            bArr[i] = (IPAddress)fromStringIPADDR(arr[i]);
        
        return bArr;
    }
}
