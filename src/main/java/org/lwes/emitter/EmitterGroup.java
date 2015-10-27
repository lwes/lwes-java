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
import java.util.Random;

import org.lwes.Event;
import org.lwes.EventFactory;
import org.lwes.EventSystemException;

/**
 * @author Joel Meyer
 */
public abstract class EmitterGroup {
  protected final EmitterGroupFilter filter;
  protected static final Random random = new Random();
  protected double sampleRate;
  protected EventFactory factory;

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

  public EmitterGroup(EmitterGroupFilter filter, EventFactory factory) {
    this(filter);
    this.factory = factory;
  }

  public EmitterGroup(EmitterGroupFilter filter, double sampleRate,
                      EventFactory factory) {
    this(filter, sampleRate);
    this.factory = factory;
  }

  /**
   * Creates a new event named <tt>eventName</tt>.
   * @param eventName the name of the event to be created
   * @return a new Event
   * @exception EventSystemException if there is a problem creating the event
   */
  public Event createEvent(String eventName) throws EventSystemException {
    return createEvent(eventName, true);
  }

  /**
   * Creates a new event named <tt>eventName</tt>.
   * @param eventName the name of the event to be created
   * @param validate whether or not to validate the event against the EventTemplateDB
   * @return a new Event
   * @exception EventSystemException if there is a problem creating the event
   */
  public Event createEvent(String eventName, boolean validate) throws EventSystemException {
    if (getFactory() != null) {
      return getFactory().createEvent(eventName, validate);
    } else {
      throw new EventSystemException("EventFactory not initialized");
    }
  }

  public int emitToGroup(Event e) {
    if (sampleRate == 1.0 || (sampleRate > 0.0 && random.nextDouble() <= sampleRate)) {
      if (filter == null || filter.shouldEmit(e.getEventName()))
        return emit(e);
    }
    return 0;
  }

  protected EventFactory getFactory() {
    return factory;
  }

  protected void setFactory(EventFactory factory) {
    this.factory = factory;
  }

  protected abstract int emit(Event e);

  public abstract void shutdown() throws IOException;
}
