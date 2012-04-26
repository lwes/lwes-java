package org.lwes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Enumeration;

import org.apache.log4j.Logger;
import org.junit.Test;

public final class ArrayEventTest {

    private static final Logger log = Logger.getLogger(ArrayEventTest.class);

    @Test
    public void testBasicFunctions() throws EventSystemException {
        final byte[] bytes = new byte[]{4, 'T', 'e', 's', 't', 0, 1, 2, 'a', 'b', FieldType.INT16.token, -10, 12};
        @SuppressWarnings("deprecation")
        final ArrayEvent e1 = new ArrayEvent(bytes);
        assertTrue(Arrays.equals(bytes, e1.serialize()));
        assertEquals("Test { \tab = -2548; }", e1.toOneLineString());
        assertEquals("Test { \tab = -2548; }", e1.copy().toOneLineString());
        assertTrue(e1.isSet("ab"));

        final Event e2 = e1.copy();
        e2.clear("ab");
        assertEquals("Test { }", e2.toOneLineString());
        assertTrue(e1.isSet("ab"));
        assertFalse(e2.isSet("ab"));

        final Event e3 = e1.copy();
        e3.setEventName("Different");
        assertEquals("Different { \tab = -2548; }", e3.toOneLineString());
        e3.set("cd", FieldType.STRING, "value");
        assertEquals("Different { \tab = -2548; \tcd = value; \tenc = 1; }", e3.toOneLineString());
        e3.setEncoding(Event.ISO_8859_1);
        assertEquals("Different { \tab = -2548; \tcd = value; \tenc = 0; }", e3.toOneLineString());
        e3.set("enc", FieldType.INT16, Event.DEFAULT_ENCODING);
        assertEquals("Different { \tab = -2548; \tcd = value; \tenc = 1; }", e3.toOneLineString());
        e3.set("ab", FieldType.INT16, (short) -1234);
        assertEquals("Different { \tab = -1234; \tcd = value; \tenc = 1; }", e3.toOneLineString());

        final Enumeration<String> names = e3.getEventAttributeNames();
        assertEquals("ab", names.nextElement());
        assertEquals("cd", names.nextElement());
        assertEquals("enc", names.nextElement());
        assertFalse(names.hasMoreElements());

        assertEquals(FieldType.INT16, e3.getType("ab"));
        assertEquals(FieldType.STRING, e3.getType("cd"));
        assertEquals(FieldType.INT16, e3.getType("enc"));
        assertNull(e3.getType("zzz"));

        final Event e4 = new MapEvent();
        e4.copyFrom(e3);
        assertEquals("Different { \tab = -1234; \tcd = value; \tenc = 1; }", e4.toOneLineString());

        final Event e5 = new ArrayEvent();
        e5.copyFrom(e3);
        assertEquals("Different { \tab = -1234; \tcd = value; \tenc = 1; }", e5.toOneLineString());
        e5.copyFrom(e4);
        assertEquals("Different { \tab = -1234; \tcd = value; \tenc = 1; }", e5.toOneLineString());

        e1.reset();
        assertEquals(" { }", e1.toOneLineString());
        assertFalse(e1.isSet("ab"));
        System.gc();
    }

    @Test
    public void testLengthRestriction() throws EventSystemException {
        final ArrayEvent event = new ArrayEvent("Event");
        event.setByteArray("field", new byte[65490]);
        try {
            event.setByteArray("field", new byte[65491]);
            fail("Should have failed when creating such a large event");
        }
        catch (EventSystemException e) {
            if (!e.getMessage().contains("causing an overrun")) {
                throw e;
            }
        }
    }

    @Test(expected = EventSystemException.class)
    public void testInvalidEncodingType() throws EventSystemException {
        final ArrayEvent event = new ArrayEvent("Event");
        event.set("enc", FieldType.INT32, Event.DEFAULT_ENCODING);
    }
}
