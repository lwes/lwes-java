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

import java.util.HashMap;
import java.util.Properties;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.lwes.EventFactory;
import org.lwes.emitter.EmitterGroupFilter.FilterType;


@FunctionalInterface
public interface EmitterGroupFactory {
  public EmitterGroup create(Properties props, String groupName, String prefix, EventFactory factory) throws IOException;
}
