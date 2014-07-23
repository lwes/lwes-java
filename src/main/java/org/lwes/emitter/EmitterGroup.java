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

import java.util.Random;

import org.lwes.Event;

/**
 * @author Joel Meyer
 */
public abstract class EmitterGroup {
  protected final EmitterGroupFilter filter;
  protected static final Random random = new Random();
  protected double sampleRate;


  public EmitterGroup(EmitterGroupFilter filter) {
    this(filter, 1.0);
  }

  public EmitterGroup(EmitterGroupFilter filter, double sampleRate) {
    if (sampleRate < 0.0 || sampleRate > 1.0) {
      throw new IllegalArgumentException("Sample rate must be within range [0.0, 1.0]");
    }
    this.sampleRate = sampleRate;
    this.filter = filter;
  }

  public int emitToGroup(Event e) {
    if (sampleRate == 1.0 || (sampleRate > 0.0 && random.nextDouble() <= sampleRate)) {
      if (filter == null || filter.shouldEmit(e.getEventName()))
        return emit(e);
    }
    return 0;
  }

  protected abstract int emit(Event e);
}
