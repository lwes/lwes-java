// Copyright OpenX Limited 2010. All Rights Reserved.
package org.lwes.emitter;

import java.io.IOException;

import org.lwes.emitter.UnicastEventEmitter;

/**
 * @author Joel Meyer (joel.meyer@openx.org)
 *
 */
public class PreserializedUnicastEventEmitter extends UnicastEventEmitter {
  public void emitSerializedEvent(byte[] bytes) throws IOException {
    emit(bytes);
  }

  @Override
  public String toString() {
	return "PreserializedUnicastEventEmitter [" + getAddress() + ":" + getPort() + "]";
  }
  
}
