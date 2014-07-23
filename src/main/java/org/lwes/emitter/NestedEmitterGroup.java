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

import java.util.concurrent.atomic.AtomicInteger;

import org.lwes.Event;

/**
 * A nesting of {@link EmitterGroup} that emits events
 * using the same strategy as {@link MOfNEmitterGroup}
 *
 */
public class NestedEmitterGroup extends EmitterGroup {
  private EmitterGroup[] emitterGroups;
  private AtomicInteger i;
  private int m;
  private int n;

  /**
   * @param emittergroups
   * @param m
   * @param filter
   */
  public NestedEmitterGroup(EmitterGroup[] emittergroups, int m, EmitterGroupFilter filter) {
    this(emittergroups, m, filter, 1.0);
  }

  public NestedEmitterGroup(EmitterGroup[] emittergroups, int m, EmitterGroupFilter filter, double sampleRate) {
    super(filter, sampleRate);
    this.m = m;
    this.n = emittergroups.length;
    this.emitterGroups = emittergroups;
    i = new AtomicInteger();
  }

  /**
   * Emits the event to the network.
   *
   * @param event the event to emit
   * @return number of bytes emitted
   */
  @Override
  protected int emit(Event e) {
    int start = i.getAndIncrement();
    int index = 0;
    int bytesEmitted = 0;
    for (int j = 0; j < m; j++) {
      index = Math.abs((start + j) % n);
      bytesEmitted += emitterGroups[index].emit(e);
    }
    return bytesEmitted;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder().append("NestedEmitterGroup [m=" + m + ", n=" + n + "]:\n");
    for (EmitterGroup g : emitterGroups) {
      sb.append("\t").append(g).append("\n");
    }

    return sb.toString();
  }
}
