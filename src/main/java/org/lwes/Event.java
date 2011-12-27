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
import org.lwes.db.EventTemplateDB;
import org.lwes.serializer.Deserializer;
import org.lwes.serializer.DeserializerState;
import org.lwes.serializer.Serializer;
import org.lwes.util.CharacterEncoding;
import org.lwes.util.EncodedString;
import org.lwes.util.IPAddress;
import org.lwes.util.NumberCodec;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class Event {

    private static transient Log log = LogFactory.getLog(Event.class);

    public static final int MAX_EVENT_NAME_SIZE = 127;
    public static final int MAX_FIELD_NAME_SIZE = 255;
    public static final int MAX_MESSAGE_SIZE    = 65507;

    /**
     * Reserved metadata keywords
     */
    public static final String ENCODING = "enc";
    public static final String RECEIPT_TIME = "ReceiptTime";
    public static final String SENDER_IP = "SenderIP";
    public static final String SENDER_PORT = "SenderPort";

    /**
     * Encoding variables
     */
    public static final short ISO_8859_1 = 0;
    public static final short UTF_8 = 1;
    public static final short DEFAULT_ENCODING = UTF_8;
    public static final CharacterEncoding[] ENCODING_STRINGS = {
        CharacterEncoding.ISO_8859_1, CharacterEncoding.UTF_8};
    
    private static final BigInteger UINT64_MASK = new BigInteger("10000000000000000",16);

    /**
     * Event data
     */
    private final ConcurrentHashMap<String, BaseType> attributes = new ConcurrentHashMap<String, BaseType>();
    private String name = null;
    private EventTemplateDB eventTemplateDB = null;
    private short encoding = DEFAULT_ENCODING;

    /**
     * If this is set to true, types and attributes are validated against the EventTemplateDB
     */
    private boolean validating = true;

    /**
     * Internal object for deserialization state
     */
    private DeserializerState state = null;

    /**
     * the size of the event in bytes
     */
    private int bytesStoreSize = 0;

    /**
     * Create an event called <tt>eventName</tt>
     *
     * @param eventName       the name of the event
     * @param eventTemplateDB the EventTemplateDB to use for validation
     * @throws NoSuchEventException         if the Event does not exist in the EventTemplateDB
     * @throws NoSuchAttributeException     if an attribute does not exist in the EventTemplateDB
     * @throws NoSuchAttributeTypeException if an attribute type does not exist in the EventTemplateDB
     */
    public Event(String eventName, EventTemplateDB eventTemplateDB)
        throws EventSystemException {
        this(eventName, true, eventTemplateDB);
    }

    /**
     * Create an event called <tt>eventName</tt>
     *
     * @param eventName       the name of the event
     * @param validate        true if the EventTemplateDB should be checked for types before all mutations
     * @param eventTemplateDB the EventTemplateDB to use for validation
     * @throws NoSuchEventException         if the Event does not exist in the EventTemplateDB
     * @throws NoSuchAttributeException     if an attribute does not exist in the EventTemplateDB
     * @throws NoSuchAttributeTypeException if an attribute type does not exist in the EventTemplateDB
     */
    public Event(String eventName, boolean validate, EventTemplateDB eventTemplateDB)
        throws EventSystemException {
        this(eventName, validate, eventTemplateDB, DEFAULT_ENCODING);
    }

    /**
     * Create an event called <tt>eventName</tt>
     *
     * @param eventName the name of the event
     * @param validate  true if the EventTemplateDB should be checked for types before all mutations
     * @param encoding  the character encoding used by the event
     * @throws NoSuchEventException         if the Event does not exist in the EventTemplateDB
     * @throws NoSuchAttributeException     if an attribute does not exist in the EventTemplateDB
     * @throws NoSuchAttributeTypeException if an attribute type does not exist in the EventTemplateDB
     */
    public Event(String eventName, boolean validate, EventTemplateDB eventTemplateDB, short encoding)
        throws EventSystemException {
        checkShortStringLength(eventName, encoding, MAX_EVENT_NAME_SIZE);
        setEventTemplateDB(eventTemplateDB);
        validating = validate;
        setEventName(eventName);
        setEncoding(encoding);
        setDefaultValues(eventTemplateDB);
    }

    /**
     * Creates an event by deserializing a raw byte array.
     *
     * @param bytes           the raw bytes to convert
     * @param eventTemplateDB the EventTemplateDB to use to validate the event
     * @throws NoSuchEventException
     * @throws NoSuchAttributeException
     * @throws NoSuchAttributeTypeException
     */
    public Event(byte[] bytes, EventTemplateDB eventTemplateDB)
        throws EventSystemException {
        this(bytes, true, eventTemplateDB);
    }


    /**
     * Creates an event by deserializing a raw byte array.
     *
     * @param bytes           the raw bytes to convert
     * @param validate        whether or not to validate the event
     * @param eventTemplateDB the EventTemplateDB to use to validate the event
     * @throws NoSuchEventException
     * @throws NoSuchAttributeException
     * @throws NoSuchAttributeTypeException
     */
    public Event(byte[] bytes, boolean validate, EventTemplateDB eventTemplateDB)
        throws EventSystemException {
        setEventTemplateDB(eventTemplateDB);
        validating = validate;
        deserialize(bytes);
        setDefaultValues(eventTemplateDB);
    }

    private static void checkShortStringLength(String string, short encoding, int maxLength)
    throws EventSystemException {
        final int serializedLength = EncodedString.getBytes(string, Event.ENCODING_STRINGS[encoding]).length;
        if (serializedLength > maxLength) {
            throw new EventSystemException("String "+string+" was longer than maximum length: "+serializedLength+" > "+maxLength);
        }
    }

    protected void setDefaultValues(EventTemplateDB template) throws EventSystemException {
        if (template == null) {
            return;
        }
        for (Entry<String,BaseType> entry : template.getBaseTypesForEvent(getEventName()).entrySet()) {
            final String   key = entry.getKey();
            final BaseType bt  = entry.getValue();
            if (bt.getDefaultValue() != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Setting default value: " + key + "=" + bt.getDefaultValue());
                }
                set(key, bt.getDefaultValue());
            }
        }
    }

    /**
     * Returns an enumeration of all the event attribute names
     *
     * @return an enumeration of attribute strings
     */
    public Enumeration<String> getEventAttributeNames() {
        return attributes.keys();
    }

    /**
     * Returns the number of attributes in the event
     *
     * @return number of attributes in the event
     */
    public int size() {
        return attributes.size();
    }

    /**
     * Returns true if the event validates against the EventTemplateDB before making changes
     *
     * @return the validating state
     */
    public boolean isValidating() {
        return this.validating;
    }

    /**
     * Set to true if the event should validate against the EventTemplateDB before making changes
     *
     * @param validate the validating value
     */
    public void setValidating(boolean validate) {
        this.validating = validate;
    }

    /**
     * Returns the EventTemplateDB for this event, used for validation of types and attributes.
     *
     * @return the EventTemplateDB
     */
    public EventTemplateDB getEventTemplateDB() {
        return this.eventTemplateDB;
    }

    /**
     * Sets the EventTemplateDB for this event, used for validation of types and attributes.
     *
     * @param eventTemplateDB the EventTemplateDB to be used for validation
     */
    public void setEventTemplateDB(EventTemplateDB eventTemplateDB) {
        this.eventTemplateDB = eventTemplateDB;
    }

    /**
     * Returns the name of the event
     *
     * @return the name of the event
     */
    public synchronized String getEventName() {
        return this.name;
    }

    /**
     * Sets the name of the Event
     *
     * @param name the name of the event
     * @throws NoSuchEventException if the event is validating and does not exist in the EventTemplateDB
     */
    public synchronized void setEventName(String name) throws NoSuchEventException {
        if (isValidating() && getEventTemplateDB() != null) {
            if (!getEventTemplateDB().checkForEvent(name)) {
                throw new NoSuchEventException("Event " + name + " does not exist in event definition");
            }
        }

        /* determine if we already have the name and are just resetting it */
        if (this.name != null) {
            bytesStoreSize -= (this.name.length() + 1 + 2);
        }

        bytesStoreSize += (name.length() + 1 + 2);

        this.name = name;
    }

    /**
     * Get the character encoding for this event
     *
     * @return the encoding
     */
    public short getEncoding() {
        return this.encoding;
    }

    /**
     * Set the character encoding for event strings
     *
     * @param encoding the character encoding
     * @throws NoSuchAttributeTypeException if the type for the encoding attribute does not exist
     * @throws NoSuchAttributeException     if the encoding attribute does not exist
     */
    public void setEncoding(short encoding) throws EventSystemException {
        this.encoding = encoding;
        setInt16(ENCODING, this.encoding);
    }

    /**
     * Generic accessor, checks if an attribute exists and returns its value.  The user must do their
     * own type checking.
     *
     * @param attributeName name of the attribute to lookup
     * @return the object poitned to by attributeName
     * @throws NoSuchAttributeException if the attribute does not exist in this event
     */
    public Object get(String attributeName) throws NoSuchAttributeException {
        if (attributes.containsKey(attributeName)) {
            return attributes.get(attributeName).getTypeObject();
        }

        if (isValidating() && getEventTemplateDB() != null) {
            if (getEventTemplateDB().checkForAttribute(name, attributeName)) {
                return null;
            }
            else {
                throw new NoSuchAttributeException("Attribute " + attributeName + " does not exist for event " + name);
            }
        }

        return null;
    }

    public short[] getInt16Array(String attributeName) throws NoSuchAttributeException {
        Object o = get(attributeName);
        if (o != null && o instanceof short[]) {
            return (short[]) o;
        }
        else {
            return null;
        }
    }

    public int[] getInt32Array(String attributeName) throws NoSuchAttributeException {
        Object o = get(attributeName);
        if (o != null && o instanceof int[]) {
            return (int[]) o;
        }
        else {
            return null;
        }
    }

    public long[] getInt64Array(String attributeName) throws NoSuchAttributeException {
        Object o = get(attributeName);
        if (o != null && o instanceof long[]) {
            return (long[]) o;
        }
        else {
            return null;
        }
    }

    public int[] getUInt16Array(String attributeName) throws NoSuchAttributeException {
        Object o = get(attributeName);
        if (o != null && o instanceof int[]) {
            return (int[]) o;
        }
        else {
            return null;
        }
    }

    public long[] getUInt32Array(String attributeName) throws NoSuchAttributeException {
        Object o = get(attributeName);
        if (o != null && o instanceof long[]) {
            return (long[]) o;
        }
        else {
            return null;
        }
    }

    public long[] getUInt64Array(String attributeName) throws NoSuchAttributeException {
        Object o = get(attributeName);
        if (o != null && o instanceof long[]) {
            return (long[]) o;
        }
        else {
            return null;
        }
    }

    public String[] getStringArray(String attributeName) throws NoSuchAttributeException {
        Object o = get(attributeName);
        if (o != null && o instanceof String[]) {
            return (String[]) o;
        }
        else {
            return null;
        }
    }

    public byte[] getByteArray(String attributeName) throws NoSuchAttributeException {
        Object o = get(attributeName);
        if (o != null && o instanceof byte[]) {
            return (byte[]) o;
        }
        else {
            return null;
        }
    }

    public boolean[] getBooleanArray(String attributeName) throws NoSuchAttributeException {
        Object o = get(attributeName);
        if (o != null && o instanceof boolean[]) {
            return (boolean[]) o;
        }
        else {
            return null;
        }
    }

    public double[] getDoubleArray(String attributeName) throws NoSuchAttributeException {
        Object o = get(attributeName);
        if (o != null && o instanceof double[]) {
            return (double[]) o;
        }
        else {
            return null;
        }
    }

    public float[] getFloatArray(String attributeName) throws NoSuchAttributeException {
        Object o = get(attributeName);
        if (o != null && o instanceof float[]) {
            return (float[]) o;
        }
        else {
            return null;
        }
    }

    /**
     * Method to check if an attribute is set in the event. This method does not throw
     * NoSuchAttributeException because it shouldn't really care. If it's not there, it's
     * not there.
     *
     * @param attributeName The attribute name to check for existence.
     * @return true if there is a value, false if not.
     */
    public boolean isSet(String attributeName) {
        try {
            return (get(attributeName) != null);
        }
        catch (NoSuchAttributeException e) {
            return false;
        }
    }

    public Double getDouble(String attributeName) throws NoSuchAttributeException {
        return (Double) get(attributeName);
    }

    public Float getFloat(String attributeName) throws NoSuchAttributeException {
        return (Float) get(attributeName);
    }

    public Byte getByte(String attributeName) throws NoSuchAttributeException {
        return (Byte) get(attributeName);
    }

    /**
     * Accessor that returns a boolean value for attribute <tt>attributeName</tt>
     *
     * @param attributeName the name of the attribute to fetch
     * @return the boolean value
     * @throws NoSuchAttributeException if the attribute does not exist in this event
     */
    public Boolean getBoolean(String attributeName) throws NoSuchAttributeException {
        return (Boolean) get(attributeName);
    }

    /**
     * Accessor that returns an <tt>unsigned short</tt>, in the guise of an <tt>int</tt>, for attribute <tt>attributeName</tt>
     *
     * @param attributeName the name of the attribute to fetch
     * @return the unsigned short as an int
     * @throws NoSuchAttributeException if the attribute does not exist in this event
     */
    public Integer getUInt16(String attributeName) throws NoSuchAttributeException {
        return (Integer) get(attributeName);
    }

    /**
     * Accessor that returns an <tt>short</tt>, for attribute <tt>attributeName</tt>
     *
     * @param attributeName the name of the attribute to fetch
     * @return the short value
     * @throws NoSuchAttributeException if the attribute does not exist in this event
     */
    public Short getInt16(String attributeName) throws NoSuchAttributeException {
        return (Short) get(attributeName);
    }

    /**
     * Accessor that returns an <tt>unsigned int</tt>, in the guise of an <tt>long</tt>, for attribute <tt>attributeName</tt>
     *
     * @param attributeName the name of the attribute to fetch
     * @return the unsigned int as a long
     * @throws NoSuchAttributeException if the attribute does not exist in this event
     */
    public Long getUInt32(String attributeName) throws NoSuchAttributeException {
        return (Long) get(attributeName);
    }

    /**
     * Accessor that returns an <tt>int</tt>, for attribute <tt>attributeName</tt>
     *
     * @param attributeName the name of the attribute to fetch
     * @return the int value
     * @throws NoSuchAttributeException if the attribute does not exist in this event
     */
    public Integer getInt32(String attributeName) throws NoSuchAttributeException {
        return (Integer) get(attributeName);
    }

    /**
     * Accessor that returns an <tt>unsigned long</tt>, in the guise of an <tt>BigInteger</tt>, for attribute <tt>attributeName</tt>
     *
     * @param attributeName the name of the attribute to fetch
     * @return the unsigned long as a BigInteger
     * @throws NoSuchAttributeException if the attribute does not exist in this event
     */
    public BigInteger getUInt64(String attributeName) throws NoSuchAttributeException {
        return (BigInteger) get(attributeName);
    }


    /**
     * Accessor that returns an <tt>long</tt>, for attribute <tt>attributeName</tt>
     *
     * @param attributeName the name of the attribute to fetch
     * @return the long value
     * @throws NoSuchAttributeException if the attribute does not exist in this event
     */
    public Long getInt64(String attributeName) throws NoSuchAttributeException {
        return (Long) get(attributeName);
    }

    /**
     * Accessor that returns an <tt>String</tt>, for attribute <tt>attributeName</tt>
     *
     * @param attributeName the name of the attribute to fetch
     * @return the String value
     * @throws NoSuchAttributeException if the attribute does not exist in this event
     */
    public String getString(String attributeName) throws NoSuchAttributeException {
        return (String) get(attributeName);
    }

    /**
     * Accessor that returns an <tt>InetAddress</tt>, for attribute <tt>attributeName</tt>
     *
     * @param attributeName the name of the attribute to fetch
     * @return the InetAddress value
     * @throws NoSuchAttributeException if the attribute does not exist in this event
     * @deprecated
     */
    public InetAddress getInetAddress(String attributeName) throws NoSuchAttributeException {
        IPAddress a = (IPAddress) get(attributeName);
        if (a != null) {
            return a.toInetAddress();
        }
        else {
            return null;
        }
    }

    /**
     * Accessor that returns an IP address in bytes, for attribute <tt>attributeName</tt>
     *
     * @param attributeName the name of the attribute to fetch
     * @return the IP address in bytes
     * @throws NoSuchAttributeException if the attribute does not exist in this event
     * @deprecated
     */
    public byte[] getIPAddress(String attributeName) throws NoSuchAttributeException {
        return ((IPAddress) get(attributeName)).getInetAddressAsBytes();
    }

    /**
     * Accessor that returns the ip address as an IPAddress object.
     *
     * @param attributeName name of the attribute to fetch
     * @return IPAddress
     * @throws NoSuchAttributeException if the attribute is not set.
     * @deprecated
     */
    public IPAddress getIPAddressObj(String attributeName) throws NoSuchAttributeException {
        return (IPAddress) get(attributeName);
    }

    /**
     * Set the object's attribute <tt>attributeName</tt> with the Object given
     *
     * @param attributeName  the name of the attribute to set
     * @param attributeValue the object to set the attribute with
     * @throws NoSuchAttributeException     if the attribute does not exist in this event
     * @throws NoSuchAttributeTypeException if there is an attribute with an undefined type
     */
    public void set(String attributeName, Object attributeValue)
        throws EventSystemException {
        if (isValidating() && getEventTemplateDB() != null) {
            if (getEventTemplateDB().checkForAttribute(getEventName(), attributeName)) {
                BaseType bt = getEventTemplateDB().getBaseTypeForObjectAttribute(getEventName(),
                                                                                 attributeName, attributeValue);
                if (! bt.getType().isCompatibleWith(attributeValue)) {
                    throw new NoSuchAttributeTypeException("Wrong type '" + attributeValue.getClass().getName() + "' for " + name + "." + attributeName);
                }
                set(attributeName, bt);
            }
        }
        else {
            throw new NoSuchAttributeException("Must be able to check the EventTemplateDB to use set(String,Object)");
        }
    }

    /**
     * Private method to set a BaseType
     *
     * @param attribute the name of the attribute to set
     * @param anObject  the BaseType to set in the event
     * @throws NoSuchAttributeException     if the attribute does not exist in this event
     * @throws NoSuchAttributeTypeException if there is an attribute with an undefined type
     */
    private void set(String attribute, BaseType anObject)
        throws EventSystemException {

        checkShortStringLength(attribute, encoding, MAX_FIELD_NAME_SIZE);

        if (isValidating() && getEventTemplateDB() != null) {
            if (getEventTemplateDB().checkForAttribute(name, attribute)) {
                if (!getEventTemplateDB().checkTypeForAttribute(name, attribute, anObject)) {
                    throw new NoSuchAttributeTypeException("Wrong type '" + anObject.getType() +
                                                           "' for " + name + "." + attribute);
                }
            }
            else {
                throw new NoSuchAttributeException("Attribute " + attribute + " does not exist for event " + name);
            }
            getEventTemplateDB().checkForSize(name, attribute, anObject);
        }

        // Remove the existing value, and record the reduction in the serialized size.
        final BaseType oldObject = attributes.remove(attribute);
        if (oldObject != null) {
            bytesStoreSize -= (attribute.length() + 1) + oldObject.bytesStoreSize(encoding);
        }
        
        if (anObject.getTypeObject() != null) {
            int newSize = bytesStoreSize + ((attribute.length() + 1) + anObject.bytesStoreSize(encoding));
            if (newSize > MAX_MESSAGE_SIZE) {
                throw new EventSystemException("Event size limit is " + MAX_MESSAGE_SIZE + " bytes.");
            }

            bytesStoreSize += (attribute.length() + 1) + anObject.bytesStoreSize(encoding);
            attributes.put(attribute, anObject);
        }
    }

    public void setInt16Array(String attributeName, short[] value) throws EventSystemException {
        set(attributeName, new BaseType(FieldType.INT16_ARRAY, value));
    }

    public void setInt32Array(String attributeName, int[] value) throws EventSystemException {
        set(attributeName, new BaseType(FieldType.INT32_ARRAY, value));
    }

    public void setInt64Array(String attributeName, long[] value) throws EventSystemException {
        set(attributeName, new BaseType(FieldType.INT64_ARRAY, value));
    }

    public void setUInt16Array(String attributeName, int[] value) throws EventSystemException {
        set(attributeName, new BaseType(FieldType.UINT16_ARRAY, value));
    }

    public void setUInt32Array(String attributeName, long[] value) throws EventSystemException {
        set(attributeName, new BaseType(FieldType.UINT32_ARRAY, value));
    }

    public void setUInt64Array(String attributeName, long[] value) throws EventSystemException {
        final BigInteger[] value2 = new BigInteger[value.length];
        for (int i=0; i<value.length; ++i) value2[i] = BigInteger.valueOf(value[i]).and(UINT64_MASK);
        set(attributeName, new BaseType(FieldType.UINT64_ARRAY, value));
    }

    public void setUInt64Array(String attributeName, BigInteger[] value) throws EventSystemException {
        set(attributeName, new BaseType(FieldType.UINT64_ARRAY, value));
    }

    public void setStringArray(String attributeName, String[] value)
        throws EventSystemException {
        set(attributeName, new BaseType(FieldType.STRING_ARRAY, value));
    }

    public void setIPAddressArray(String attributeName, IPAddress[] value)
            throws EventSystemException {
        set(attributeName, new BaseType(FieldType.IP_ADDR_ARRAY, value));
    }

    public void setBooleanArray(String attributeName, boolean[] value)
        throws EventSystemException {
        set(attributeName, new BaseType(FieldType.BOOLEAN_ARRAY, value));
    }

    public void setByteArray(String attributeName, byte[] value)
        throws EventSystemException {
        set(attributeName, new BaseType(FieldType.BYTE_ARRAY, value));
    }

    public void setDoubleArray(String attributeName, double[] value)
        throws EventSystemException {
        set(attributeName, new BaseType(FieldType.DOUBLE_ARRAY, value));
    }

    public void setFloatArray(String attributeName, float[] value)
        throws EventSystemException {
        set(attributeName, new BaseType(FieldType.FLOAT_ARRAY, value));
    }

    public void setDouble(String attributeName, Double value) throws EventSystemException {
        set(attributeName, new BaseType(FieldType.DOUBLE, value));
    }

    public void setFloat(String attributeName, Float value) throws EventSystemException {
        set(attributeName, new BaseType(FieldType.FLOAT, value));
    }

    public void setByte(String attributeName, Byte value)
            throws EventSystemException {
        set(attributeName, new BaseType(FieldType.BYTE, value));
    }
    
    /**
     * Sets the given attribute with a <tt>Boolean</tt> value given by <tt>aBool</tt>.
     *
     * @param attributeName the attribute to set
     * @param aBool         the boolean value to set
     * @throws NoSuchAttributeException
     * @throws NoSuchAttributeTypeException
     */
    public void setBoolean(String attributeName, Boolean aBool)
        throws EventSystemException {
        set(attributeName, new BaseType(FieldType.BOOLEAN, aBool));
    }

    /**
     * Set the given attribute with the <tt>Integer</tt> value given by <tt>aNumber</tt>.
     * This should be an <tt>unsigned short</tt>, but is an Integer because Java does not support unsigned types,
     * and a signed integer is needed to cover the range of an unsigned short.
     *
     * @param attributeName the attribute to set
     * @param aNumber       the value
     * @throws NoSuchAttributeException     if the attribute does not exist in the event
     * @throws NoSuchAttributeTypeException if the attribute type does not match the EventTemplateDB
     */
    public void setUInt16(String attributeName, Integer aNumber)
        throws EventSystemException {
        set(attributeName, new BaseType(FieldType.UINT16, aNumber));
    }

    /**
     * Set the given attribute with the <tt>Short</tt> value given by <tt>aNumber</tt>.
     *
     * @param attributeName the attribute to set
     * @param aNumber       the short value to set
     * @throws NoSuchAttributeException     if the attribute does not exist in the event
     * @throws NoSuchAttributeTypeException if the attribute type does not match the EventTemplateDB
     */
    public void setInt16(String attributeName, Short aNumber)
        throws EventSystemException {
        set(attributeName, new BaseType(FieldType.INT16, aNumber));
    }

    /**
     * Set the given attribute with the <tt>Long</tt> value given by <tt>aNumber</tt>.
     * This should be an <tt>unsigned int</tt>, but is an Long because Java does not support unsigned types,
     * and a signed long is needed to cover the range of an unsigned int.
     *
     * @param attributeName the attribute to set
     * @param aNumber       the value
     * @throws NoSuchAttributeException     if the attribute does not exist in the event
     * @throws NoSuchAttributeTypeException if the attribute type does not match the EventTemplateDB
     */
    public void setUInt32(String attributeName, Long aNumber)
        throws EventSystemException {
        set(attributeName, new BaseType(FieldType.UINT32, aNumber));
    }

    /**
     * Set the given attribute with the <tt>Integer</tt> value given by <tt>aNumber</tt>.
     *
     * @param attributeName the attribute to set
     * @param aNumber       the Integer value to set
     * @throws NoSuchAttributeException     if the attribute does not exist in the event
     * @throws NoSuchAttributeTypeException if the attribute type does not match the EventTemplateDB
     */
    public void setInt32(String attributeName, Integer aNumber)
        throws EventSystemException {
        set(attributeName, new BaseType(FieldType.INT32, aNumber));
    }

    /**
     * Set the given attribute with the <tt>Long</tt> value given by <tt>aNumber</tt>.
     *
     * @param attributeName the attribute to set
     * @param aNumber       the value
     * @throws NoSuchAttributeException     if the attribute does not exist in the event
     * @throws NoSuchAttributeTypeException if the attribute type does not match the EventTemplateDB
     */
    public void setUInt64(String attributeName, Long aNumber)
        throws EventSystemException {
        set(attributeName,
            new BaseType(FieldType.UINT64, BigInteger.valueOf(aNumber)));
    }

    /**
     * Set the given attribute with the <tt>BigInteger</tt> value given by <tt>aNumber</tt>.
     * This should be an <tt>unsigned long</tt>, but is an BigInteger because Java does not support unsigned types,
     * and a BigInteger is needed to cover the range of an unsigned long.
     *
     * @param attributeName the attribute to set
     * @param aNumber       the value
     * @throws NoSuchAttributeException     if the attribute does not exist in the event
     * @throws NoSuchAttributeTypeException if the attribute type does not match the EventTemplateDB
     */
    public void setUInt64(String attributeName, BigInteger aNumber)
        throws EventSystemException {
        set(attributeName, new BaseType(FieldType.UINT64, aNumber));
    }

    /**
     * Set the given attribute with the <tt>Long</tt> value given by <tt>aNumber</tt>.
     *
     * @param attributeName the attribute to set
     * @param aNumber       the Long value to set
     * @throws NoSuchAttributeException     if the attribute does not exist in the event
     * @throws NoSuchAttributeTypeException if the attribute type does not match the EventTemplateDB
     */
    public void setInt64(String attributeName, Long aNumber)
        throws EventSystemException {
        set(attributeName, new BaseType(FieldType.INT64, aNumber));
    }

    /**
     * Set the given attribute with a <tt>String</tt>
     *
     * @param attributeName the attribute to set
     * @param aString       the String value to set
     * @throws NoSuchAttributeException     if the attribute does not exist in the event
     * @throws NoSuchAttributeTypeException if the attribute type does not match the EventTemplateDB
     */
    public void setString(String attributeName, String aString)
        throws EventSystemException {
        set(attributeName, new BaseType(FieldType.STRING, aString));
    }

    /**
     * Set the given attribute with the <tt>ip address</tt> value given by <tt>address</tt>
     *
     * @param attributeName the attribute to set
     * @param address       the ip address in bytes
     * @throws NoSuchAttributeException     if the attribute does not exist in the event
     * @throws NoSuchAttributeTypeException if the attribute type does not match the EventTemplateDB
     * @deprecated
     */
    public void setIPAddress(String attributeName, byte[] address)
        throws EventSystemException {
        setIPAddress(attributeName, new IPAddress(address));
    }

    /**
     * Set the given attribute with the <tt>ip address</tt> value given by <tt>address</tt>
     *
     * @param attributeName the attribute to set
     * @param address       the ip address in bytes
     * @throws NoSuchAttributeException     if the attribute does not exist in the event
     * @throws NoSuchAttributeTypeException if the attribute type does not match the EventTemplateDB
     * @deprecated
     */
    public void setIPAddress(String attributeName, InetAddress address)
        throws EventSystemException {
        setIPAddress(attributeName, new IPAddress(address));
    }

    /**
     * Set the given attribute with the <tt>ip address</tt> value given by <tt>address</tt>
     *
     * @param attributeName the attribute to set
     * @param address       the ip address in bytes
     * @throws NoSuchAttributeException     if the attribute does not exist in the event
     * @throws NoSuchAttributeTypeException if the attribute type does not match the EventTemplateDB
     * @deprecated
     */
    public void setIPAddress(String attributeName, IPAddress address)
        throws EventSystemException {
        set(attributeName, new BaseType(FieldType.IPADDR, address));
    }

    /**
     * Serializes the Event into a byte array
     *
     * @return the serialized byte array
     * @throws EventSystemException if there is a bug in predicting the serialized size
     */
    public byte[] serialize() throws EventSystemException {
        /*
           * Serialization uses the following protocol
           * EVENTWORD,<number of elements>,ATTRIBUTEWORD,TYPETOKEN,
           * (UINT16|INT16|UINT32|INT32|UINT64|INT64|BOOLEAN|STRING)
           * ...ATTRIBUTEWORD,TYPETOKEN(UINT16|INT16|UINT32|INT32|
           * UINT64|INT64|BOOLEAN|STRING)
           *
           * The first attribute will always be the encoding if present.
           */
        byte[] bytes = new byte[this.bytesStoreSize];
        int offset = 0;
        int attributeCount = attributes.size();
        short encoding = DEFAULT_ENCODING;

        offset += Serializer.serializeEVENTWORD(name, bytes, offset);
        offset += Serializer.serializeUINT16((short) (attributeCount), bytes, offset);

        /*
           * Set the encoding attributes in the event
           */
        BaseType encodingBase = attributes.get(ENCODING);
        if (encodingBase != null) {
            Object encodingObj = encodingBase.getTypeObject();
            FieldType encodingType = encodingBase.getType();
            if (encodingObj != null) {
                if (encodingType == FieldType.INT16) {
                    encoding = (Short) encodingObj;
                    log.trace("Character encoding: " + encoding);
                    offset += Serializer.serializeATTRIBUTEWORD(ENCODING, bytes, offset);
                    offset += Serializer.serializeBYTE(encodingType.token, bytes, offset);
                    offset += Serializer.serializeUINT16(encoding, bytes, offset);
                }
            }
        }
        else {
            log.warn("Character encoding null in event " + name);
        }

        Enumeration<String> e = attributes.keys();
        while (e.hasMoreElements()) {
            String key = e.nextElement();
            if (key.equals(ENCODING)) {
                continue;
            }

            BaseType value = attributes.get(key);
            Object data = value.getTypeObject();
            FieldType type = value.getType();

            /* don't try to serialize nulls */
            if (data == null) {
                log.warn("Attribute " + key + " was null in event " + name);
                continue;
            }

            offset += Serializer.serializeATTRIBUTEWORD(key, bytes, offset);
            offset += Serializer.serializeBYTE(type.token, bytes, offset);
            offset += Serializer.serializeValue(type, data, encoding, bytes, offset);

            log.trace("Serialized attribute " + key);
        } // while(e.hasMoreElements())
        
        if (bytesStoreSize != offset) {
        	throw new EventSystemException("Expected to write "+bytesStoreSize+" bytes, but actually wrote "+offset);
        }

        return bytes;
    }

    /**
     * Deserialize the Event from byte array
     *
     * @param bytes the byte array containing a serialized Event
     * @throws EventSystemException
     */
    public void deserialize(byte[] bytes)
        throws EventSystemException {
        if (bytes == null) {
            return;
        }
        if (state == null) {
            state = new DeserializerState();
        }

        state.reset();
        setEventName(Deserializer.deserializeEVENTWORD(state, bytes));
        long num = Deserializer.deserializeUINT16(state, bytes);
        if (log.isTraceEnabled()) {
            log.trace("Event name = " + getEventName());
            log.trace("Number of attribute: " + num);
        }
        for (int i = 0; i < num; ++i) {
            String attribute = Deserializer.deserializeATTRIBUTEWORD(state, bytes);

            final FieldType type = FieldType.byToken(Deserializer.deserializeBYTE(state, bytes));
            if (log.isTraceEnabled()) {
                log.trace("Attribute: " + attribute);
                log.trace("Type: " + type);
                log.trace("State: " + state);
            }
            if (attribute != null) {
                if (i == 0 && attribute.equals(ENCODING)) {
                    if (type == FieldType.INT16) {
                        setEncoding(Deserializer.deserializeINT16(state, bytes));
                        continue;
                    }
                    else {
                        log.warn("Found encoding, but type was not int16 while deserializing");
                    }
                }

                switch (type) {
                    case BOOLEAN:
                        setBoolean(attribute, Deserializer.deserializeBOOLEAN(state, bytes));
                        break;
                    case BYTE:
                        setByte(attribute, Deserializer.deserializeBYTE(state, bytes));
                        break;
                    case UINT16:
                        setUInt16(attribute, Deserializer.deserializeUINT16(state, bytes));
                        break;
                    case INT16:
                        setInt16(attribute, Deserializer.deserializeINT16(state, bytes));
                        break;
                    case UINT32:
                        setUInt32(attribute, Deserializer.deserializeUINT32(state, bytes));
                        break;
                    case INT32:
                        setInt32(attribute, Deserializer.deserializeINT32(state, bytes));
                        break;
                    case FLOAT:
                        setFloat(attribute, Deserializer.deserializeFLOAT(state, bytes));
                        break;
                    case UINT64:
                        setUInt64(attribute, BigInteger.valueOf(Deserializer.deserializeUINT64(state, bytes)));
                        break;
                    case INT64:
                        setInt64(attribute, Deserializer.deserializeINT64(state, bytes));
                        break;
                    case DOUBLE:
                        setDouble(attribute, Deserializer.deserializeDOUBLE(state, bytes));
                        break;
                    case STRING:
                        setString(attribute, Deserializer.deserializeSTRING(state, bytes, encoding));
                        break;
                    case IPADDR:
                        setIPAddress(attribute, Deserializer.deserializeIPADDR(state, bytes));
                        break;
                    case STRING_ARRAY:
                        setStringArray(attribute, Deserializer.deserializeStringArray(state, bytes, encoding));
                        break;
                    case INT16_ARRAY:
                        setInt16Array(attribute, Deserializer.deserializeInt16Array(state, bytes));
                        break;
                    case INT32_ARRAY:
                        setInt32Array(attribute, Deserializer.deserializeInt32Array(state, bytes));
                        break;
                    case INT64_ARRAY:
                        setInt64Array(attribute, Deserializer.deserializeInt64Array(state, bytes));
                        break;
                    case UINT16_ARRAY:
                        setUInt16Array(attribute, Deserializer.deserializeUInt16Array(state, bytes));
                        break;
                    case UINT32_ARRAY:
                        setUInt32Array(attribute, Deserializer.deserializeUInt32Array(state, bytes));
                        break;
                    case UINT64_ARRAY:
                        setUInt64Array(attribute, Deserializer.deserializeUInt64Array(state, bytes));
                        break;
                    case BOOLEAN_ARRAY:
                        setBooleanArray(attribute, Deserializer.deserializeBooleanArray(state, bytes));
                        break;
                    case BYTE_ARRAY:
                        setByteArray(attribute, Deserializer.deserializeByteArray(state, bytes));
                        break;
                    case DOUBLE_ARRAY:
                        setDoubleArray(attribute, Deserializer.deserializeDoubleArray(state, bytes));
                        break;
                    case FLOAT_ARRAY:
                        setFloatArray(attribute, Deserializer.deserializeFloatArray(state, bytes));
                        break;
                    case IP_ADDR_ARRAY:
                        setIPAddressArray(attribute, Deserializer.deserializeIPADDRArray(state, bytes));
                        break;
                    default:
                        log.warn("Unknown type " + type + " in deserialization");
                }
            }
        } // for (int i =0 ...

    }

    /**
     * Returns a mutable copy of the event.  This is a SLOW operation.
     *
     * @return Event the Event object
     * @throws NoSuchEventException         if the Event does not exist in the EventTemplateDB
     * @throws NoSuchAttributeException     if the attribute does not exist in this event
     * @throws NoSuchAttributeTypeException if there is an attribute that does not match a type in the EventTemplateDB
     */
    public Event copy() throws EventSystemException {
        /* match the type-checking of the original event */
        Event evt = new Event(name, isValidating(), getEventTemplateDB());
        for (Enumeration<String> e = attributes.keys(); e.hasMoreElements(); ) {
            String key = e.nextElement();
            BaseType value = attributes.get(key);
            evt.set(key, value);
        }

        return evt;
    }

    /**
     * Returns a String representation of this event
     *
     * @return a String return of this event.
     */
    public String toString() {
        if (name == null) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        sb.append(name);
        sb.append("\n{\n");

        int i = 0;
        String[] keys = new String[attributes.size()];
        for (Enumeration<String> e = attributes.keys(); e.hasMoreElements(); ) {
            keys[i++] = e.nextElement();
        }

        Arrays.sort(keys);

        for (i = 0; i < attributes.size(); ++i) {
            BaseType value = attributes.get(keys[i]);
            if (isValidating() && getEventTemplateDB() != null) {
                if (getEventTemplateDB().checkTypeForAttribute(name, keys[i], FieldType.UINT64)) {
                    try {
                        sb.append("\t")
                        .append(keys[i])
                        .append(" = ")
                        .append(NumberCodec.toHexString(getUInt64(keys[i])))
                        .append(";\n");
                    }
                    catch (EventSystemException exc) {
                        log.warn("Event.toString : ", exc);
                    }
                }
                else {
                    sb.append("\t").append(keys[i]).append(" = ").append(value).append(";\n");
                }
            }
            else {
                sb.append("\t").append(keys[i]).append(" = ").append(value).append(";\n");
            }
        } // for(i = 0; i < attributes.size() ...

        sb.append("}");
        return sb.toString();
    }

    public String toOneLineString() {
        return toString().replaceAll("\n", " ");
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (getClass().getName().equals(o.getClass().getName())) {
            return toString().equals(o.toString());
        }
        else {
            return false;
        }
    }

    /**
     * This method can be used to validate an event after it has been created.
     *
     * @throws ValidationExceptions A list of validation errors
     */
    public void validate() throws ValidationExceptions {
        ValidationExceptions ve = new ValidationExceptions(name);

        EventTemplateDB templ = getEventTemplateDB();
        if (templ == null) {
            ve.addException(new EventSystemException("No template defined."));
            throw ve;
        }
        if (!templ.checkForEvent(name)) {
            ve.addException(new NoSuchEventException("Event " + name + " does not exist in event definition"));
            throw ve;
        }
        for (String key : attributes.keySet()) {
            if (!templ.checkForAttribute(name, key)) {
                ve.addException(new NoSuchAttributeException("Attribute " + key + " does not exist for event " + name));
                continue;
            }
            Object value;
            try {
                value = get(key);
            }
            catch (NoSuchAttributeException e) {
                ve.addException(e);
                continue;
            }

            final BaseType expected = templ.getBaseTypeForObjectAttribute(name, key, value);
            if (! expected.getType().isCompatibleWith(value)) {
                ve.addException(new NoSuchAttributeTypeException("Wrong type '" + value.getClass().getName() + "' for " + name + "." + key));
            }
        }
        
        for (Entry<String,BaseType> entry : templ.getEvents().get(name).entrySet()) {
            final String   key = entry.getKey();
            if (entry.getValue().isRequired()) {
                if (!attributes.containsKey(key)) {
                    ve.addException(new AttributeRequiredException(key));
                }
            }
        }

        if (ve.hasExceptions()) {
            throw ve;
        }
    }

    public FieldType getType(String field) {
        final BaseType bt = attributes.get(field);
        return bt==null ? null : bt.getType();
    }

}
