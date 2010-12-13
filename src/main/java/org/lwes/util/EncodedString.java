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
		if (bytes == null) {
            return null;
        }

		try {
			return new String(bytes, enc.getEncodingString());
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Unknown Encoding");
		}
	}

	public static String bytesToString(byte[] bytes, int offset, int length, CharacterEncoding enc) {
		if (bytes == null) {
            return null;
        }

		try {
			return new String(bytes, offset, length, enc.getEncodingString());
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Unknown Encoding");
		}
	}

	public static byte[] getBytes(String string, CharacterEncoding enc) {
        if (string == null) {
            return null;
        }

		try {
			return string.getBytes(enc.getEncodingString());
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Unknown Encoding");
		}
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
