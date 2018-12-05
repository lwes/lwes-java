/*======================================================================*
 * Copyright (c) 2010, Frank Maritato All rights reserved.              *
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
/**
 * @author fmaritato
 */

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import org.junit.Test;
import org.lwes.MemoryPool;
import org.lwes.MemoryPool.Buffer;

import static org.lwes.Event.MAX_MESSAGE_SIZE;

public class EncodedStringTest {

    @Test
    public void testEncode() {
        Buffer buffer = EncodedString.encode("testing");
        assertTrue(buffer.getEncoderOutputBuffer().position() == "testing".length() );
        assertTrue( Util.compareByteArrays(buffer.getEncoderOutputBuffer().array(), 7, "testing".getBytes(), 7) );
        MemoryPool.putBack(buffer);

        buffer = EncodedString.encode(null);
        assertEquals(buffer.getEncoderOutputBuffer().position(), 0);
        MemoryPool.putBack(buffer);

        buffer = EncodedString.encode("");
        assertEquals(buffer.getEncoderOutputBuffer().position(), 0);
        MemoryPool.putBack(buffer);

        // 64k of chars that are encoded to 1 byte, there should be no exception
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<MAX_MESSAGE_SIZE; ++i) {
          sb.append( "a" );
        }
        try {
          buffer = EncodedString.encode(sb.toString());
        } catch (IllegalArgumentException e) {
          fail();
        }

        // 64k of chars that may be encoded to more than one byte, there should be exception
        sb = new StringBuilder();
        for(int i=0; i<MAX_MESSAGE_SIZE; ++i) {
          sb.append( (char)(new Random()).nextInt(255) );
        }
        try {
          buffer = EncodedString.encode(sb.toString());
          fail();
        } catch (IllegalArgumentException e) {
        }

        // larger than 64k, should throw exception
        sb = new StringBuilder();
        for(int i=0; i<MAX_MESSAGE_SIZE+100; ++i) {
          sb.append( (char)(new Random()).nextInt(255) );
        }
        try {
          buffer = EncodedString.encode(sb.toString());
          fail();
        } catch (IllegalArgumentException e) {}

    }

    @Test
    public void testEncodedLength() throws UnsupportedEncodingException {
      assertEquals(0, EncodedString.getEncodedLength(null));

      String s = "abcdefghijklmnopqrstuvwxyz";
      int len = s.length();
      assertEquals( len, EncodedString.getEncodedLength(s));

      s = new String("A" + "\u00ea" + "\u00f1" + "\u00fc" + "C");
      len = s.getBytes("utf-8").length;
      assertEquals( len, EncodedString.getEncodedLength(s));
    }

    @Test
    public void testDecode() throws UnsupportedEncodingException {

      // decode null
      assertNull( EncodedString.decode(null, 0, 0));

      // empty bytes to empty string
      byte[] b = new byte[0];
      assertEquals( "", EncodedString.decode(b,  0,  b.length) );

      // random string, decode, then encode and compare
      StringBuilder sb = new StringBuilder();
      for(int i=0; i<1000; ++i) {
        sb.append( (char)(new Random()).nextInt(255) );
      }
      byte[] input = sb.toString().getBytes("utf-8");
      String decoded = EncodedString.decode(input, 0, input.length);
      byte[] encoded = decoded.getBytes("utf-8");
      assertTrue( Util.compareByteArrays(input,  input.length, encoded, encoded.length));

      // to big of a buffer, should throw exception
      sb = new StringBuilder();
      for(int i=0; i<MAX_MESSAGE_SIZE+1000; ++i) {
        sb.append( (char)(new Random()).nextInt(255) );
      }
      input = sb.toString().getBytes();
      try {
        decoded = EncodedString.decode(input, 0, input.length);
        fail();
      } catch(IllegalArgumentException e) {}
    }

}
