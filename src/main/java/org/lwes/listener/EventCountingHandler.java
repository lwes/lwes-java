/*======================================================================*
 * Copyright (c) 2010, Frank Maritato All rights reserved.              *
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
/**
 * @author fmaritato
 */

import org.lwes.Event;

import java.net.InetAddress;

public class EventCountingHandler implements EventHandler {

    protected int count = 0;

    public void destroy() {
        System.out.println("Count: " + count);
    }

    public void handleEvent(Event event) {
        count++;
        System.out.println("Count: "+count);
    }

    public static void main(String[] args) throws Exception {
        EventCountingHandler eventHandler = new EventCountingHandler();
        DatagramEventListener listener = new DatagramEventListener();
        listener.setAddress(InetAddress.getByName("224.1.1.11"));
        listener.setPort(6969);
        listener.addHandler(eventHandler);
        listener.setTimeToLive(1);
        listener.initialize();

        Runtime.getRuntime().addShutdownHook(new ShutdownThread(eventHandler));

        while (true) {
            Thread.sleep(1000);
        }
    }

    static class ShutdownThread extends Thread {

        EventHandler eventHandler;

        ShutdownThread(EventHandler eh) {
            eventHandler = eh;
        }

        public void run() {
            eventHandler.destroy();
        }
    }
}
