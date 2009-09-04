package org.lwes;
/**
 * @author fmaritato
 */

import org.apache.commons.codec.binary.Base64;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.lwes.db.EventTemplateDB;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class EventTest {

    private EventTemplateDB eventTemplate;

    public void setUp() {
        eventTemplate = new EventTemplateDB();
    }

    @Test
    public void testSerialize() throws EventSystemException {
        Event evt = new Event("Test", false, eventTemplate);
        evt.setString("attr_s", "str_value");
        evt.setInt32("attr_i", 1);
        byte[] bytes = evt.serialize();
        String str = new String(bytes);
        System.out.println("as string: "+str);
        byte[] encoded = Base64.encodeBase64(bytes);
        System.out.println(new String(encoded));
    }

    @Test
    public void testEventAccessors() throws EventSystemException, UnknownHostException {
        Event evt = new Event("Test", false, eventTemplate);

        evt.setInt16("int16", (short) 1);
        evt.setInt32("int32", 1337);
        evt.setInt64("int64", 1337133713371337l);
        evt.setBoolean("bool", true);
        evt.setString("str", "string");
        evt.setUInt16("uint16", 1337); // uint16 in java is just an int
        evt.setUInt32("uint32", 1337133713371337l); // uint32 in java is a long
        evt.setUInt64("uint64", 1337133713371337l); // uint64 is a BigInteger
        evt.setIPAddress("ipaddr", InetAddress.getByName("localhost"));

        assertEquals("int16 wrong", 1, evt.getInt16("int16"));
        assertEquals("int32 wrong", 1337, evt.getInt32("int32"));
        assertEquals("int64 wrong", 1337133713371337l, evt.getInt64("int64"));
        assertEquals("bool wrong", true, evt.getBoolean("bool"));
        assertEquals("str wrong", "string", evt.getString("str"));
        assertEquals("uint16 wrong", 1337, evt.getUInt16("uint16"));
        assertEquals("uint32 wrong", 1337133713371337l, evt.getUInt32("uint32"));
        assertEquals("uint64 wrong",
                     BigInteger.valueOf(1337133713371337l),
                     evt.getUInt64("uint64"));

    }

    @Test
    public void testEventSize() throws EventSystemException {

        Event evt = new Event("Test", false, eventTemplate);

        for (int i = 0; i < 5000; i++) {
            evt.setInt32("" + i, i);
        }

        byte[] bytes = evt.serialize();
        assertEquals("number of bytes wrong?", 48904, bytes.length);

        boolean exceptionThrown = false;
        try {
            for (int i = 5001; i < 10000; i++) {
                evt.setInt32("" + i, i);
            }
        }
        catch (Exception e) {
            exceptionThrown = true;
            assertEquals("Different exception",
                         "org.lwes.EventSystemException",
                         e.getClass().getName());
        }
        assertTrue("Size exception was not thrown", exceptionThrown);
    }
}
