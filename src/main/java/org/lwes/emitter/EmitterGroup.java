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

import org.lwes.Event;

/**
 * @author Joel Meyer
 */
public abstract class EmitterGroup {
  protected final EmitterGroupFilter filter;
  
  public EmitterGroup(EmitterGroupFilter filter) {
    this.filter = filter;
  }

  public void emitToGroup(Event e) {
    if (filter == null || filter.shouldEmit(e.getEventName())) emit(e);
  }

  protected abstract void emit(Event e);
}
