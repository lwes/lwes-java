package org.lwes;

/** 
 * This class contains some global variables used in various parts of
 * the event system.
 * @author Anthony Molinaro
 * @author Michael P. Lum
 */
public class TypeID
{
	/**
	 * The token used for <tt>undefined</tt> types in LWES
	 */
	public final static byte UNDEFINED_TOKEN = (byte)0xff;

	/**
	 * The token used by <tt>uint16</tt> in the Event Serialization Protocol
	 */
	public final static byte UINT16_TOKEN = (byte)0x01;

	/**
	 * The token used by <tt>int16</tt> in the Event Serialization Protocol
	 */
	public final static byte INT16_TOKEN  = (byte)0x02;
	/**
	 * The token used by <tt>uint32</tt> in the Event Serialization Protocol
	 */
	public final static byte UINT32_TOKEN = (byte)0x03;
	/**
	 * The token used by <tt>int32</tt> in the Event Serialization Protocol
	 */
	public final static byte INT32_TOKEN  = (byte)0x04;
	/**
	 * The token used by <tt>string</tt> in the Event Serialization Protocol
	 */
	public final static byte STRING_TOKEN = (byte)0x05;
	/**
	 * The token used by <tt>ip_addr</tt> in the Event Serialization Protocol
	 */
	public final static byte IPADDR_TOKEN = (byte)0x06;
	/**
	 * The token used by <tt>int64</tt> in the Event Serialization Protocol
	 */
	public final static byte INT64_TOKEN  = (byte)0x07;
	/**
	 * The token used by <tt>uint64</tt> in the Event Serialization Protocol
	 */ 
	public final static byte UINT64_TOKEN = (byte)0x08;
	/**
	 * The token used by <tt>boolean</tt> in the Event Serialization Protocol
	 */  
	public final static byte BOOLEAN_TOKEN= (byte)0x09;

	/**
	 * The  string used by <tt>uint16</tt> in the Event Serialization Protocol
	 */
	public final static String UINT16_STRING = "uint16";
	/**
	 * The  string used by <tt>int16</tt> in the Event Serialization Protocol
	 */
	public final static String INT16_STRING  = "int16";
	/**
	 * The  string used by <tt>uint32</tt> in the Event Serialization Protocol
	 */
	public final static String UINT32_STRING = "uint32";
	/**
	 * The  string used by <tt>int32</tt> in the Event Serialization Protocol
	 */
	public final static String INT32_STRING  = "int32";
	/**
	 * The  string used by <tt>string</tt> in the Event Serialization Protocol
	 */
	public final static String STRING_STRING = "string";
	/**
	 * The  string used by <tt>ip_addr</tt> in the Event Serialization Protocol
	 */
	public final static String IPADDR_STRING= "ip_addr";
	/**
	 * The  string used by <tt>int64</tt> in the Event Serialization Protocol
	 */
	public final static String INT64_STRING  = "int64";
	/**
	 * The  string used by <tt>uint64</tt> in the Event Serialization Protocol
	 */
	public final static String UINT64_STRING = "uint64";
	/**
	 * The  string used by <tt>boolean</tt> in the Event Serialization Protocol
	 */
	public final static String BOOLEAN_STRING= "boolean";  

	/**
	 * This is a regular expression for parsing an integer number from a string
	 */
	public final static String SIGNED_INTEGER_REGEX = "/-?\\d+/i";
	/**
	 * This is a regular expression for parsing an unsigned integer number 
	 * from a string
	 */
	public final static String UNSIGNED_INTEGER_REGEX = "/\\d+(?=\\s|$)/i";
	/** 
	 * This is a regular expression for matching a hexidecimal short from a string
	 */
	public final static String HEX_SHORT_REGEX = "/0x[0-9a-fA-F]{1,4}(?=\\s|$)/i";
	/**
	 * This is a regular expression for matching a hexidecimal int from a string
	 */
	public final static String HEX_INT_REGEX = "/0x[0-9a-fA-F]{5,8}(?=\\s|$)/i";
	/**
	 * This is a regular expression for matching a hexidecimal long from a string
	 */
	public final static String HEX_LONG_REGEX = "/0x[0-9a-fA-F]{9,16}(?=\\s|$)/i";
	/**
	 * This is a regular expression for matching an ip address from a string
	 */
	public final static String IP_ADDR_REGEX 
	= "/(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(?=\\s|$)/i";
	/**
	 * This is a regular expression for matching a boolean from a string
	 */
	public final static String BOOLEAN_REGEX = "/true|false/";

	/**
	 * Simple conversion utility
	 */
	public static String byteIDToString(byte id)
	{
		switch(id) 
		{
		case UINT16_TOKEN :
			return UINT16_STRING;
		case INT16_TOKEN  :
			return INT16_STRING;
		case UINT32_TOKEN :
			return UINT32_STRING;
		case INT32_TOKEN  :
			return INT32_STRING;
		case STRING_TOKEN :
			return STRING_STRING;
		case IPADDR_TOKEN :
			return IPADDR_STRING;
		case INT64_TOKEN  :
			return INT64_STRING;
		case UINT64_TOKEN :
			return UINT64_STRING;
		case BOOLEAN_TOKEN:
			return BOOLEAN_STRING;
		default:
			return null;
		}
	}

	/**
	 * Another conversion utility
	 */
	public static byte stringToByteID(String id)
	{
		if ( id.equals(UINT16_STRING) )
			return UINT16_TOKEN;
		else if ( id.equals(INT16_STRING) )
			return INT16_TOKEN;
		else if ( id.equals(UINT32_STRING) )
			return UINT32_TOKEN;
		else if ( id.equals(INT32_STRING) )
			return INT32_TOKEN;
		else if ( id.equals(STRING_STRING) ) 
			return STRING_TOKEN;
		else if ( id.equals(IPADDR_STRING) )
			return IPADDR_TOKEN;
		else if ( id.equals(INT64_STRING) )
			return INT64_TOKEN;
		else if ( id.equals(UINT64_STRING) )
			return UINT64_TOKEN;
		else if ( id.equals(BOOLEAN_STRING) )
			return BOOLEAN_TOKEN;
		else
			return UNDEFINED_TOKEN;
	}
}
