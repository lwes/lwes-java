package org.lwes.util;

import java.io.UnsupportedEncodingException;

/**
 * EncodedString is a wrapper class which wraps a String, but replaces all
 * methods using a string representation of a character encoding with
 * ones using the CharacterEncoding class, thereby guaranteeing
 * validity and eliminating the need to throw any exceptions.
 *
 * @author Kevin Scaldeferri
 * @version     %I%, %G%
 * @since       0.0.1
 */
public class EncodedString {
	private String myString;
	private CharacterEncoding myEncoding;

	public static String bytesToString(byte[] bytes, CharacterEncoding enc) {
		String ret = null;
		try {
			ret = new String(bytes, enc.getEncodingString());
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Unknown Encoding");
		} catch (NullPointerException e) {
			return null;
		}
		return ret;
	}

	public static String bytesToString(byte[] bytes, int offset,
			int length, CharacterEncoding enc) {
		String ret = null;
		try {
			ret = new String(bytes, offset, length, enc.getEncodingString());
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Unknown Encoding");
		} catch (NullPointerException e) {
			return null;
		}
		return ret;
	}

	public static byte[] getBytes(String string, CharacterEncoding enc) {
		byte[] ret = null;
		try {
			ret = string.getBytes(enc.getEncodingString());
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Unknown Encoding");
		} catch (NullPointerException e) {
			return null;
		}
		return ret;
	}

	public EncodedString(String string, CharacterEncoding enc) {
		myString = string;
		myEncoding = enc;
	}

	public EncodedString(byte[] bytes, CharacterEncoding enc) {
		myString = bytesToString(bytes, enc);
		myEncoding = enc;
	}

	public EncodedString(byte[] bytes, int offset,
			int length, CharacterEncoding enc)
	{
		myString = bytesToString(bytes, offset, length, enc);
		myEncoding = enc;
	}

	public byte[] getBytes() { return getBytes(myString, myEncoding); }
	public String toString() { return myString; }
}
