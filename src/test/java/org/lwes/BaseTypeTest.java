/*======================================================================*
 * Licensed under the New BSD License (the "License"); you may not use  *
 * this file except in compliance with the License.  Unless required    *
 * by applicable law or agreed to in writing, software distributed      *
 * under the License is distributed on an "AS IS" BASIS, WITHOUT        *
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     *
 * See the License for the specific language governing permissions and  *
 * limitations under the License. See accompanying LICENSE file.        *
 *======================================================================*/
package org.lwes;

import org.junit.Test;
import org.lwes.serializer.StringParser;
import org.lwes.util.IPAddress;

import static org.junit.Assert.assertEquals;

public class BaseTypeTest {
    @SuppressWarnings({ "deprecation" })
    @Test(expected = IllegalStateException.class)
    public void inconsistentConstructor() {
        new BaseType(FieldType.INT16.name, FieldType.UINT16.token);
    }

    @Test
    public void parsing() {
      assertEquals(Byte.valueOf((byte) 100), new BaseType(FieldType.BYTE).parseFromString("100"));
      assertEquals(Short.valueOf((short) 100), new BaseType(FieldType.INT16).parseFromString("100"));
      assertEquals(Integer.valueOf(100), new BaseType(FieldType.INT32).parseFromString("100"));
      assertEquals(Long.valueOf(100), new BaseType(FieldType.INT64).parseFromString("100"));
      assertEquals(Integer.valueOf(100), new BaseType(FieldType.UINT16).parseFromString("100"));
      assertEquals(Long.valueOf(100), new BaseType(FieldType.UINT32).parseFromString("100"));
      assertEquals(Long.valueOf(100), new BaseType(FieldType.UINT64).parseFromString("100"));  // FIXME: why not BigInteger?
      assertEquals("100", new BaseType(FieldType.STRING).parseFromString("100"));
      assertEquals(new IPAddress(new byte[] { 1, 2, 3, 4 }), new BaseType(FieldType.IPADDR).parseFromString("1.2.3.4"));
      assertEquals(Boolean.TRUE, new BaseType(FieldType.BOOLEAN).parseFromString("true"));
      assertEquals(Float.valueOf(100), new BaseType(FieldType.FLOAT).parseFromString("100"));
      assertEquals(Double.valueOf(100), new BaseType(FieldType.DOUBLE).parseFromString("100"));
    }

    @Test
    public void sizes() {
      // constant sizes
      {
        assertEquals(2, (new BaseType(FieldType.UINT16, StringParser.fromStringUINT16("100")) ).getByteSize() );
        assertEquals(2, (new BaseType(FieldType.INT16, StringParser.fromStringINT16("100")) ).getByteSize() );
        assertEquals(4, (new BaseType(FieldType.UINT32, StringParser.fromStringUINT32("10000")) ).getByteSize() );
        assertEquals(4, (new BaseType(FieldType.INT32, StringParser.fromStringINT32("10000")) ).getByteSize() );
        assertEquals(8, (new BaseType(FieldType.INT64, StringParser.fromStringINT64("1000000")) ).getByteSize() );
      }

      // string
      {
        assertEquals(21, (new BaseType(FieldType.STRING, (Object)"string with size 19") ).getByteSize() );
        assertEquals(10, (new BaseType(FieldType.STRING, (Object)new String("A" + "\u00ea" + "\u00f1" + "\u00fc" + "C")) ).getByteSize() );
      }

      // array
      {
        String[] strArray = new String[3];
        strArray[0] = "size-6";
        strArray[1] = "size--7";
        strArray[2] = "size---8";
        assertEquals(2 + (2+6) + (2+7) + (2+8), (new BaseType(FieldType.STRING_ARRAY, (Object)strArray) ).getByteSize() );
        boolean[] boolArray = new boolean[3];
        boolArray[0]= false;
        boolArray[1]= false;
        boolArray[2]= true;
        assertEquals(2 + boolArray.length, (new BaseType(FieldType.BOOLEAN_ARRAY, (Object)boolArray) ).getByteSize() );
      }
      // nullable array
      {
        String[] strArray = new String[3];
        strArray[0] = "size-6";
        strArray[1] = null;
        strArray[2] = new String("A" + "\u00ea" + "\u00f1" + "\u00fc" + "C");
        assertEquals( (2+2+1) + (2+6) + (0) + (10), (new BaseType(FieldType.NSTRING_ARRAY, (Object)strArray) ).getByteSize() );

      }
    }
}
