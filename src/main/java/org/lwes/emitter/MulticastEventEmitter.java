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
import org.lwes.Event;
import org.lwes.EventSystemException;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * MulticastEventEmitter emits events to multicast groups on the network.  This is the most common
 * class used by users of the Light Weight Event System.
 * <p/>
 * Example code:
 * <pre>
 * MulticastEventEmitter emitter = new MulticastEventEmitter();
 * emitter.setESFFilePath("/path/to/esf/file");
 * emitter.setMulticastAddress(InetAddress.getByName("224.0.0.69"));
 * emitter.setMulticastPort(9191);
 * emitter.initialize();
 * <p/>
 * Event e = emitter.createEvent("MyEvent", false);
 * e.setString("key","value");
 * emitter.emit(e);
 * </pre>
 *
 * @author Michael P. Lum
 * @author Anthony Molinaro
 * @since 0.0.1
 */
public class MulticastEventEmitter extends DatagramSocketEventEmitter<MulticastSocket> {

  private static transient Log log = LogFactory.getLog(MulticastEventEmitter.class);

  /* the multicast interface */
  protected InetAddress iface = null;

  /* the multicast time-to-live */
  protected int ttl = 31;

  /**
   * Default constructor.
   */
  public MulticastEventEmitter() {
  }

  /**
   * Sets the multicast address for this emitter.  Preserved for backwards compatibility.
   *
   * @param address the multicast address
   */
  public void setMulticastAddress(InetAddress address) {
    super.setAddress(address);
  }

  /**
   * Gets the multicast address for this emitter.  Preserved for backwards compatibility.
   *
   * @return the address
   */
  public InetAddress getMulticastAddress() {
    return super.getAddress();
  }

  /**
   * Sets the multicast port for this emitter.  Preserved for backwards compatibility.
   *
   * @param port the multicast port
   */
  public void setMulticastPort(int port) {
    super.setPort(port);
  }

  /**
   * Gets the multicast port for this emitter.  Preserved for backwards compatibility.
   *
   * @return the multicast port
   */
  public int getMulticastPort() {
    return super.getPort();
  }

  /**
   * Sets the network interface for this emitter.
   *
   * @param iface the network interface
   */
  public void setInterface(InetAddress iface) {
    this.iface = iface;
  }

  /**
   * Gets the network interface for this emitter.
   *
   * @return the interface address
   */
  public InetAddress getInterface() {
    return this.iface;
  }

  /**
   * Sets the multicast time-to-live for this emitter.
   *
   * @param ttl the time to live
   */
  public void setTimeToLive(int ttl) {
    this.ttl = ttl;
  }

  /**
   * Gets the multicast time-to-live for this emitter.
   *
   * @return the time to live
   */
  public int getTimeToLive() {
    return this.ttl;
  }

  /**
   * Creates the multicast <code>MulticastSocket</code>.
   */
  @Override
  protected void createSocket() throws IOException {
    socket = new MulticastSocket();

    if (iface != null) {
      socket.setInterface(iface);
    }

    socket.setTimeToLive(ttl);
  }
}
