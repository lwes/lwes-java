/*======================================================================*
 * Copyright OpenX Limited 2010. All Rights Reserved.                   *
 *                                                                      *
 * Licensed under the New BSD License (the "License"); you may not use  *
 * this file except in compliance with the License.  Unless required    *
 * by applicable law or agreed to in writing, software distributed      *
 * under the License is distributed on an "AS IS" BASIS, WITHOUT        *
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     *
 * See the License for the specific language governing permissions and  *
 * limitations under the License. See accompanying LICENSE file.        *
 *======================================================================*/
package org.lwes.emitter;

import java.io.IOException;

/**
 * @author Joel Meyer
 *
 */
public class PreserializedUnicastEventEmitter extends UnicastEventEmitter {
  public int emitSerializedEvent(byte[] bytes) throws IOException {
    return emit(bytes);
  }

  @Override
  public String toString() {
  return "PreserializedUnicastEventEmitter [" + getAddress() + ":" + getPort() + "]";
  }

}
