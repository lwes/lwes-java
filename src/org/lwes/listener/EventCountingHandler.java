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
