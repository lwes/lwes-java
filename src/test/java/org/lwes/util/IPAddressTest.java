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

package org.lwes.util;
/**
 * @author fmaritato
 */

import org.junit.Test;
import org.lwes.Event;
import org.lwes.EventSystemException;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testGetSet() throws EventSystemException, UnknownHostException {
        Event e = new Event("TestEvent", null);
        IPAddress ip = new IPAddress("127.0.0.1");
        e.setIPAddress("ipaddr", ip);
        assertEquals("Type fail",
                     ip.getClass().getName(),
                     e.getIPAddressObj("ipaddr").getClass().getName());
        assertEquals("Type fail",
                     new byte[]{}.getClass().getName(),
                     e.getIPAddress("ipaddr").getClass().getName());

        e.setIPAddress("inetaddr", InetAddress.getLocalHost());
        assertEquals("Type fail",
                     ip.getClass().getName(),
                     e.getIPAddressObj("inetaddr").getClass().getName());
        assertEquals("Type fail",
                     new byte[]{}.getClass().getName(),
                     e.getIPAddress("inetaddr").getClass().getName());

        e.setIPAddress("bytesaddr", new byte[]{(byte) 127, (byte) 0, (byte) 0, (byte) 1});
        assertEquals("Type fail",
                     ip.getClass().getName(),
                     e.getIPAddressObj("bytesaddr").getClass().getName());
        assertEquals("Type fail",
                     new byte[]{}.getClass().getName(),
                     e.getIPAddress("bytesaddr").getClass().getName());
    }
}
