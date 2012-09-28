// Copyright OpenX Limited 2010. All Rights Reserved.
package org.lwes.emitter;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.lwes.Event;

/**
 * This class emits an event to all members of the group.
 * 
 * @author Joel Meyer (joel.meyer@openx.org)
 */
public class BroadcastEmitterGroup extends EmitterGroup {
  private static final Logger LOG = Logger.getLogger(BroadcastEmitterGroup.class);

  protected final PreserializedUnicastEventEmitter[] emitters;

  public BroadcastEmitterGroup(PreserializedUnicastEventEmitter[] emitters, EmitterGroupFilter filter) {
    super(filter);
    this.emitters = emitters;
  }

  @Override
  protected void emit(Event e) {
    byte[] bytes = e.serialize();
    for (int i = 0; i < emitters.length; i++) {
      try {
        emitters[i].emitSerializedEvent(bytes);
      } catch (IOException ioe) {
        LOG.error(String.format("Problem emitting event to emitter %s", emitters[i].getAddress()), ioe);
      }
    }
  }
}
