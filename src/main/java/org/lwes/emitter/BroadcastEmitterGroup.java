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

import org.apache.log4j.Logger;
import org.lwes.Event;

/**
 * This class emits an event to all members of the group.
 *
 * @author Joel Meyer
 */
public class BroadcastEmitterGroup extends EmitterGroup {
  private static final Logger LOG = Logger.getLogger(BroadcastEmitterGroup.class);

  protected final DatagramSocketEventEmitter<?>[] emitters;

  public BroadcastEmitterGroup(DatagramSocketEventEmitter<?>[] emitters, EmitterGroupFilter filter) {
    this(emitters, filter, 1.0);
  }

  public BroadcastEmitterGroup(DatagramSocketEventEmitter<?>[] emitters, EmitterGroupFilter filter, double sampleRate) {
    super(filter, sampleRate);
    this.emitters = emitters;
  }

  @Override
  protected int emit(Event e) {
    byte[] bytes = e.serialize();
    int bytesEmitted = 0;
    for (int i = 0; i < emitters.length; i++) {
      try {
        bytesEmitted += emitters[i].emit(bytes);
      } catch (IOException ioe) {
        LOG.error(String.format("Problem emitting event to emitter %s", emitters[i].getAddress()), ioe);
      }
    }
    return bytesEmitted;
  }
}
