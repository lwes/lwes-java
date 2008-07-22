package org.lwes.util;

/**
 * This is a class to efficiently encode built-in primitive types into
 * byte arrays and decode them back.  While this can be done with a 
 * combination of ByteArrayOutputStreams, DataOutputStreams, 
 * ByteArrayInputStreams, DataInputStreams, merely creating those
 * objects is quite costly and it is difficult to make them persistent.
 * As such, this contains code lifted from the guts of the Data*Stream
 * classes.
 *
 * Also, this class defines functions to convert primitive types and 
 * byte arrays to and from hexadecimal strings.
 * 
 * Hopefully, support for these operations will be added to
 * the standard Java API and this class can be retired.
 * 
 * @author  Preston Pfarner
 * @version     %I%, %G%
 * @since       0.0.1
 */
public final class NumberCodec {
	public static final int BYTE_BYTES  = 1;
	public static final int SHORT_BYTES = 2;
	public static final int INT_BYTES   = 4;
	public static final int LONG_BYTES  = 8;

	public static final int BYTE_BITS   = 8;
	public static final int SHORT_BITS  = SHORT_BYTES * BYTE_BITS;
	public static final int INT_BITS    = INT_BYTES   * BYTE_BITS;
	public static final int LONG_BITS   = LONG_BYTES  * BYTE_BITS;

	public static final short BYTE_MASK   = 0xFF;
	public static final int   SHORT_MASK  = 0xFFFF;
	public static final long  INT_MASK    = 0xFFFFFFFFL;

	private static final char[] hexCharMap = {
		'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
	};

	private static final byte[] hexByteMap = {
		(byte) '0', (byte) '1', (byte) '2', (byte) '3',
		(byte) '4', (byte) '5', (byte) '6', (byte) '7',
		(byte) '8', (byte) '9', (byte) 'A', (byte) 'B',
		(byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F',
	};

	/** Prohibited constructor; this is an uninstantiable class. */
	private NumberCodec() { }


	/* ***********************************************************************
	 * ENCODING TO BYTE ARRAYS
	 * ***********************************************************************/
	/**
	 * Encode a byte into a byte-array buffer. <br>
	 * This version does not perform any null or range checks!
	 * @param b      the byte to be encoded
	 * @param buffer the byte array into which the encoding should be written
	 * @param offset the position in the array to start writing the encoding
	 */
	public static void encodeByteUnchecked(byte b, byte[] buffer,int offset) {
		buffer[offset] = b;
	}

	/**
	 * Encode a byte into a byte-array buffer.
	 * @param b      the byte to be encoded
	 * @param buffer the byte array into which the encoding should be written
	 * @param offset the position in the array to start writing the encoding
	 * @param length the maximum number of bytes that may be written
	 */
	public static void encodeByte(byte b, byte[] buffer,int offset,int length) 
	throws IllegalArgumentException {
		checkRange(BYTE_BYTES, buffer, offset, length);
		encodeByteUnchecked(b,buffer,offset);
	}

	/**
	 * Encode a short into a byte-array buffer. <br>
	 * This version does not perform any null or range checks!
	 * @param s      the short to be encoded
	 * @param buffer the byte array into which the encoding should be written
	 * @param offset the position in the array to start writing the encoding
	 */
	public static void encodeShortUnchecked(short s, byte[] buffer,int offset) {
		buffer[offset++] = (byte) ((s >>> (1*BYTE_BITS)) & BYTE_MASK);
		buffer[offset  ] = (byte) ((s >>> (0*BYTE_BITS)) & BYTE_MASK);
	}

	/**
	 * Encode a short into a byte-array buffer.
	 * @param s      the short to be encoded
	 * @param buffer the byte array into which the encoding should be written
	 * @param offset the position in the array to start writing the encoding
	 * @param length the maximum number of bytes that may be written
	 */
	public static void encodeShort(short s, byte[] buffer,int offset,int length)
	throws IllegalArgumentException {
		checkRange(SHORT_BYTES, buffer, offset, length);
		encodeShortUnchecked(s, buffer,offset);
	}

	/**
	 * Encode an int into a byte-array buffer. <br>
	 * This version does not perform any null or range checks!
	 * @param i      the int to be encoded
	 * @param buffer the byte array into which the encoding should be written
	 * @param offset the position in the array to start writing the encoding
	 */
	public static void encodeIntUnchecked(int i, byte[] buffer,int offset) {
		buffer[offset++] = (byte) ((i >>> (3*BYTE_BITS)) & BYTE_MASK);
		buffer[offset++] = (byte) ((i >>> (2*BYTE_BITS)) & BYTE_MASK);
		buffer[offset++] = (byte) ((i >>> (1*BYTE_BITS)) & BYTE_MASK);
		buffer[offset  ] = (byte) ((i >>> (0*BYTE_BITS)) & BYTE_MASK);
	}

	/**
	 * Encode an int into a byte-array buffer.
	 * @param i      the int to be encoded
	 * @param buffer the byte array into which the encoding should be written
	 * @param offset the position in the array to start writing the encoding
	 * @param length the maximum number of bytes that may be written
	 */
	public static void encodeInt(int i, byte[] buffer,int offset,int length)
	throws IllegalArgumentException {
		checkRange(INT_BYTES, buffer, offset, length);
		encodeIntUnchecked(i, buffer,offset);
	}

	/**
	 * Encode a long into a byte-array buffer.
	 * @param l      the long to be encoded
	 * @param buffer the byte array into which the encoding should be written
	 * @param offset the position in the array to start writing the encoding
	 */
	public static void encodeLongUnchecked(long l, byte[] buffer,int offset) {
		buffer[offset++] = (byte) ((l >>> (7*BYTE_BITS)) & BYTE_MASK);
		buffer[offset++] = (byte) ((l >>> (6*BYTE_BITS)) & BYTE_MASK);
		buffer[offset++] = (byte) ((l >>> (5*BYTE_BITS)) & BYTE_MASK);
		buffer[offset++] = (byte) ((l >>> (4*BYTE_BITS)) & BYTE_MASK);
		buffer[offset++] = (byte) ((l >>> (3*BYTE_BITS)) & BYTE_MASK);
		buffer[offset++] = (byte) ((l >>> (2*BYTE_BITS)) & BYTE_MASK);
		buffer[offset++] = (byte) ((l >>> (1*BYTE_BITS)) & BYTE_MASK);
		buffer[offset  ] = (byte) ((l >>> (0*BYTE_BITS)) & BYTE_MASK);
	}

	/**
	 * Encode a long into a byte-array buffer.
	 * @param l      the long to be encoded
	 * @param buffer the byte array into which the encoding should be written
	 * @param offset the position in the array to start writing the encoding
	 * @param length the maximum number of bytes that may be written
	 */
	public static void encodeLong(long l, byte[] buffer,int offset,int length)
	throws IllegalArgumentException {
		checkRange(LONG_BYTES, buffer, offset, length);
		encodeLongUnchecked(l, buffer,offset);
	}

	/* ***********************************************************************
	 * DECODING FROM BYTE ARRAYS
	 * ***********************************************************************/
	/**
	 * Extract and decode a byte out of a byte-array buffer. <br>
	 * This version does not perform any null or range checks!
	 * @param buffer the byte array from which the encoded form should be read
	 * @param offset the position in the array to start reading the encoded form
	 * @param length the maximum number of bytes that may be read
	 */
	public static final byte decodeByteUnchecked(byte[] buffer,int offset) {
		return buffer[offset];
	}

	/**
	 * Decode a byte out of a byte-array buffer.
	 * @param buffer the byte array from which the encoded form should be read
	 * @param offset the position in the array to start reading the encoded form
	 * @param length the maximum number of bytes that may be read
	 */
	public static final byte decodeByte(byte[] buffer,int offset,int length)
	throws IllegalArgumentException {
		checkRange(BYTE_BYTES, buffer, offset, length);
		return decodeByteUnchecked(buffer,offset);
	}

	/**
	 * Extract and decode a short out of a byte-array buffer. <br>
	 * This version does not perform any null or range checks!
	 * @param buffer the byte array from which the encoded form should be read
	 * @param offset the position in the array to start reading the encoded form
	 * @param length the maximum number of bytes that may be read
	 */
	public static short decodeShortUnchecked(byte[] buffer,int offset) {
		return 
		(short) ((decodeByteUnchecked(buffer,offset) << BYTE_BITS) +
				(decodeByteUnchecked(buffer,offset+BYTE_BYTES) & BYTE_MASK));
	}

	/**
	 * Decode a short out of a byte-array buffer.
	 * @param buffer the byte array from which the encoded form should be read
	 * @param offset the position in the array to start reading the encoded form
	 * @param length the maximum number of bytes that may be read
	 */
	public static short decodeShort(byte[] buffer,int offset,int length)
	throws IllegalArgumentException {
		checkRange(SHORT_BYTES, buffer, offset, length);
		return decodeShortUnchecked(buffer,offset);
	}

	/**
	 * Extract and decode an int out of a byte-array buffer. <br>
	 * This version does not perform any null or range checks!
	 * @param buffer the byte array from which the encoded form should be read
	 * @param offset the position in the array to start reading the encoded form
	 * @param length the maximum number of bytes that may be read
	 */
	public static int decodeIntUnchecked(byte[] buffer,int offset) {
		return ((decodeShortUnchecked(buffer,offset) << SHORT_BITS) +
				(decodeShortUnchecked(buffer,offset+SHORT_BYTES) & SHORT_MASK));
	}

	/**
	 * Decode an int out of a byte-array buffer.
	 * @param buffer the byte array from which the encoded form should be read
	 * @param offset the position in the array to start reading the encoded form
	 * @param length the maximum number of bytes that may be read
	 */
	public static int decodeInt(byte[] buffer,int offset,int length)
	throws IllegalArgumentException {
		checkRange(INT_BYTES, buffer, offset, length);
		return decodeIntUnchecked(buffer,offset);
	}

	/**
	 * Extract and decode a long out of a byte-array buffer. <br>
	 * This version does not perform any null or range checks!
	 * @param buffer the byte array from which the encoded form should be read
	 * @param offset the position in the array to start reading the encoded form
	 * @param length the maximum number of bytes that may be read
	 */
	public static long decodeLongUnchecked(byte[] buffer,int offset) {
		return ((((long) decodeIntUnchecked(buffer,offset)) << INT_BITS) +
				(decodeIntUnchecked(buffer,offset+INT_BYTES) & INT_MASK));
	}

	/**
	 * Decode a long out of a byte-array buffer.
	 * @param buffer the byte array from which the encoded form should be read
	 * @param offset the position in the array to start reading the encoded form
	 * @param length the maximum number of bytes that may be read
	 */
	public static long decodeLong(byte[] buffer,int offset,int length)
	throws IllegalArgumentException {
		checkRange(LONG_BYTES, buffer, offset, length);
		return decodeLongUnchecked(buffer,offset);
	}

	/**
	 * Verifies that the buffer exists, that the writeable region fits into
	 * the buffer, and that the writeable length is long enough.
	 * @param minLength the length that will be written
	 * @param buffer    the destination array
	 * @param offset    the first position that should be written
	 * @param length    the number of bytes that may be written
	 * @exception IllegalArgumentException if the check fails
	 */
	public static void checkRange(int minLength, 
			byte[] buffer, int offset, int length)
	throws IllegalArgumentException {
		if (buffer == null)
			throw new IllegalArgumentException("Buffer is null.");
		if ((offset < 0) || (length < 0) || (offset + length > buffer.length))
			throw new IllegalArgumentException("Writeable region does not fit: "+
					offset+","+length+","+buffer.length);
		if (minLength > length)
			throw new IllegalArgumentException("Writeable region is too small: "+
					minLength+">"+length);
	}

	/* ***********************************************************************
	 * WRITING TO STRINGS (IN HEX)
	 * ***********************************************************************/
	/**
	 * Output a number in unsigned hexadecimal form, padding with zeroes,
	 * with a fixed result size.  Extra opening "f"'s are removed.
	 * @param  val      the number to convert
	 * @param  numBytes the number of bytes to write (each is two hex digits)
	 * @return a String representing the number.
	 */
	private static String toHexString(long num, int numBytes) {
		final StringBuffer buf = new StringBuffer(2*numBytes);
		writeHexString(num,numBytes,buf);
		return buf.toString();
	}

	/**
	 * Write a number in unsigned hexadecimal form, padding with zeroes,
	 * with a fixed result size.  Extra opening "f"'s are removed.
	 * @param  val      the number to convert
	 * @param  numBytes the number of bytes to write (each is two hex digits)
	 * @param  buf      the StringBuffer into which to write
	 */
	private static void writeHexString(long num, int numBytes, StringBuffer buf){
		final int startLen   = buf.length();
		int       numNibbles = numBytes << 1;
		int       pos        = startLen+numNibbles;
		buf.setLength(pos);
		while (numNibbles != 0) {
			--pos;
			final byte masked = (byte) (num & 0xf);
			buf.setCharAt(pos,hexCharMap[masked]);
			num >>>= 4;
		--numNibbles;
		}
	}

	/**
	 * Write a number in unsigned hexadecimal form, padding with zeroes,
	 * with a fixed result size.  Extra opening "f"'s are removed.
	 * @param  value    the number to convert
	 * @param  bytes    the byte array into which to write
	 * @param  offset   the offset into <code>bytes</code> to start
	 * @param  numBytes the number of bytes to write (each is two hex digits)
	 */
	private static void writeHexString
	(long value, byte[] bytes, int offset, int numBytes) {
		int       numNibbles = numBytes << 1;
		int       pos        = offset+numNibbles;
		while (numNibbles != 0) {
			--pos;
			final byte masked = (byte) (value & 0xf);
			bytes[pos] = hexByteMap[masked];
			value >>>= 4;
		--numNibbles;
		}
	}

	/* ***********************************************************************
	 * Convert numbers to hex strings
	 * ***********************************************************************/
	/**
	 * Output a byte in unsigned hexadecimal form, padding with zeroes.
	 * @param  b the byte
	 * @return a String representing the byte.
	 */
	public static String toHexString(byte b) {
		return toHexString(b,BYTE_BYTES);
	}

	/**
	 * Output a short in unsigned hexadecimal form, padding with zeroes.
	 * @param  s the short
	 * @return a String representing the short.
	 */
	public static String toHexString(short s) {
		return toHexString(s,SHORT_BYTES);
	}

	/**
	 * Output an int in unsigned hexadecimal form, padding with zeroes.
	 * @param  i the int
	 * @return a String representing the int.
	 */
	public static String toHexString(int i) {
		return toHexString(i,INT_BYTES);
	}

	/**
	 * Output a long in unsigned hexadecimal form, padding with zeroes.
	 * @param  l the long
	 * @return a String representing the long.
	 */
	public static String toHexString(long l) {
		return toHexString(l,LONG_BYTES);
	}

	/* ***********************************************************************
	 * Write hex strings into string buffers
	 * ***********************************************************************/
	/**
	 * Write a byte in unsigned hexadecimal form, padding with zeroes.
	 * @param  buf the StringBuffer into which to write
	 * @param  b   the byte
	 */
	public static void writeHexString(byte b, StringBuffer buf) {
		writeHexString(b,BYTE_BYTES,buf);
	}

	/**
	 * Write a short in unsigned hexadecimal form, padding with zeroes.
	 * @param  buf the StringBuffer into which to write
	 * @param  s   the short
	 */
	public static void writeHexString(short s, StringBuffer buf) {
		writeHexString(s,SHORT_BYTES,buf);
	}

	/**
	 * Write a int in unsigned hexadecimal form, padding with zeroes.
	 * @param  buf the StringBuffer into which to write
	 * @param  i   the int
	 */
	public static void writeHexString(int i, StringBuffer buf) {
		writeHexString(i,INT_BYTES,buf);
	}

	/**
	 * Write a long in unsigned hexadecimal form, padding with zeroes.
	 * @param  buf the StringBuffer into which to write
	 * @param  l   the long
	 */
	public static void writeHexString(long l, StringBuffer buf) {
		writeHexString(l,LONG_BYTES,buf);
	}

	/* ***********************************************************************
	 * Write hex strings into byte arrays
	 * (with each byte representing a nibble in ASCII)
	 * ***********************************************************************/
	/**
	 * Write a byte in unsigned hexadecimal form, padding with zeroes.
	 * @param  b      the byte
	 * @param  bytes  the byte array into which to write
	 * @param  offset the index in the byte array to start writing
	 */
	public static void writeHexString(byte b, byte[] bytes, int offset) {
		writeHexString(b,bytes,offset,BYTE_BYTES);
	}

	/**
	 * Write a short in unsigned hexadecimal form, padding with zeroes.
	 * @param  value  the value to write
	 * @param  bytes  the byte array into which to write
	 * @param  offset the index in the byte array to start writing
	 */
	public static void writeHexString(short value, byte[] bytes, int offset) {
		writeHexString(value,bytes,offset,SHORT_BYTES);
	}

	/**
	 * Write a int in unsigned hexadecimal form, padding with zeroes.
	 * @param  value  the value to write
	 * @param  bytes  the byte array into which to write
	 * @param  offset the index in the byte array to start writing
	 */
	public static void writeHexString(int value, byte[] bytes, int offset) {
		writeHexString(value,bytes,offset,INT_BYTES);
	}

	/**
	 * Write a long in unsigned hexadecimal form, padding with zeroes.
	 * @param  value  the value to write
	 * @param  bytes  the byte array into which to write
	 * @param  offset the index in the byte array to start writing
	 */
	public static void writeHexString(long value, byte[] bytes, int offset) {
		writeHexString(value,bytes,offset,LONG_BYTES);
	}



	/**
	 * Return a String encoding the bytes from a portion of a byte array
	 * in hex form.
	 * @param  bytes the byte array
	 * @param  offset the first byte to output
	 * @param  length the number of bytes to output
	 * @return the hex dump of the byte array
	 */
	public static String byteArrayToHexString(byte[] bytes, 
			int offset, int length) {
		StringBuffer buf = new StringBuffer(2*length);
		for (int i=offset; i<offset+length; i++) {
			buf.append(Character.forDigit(((bytes[i]>>>4) & 0x0f), 16));
			buf.append(Character.forDigit(( bytes[i]      & 0x0f), 16));
		}
		return buf.toString();
	}

	public static byte[] hexStringToByteArray(String aString)
	{
		int length = aString.length();
		if ( (length % 2) != 0 ) 
		{ 
			System.err.println("ERROR: Odd Number, can't convert to byte array"); 
			return null;
		}
		byte [] bytes = new byte[(length/2)];
		for ( int k = 0 ; k < (length/2) ; k++ )
		{
			bytes[k] = (byte)0;
		}
		byte [] str_bytes = aString.getBytes();
		if ( str_bytes.length != length ) 
		{ 
			System.err.println("ERROR: Mismatching lengths");
			return null;
		}
		int count = 0;
		boolean waitingForSecondNibble = false;
		for ( int i = 0; i < length ; i++ )
		{
			switch ( str_bytes[i] ) {
			case ((byte)'0'):
				if ( waitingForSecondNibble )
				{
					bytes[count] |= (byte)( (byte)0x0 << 0 );
					count++;
					waitingForSecondNibble = false;
				}
				else
				{
					bytes[count] |= (byte)( (byte)0x0 << 4 );
					waitingForSecondNibble = true;
				}
			break;
			case ((byte)'1'):
				if ( waitingForSecondNibble )
				{
					bytes[count] |= (byte)( (byte)0x1 << 0 );
					count++;
					waitingForSecondNibble = false;
				}
				else
				{
					bytes[count] |= (byte)( (byte)0x1 << 4 );
					waitingForSecondNibble = true;
				}
			break;
			case ((byte)'2'):
				if ( waitingForSecondNibble )
				{
					bytes[count] |= (byte)( (byte)0x2 << 0 );
					count++;
					waitingForSecondNibble = false;
				}
				else
				{
					bytes[count] |= (byte)( (byte)0x2 << 4 );
					waitingForSecondNibble = true;
				}
			break;
			case ((byte)'3'):
				if ( waitingForSecondNibble )
				{
					bytes[count] |= (byte)( (byte)0x3 << 0 );
					count++;
					waitingForSecondNibble = false;
				}
				else
				{
					bytes[count]  |= (byte)( (byte)0x3 << 4 );
					waitingForSecondNibble = true;
				}
			break;
			case ((byte)'4'):
				if ( waitingForSecondNibble )
				{
					bytes[count]  |= (byte)( (byte)0x4 << 0 );
					count++;
					waitingForSecondNibble = false;
				}
				else
				{
					bytes[count]  |= (byte)( (byte)0x4 << 4 );
					waitingForSecondNibble = true;
				}
			break;
			case ((byte)'5'):
				if ( waitingForSecondNibble )
				{
					bytes[count] |= (byte)( (byte)0x5 << 0 );
					count++;
					waitingForSecondNibble = false;
				}
				else
				{
					bytes[count] |= (byte)( (byte)0x5 << 4 );
					waitingForSecondNibble = true;
				}
			break;
			case ((byte)'6'):
				if ( waitingForSecondNibble )
				{
					bytes[count] |= (byte)( (byte)0x6 << 0 );
					count++;
					waitingForSecondNibble = false;
				}
				else
				{
					bytes[count] |= (byte)( (byte)0x6 << 4 );
					waitingForSecondNibble = true;
				}
			break;
			case ((byte)'7'):
				if ( waitingForSecondNibble )
				{
					bytes[count] |= (byte)( (byte)0x7 << 0 );
					count++;
					waitingForSecondNibble = false;
				}
				else
				{
					bytes[count] |= (byte)( (byte)0x7 << 4 );
					waitingForSecondNibble = true;
				}
			break;
			case ((byte)'8'):
				if ( waitingForSecondNibble )
				{
					bytes[count] |= (byte)( (byte)0x8 << 0 );
					count++;
					waitingForSecondNibble = false;
				}
				else
				{
					bytes[count] |= (byte)( (byte)0x8 << 4 );
					waitingForSecondNibble = true;
				}
			break;
			case ((byte)'9'):
				if ( waitingForSecondNibble )
				{
					bytes[count] |= (byte)( (byte)0x9 << 0 );
					count++;
					waitingForSecondNibble = false;
				}
				else
				{
					bytes[count] |= (byte)( (byte)0x9 << 4 );
					waitingForSecondNibble = true;
				}
			break;
			case ((byte)'a'):
			case ((byte)'A'):
				if ( waitingForSecondNibble )
				{
					bytes[count] |= (byte)( (byte)0xa << 0 );
					count++;
					waitingForSecondNibble = false;
				}
				else
				{
					bytes[count] |= (byte)( (byte)0xa << 4 );
					waitingForSecondNibble = true;
				}
			break;
			case ((byte)'b'):
			case ((byte)'B'):
				if ( waitingForSecondNibble )
				{
					bytes[count] |= (byte)( (byte)0xb << 0 );
					count++;
					waitingForSecondNibble = false;
				}
				else
				{
					bytes[count] |= (byte)( (byte)0xb << 4 );
					waitingForSecondNibble = true;
				}
			break;
			case ((byte)'c'):
			case ((byte)'C'):
				if ( waitingForSecondNibble )
				{
					bytes[count] |= (byte)( (byte)0xc << 0 );
					count++;
					waitingForSecondNibble = false;
				}
				else
				{
					bytes[count] |= (byte)( (byte)0xc << 4 );
					waitingForSecondNibble = true;
				}
			break;

			case ((byte)'d'):
			case ((byte)'D'):
				if ( waitingForSecondNibble )
				{
					bytes[count] |= (byte)( (byte)0xd << 0 );
					count++;
					waitingForSecondNibble = false;
				}
				else
				{
					bytes[count] |= (byte)( (byte)0xd << 4 );
					waitingForSecondNibble = true;
				}
			break;

			case ((byte)'e'):
			case ((byte)'E'):
				if ( waitingForSecondNibble )
				{
					bytes[count] |= (byte)( (byte)0xe << 0 );
					count++;
					waitingForSecondNibble = false;
				}
				else
				{
					bytes[count] |= (byte)( (byte)0xe << 4 );
					waitingForSecondNibble = true;
				}
			break;

			case ((byte)'f'):
			case ((byte)'F'):
				if ( waitingForSecondNibble )
				{
					bytes[count] |= (byte)( (byte)0xf << 0 );
					count++;
					waitingForSecondNibble = false;
				}
				else
				{
					bytes[count] |= (byte)( (byte)0xf << 4 );
					waitingForSecondNibble = true;
				}
			break;

			default:
				System.err.println("ERROR: non-hex character");
			return null;
			}
		}
		return bytes;
	}

	/**
	 * Return a String encoding the bytes in a byte array in hex form. <br>
	 * This is equivalent to 
	 * <code>byteArrayToHexString(bytes,0,bytes.length);</code>
	 * @param  bytes the byte array
	 * @return the hex dump of the byte array
	 */
	public static String byteArrayToHexString(byte[] bytes) {
		return byteArrayToHexString(bytes,0,bytes.length);
	}

	/**
	 * Turn an unsigned hex string into a long.  This whole thing is here
	 * to replace Long.parseLong(str,16) because it fails with large unsigned
	 * hex strings (ones that denote negative values).  When they work out
	 * the signed hex vs. unsigned hex issue in the Java API, this can be
	 * retired.
	 * @param str the String to parse
	 * @return    the long that was written in <code>str</code>
	 */
	private static long fromHexString(String str,long min) {
		final int  hex       = 16;
		final char firstChar = str.charAt(0);
		final int  digit     = Character.digit(firstChar,hex);
		if (digit < hex/2) {
			return Long.parseLong(str,hex);
		} else {
			/* Subtract <code>hex/2</code> from the first digit and flip the sign. */
			final String posStr = (Character.forDigit(digit-hex/2, hex) + 
					str.substring(1,str.length()));
			final long offsetLong = Long.parseLong(posStr,hex);
			return offsetLong+min;
		}
	}

	/**
	 * Output a byte in unsigned hexadecimal form, padding with zeroes.
	 * @param  s the String representing the byte.
	 * @return the parsed byte
	 */
	public static byte byteFromHexString(String s) {
		return (byte)fromHexString(s,Byte.MIN_VALUE);
	}

	/**
	 * Output a short in unsigned hexadecimal form, padding with zeroes.
	 * @param  s the String representing the short
	 * @return the parsed short
	 */
	public static short shortFromHexString(String s) {
		return (short)fromHexString(s,Short.MIN_VALUE);
	}

	/**
	 * Output an int in unsigned hexadecimal form, padding with zeroes.
	 * @param  s the String representing the int.
	 * @return the parsed int
	 */
	public static int intFromHexString(String s) {
		return (int)fromHexString(s,Integer.MIN_VALUE);
	}

	/**
	 * Output a long in unsigned hexadecimal form, padding with zeroes.
	 * @param  s the String representing the long.
	 * @return the parsed long
	 */
	public static long longFromHexString(String s) {
		return fromHexString(s,Long.MIN_VALUE);
	}

	/**
	 * Decode a long out of a byte-array buffer. (convienience method)
	 * @param pBytes the byte array from which the encoded form should be read
	 * @return long decoded from bytes
	 */
	public static long decodeLong (byte[] pBytes)
	throws NumberFormatException
	{
		if (pBytes == null)
		{
			throw new NumberFormatException("null byte array passed");
		}
		if (pBytes.length != NumberCodec.LONG_BYTES)
		{
			throw new NumberFormatException("expecting byte array length of: " +
					NumberCodec.LONG_BYTES + " got: " + pBytes.length);
		}
		return NumberCodec.decodeLong(pBytes, 0, pBytes.length);
	}

	/**
	 * Encode a long into a byte-array buffer. (convienience method)
	 * @param pLong      the long to be encoded
	 * @return encoded bytes of the long
	 */
	public static byte[] encodeLong (long pLong)
	{
		byte[] bytes = new byte[NumberCodec.LONG_BYTES];
		NumberCodec.encodeLong(pLong, bytes, 0, bytes.length);
		return bytes;
	}

	/**
	 * Decode a int out of a byte-array buffer. (convienience method)
	 * @param pBytes the byte array from which the encoded form should be read
	 * @return int decoded from bytes
	 */
	public static int decodeInt (byte[] pBytes)
	throws NumberFormatException
	{
		if (pBytes == null)
		{
			throw new NumberFormatException("null byte array passed");
		}
		if (pBytes.length != NumberCodec.LONG_BYTES)
		{
			throw new NumberFormatException("expecting byte array length of: " +
					NumberCodec.INT_BYTES + " got: " + pBytes.length);
		}
		return NumberCodec.decodeInt(pBytes, 0, pBytes.length);
	}

	/**
	 * Encode a int into a byte-array buffer. (convienience method)
	 * @param pInt      the int to be encoded
	 * @return encoded bytes of the int
	 */
	public static byte[] encodeInt (int pInt)
	throws NumberFormatException
	{
		byte[] bytes = new byte[NumberCodec.INT_BYTES];
		NumberCodec.encodeInt(pInt, bytes, 0, bytes.length);
		return bytes;
	}
}
