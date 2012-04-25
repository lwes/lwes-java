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
import java.util.SortedSet;

import org.lwes.util.CharacterEncoding;
import org.lwes.util.IPAddress;

public interface Event {
    static final int MAX_EVENT_NAME_SIZE = 127;
    static final int MAX_FIELD_NAME_SIZE = 255;
    static final int MAX_MESSAGE_SIZE    = 65507;

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
    static final CharacterEncoding[] ENCODING_STRINGS = { CharacterEncoding.ISO_8859_1, CharacterEncoding.UTF_8 };
    
    // SETTERS
    
    void reset();
    
    void clear(String key) throws EventSystemException;
    
    void setEventName(String name) throws EventSystemException;
    
    void set(String key, FieldType type, Object value) throws EventSystemException;

    void setInt16Array(String attributeName, short[] value) throws EventSystemException;

    void setInt32Array(String attributeName, int[] value) throws EventSystemException;

    void setInt64Array(String attributeName, long[] value) throws EventSystemException;

    void setUInt16Array(String attributeName, int[] value) throws EventSystemException;

    void setUInt32Array(String attributeName, long[] value) throws EventSystemException;

    void setUInt64Array(String attributeName, long[] value) throws EventSystemException;

    void setUInt64Array(String attributeName, BigInteger[] value) throws EventSystemException;

    void setStringArray(String attributeName, String[] value) throws EventSystemException;

    void setIPAddressArray(String attributeName, IPAddress[] value) throws EventSystemException;

    void setBooleanArray(String attributeName, boolean[] value) throws EventSystemException;

    void setByteArray(String attributeName, byte[] value) throws EventSystemException;

    void setDoubleArray(String attributeName, double[] value) throws EventSystemException;

    void setFloatArray(String attributeName, float[] value) throws EventSystemException;

    void setDouble(String attributeName, Double value) throws EventSystemException;
    void setDouble(String attributeName, double value) throws EventSystemException;

    void setFloat(String attributeName, Float value) throws EventSystemException;
    void setFloat(String attributeName, float value) throws EventSystemException;

    void setByte(String attributeName, Byte value) throws EventSystemException;
    void setByte(String attributeName, byte value) throws EventSystemException;

    void setBoolean(String attributeName, Boolean aBool) throws EventSystemException;
    void setBoolean(String attributeName, boolean aBool) throws EventSystemException;

    void setUInt16(String attributeName, Integer aNumber) throws EventSystemException;
    void setUInt16(String attributeName, int aNumber) throws EventSystemException;

    void setInt16(String attributeName, Short aNumber) throws EventSystemException;
    void setInt16(String attributeName, short aNumber) throws EventSystemException;

    void setUInt32(String attributeName, Long aNumber) throws EventSystemException;
    void setUInt32(String attributeName, long aNumber) throws EventSystemException;

    void setInt32(String attributeName, Integer aNumber) throws EventSystemException;
    void setInt32(String attributeName, int aNumber) throws EventSystemException;

    void setUInt64(String attributeName, BigInteger aNumber) throws EventSystemException;
    void setUInt64(String attributeName, Long aNumber) throws EventSystemException;
    void setUInt64(String attributeName, long aNumber) throws EventSystemException;

    void setInt64(String attributeName, Long aNumber) throws EventSystemException;
    void setInt64(String attributeName, long aNumber) throws EventSystemException;

    void setString(String attributeName, String aString) throws EventSystemException;

    void setIPAddress(String attributeName, byte[] address) throws EventSystemException;

    void setIPAddress(String attributeName, InetAddress address) throws EventSystemException;

    void setIPAddress(String attributeName, IPAddress address) throws EventSystemException;
    
    void setEncoding(short encoding) throws EventSystemException;

    // GETTERS

    String getEventName();
    
    int getNumEventAttributes();
    
    Enumeration<String> getEventAttributeNames();
    
    SortedSet<String> getEventAttributes();
    
    boolean isSet(String attributeName);
    
    FieldType getType(String attributeName);

    Object get(String attributeName) throws NoSuchAttributeException;

    short[] getInt16Array(String attributeName) throws NoSuchAttributeException;

    int[] getInt32Array(String attributeName) throws NoSuchAttributeException;

    long[] getInt64Array(String attributeName) throws NoSuchAttributeException;

    int[] getUInt16Array(String attributeName) throws NoSuchAttributeException;

    long[] getUInt32Array(String attributeName) throws NoSuchAttributeException;

    long[] getUInt64Array(String attributeName) throws NoSuchAttributeException;

    String[] getStringArray(String attributeName) throws NoSuchAttributeException;

    byte[] getByteArray(String attributeName) throws NoSuchAttributeException;

    boolean[] getBooleanArray(String attributeName) throws NoSuchAttributeException;

    double[] getDoubleArray(String attributeName) throws NoSuchAttributeException;

    float[] getFloatArray(String attributeName) throws NoSuchAttributeException;

    Double getDouble(String attributeName) throws NoSuchAttributeException;

    Float getFloat(String attributeName) throws NoSuchAttributeException;

    Byte getByte(String attributeName) throws NoSuchAttributeException;

    Boolean getBoolean(String attributeName) throws NoSuchAttributeException;

    Integer getUInt16(String attributeName) throws NoSuchAttributeException;

    Short getInt16(String attributeName) throws NoSuchAttributeException;

    Long getUInt32(String attributeName) throws NoSuchAttributeException;

    Integer getInt32(String attributeName) throws NoSuchAttributeException;

    BigInteger getUInt64(String attributeName) throws NoSuchAttributeException;

    Long getInt64(String attributeName) throws NoSuchAttributeException;

    String getString(String attributeName) throws NoSuchAttributeException;

    InetAddress getInetAddress(String attributeName) throws NoSuchAttributeException;

    byte[] getIPAddress(String attributeName) throws NoSuchAttributeException;

    IPAddress getIPAddressObj(String attributeName) throws NoSuchAttributeException;
    
    short getEncoding() throws EventSystemException;
    
    // SERIALIZATION
    
    byte[] serialize();

    int serialize(byte[] bytes, int offset);
    
    int serialize(DataOutput output) throws IOException;
    
    void deserialize(byte[] bytes) throws EventSystemException;
    
    void deserialize(byte[] bytes, int offset, int length) throws EventSystemException;
    
    void deserialize(DataInput stream, int length) throws IOException, EventSystemException;

    int getBytesSize();
    
    // MISCELLANEOUS
    
    Event copy() throws EventSystemException;
    
    void copyFrom(Event event) throws EventSystemException;
    
    String toOneLineString();
}
