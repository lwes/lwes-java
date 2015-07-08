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
import org.lwes.EventFactory;
import org.lwes.EventSystemException;
import org.lwes.util.NumberCodec;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Abstract class for emitting events on a datagram socket.
 *
 * @author      Kenneth Kharma
 * @since       2.1.0
 */

public abstract class DatagramSocketEventEmitter<T extends DatagramSocket>
    extends AbstractEventEmitter {

  private static transient Log log =
    LogFactory.getLog(DatagramSocketEventEmitter.class);

  /* the socket being used */
  protected T socket = null;

  /* the address */
  protected InetAddress address = null;

  /* the port */
  protected int port = 9191;

  /* the socket interface */
  protected InetAddress iface = null;

  /* a lock variable to synchronize events */
  protected Object lock = new Object();

  /**
   * Default constructor.
   */
  public DatagramSocketEventEmitter() {
  }

  /**
   * Sets the destination address for this emitter.
   *
   * @param address the address
   *
   */
  public void setAddress(InetAddress address) {
    this.address = address;
  }

  /**
   * Gets the address for this emitter.
   *
   * @return the address
   */
  public InetAddress getAddress() {
    return this.address;
  }

  /**
   * Sets the destination port for this emitter.
   *
   * @param port the port
   *
   */
  public void setPort(int port) {
    this.port = port;
  }

  /**
   * Gets the destination port for this emitter.
   *
   * @return the port
   */
  public int getPort() {
    return this.port;
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
   * Sets the ESF file used for event validation.
   * @param esfFilePath the path of the ESF file
   */
  public void setESFFilePath(String esfFilePath) {
    if (getFactory() != null) {
      getFactory().setESFFilePath(esfFilePath);
    }
  }

  /**
   * Gets the ESF file used for event validation
   * @return the ESF file path
   */
  public String getESFFilePath() {
    if (getFactory() != null) {
      return getFactory().getESFFilePath();
    } else {
      return null;
    }
  }

  /**
   * Sets an InputStream to be used for event validation.
   * @param esfInputStream an InputStream used for event validation
   */
  public void setESFInputStream(InputStream esfInputStream) {
    if (getFactory() != null) {
      getFactory().setESFInputStream(esfInputStream);
    }
  }

  /**
   * Gets the InputStream being used for event validation.
   * @return the InputStream of the ESF validator
   */
  public InputStream getESFInputStream() {
    if (getFactory() != null) {
      return getFactory().getESFInputStream();
    } else {
      return null;
    }
  }

  /**
   * Creates the underlying datagram socket.
   * @throws IOException if an I/O error occurs during socket creation.
   */
  protected abstract void createSocket() throws IOException;

  /**
   * Initializes the emitter.
   * @throws IOException if an I/O error occurs during initialization.
   */
  @Override
  public void initialize() throws IOException {
    createSocket();
    super.initialize();
  }

  /**
   * @throws IOException if an I/O error occurs on shutdown.
   */
  public void shutdown() throws IOException {
    // close the socket AFTER calling super shutdown since that is trying to
    // send a shutdown message.
    super.shutdown();
    if (socket != null) {
      socket.close();
    }
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

  /**
   * Emits the event to the network.
   *
   * @param event the event to emit
   * @exception IOException throws an IOException is there is a network error.
   * @throws EventSystemException if unable to serialize the event
   * @return number of bytes emitted
   */
  public int emit(Event event) throws IOException, EventSystemException {
    byte[] msg = event.serialize();
    int bytesEmitted = 0;
    synchronized (lock) {
      bytesEmitted = emit(msg);
      try {
        collectStatistics();
      }
      catch (EventSystemException e) {
        log.error(e.getMessage(), e);
      }
    }
    return bytesEmitted;
  }

  /**
   * Emits a byte array to the network.
   *
   * @param bytes the byte array to emit
   * @exception IOException throws an IOException if there is a network error.
   * @return number of bytes emitted
   */
  protected int emit(byte[] bytes) throws IOException {
    return emit(bytes, this.address, this.port);
  }

  /**
   * @param bytes the byte array to emit
   * @param address the address to use
   * @param port the port to use
   * @throws IOException throws an IOException if there is a network error
   * @return number of bytes emitted
   */
  protected int emit(byte[] bytes, InetAddress address, int port) throws IOException {
    /* don't send null bytes */
    if (bytes == null) return 0;

    if (socket == null || socket.isClosed()) {
      throw new IOException("Socket wasn't initialized or was closed.");
    }

    DatagramPacket dp = new DatagramPacket(bytes, bytes.length, address, port);
    socket.send(dp);

    if (log.isTraceEnabled()) {
      log.trace("Sent to network '" +
          NumberCodec.byteArrayToHexString(dp.getData(), 0, dp.getLength()));
    }
    return bytes.length;
  }
}
