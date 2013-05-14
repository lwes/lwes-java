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

import org.lwes.EventSystemException;

import java.net.InetAddress;
import java.util.Collection;

/**
 * This is an event listener that handles UDP packets.  Automatically
 * detects multicast addresses and joins those groups.
 * <p/>
 * Sample code that prints multicast events to stdout:
 * <pre>
 * EventHandler myHandler = new EventPrintingHandler();
 * InetAddress address = InetAddress.getByName("224.0.0.69");
 * <p/>
 * DatagramEventListener listener = new DatagramEventListener();
 * listener.setAddress(address);
 * listener.setPort(9191);
 * listener.addHandler(myHandler);
 * listener.initialize();
 * </pre>
 *
 * @author Michael P. Lum
 */
public class DatagramEventListener extends ThreadedEventListener {
    /* the enqueuer to use to acquire multicast packets */
    private DatagramEnqueuer enqueuer;

    /* the dequeuer to use to handle packets */
    private DatagramDequeuer dequeuer;

    /**
     * Default constructor.
     */
    public DatagramEventListener() {
    	this(new DatagramEnqueuer(), new DatagramDequeuer());
    }
    
    protected DatagramEventListener(DatagramEnqueuer enqueuer, DatagramDequeuer dequeuer){
    	this.enqueuer = enqueuer;
    	this.dequeuer = dequeuer;
    }

    /**
     * Gets the address to use for this listener
     *
     * @return the address
     */
    public InetAddress getAddress() {
        if (enqueuer == null) {
            return null;
        }
        return enqueuer.getAddress();
    }

    /**
     * Sets the address to use for this listener
     *
     * @param address the address
     */
    public void setAddress(InetAddress address) {
        if (enqueuer != null) {
            enqueuer.setAddress(address);
        }
    }

    /**
     * Gets the port to use for this listener
     *
     * @return the port
     */
    public int getPort() {
        if (enqueuer == null) {
            return 0;
        }
        return enqueuer.getPort();
    }

    /**
     * Sets the port to use for this listener
     *
     * @param port the port
     */
    public void setPort(int port) {
        if (enqueuer != null) {
            enqueuer.setPort(port);
        }
    }

    /**
     * Get the interface to use for this listener
     *
     * @return the interface
     */
    public InetAddress getInterface() {
        if (enqueuer == null) {
            return null;
        }
        return enqueuer.getInterface();
    }

    /**
     * Sets the interface to use for this listener
     *
     * @param iface the interface
     */
    public void setInterface(InetAddress iface) {
        if (enqueuer != null) {
            enqueuer.setInterface(iface);
        }
    }

    /**
     * Get the TTL to use for this listener.  Only applies to
     * multicast listeners.  Typically this does not need to be changed.
     *
     * @return the interface
     */
    public int getTimeToLive() {
        if (enqueuer == null) {
            return 0;
        }
        return enqueuer.getTimeToLive();
    }

    /**
     * Sets the TTL to use for this listener.  Only applies to multicast listeners.
     * Typically this does not need to be changed.
     *
     * @param ttl the TTL value
     */
    public void setTimeToLive(int ttl) {
        if (enqueuer != null) {
            enqueuer.setTimeToLive(ttl);
        }
    }

    /**
     * Returns the max number of threads allowed by the system.
     *
     * @return the allowed threads
     */
    public int getMaxThreads() {
        if (dequeuer == null) {
            return 0;
        }
        return dequeuer.getMaxThreads();
    }

    /**
     * Sets the max number of threads allowed by the system.
     *
     * @param threads the max number of threads
     */
    public void setMaxThreads(int threads) {
        if (dequeuer != null) {
            dequeuer.setMaxThreads(threads);
        }
    }

    /**
     * Adds an event handler to this listener.  This has a callback that will be invoked
     * for every event coming through the system.
     *
     * @param handler the EventHandler to add
     */
    @Override
    public void addHandler(EventHandler handler) {
        if (dequeuer != null) {
            dequeuer.addHandler(handler);
        }
    }

    /**
     * Removes an event handler from the system.  This causes the event handler to no longer
     * receive events coming through the system.
     *
     * @param handler the EventHandler to remove
     */
    public void removeHandler(EventHandler handler) {
        if (dequeuer != null) {
            dequeuer.removeHandler(handler);
        }
    }

    public Collection<EventHandler> getHandlers() {
        if (dequeuer != null) {
            return dequeuer.getHandlers();
        }
        return null;
    }

    /**
     * Initializes the listener.
     *
     * @throws EventSystemException thrown if there is a problem initializing the listener
     */
    @Override
    public void initialize() throws EventSystemException {
        this.setEnqueuer(enqueuer);
        this.setDequeuer(dequeuer);
        super.initialize();
	}

}
