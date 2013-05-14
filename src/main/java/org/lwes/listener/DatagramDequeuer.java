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
import org.lwes.Event;
import org.lwes.EventFactory;
import org.lwes.util.IPAddress;

import java.io.IOException;
import java.net.DatagramPacket;

public class DatagramDequeuer extends ThreadedDequeuer {

    private static transient Log log = LogFactory.getLog(DatagramDequeuer.class);

    private boolean running = false;

    /* an event factory */
    private EventFactory factory = new EventFactory();

    public DatagramDequeuer() {
    }

    @Override
    public void initialize() throws IOException {
        super.initialize();
    }

    @Override
    public synchronized void shutdown() {
        running = false;
    }

    @Override
    public void run() {
        running = true;

        while (running) {
            try {
                QueueElement element = null;
                element = queue.take();
                if (log.isTraceEnabled()) {
                    log.trace("Removed from queue: " + element);
                }
                handleElement((DatagramQueueElement) element);
            }
            catch (UnsupportedOperationException uoe) {
                // not a problem, someone grabbed the event before we did
            }
            catch (Exception e) {
                log.error("Error in dequeueing event for processing", e);
            }
        }
    }

    /**
     * Determines whether the collection of pending tasks has any elements. Note
     * that if this returns false at some point and then someone calls
     * <code>addTask()</code> the state will change to true. Do not use this
     * function to determine when there cannot be any more tasks.
     *
     * @return true iff there are any pending tasks.
     */
    protected final boolean hasPending() {
        return queue.size() > 0;
    }

    protected void handleElement(DatagramQueueElement element) {
        if (element == null) {
            return;
        }

        DatagramPacket packet = element.getPacket();
        if (packet == null) {
            return;
        }

        /* get some metadata */
        long timestamp = element.getTimestamp();
        IPAddress address = new IPAddress(packet.getAddress());
        int port = packet.getPort();

        /* now try to deserialize the packet */
        try {
            /* don't validate the event for now to save time */
            Event event = factory.createEvent(getData(packet), false);
            event.setInt64(Event.RECEIPT_TIME, timestamp);
            event.setIPAddress(Event.SENDER_IP, address);
            event.setUInt16(Event.SENDER_PORT, port);
            if (log.isTraceEnabled()) {
                log.trace("Dispatching event " + event.toString());
            }
            dispatchEvent(event);
        }
        catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Unable to deserialize event in handleElement()", e);
            }
        }
    }
    
    protected byte[] getData (DatagramPacket packet) throws IOException {
    	return packet.getData();
    }
}
