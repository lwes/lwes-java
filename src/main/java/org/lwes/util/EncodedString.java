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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

import org.lwes.MemoryPool;
import org.lwes.MemoryPool.Buffer;

import static org.lwes.Event.UTF_8_NAME;

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

  // encoder object, one per thread
  static protected ThreadLocal<CharsetEncoder> encoder =
      new ThreadLocal<CharsetEncoder>() {
    @Override protected CharsetEncoder initialValue() {
      return Charset.forName(UTF_8_NAME).newEncoder();
    }
  };

  // decoder object, one per thread
  static protected ThreadLocal<CharsetDecoder> decoder =
      new ThreadLocal<CharsetDecoder>() {
    @Override protected CharsetDecoder initialValue() {
      return Charset.forName(UTF_8_NAME).newDecoder();
    }
  };


  /**
   * return a {@code Buffer} object that contains the encoded version of the
   * input string. The returned {@code Buffer} object must be put back in to
   * memory pool when no longer needed.
   *
   * @param input - input string to encode
   * @return a {@code Buffer} object that contains the encoded input
   *
   * @throws IllegalArgumentException if encoding fails because of overflow of
   *         the buffer, or any other encoding failures.
   */
  public static Buffer encode(String input) {
    // get a buffer from memory pool
    Buffer buffer = MemoryPool.get();
    ByteBuffer outputBuffer = buffer.getEncoderOutputBuffer();
    CharBuffer inputBuffer = buffer.getEncoderInputBuffer();

    // no need to encode if input is null
    if(input == null) {
      inputBuffer.limit(0);
      return buffer;
    }

    if(input.length() > inputBuffer.capacity()) {
      MemoryPool.putBack(buffer);
      throw new IllegalArgumentException("Failed to encode the input, input is too long, size: " + input.length());
    }

    inputBuffer.limit(input.length());

    // copy input string
    char[] tempChars = inputBuffer.array();
    input.getChars(0, input.length(), tempChars, 0);
    // reset encoder and then encode
    encoder.get().reset();
    CoderResult result = encoder.get().encode(inputBuffer, outputBuffer, true);
    if(result != CoderResult.UNDERFLOW) {
      // return the buffer back to the pool
      MemoryPool.putBack(buffer);
      throw new IllegalArgumentException("Failed to encode the input, code: " + result);
    }
    encoder.get().flush(outputBuffer);
    return buffer;
  }

  /**
   * return the length of a string when encoded in utf-8
   *
   * @param input - input string
   * @return length of the encoded input string in utf-8, or 0 if input is null
   *
   * @throws IllegalArgumentException if encoding fails because of overflow of
   *         the buffer, or any other encoding failures.
   */
  public static int getEncodedLength(String input) {
    if(input == null) {
      return 0;
    }
    // encode and put the buffer back in to the memory pool
    Buffer buffer = encode(input);
    int len = buffer.getEncoderOutputBuffer().position();
    MemoryPool.putBack(buffer);
    return len;
  }

  /**
   * decodes an array of bytes to a string using utf-8
   *
   * @param input - input array of bytes
   * @param offset - offset in the array
   * @param length - length
   * @return a string representing decoded bytes, or empty string if the length
   *         of the input is 0
   */
  public static String decode(byte[] input, int offset, int length) {
    // nothing to decode if empty bytes
    if(input == null) {
      return null;
    }
    if(length == 0) {
      return "";
    }

    Buffer buffer = MemoryPool.get();
    CharBuffer outputBuffer = buffer.getDecoderOutputBuffer();
    ByteBuffer inputBuffer = ByteBuffer.wrap(input, offset, length);

    // reset decoder
    decoder.get().reset();

    CoderResult result = decoder.get().decode(inputBuffer, outputBuffer, true);
    if(result != CoderResult.UNDERFLOW) {
      // put the buffer back in to the pool
      MemoryPool.putBack(buffer);
      throw new IllegalArgumentException("Failed to decode the input, code: " + result);
    }
    decoder.get().flush(outputBuffer);
    String output = new String(outputBuffer.array(), 0, outputBuffer.position());

    MemoryPool.putBack(buffer);

    return output;
  }

}
