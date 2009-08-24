package org.lwes.util;
/**
 * @author fmaritato
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPAddressTest {

    @Test
    public void testEmptyConstructor() {
        IPAddress ip = new IPAddress();
        assertEquals("IPAddress.toString incorrect. ", "0.0.0.0", ip.toString());
    }

    @Test
    public void testStringConstructor() {
        IPAddress ip = new IPAddress("192.168.1.1");
        assertEquals("IPAddress.toString incorrect.", "192.168.1.1", ip.toString());
    }

    @Test
    public void testByteConstructor() {
        byte[] ipBytes = new byte[]{
                (byte) 192,
                (byte) 168,
                (byte) 1,
                (byte) 1,
        };
        IPAddress ip = new IPAddress(ipBytes);
        assertEquals("Byte constructor incorrect", "192.168.1.1", ip.toString());
    }

    @Test
    public void testInetConstructor() throws UnknownHostException {
        IPAddress ip = new IPAddress(InetAddress.getByAddress(new byte[]
                {(byte) 127, (byte) 0, (byte) 0, (byte) 1}
        ));
        assertEquals("Inet constructor incorrect", "127.0.0.1", ip.toString());
    }

    @Test
    public void testEquality() throws UnknownHostException {
        IPAddress one = new IPAddress("192.168.1.1");
        IPAddress two = new IPAddress("192.168.1.1");
        assertTrue("192.168.1.1 werent equal", one.equals(two));
        assertEquals("hash codes not equal", one.hashCode(), two.hashCode());

        two = new IPAddress(InetAddress.getByName("www.yahoo.com"));
        assertFalse("InetAddress.getByName not equal", one.equals(two));
        assertNotSame("hash codes equal", one.hashCode(), two.hashCode());
    }
}
