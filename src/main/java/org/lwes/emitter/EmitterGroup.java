// Copyright OpenX Limited 2010. All Rights Reserved.
package org.lwes.emitter;

import org.lwes.Event;

/**
 * @author Joel Meyer (joel.meyer@openx.org)
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
