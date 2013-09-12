package org.lwes.listener;
/**
 * User: frank.maritato
 * Date: 5/1/12
 */

import java.util.Collection;

import junit.framework.Assert;

import org.junit.Test;
import org.lwes.Event;

public class DatagramEventListenerTest {

    @Test
    public void testNulls() {
        DatagramEventListener listener = new DatagramEventListener();
        listener.initialize();

        Assert.assertNotNull(listener.getAddress());
        Assert.assertNull(listener.getInterface());
        Assert.assertNotNull(listener.getEnqueuer());
        Assert.assertNotNull(listener.getDequeuer());

    }

    @Test
    public void testNormal() {
        DatagramEventListener listener = new DatagramEventListener();

        listener.initialize();
        Assert.assertNotNull(listener.getAddress());
        Assert.assertNull(listener.getInterface());
        Assert.assertNotNull(listener.getEnqueuer());
        Assert.assertNotNull(listener.getDequeuer());

        Assert.assertEquals(-1, listener.getQueueSize());
        listener.setQueueSize(10);
        Assert.assertEquals(10, listener.getQueueSize());


        // Test some default values
        Assert.assertEquals(9191, listener.getPort());
        Assert.assertEquals(20, listener.getMaxThreads());
        Assert.assertEquals(31, listener.getTimeToLive());

        // Now set some stuff and make sure it sticks
        listener.setPort(1234);
        Assert.assertEquals(1234, listener.getPort());
        listener.setMaxThreads(12);
        Assert.assertEquals(12, listener.getMaxThreads());
        listener.setTimeToLive(1);
        Assert.assertEquals(1, listener.getTimeToLive());

        EmptyEventHandler emptyEventHandler = new EmptyEventHandler();
        listener.addHandler(emptyEventHandler);
        Collection<EventHandler> handlers = listener.getHandlers();
        Assert.assertNotNull(handlers);
        Assert.assertEquals(1, handlers.size());
        listener.removeHandler(emptyEventHandler);
        handlers = listener.getHandlers();
        Assert.assertNotNull(handlers);
        Assert.assertEquals(0, handlers.size());

        listener.shutdown();
    }

    class EmptyEventHandler implements EventHandler {
        public void handleEvent(Event event) {
        }
        public void destroy() {
        }
    }
}
