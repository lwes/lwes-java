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

public class BaseTypeTest {
    @SuppressWarnings("deprecation")
    @Test(expected = IllegalStateException.class)
    public void inconsistentConstructor() {
        new BaseType(FieldType.INT16.name, FieldType.UINT16.token);
    }
}
