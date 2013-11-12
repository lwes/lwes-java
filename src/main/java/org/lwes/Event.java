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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;

import org.lwes.util.CharacterEncoding;
import org.lwes.util.IPAddress;

public interface Event extends Iterable<FieldAccessor> {
    static final int MAX_EVENT_NAME_SIZE = 127;
    static final int MAX_FIELD_NAME_SIZE = 255;
    static final int MAX_MESSAGE_SIZE = 65507;

    /**
     * Reserved metadata keywords
     */
    static final String ENCODING = "enc";
    static final String RECEIPT_TIME = "ReceiptTime";
    static final String SENDER_IP = "SenderIP";
    static final String SENDER_PORT = "SenderPort";

    /**
     * Encoding variables
     */
    static final short ISO_8859_1 = 0;
    static final short UTF_8 = 1;
    static final short DEFAULT_ENCODING = UTF_8;
    static final CharacterEncoding[] ENCODING_STRINGS = {CharacterEncoding.ISO_8859_1, CharacterEncoding.UTF_8};

    // SETTERS

    void reset();

    void clear(String key);

    void setEventName(String name);

    void set(String key, FieldType type, Object value);

    void setInt16Array(String attributeName, short[] value);

    void setInt32Array(String attributeName, int[] value);

    void setInt64Array(String attributeName, long[] value);

    void setUInt16Array(String attributeName, int[] value);

    void setUInt32Array(String attributeName, long[] value);

    void setUInt64Array(String attributeName, long[] value);

    void setUInt64Array(String attributeName, BigInteger[] value);

    void setStringArray(String attributeName, String[] value);

    void setStringObjArray(String attributeName, String[] value);

    void setIPAddressArray(String attributeName, IPAddress[] value);

    void setBooleanArray(String attributeName, boolean[] value);

    void setByteArray(String attributeName, byte[] value);

    void setDoubleArray(String attributeName, double[] value);

    void setFloatArray(String attributeName, float[] value);

    void setDouble(String attributeName, double value);

    void setFloat(String attributeName, float value);

    void setByte(String attributeName, byte value);

    void setBoolean(String attributeName, boolean aBool);

    void setUInt16(String attributeName, int aNumber);

    void setInt16(String attributeName, short aNumber);

    void setUInt32(String attributeName, long aNumber);

    void setInt32(String attributeName, int aNumber);

    void setUInt64(String attributeName, BigInteger aNumber);

    void setUInt64(String attributeName, long aNumber);

    void setInt64(String attributeName, long aNumber);

    void setString(String attributeName, String aString);

    void setIPAddress(String attributeName, byte[] address);

    void setIPAddress(String attributeName, InetAddress address);

    void setIPAddress(String attributeName, IPAddress address);

    void setShortArray(String attributeName, Short[] value);

    void setIntegerArray(String attributeName, Integer[] value);

    void setLongArray(String attributeName, Long[] value);

    void setDoubleArray(String attributeName, Double[] value);

    void setFloatArray(String attributeName, Float[] value);

    void setEncoding(short encoding);

    // GETTERS

    String getEventName();

    int getNumEventAttributes();

    Enumeration<String> getEventAttributeNames();

    Set<String> getEventAttributes();

    boolean isSet(String attributeName);

    FieldType getType(String attributeName);

    Object get(String attributeName);

    short[] getInt16Array(String attributeName);

    int[] getInt32Array(String attributeName);

    long[] getInt64Array(String attributeName);

    int[] getUInt16Array(String attributeName);

    long[] getUInt32Array(String attributeName);

    BigInteger[] getUInt64Array(String attributeName);

    String[] getStringArray(String attributeName);

    Integer[] getIntegerObjArray(String attributeName);

    Long[] getLongObjArray(String attributeName);

    Short[] getShortObjArray(String attributeName);

    Double[] getDoubleObjArray(String attributeName);

    Boolean[] getBooleanObjArray(String attributeName);

    Byte[] getByteObjArray(String attributeName);

    Float[] getFloatObjArray(String attributeName);

    String[] getStringObjArray(String attributeName);

    BigInteger[] getBigIntegerObjArray(String attributeName);

    byte[] getByteArray(String attributeName);

    boolean[] getBooleanArray(String attributeName);

    double[] getDoubleArray(String attributeName);

    float[] getFloatArray(String attributeName);

    Double getDouble(String attributeName);

    Float getFloat(String attributeName);

    Byte getByte(String attributeName);

    Boolean getBoolean(String attributeName);

    Integer getUInt16(String attributeName);

    Short getInt16(String attributeName);

    Long getUInt32(String attributeName);

    Integer getInt32(String attributeName);

    BigInteger getUInt64(String attributeName);

    Long getInt64(String attributeName);

    String getString(String attributeName);

    InetAddress getInetAddress(String attributeName);

    byte[] getIPAddress(String attributeName);

    IPAddress getIPAddressObj(String attributeName);

    short getEncoding();

    // SERIALIZATION

    byte[] serialize();

    int serialize(byte[] bytes, int offset);

    int serialize(DataOutput output) throws IOException;

    void deserialize(byte[] bytes);

    void deserialize(byte[] bytes, int offset, int length);

    void deserialize(DataInput stream, int length) throws IOException;

    int getBytesSize();
    
    String toJson();

    // MISCELLANEOUS

    Event copy();

    void copyFrom(Event event);

    String toOneLineString();

    /**
     * Some implementations will re-use the FieldAccessor object, so next() may invalidate it.
     */
    Iterator<FieldAccessor> iterator();
}
