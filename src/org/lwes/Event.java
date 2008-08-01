package org.lwes;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import org.lwes.db.EventTemplateDB;
import org.lwes.serializer.Serializer;
import org.lwes.serializer.Deserializer;
import org.lwes.serializer.DeserializerState;
import org.lwes.util.CharacterEncoding;
import org.lwes.util.IPAddress;
import org.lwes.util.Log;
import org.lwes.util.NumberCodec;

public class Event {
	/**
	 * Encoding variables
	 */
	public static final short ISO_8859_1 = 0;
	public static final short UTF_8 = 1;
	public static final short DEFAULT_ENCODING = UTF_8;
	public static final CharacterEncoding[] ENCODING_STRINGS = {
			CharacterEncoding.ISO_8859_1, CharacterEncoding.UTF_8 };
	private static final String ENCODING = "enc";

	/**
	 * Event data
	 */
	private ConcurrentHashMap<String, BaseType> attributes = new ConcurrentHashMap<String, BaseType>();
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

	public Event(String eventName, boolean validate, EventTemplateDB eventTemplateDB) 
		throws NoSuchEventException, NoSuchAttributeException, NoSuchAttributeTypeException {
		this(eventName, validate, eventTemplateDB, DEFAULT_ENCODING);
	}
	
	/**
	 * Create an event called <tt>eventName</tt>
	 * @param eventName the name of the event
	 * @param validate true if the EventTemplateDB should be checked for types before all mutations
	 * @param encoding the character encoding used by the event
	 * @exception EventSystemException if the Event does not exist in the EventTemplateDB
	 */
	public Event(String eventName, boolean validate, EventTemplateDB eventTemplateDB, short encoding)
		throws NoSuchEventException, NoSuchAttributeException, NoSuchAttributeTypeException {
		setEventTemplateDB(eventTemplateDB);
		validating = validate;
		setEventName(eventName);
		setEncoding(encoding);
	}

	/**
	 * Returns an enumeration of all the event attribute names
	 * @return an enumeration of attribute strings
	 */
	public Enumeration<String> getEventAttributeNames() {
		if(attributes == null) return null;
		
		return attributes.keys();
	}
	
	/**
	 * Returns the number of attributes in the event
	 * @return number of attributes in the event
	 */
	public int size() {
		if(attributes == null) return 0;
		return attributes.size();
	}
	
	/**
	 * Returns true if the event validates against the EventTemplateDB before making changes
	 * @return the validating state
	 */
	public boolean isValidating() {
		return this.validating;
	}
	
	/**
	 * Set to true if the event should validate against the EventTemplateDB before making changes
	 * @param validate the validating value
	 */
	public void setValidating(boolean validate) {
		this.validating = validate;
	}

	/**
	 * Returns the EventTemplateDB for this event, used for validation of types and attributes.
	 * @return the EventTemplateDB
	 */
	public EventTemplateDB getEventTemplateDB() {
		return this.eventTemplateDB;
	}
	
	/**
	 * Sets the EventTemplateDB for this event, used for validation of types and attributes.
	 * @param eventTemplateDB the EventTemplateDB to be used for validation
	 */
	public void setEventTemplateDB(EventTemplateDB eventTemplateDB) {
		this.eventTemplateDB = eventTemplateDB;
	}

	/**
	 * Returns the name of the event
	 * @return the name of the event
	 */
	public synchronized String getEventName() {
		return this.name;
	}
	
	/**
	 * Sets the name of the Event
	 * @param eventName the name of the event
	 * @exception NoSuchEventException if the event is validating and does not exist in the EventTemplateDB
	 */
	public synchronized void setEventName(String name) throws NoSuchEventException {
		if( isValidating() && getEventTemplateDB() != null) {
			if(getEventTemplateDB().checkForEvent(name) == false) {
				throw new NoSuchEventException("Event " + name + " does not exist in event definition");
			}
		}
		
		/* determine if we already have the name and are just resetting it */
		if( this.name != null ) {
			bytesStoreSize -= (this.name.length() + 1 + 2);
		}
		
		bytesStoreSize += (name.length() + 1 + 2);
		
		this.name = name;
	}
	
	/**
	 * Get the character encoding for this event
	 * @return the encoding
	 */
	public short getEncoding() {
		return this.encoding;
	}
	
	/**
	 * Set the character encoding for event strings
	 * 
	 * @param encoding the character encoding
	 * @exception NoSuchAttributeTypeException if the type for the encoding attribute does not exist
	 * @exception NoSuchAttributeException if the encoding attribute does not exist
	 */
	public void setEncoding(short encoding) throws NoSuchAttributeException, NoSuchAttributeTypeException {
		this.encoding = encoding;
		setInt16(ENCODING, this.encoding);
	}
	
	/**
	 * Generic accessor, checks if an attribute exists and returns its value.  The user must do their
	 * own type checking.
	 * 
	 * @param attributeName name of the attribute to lookup
	 * @return the object poitned to by attributeName
	 * @exception NoSuchAttributeException if the attribute does not exist in this event
	 */
	public Object get(String attributeName) throws NoSuchAttributeException {
		if(attributes == null) return null;
		
		if(attributes.containsKey(attributeName)) {
			return ((BaseType) (attributes.get(attributeName))).getTypeObject();
		}
		
		if( isValidating() && getEventTemplateDB() != null ) {
			if( getEventTemplateDB().checkForAttribute(name, attributeName)) {
				return null;
			} else {
				throw new NoSuchAttributeException("Attribute " + attributeName + " does not exist for event " + name);
			}
		}
		
		return null;
	}
	
	/**
	 * Accessor that returns a boolean value for attribute <tt>attributeName</tt>
	 * 
	 * @param attributeName the name of the attribute to fetch
	 * @return the boolean value
	 * @exception AttributeNotSetException if the attribute has not been set in this event
	 * @exception NoSuchAttributeException if the attribute does not exist in this event
	 */
	public boolean getBoolean(String attributeName) throws AttributeNotSetException, NoSuchAttributeException {
		Object o = get(attributeName);
		if( o != null) {
			return ((Boolean) o).booleanValue();
		} else {
			throw new AttributeNotSetException("Attribute " + attributeName + " not set");
		}
	}
	
	/**
	 * Accessor that returns an <tt>unsigned short</tt>, in the guise of an <tt>int</tt>, for attribute <tt>attributeName</tt>
	 * 
	 * @param attributeName the name of the attribute to fetch
	 * @return the unsigned short as an int
	 * @exception AttributeNotSetException if the attribute has not been set in this event
	 * @exception NoSuchAttributeException if the attribute does not exist in this event
	 */
	public int getUInt16(String attributeName) throws AttributeNotSetException, NoSuchAttributeException {
		Object o = get(attributeName);
		if( o != null) {
			return ((Integer) o).intValue();
		} else {
			throw new AttributeNotSetException("Attribute " + attributeName + " not set");
		}
	}

	/**
	 * Accessor that returns an <tt>short</tt>, for attribute <tt>attributeName</tt>
	 * 
	 * @param attributeName the name of the attribute to fetch
	 * @return the short value
	 * @exception AttributeNotSetException if the attribute has not been set in this event
	 * @exception NoSuchAttributeException if the attribute does not exist in this event
	 */
	public short getInt16(String attributeName) throws AttributeNotSetException, NoSuchAttributeException {
		Object o = get(attributeName);
		if( o != null) {
			return ((Short) o).shortValue();
		} else {
			throw new AttributeNotSetException("Attribute " + attributeName + " not set");
		}
	}

	/**
	 * Accessor that returns an <tt>unsigned int</tt>, in the guise of an <tt>long</tt>, for attribute <tt>attributeName</tt>
	 * 
	 * @param attributeName the name of the attribute to fetch
	 * @return the unsigned int as a long
	 * @exception AttributeNotSetException if the attribute has not been set in this event
	 * @exception NoSuchAttributeException if the attribute does not exist in this event
	 */
	public long getUInt32(String attributeName) throws AttributeNotSetException, NoSuchAttributeException {
		Object o = get(attributeName);
		if( o != null) {
			return ((Long) o).longValue();
		} else {
			throw new AttributeNotSetException("Attribute " + attributeName + " not set");
		}
	}

	/**
	 * Accessor that returns an <tt>int</tt>, for attribute <tt>attributeName</tt>
	 * 
	 * @param attributeName the name of the attribute to fetch
	 * @return the int value
	 * @exception AttributeNotSetException if the attribute has not been set in this event
	 * @exception NoSuchAttributeException if the attribute does not exist in this event
	 */
	public int getInt32(String attributeName) throws AttributeNotSetException, NoSuchAttributeException {
		Object o = get(attributeName);
		if( o != null) {
			return ((Integer) o).intValue();
		} else {
			throw new AttributeNotSetException("Attribute " + attributeName + " not set");
		}
	}	

	/**
	 * Accessor that returns an <tt>unsigned long</tt>, in the guise of an <tt>BigInteger</tt>, for attribute <tt>attributeName</tt>
	 * 
	 * @param attributeName the name of the attribute to fetch
	 * @return the unsigned long as a BigInteger
	 * @exception AttributeNotSetException if the attribute has not been set in this event
	 * @exception NoSuchAttributeException if the attribute does not exist in this event
	 */
	public BigInteger getUInt64(String attributeName) throws AttributeNotSetException, NoSuchAttributeException {
		Object o = get(attributeName);
		if( o != null) {
			return (BigInteger) o;
		} else {
			throw new AttributeNotSetException("Attribute " + attributeName + " not set");
		}
	}
	
	
	/**
	 * Accessor that returns an <tt>long</tt>, for attribute <tt>attributeName</tt>
	 * 
	 * @param attributeName the name of the attribute to fetch
	 * @return the long value
	 * @exception AttributeNotSetException if the attribute has not been set in this event
	 * @exception NoSuchAttributeException if the attribute does not exist in this event
	 */
	public long getInt64(String attributeName) throws AttributeNotSetException, NoSuchAttributeException {
		Object o = get(attributeName);
		if( o != null) {
			return ((Long) o).longValue();
		} else {
			throw new AttributeNotSetException("Attribute " + attributeName + " not set");
		}
	}	

	/**
	 * Accessor that returns an <tt>String</tt>, for attribute <tt>attributeName</tt>
	 * 
	 * @param attributeName the name of the attribute to fetch
	 * @return the String value
	 * @exception AttributeNotSetException if the attribute has not been set in this event
	 * @exception NoSuchAttributeException if the attribute does not exist in this event
	 */
	public String getString(String attributeName) throws AttributeNotSetException, NoSuchAttributeException {
		Object o = get(attributeName);
		if( o != null) {
			return (String) o;
		} else {
			throw new AttributeNotSetException("Attribute " + attributeName + " not set");
		}
	}	
	
	/**
	 * Accessor that returns an <tt>InetAddress</tt>, for attribute <tt>attributeName</tt>
	 * 
	 * @param attributeName the name of the attribute to fetch
	 * @return the InetAddress value
	 * @exception AttributeNotSetException if the attribute has not been set in this event
	 * @exception NoSuchAttributeException if the attribute does not exist in this event
	 */
	public InetAddress getInetAddress(String attributeName) throws AttributeNotSetException, NoSuchAttributeException {
		Object o = get(attributeName);
		if( o != null) {
			return ((IPAddress) o).toInetAddress();
		} else {
			throw new AttributeNotSetException("Attribute " + attributeName + " not set");
		}
	}		

	/**
	 * Accessor that returns an IP address in bytes, for attribute <tt>attributeName</tt>
	 * 
	 * @param attributeName the name of the attribute to fetch
	 * @return the IP address in bytes
	 * @exception AttributeNotSetException if the attribute has not been set in this event
	 * @exception NoSuchAttributeException if the attribute does not exist in this event
	 */
	public byte[] getIPAddress(String attributeName) throws AttributeNotSetException, NoSuchAttributeException {
		Object o = get(attributeName);
		if( o != null) {
			return ((IPAddress) o).getInetAddressAsBytes();
		} else {
			throw new AttributeNotSetException("Attribute " + attributeName + " not set");
		}
	}		
	
	
	/**
	 * Set the object's attribute <tt>attributeName</tt> with the Object given
	 * 
	 * @param attributeName the name of the attribute to set
	 * @param anObject the object to set the attribute with
	 * @exception NoSuchAttributeException if the attribute does not exist in this event
	 * @exception NoSuchAttributeTypeException if there is an attribute with an undefined type
	 */
	public void set(String attributeName, Object attributeValue) throws NoSuchAttributeException, NoSuchAttributeTypeException {
		if(isValidating() && getEventTemplateDB() != null) {
			if(getEventTemplateDB().checkForAttribute(getEventName(), attributeName)) {
				BaseType bt = getEventTemplateDB().getBaseTypeForObjectAttribute(getEventName(),
						attributeName, attributeValue);
				set(attributeName, bt);
			}
		} else {
			throw new NoSuchAttributeException("Must be able to check the EventTemplateDB to use set(String,Object)");
		}
	}
	
	/**
	 * Private method to set a BaseType
	 * @param attribute the name of the attribute to set
	 * @param anObject the BaseType to set in the event
	 * @exception NoSuchAttributeException if the attribute does not exist in this event
	 * @exception NoSuchAttributeTypeException if there is an attribute with an undefined type
	 */
	private void set(String attribute, BaseType anObject) throws NoSuchAttributeException, NoSuchAttributeTypeException {
		if( isValidating() && getEventTemplateDB() != null ) {
			if(getEventTemplateDB().checkForAttribute(name, attribute)) {
				if(!getEventTemplateDB().checkTypeForAttribute(name, attribute, anObject)) {
					throw new NoSuchAttributeTypeException("Wrong type '" + anObject.getTypeName() + 
							"' for " + name + "." + attribute);
				}
			} else {
				throw new NoSuchAttributeException("Attribute " + attribute + " does not exist for event " + name);
			}
		}
		
		if(anObject.getTypeObject() != null) {
			BaseType oldObject = null;
			if((oldObject = (BaseType) attributes.remove(attribute)) != null) {
				bytesStoreSize -= (attribute.length()+1) + oldObject.bytesStoreSize(encoding);
			}
			
			bytesStoreSize += (attribute.length()+1) + anObject.bytesStoreSize(encoding);
			attributes.put(attribute, anObject);
		}
	}
	
	/**
	 * Sets the given attribute with a <tt>boolean</tt> value given by <tt>aBool</tt>.
	 * 
	 * @param attributeName the attribute to set
	 * @param aBool the boolean value to set
	 * @throws NoSuchAttributeException if the attribute does not exist in the event
	 * @throws NoSuchAttributeTypeException if the attribute type does not match the EventTemplateDB
	 */
	public void setBoolean(String attributeName, boolean aBool) throws NoSuchAttributeException, NoSuchAttributeTypeException {
		setBoolean(attributeName, new Boolean(aBool));
	}
	
	/**
	 * Sets the given attribute with a <tt>Boolean</tt> value given by <tt>aBool</tt>.
	 * 
	 * @param attributeName the attribute to set
	 * @param aBool the boolean value to set
	 * @throws NoSuchAttributeException
	 * @throws NoSuchAttributeTypeException
	 */
	public void setBoolean(String attributeName, Boolean aBool) throws NoSuchAttributeException, NoSuchAttributeTypeException {
		set(attributeName, new BaseType( TypeID.BOOLEAN_STRING, TypeID.BOOLEAN_TOKEN, aBool));
	}
	
	/**
	 * Set the given attribute with the <tt>unsigned short</tt> value given by <tt>aNumber</tt>.
	 * Because Java does not support unsigned types, we must use a signed int to cover the range of unsigned short.
	 * 
	 * @param attributeName the attribute to set
	 * @param aNumber the unsigned short value as an integer
	 * @exception NoSuchAttributeException if the attribute does not exist in the event
	 * @exception NoSuchAttributeTypeException if the attribute type does not match the EventTemplateDB
	 */
	public void setUInt16(String attributeName, int aNumber) throws NoSuchAttributeException, NoSuchAttributeTypeException {
		setUInt16(attributeName, new Integer(aNumber));
	}

	/**
	 * Set the given attribute with the <tt>Integer</tt> value given by <tt>aNumber</tt>.
	 * This should be an <tt>unsigned short</tt>, but is an Integer because Java does not support unsigned types,
	 * and a signed integer is needed to cover the range of an unsigned short.
	 * 
	 * @param attributeName the attribute to set
	 * @param aNumber the value
	 */
	public void setUInt16(String attributeName, Integer aNumber) throws NoSuchAttributeException, NoSuchAttributeTypeException {
		set(attributeName, new BaseType(TypeID.UINT16_STRING, TypeID.UINT16_TOKEN, aNumber));
	}

	/**
	 * Set the given attribute with the <tt>short</tt> value given by <tt>aNumber</tt>.
	 * 
	 * @param attributeName the attribute to set
	 * @param aNumber the short value to set
	 * @exception NoSuchAttributeException if the attribute does not exist in the event
	 * @exception NoSuchAttributeTypeException if the attribute type does not match the EventTemplateDB
	 */
	public void setInt16(String attributeName, short aNumber) throws NoSuchAttributeException, NoSuchAttributeTypeException {
		setInt16(attributeName, new Short(aNumber));
	}

	/**
	 * Set the given attribute with the <tt>Short</tt> value given by <tt>aNumber</tt>.
	 * 
	 * @param attributeName the attribute to set
	 * @param aNumber the short value to set
	 * @exception NoSuchAttributeException if the attribute does not exist in the event
	 * @exception NoSuchAttributeTypeException if the attribute type does not match the EventTemplateDB
	 */
	public void setInt16(String attributeName, Short aNumber) throws NoSuchAttributeException, NoSuchAttributeTypeException {
		set(attributeName, new BaseType(TypeID.INT16_STRING, TypeID.INT16_TOKEN, aNumber));
	}
	
	/**
	 * Set the given attribute with the <tt>unsigned int</tt> value given by <tt>aNumber</tt>.
	 * Because Java does not support unsigned types, we must use a signed long to cover the range of an unsigned int.
	 * 
	 * @param attributeName the attribute to set
	 * @param aNumber the unsigned int value as a long
	 * @exception NoSuchAttributeException if the attribute does not exist in the event
	 * @exception NoSuchAttributeTypeException if the attribute type does not match the EventTemplateDB
	 */
	public void setUInt32(String attributeName, long aNumber) throws NoSuchAttributeException, NoSuchAttributeTypeException {
		setUInt32(attributeName, new Long(aNumber));
	}

	/**
	 * Set the given attribute with the <tt>Long</tt> value given by <tt>aNumber</tt>.
	 * This should be an <tt>unsigned int</tt>, but is an Long because Java does not support unsigned types,
	 * and a signed long is needed to cover the range of an unsigned int.
	 * 
	 * @param attributeName the attribute to set
	 * @param aNumber the value
	 */
	public void setUInt32(String attributeName, Long aNumber) throws NoSuchAttributeException, NoSuchAttributeTypeException {
		set(attributeName, new BaseType(TypeID.UINT32_STRING, TypeID.UINT32_TOKEN, aNumber));
	}	

	/**
	 * Set the given attribute with the <tt>int</tt> value given by <tt>aNumber</tt>.
	 * 
	 * @param attributeName the attribute to set
	 * @param aNumber the integer value to set
	 * @exception NoSuchAttributeException if the attribute does not exist in the event
	 * @exception NoSuchAttributeTypeException if the attribute type does not match the EventTemplateDB
	 */
	public void setInt32(String attributeName, int aNumber) throws NoSuchAttributeException, NoSuchAttributeTypeException {
		setInt32(attributeName, new Integer(aNumber));
	}

	/**
	 * Set the given attribute with the <tt>Integer</tt> value given by <tt>aNumber</tt>.
	 * 
	 * @param attributeName the attribute to set
	 * @param aNumber the Integer value to set
	 * @exception NoSuchAttributeException if the attribute does not exist in the event
	 * @exception NoSuchAttributeTypeException if the attribute type does not match the EventTemplateDB
	 */
	public void setInt32(String attributeName, Integer aNumber) throws NoSuchAttributeException, NoSuchAttributeTypeException {
		set(attributeName, new BaseType(TypeID.INT32_STRING, TypeID.INT32_TOKEN, aNumber));
	}

	/**
	 * Set the given attribute with the <tt>unsigned long</tt> value given by <tt>aNumber</tt>.
	 * 
	 * @param attributeName the attribute to set
	 * @param aNumber the value
	 * @exception NoSuchAttributeException if the attribute does not exist in the event
	 * @exception NoSuchAttributeTypeException if the attribute type does not match the EventTemplateDB
	 */
	public void setUInt64(String attributeName, long aNumber) throws NoSuchAttributeException, NoSuchAttributeTypeException {
		set(attributeName, new BaseType(TypeID.UINT64_STRING, TypeID.UINT64_TOKEN, BigInteger.valueOf(aNumber)));
	}			

	/**
	 * Set the given attribute with the <tt>Long</tt> value given by <tt>aNumber</tt>.
	 * 
	 * @param attributeName the attribute to set
	 * @param aNumber the value
	 * @exception NoSuchAttributeException if the attribute does not exist in the event
	 * @exception NoSuchAttributeTypeException if the attribute type does not match the EventTemplateDB
	 */
	public void setUInt64(String attributeName, Long aNumber) throws NoSuchAttributeException, NoSuchAttributeTypeException {
		set(attributeName, new BaseType(TypeID.UINT64_STRING, TypeID.UINT64_TOKEN, BigInteger.valueOf(aNumber.longValue())));
	}			
	
	/**
	 * Set the given attribute with the <tt>BigInteger</tt> value given by <tt>aNumber</tt>.
	 * This should be an <tt>unsigned long</tt>, but is an BigInteger because Java does not support unsigned types,
	 * and a BigInteger is needed to cover the range of an unsigned long.
	 * 
	 * @param attributeName the attribute to set
	 * @param aNumber the value
	 */
	public void setUInt64(String attributeName, BigInteger aNumber) throws NoSuchAttributeException, NoSuchAttributeTypeException {
		set(attributeName, new BaseType(TypeID.UINT64_STRING, TypeID.UINT64_TOKEN, aNumber));
	}		
	
	/**
	 * Set the given attribute with the <tt>long</tt> value given by <tt>aNumber</tt>.
	 * 
	 * @param attributeName the attribute to set
	 * @param aNumber the long value to set
	 * @exception NoSuchAttributeException if the attribute does not exist in the event
	 * @exception NoSuchAttributeTypeException if the attribute type does not match the EventTemplateDB
	 */
	public void setInt64(String attributeName, long aNumber) throws NoSuchAttributeException, NoSuchAttributeTypeException {
		setInt64(attributeName, new Long(aNumber));
	}

	/**
	 * Set the given attribute with the <tt>Long</tt> value given by <tt>aNumber</tt>.
	 * 
	 * @param attributeName the attribute to set
	 * @param aNumber the Long value to set
	 * @exception NoSuchAttributeException if the attribute does not exist in the event
	 * @exception NoSuchAttributeTypeException if the attribute type does not match the EventTemplateDB
	 */
	public void setInt64(String attributeName, Long aNumber) throws NoSuchAttributeException, NoSuchAttributeTypeException {
		set(attributeName, new BaseType(TypeID.INT64_STRING, TypeID.INT64_TOKEN, aNumber));
	}	
	
	/**
	 * Set the given attribute with a <tt>String</tt>
	 * 
	 * @param attributeName the attribute to set
	 * @param aString the String value to set
	 * @exception NoSuchAttributeException if the attribute does not exist in the event
	 * @exception NoSuchAttributeTypeException if the attribute type does not match the EventTemplateDB
	 */
	public void setString(String attributeName, String aString) throws NoSuchAttributeException, NoSuchAttributeTypeException {
		set(attributeName, new BaseType (TypeID.STRING_STRING, TypeID.STRING_TOKEN, aString));
	}
	
	/**
	 * Set the given attribute with the <tt>ip address</tt> value given by <tt>address</tt>
	 * 
	 * @param attributeName the attribute to set
	 * @param address the ip address in bytes
	 * @exception NoSuchAttributeException if the attribute does not exist in the event
	 * @exception NoSuchAttributeTypeException if the attribute type does not match the EventTemplateDB
	 */
	public void setIPAddress(String attributeName, byte[] address) throws NoSuchAttributeException, NoSuchAttributeTypeException {
		setIPAddress(attributeName, new IPAddress(address));
	}

	/**
	 * Set the given attribute with the <tt>ip address</tt> value given by <tt>address</tt>
	 * 
	 * @param attributeName the attribute to set
	 * @param address the ip address in bytes
	 * @exception NoSuchAttributeException if the attribute does not exist in the event
	 * @exception NoSuchAttributeTypeException if the attribute type does not match the EventTemplateDB
	 */
	public void setIPAddress(String attributeName, InetAddress address) throws NoSuchAttributeException, NoSuchAttributeTypeException {
		setIPAddress(attributeName, new IPAddress(address));
	}	
	
	/**
	 * Set the given attribute with the <tt>ip address</tt> value given by <tt>address</tt>
	 * 
	 * @param attributeName the attribute to set
	 * @param address the ip address in bytes
	 * @exception NoSuchAttributeException if the attribute does not exist in the event
	 * @exception NoSuchAttributeTypeException if the attribute type does not match the EventTemplateDB
	 */
	public void setIPAddress(String attributeName, IPAddress address) throws NoSuchAttributeException, NoSuchAttributeTypeException {
		set(attributeName, new BaseType( TypeID.IPADDR_STRING, TypeID.IPADDR_TOKEN, address));
	}
		
	/**
	 * Serializes the Event into a byte array
	 * 
	 * @return the serialized byte array
	 */
	public byte[] serialize() {
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
		int attributeCount = 0;
		short encoding = DEFAULT_ENCODING;
		
		if(attributes != null) attributeCount = attributes.size(); 		
		
		offset += Serializer.serializeEVENTWORD(name, bytes, offset);
		offset += Serializer.serializeUINT16((short)(attributeCount), bytes, offset);
		
		/* 
		 * Set the encoding attributes in the event
		 */
		BaseType encodingBase = (BaseType) attributes.get(ENCODING);
		if(encodingBase != null) {
			Object encodingObj = encodingBase.getTypeObject();
			byte encodingType = encodingBase.getTypeToken();
			if(encodingObj != null) {
				if(encodingType == TypeID.INT16_TOKEN) {
					encoding = ((Short) encodingObj).shortValue();
					Log.trace("Character encoding: " + encoding);
					offset += Serializer.serializeATTRIBUTEWORD(ENCODING, bytes, offset);
					offset += Serializer.serializeBYTE(encodingType, bytes, offset);
					offset += Serializer.serializeUINT16(encoding, bytes, offset);
				}
			}
		} else {
			Log.warning("Character encoding null in event " + name);
		}
		
		if(attributes != null) {
			Enumeration e = attributes.keys();
			while(e.hasMoreElements()) {
				String key = (String) e.nextElement();
				if(key == ENCODING) {
					continue;
				}
				
				BaseType value = (BaseType) attributes.get(key);
				Object data = value.getTypeObject();
				byte typeToken = value.getTypeToken();
				
				/* don't try to serialize nulls */
				if(data == null) {
					Log.warning("Attribute " + key + " was null in event " + name);
					continue;
				}
				
				offset += Serializer.serializeATTRIBUTEWORD(key, bytes, offset);
				offset += Serializer.serializeBYTE(typeToken, bytes, offset);
				
				switch(typeToken) {
				case TypeID.BOOLEAN_TOKEN:
					offset += Serializer.serializeBOOLEAN(((Boolean) data).booleanValue(), bytes, offset);
					break;
				case TypeID.UINT16_TOKEN:
					offset += Serializer.serializeUINT16(((Integer) data).intValue(), bytes, offset);
					break;
				case TypeID.INT16_TOKEN:
					offset += Serializer.serializeINT16(((Short) data).shortValue(), bytes, offset);
					break;
				case TypeID.UINT32_TOKEN:
					offset += Serializer.serializeUINT32(((Long) data).longValue(), bytes, offset);
					break;
				case TypeID.INT32_TOKEN:
					offset += Serializer.serializeINT32(((Integer) data).intValue(), bytes, offset);
					break;
				case TypeID.UINT64_TOKEN:
					offset += Serializer.serializeUINT64((BigInteger) data, bytes, offset);
					break;
				case TypeID.INT64_TOKEN:
					offset += Serializer.serializeINT64(((Long)data).longValue(), bytes, offset);
					break;
				case TypeID.STRING_TOKEN:
					offset += Serializer.serializeSTRING(((String) data), bytes, offset, encoding);
					break;
				case TypeID.IPADDR_TOKEN:
					offset += Serializer.serializeIPADDR(((IPAddress) data), bytes, offset);
					break;
				default:
					Log.warning("Unknown BaseType token: " + typeToken);
					break;											
				} // switch(typeToken)
				
				Log.trace("Serialized attribute " + key);
			} // while(e.hasMoreElements())
		} // if(attributes != null)

		return bytes;
	}
	
	/**
	 * Deserialize the Event from byte array
	 * @param bytes the byte array containing a serialized Event
	 */
	public void deserialize(byte[] bytes) throws NoSuchEventException, NoSuchAttributeException, NoSuchAttributeTypeException {
		if(bytes == null) return;		
		if(state == null) state = new DeserializerState();
		
		state.reset();
		setEventName(Deserializer.deserializeEVENTWORD(state, bytes));
		long num = Deserializer.deserializeUINT16(state, bytes);
		Log.trace("Event name = " + getEventName());
		Log.trace("Number of attribute: " + num);
		
		for(int i=0; i<num; ++i) {
			String attribute = Deserializer.deserializeATTRIBUTEWORD(state, bytes);
			
			byte type = Deserializer.deserializeBYTE(state, bytes);
			Log.trace("Attribute: " + attribute);
			Log.trace("Type: " + TypeID.byteIDToString(type));
			Log.trace("State: " + state);
			
			if(attribute != null) {
				if(i == 0 && attribute.equals(ENCODING)) {
					if(type == TypeID.INT16_TOKEN) {
						setEncoding(Deserializer.deserializeINT16(state, bytes));
						continue;
					} else {
						Log.warning("Found encoding, but type was not int16 while deserializing");
					}
				}
				
				switch(type) {
				case TypeID.BOOLEAN_TOKEN:
					boolean aBool = Deserializer.deserializeBOOLEAN(state, bytes);
					setBoolean(attribute, aBool);
					break;
				case TypeID.UINT16_TOKEN:
					int uShort = Deserializer.deserializeUINT16(state,bytes);
					setUInt16(attribute,uShort);
					break;
				case TypeID.INT16_TOKEN:
					short aShort = Deserializer.deserializeINT16(state, bytes);
					setInt16(attribute,aShort);
					break;
				case TypeID.UINT32_TOKEN:
					long uInt = Deserializer.deserializeUINT32(state, bytes);
					setUInt32(attribute, uInt);
					break;
				case TypeID.INT32_TOKEN:
					int aInt = Deserializer.deserializeINT32(state, bytes);
					setInt32(attribute, aInt);
					break;
				case TypeID.UINT64_TOKEN:
					long uLong = Deserializer.deserializeUINT64(state, bytes);
					setUInt64(attribute, BigInteger.valueOf(uLong));
					break;
				case TypeID.INT64_TOKEN:
					long aLong = Deserializer.deserializeINT64(state, bytes);
					setInt64(attribute, aLong);
					break;
				case TypeID.IPADDR_TOKEN:
					byte[] inetAddress = Deserializer.deserializeIPADDR(state, bytes);
					setIPAddress(attribute, inetAddress);
					break;
				default:
					Log.warning("Unknown type " + type + " in deserialization");
				}
			}
		} // for (int i =0 ...
		
	}
	
	/**
	 * Returns a mutable copy of the event.  This is a SLOW operation.
	 * 
	 * @return Event the Event object
	 * @exception NoSuchEventException if the Event does not exist in the EventTemplateDB
	 * @exception NoSuchAttributeException if the attribute does not exist in this event
	 * @exception NoSuchAttributeTypeException if there is an attribute that does not match a type in the EventTemplateDB
	 */
	public Event copy() throws NoSuchEventException, NoSuchAttributeException, NoSuchAttributeTypeException {
		/* match the type-checking of the original event */
		Event evt = new Event(name, isValidating(), getEventTemplateDB());
		for( Enumeration e = attributes.keys(); e.hasMoreElements(); ) {
			String key = (String) e.nextElement();
			BaseType value = (BaseType) (attributes.get(key));
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
		if(name == null) return new String();
		
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		sb.append("\n{\n");
		
		if(attributes != null) {
			int i = 0;
			String[] keys = new String[attributes.size()];
			for( Enumeration e = attributes.keys(); e.hasMoreElements(); ) {
				keys[i++] = (String) e.nextElement();
			}
			
			Arrays.sort(keys);
			
			for(i = 0; i < attributes.size(); ++i) {
				BaseType value = ((BaseType) attributes.get(keys[i]));
				if( isValidating() && getEventTemplateDB() != null) {
					if( getEventTemplateDB().checkTypeForAttribute(name, keys[i], TypeID.UINT64_STRING) ) {
						try {
							sb.append("\t" + keys[i] + " = " + NumberCodec.toHexString(getUInt64(keys[i])) + ";\n");
						} catch(EventSystemException exc) {
							Log.warning("Event.toString : ", exc);
						}
					}
				} else {
					sb.append("\t" + keys[i] + " = " + value + ";\n");
				}
			} // for(i = 0; i < attributes.size() ...
		} // if(attributes != null) 
		
		sb.append("}");
		return sb.toString();		
	}
	
	public boolean equals(Object o) {
		if(o == null) return false;
		if(getClass().getName().equals(o.getClass().getName())) {
			return toString().equals(o.toString());
		} else {
			return false;
		}
	}
}
