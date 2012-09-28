// Copyright OpenX Limited 2010. All Rights Reserved.
package org.lwes.emitter;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.lwes.Event;

/**
 * This class extends {@link BroadcastEmitterGroup} and modifies it to
 * emit an event to M of the total emitters. To choose the M emitters an
 * {@link AtomicInteger} is retrieved and incremented and the values of
 * [index + 0, ... , index + M] mod N are used to select the emitters.
 * 
 * @author Joel Meyer (joel.meyer@openx.org)
 */
public class MOfNEmitterGroup extends BroadcastEmitterGroup {
  private static final Logger LOG = Logger.getLogger(MOfNEmitterGroup.class);

  private final int m;
  private final int n;
  private final AtomicInteger i;

  /**
   * @param emitters
   */
  public MOfNEmitterGroup(PreserializedUnicastEventEmitter[] emitters, int m, EmitterGroupFilter filter) {
    super(emitters, filter);
    this.m = m;
    this.n = emitters.length;
    this.i = new AtomicInteger(0);
  }

  /**
   * @see org.openx.lwes.emittergroup.BroadcastEmitterGroup#emitToGroup(org.lwes.Event)
   */
  @Override
  protected void emit(Event e) {
    if (m == n) {
      // Just call parent if we're emitting to all listeners
      super.emit(e);
    } else {
      // Choose M emitters to emit to
      byte[] bytes = e.serialize();
      
      int start = i.getAndIncrement();
      int index = 0;
      for (int j = 0; j < m; j++) {
        index = Math.abs((start + j) % n);
        try {
          emitters[index].emitSerializedEvent(bytes);
        } catch (IOException ioe) {
          LOG.error(String.format("Problem emitting event to emitter %s", emitters[index].getAddress()), ioe);
        }
      }
    }
  }

  @Override
  public String toString() {
	return "MOfNEmitterGroup [m=" + m + ", n=" + n + ", emitters=" + Arrays.toString(emitters) + "]";
  }
  
}
