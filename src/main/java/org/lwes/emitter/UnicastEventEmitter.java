/*======================================================================*
 * Copyright (c) 2008, Yahoo! Inc. All rights reserved.                 *
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.DatagramSocket;

/**
 * UnicastEventEmitter emits events as unicast datagrams on the network.
 *
 * @author      Michael P. Lum
 * @author      Anthony Molinaro
 * @since       0.0.1
 *
 * Example code:
 * <pre>
 * UnicastEventEmitter emitter = new UnicastEventEmitter();
 * emitter.setESFFilePath("/path/to/esf/file");
 * emitter.setAddress(InetAddress.getByName("224.0.0.69"));
 * emitter.setPort(9191);
 * emitter.initialize();
 *
 * Event e = emitter.createEvent("MyEvent", false);
 * e.setString("key","value");
 * emitter.emit(e);
 * </pre>
 */

public class UnicastEventEmitter extends DatagramSocketEventEmitter<DatagramSocket> {

  private static transient Log log = LogFactory.getLog(UnicastEventEmitter.class);

  /**
   * Default constructor.
   */
  public UnicastEventEmitter() {
  }

  /**
   * Creates the unicast <code>DatagramSocket</code>.
   */
  @Override
  protected void createSocket() throws IOException {
    socket = new DatagramSocket(0, iface);
    socket.setReuseAddress(true);
  }
}
