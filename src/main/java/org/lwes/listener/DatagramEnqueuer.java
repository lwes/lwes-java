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

package org.lwes.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * This class listens to packets sent via UDP, and enqueues them for processing.
 * It detects multicast addresses and listens to multicast groups if one is detected, otherwise
 * it listens for unicast datagrams.
 *
 * @author Anthony Molinaro
 * @author Michael P. Lum
 */
public class DatagramEnqueuer extends ThreadedEnqueuer {

    private static transient Log log = LogFactory.getLog(DatagramEnqueuer.class);

    /* max datagram size in bytes */
    private static final int MAX_DATAGRAM_SIZE = 65535;
    private String DEFAULT_ADDRESS = "224.0.0.69";

    /* the default network settings */
    private InetAddress address = null;
    private int port = 9191;
    private InetAddress iface = null;
    private int ttl = 31;

    /* the network socket */
    private DatagramSocket socket = null;

    /* a running buffer */
    private byte[] buffer = null;

    /* thread control */
    private boolean running = false;

    public DatagramEnqueuer() {
        super();
        buffer = new byte[MAX_DATAGRAM_SIZE];
    }

    /**
     * Gets the network address being used for this listener.
     *
     * @return the address
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * Sets the address being used for this listener.
     *
     * @param address the address to use
     */
    public void setAddress(InetAddress address) {
        this.address = address;
    }

    /**
     * Gets the port being used for this listener.
     *
     * @return the port number
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the port being used for this listener.
     *
     * @param port the port number
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Gets the network interface being used for this listener.
     *
     * @return the interface
     */
    public InetAddress getInterface() {
        return iface;
    }

    /**
     * Sets the network interface to use for this listener.
     *
     * @param iface the interface
     */
    public void setInterface(InetAddress iface) {
        this.iface = iface;
    }

    /**
     * Returns the multicast TTL (if applicable).
     * Applies to multicast listeners only.
     *
     * @return the TTL value
     */
    public int getTimeToLive() {
        return ttl;
    }

    /**
     * Sets the multicast TTL.  This typically does not need to be modified.
     * Applies to multicast listeners only.
     *
     * @param ttl the multicast TTL value.
     */
    public void setTimeToLive(int ttl) {
        this.ttl = ttl;
    }

    @Override
    public void initialize() throws IOException {
        if (address == null) {
            address = InetAddress.getByName(DEFAULT_ADDRESS);
        }

        if (address.isMulticastAddress()) {
            socket = new MulticastSocket(port);
            ((MulticastSocket) socket).setTimeToLive(ttl);
            if (iface != null) {
                ((MulticastSocket) socket).setInterface(iface);
            }
            ((MulticastSocket) socket).joinGroup(address);
        }
        else {
            if (iface != null) {
                socket = new DatagramSocket(port, iface);
            }
            else {
                socket = new DatagramSocket(port, address);
            }
            
            if(port == 0){
            	port = socket.getLocalPort();
            }
        }
        int bufSize = MAX_DATAGRAM_SIZE * 50;
        String bufSizeStr = System.getProperty("MulticastReceiveBufferSize");
        if (bufSizeStr != null && !"".equals(bufSizeStr)) {
            bufSize = Integer.parseInt(bufSizeStr);
        }
        socket.setReceiveBufferSize(bufSize);
    }

    @Override
    public synchronized void shutdown() {
        running = false;
    }

    /**
     * While running, repeatedly read datagrams and insert them into the queue along with the
     * receipt time and other metadata.
     */
    @Override
    public void run() {
        running = true;

        while (running) {
            try {
                DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
                socket.receive(datagram);
                if (log.isTraceEnabled()) {
                    log.trace("Received datagram: " + datagram);
                }
                /* we record the time *after* the receive because it blocks */
                long receiptTime = System.currentTimeMillis();

                /* copy the data into a tight buffer so we can release the loose buffer */
                final byte[] tightBuffer = new byte[datagram.getLength()];
                System.arraycopy(datagram.getData(), 0, tightBuffer, 0, tightBuffer.length);
                datagram.setData(tightBuffer);

                /* create an element for the queue */
                DatagramQueueElement element = new DatagramQueueElement();
                element.setPacket(datagram);
                element.setTimestamp(receiptTime);

                /* add the element to the queue and notify everyone there's work to do */
                queue.put(element);
                if (log.isTraceEnabled()) {
                    log.trace("Enqueued: " + element);
                }
			} catch(Exception e) {
				log.warn("Unable to read datagram", e);
			}
		}
	}

}
