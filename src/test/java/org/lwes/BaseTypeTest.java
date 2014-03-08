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

import java.math.BigInteger;

import org.junit.Test;
import org.lwes.util.IPAddress;

import static org.junit.Assert.assertEquals;

public class BaseTypeTest {
    @SuppressWarnings({ "deprecation", "unused" })
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
      assertEquals(BigInteger.valueOf(100), new BaseType(FieldType.UINT64).parseFromString("100"));
      assertEquals("100", new BaseType(FieldType.STRING).parseFromString("100"));
      assertEquals(new IPAddress(new byte[] { 1, 2, 3, 4 }), new BaseType(FieldType.IPADDR).parseFromString("1.2.3.4"));
      assertEquals(Boolean.TRUE, new BaseType(FieldType.BOOLEAN).parseFromString("true"));
      assertEquals(Float.valueOf(100), new BaseType(FieldType.FLOAT).parseFromString("100"));
      assertEquals(Double.valueOf(100), new BaseType(FieldType.DOUBLE).parseFromString("100"));
    }
}
