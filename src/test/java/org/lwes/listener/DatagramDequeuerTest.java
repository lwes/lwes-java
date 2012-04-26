package org.lwes.listener;
/**
 * User: frank.maritato
 * Date: 4/26/12
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.lwes.Event;
import org.lwes.FieldType;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;

public class DatagramDequeuerTest {

    private static transient Log log = LogFactory.getLog(DatagramDequeuerTest.class);

    boolean eventHandled = false;

    @Test
    public void testCreationNotHandled() {
        DatagramDequeuer dequeuer = new DatagramDequeuer();
        dequeuer.setQueue(new LinkedBlockingQueue<QueueElement>(10));
        try {
            dequeuer.initialize();
        }
        catch (IOException e) {
            Assert.fail(e.getMessage());
            log.error(e.getMessage(), e);
        }
        Assert.assertFalse(dequeuer.hasPending());

        dequeuer.addHandler(new EventHandler() {
            public void handleEvent(Event event) {
                eventHandled = true;
            }

            public void destroy() {
            }
        });

        Assert.assertFalse(eventHandled);
        dequeuer.handleElement(null);
        Assert.assertFalse(eventHandled);
        dequeuer.handleElement(createBadDatagramQueueElement());
        Assert.assertFalse(eventHandled);
        dequeuer.handleElement(createDatagramQueueElement());
       // Assert.assertTrue(eventHandled);

        dequeuer.shutdown();
    }

    private DatagramQueueElement createBadDatagramQueueElement() {
        DatagramQueueElement dqe = new DatagramQueueElement();
        return dqe;
    }

    private DatagramQueueElement createDatagramQueueElement() {
        DatagramQueueElement dqe = new DatagramQueueElement();
        byte[] packetBytes = new byte[]{4, 'T', 'e', 's', 't', 0, 1, 2, 'a', 'b', FieldType.INT16.token, -10, 12};
        DatagramPacket packet = null;
        try {
            packet = new DatagramPacket(packetBytes,
                                        packetBytes.length,
                                        InetAddress.getLocalHost(),
                                        1234);
        }
        catch (UnknownHostException e) {

        }
        dqe.setPacket(packet);
        dqe.setTimestamp(1335459871);
        return dqe;
    }

}
