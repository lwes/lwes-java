package org.lwes.listener;
/**
 * User: frank.maritato
 * Date: 4/26/12
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.lwes.FieldType;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DatagramQueueElementTest {

    private static transient Log log = LogFactory.getLog(DatagramQueueElementTest.class);

    @Test
    public void testObjectMethods() {
        DatagramQueueElement dqe1 = new DatagramQueueElement();
        DatagramPacket p = createPacket1();
        dqe1.setPacket(p);
        dqe1.setTimestamp(1335459871);

        DatagramQueueElement dqe2 = new DatagramQueueElement();
        dqe2.setPacket(p);
        dqe2.setTimestamp(1335459871);

        // TODO -- apparently the equals and hashCode for DQE rely on DatagramPacket
        // TODO -- equals and hashCode which are not overridden so it is checking memory locations...
        Assert.assertTrue(dqe1.equals(dqe2));
        Assert.assertEquals(dqe1.hashCode(), dqe2.hashCode());

        dqe2.setPacket(createPacket2());
        Assert.assertFalse(dqe1.equals(dqe2));
        Assert.assertNotSame(dqe1.hashCode(), dqe2.hashCode());

        dqe1.clear();
        Assert.assertNull(dqe1.getPacket());
        Assert.assertEquals(0l, dqe1.getTimestamp());
    }

    private DatagramPacket createPacket1() {
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
        return packet;
    }

    private DatagramPacket createPacket2() {
        byte[] packetBytes = new byte[]{4, 'M', 'e', 's', 's', 0, 1, 2, 'a', 'b', FieldType.INT16.token, -10, 12};
        DatagramPacket packet = null;
        try {
            packet = new DatagramPacket(packetBytes,
                                        packetBytes.length,
                                        InetAddress.getLocalHost(),
                                        1234);
        }
        catch (UnknownHostException e) {

        }
        return packet;
    }
}
