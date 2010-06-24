package org.lwes.util;
/**
 * @author fmaritato
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class EncodedStringTest {

    private static transient Log log = LogFactory.getLog(EncodedStringTest.class);

    @Test
    public void testByteConstructor() {
        byte[] bytes = new byte[] {116,101,115,116,105,110,103};
        EncodedString str = new EncodedString(bytes, CharacterEncoding.UTF_8);
        assertEquals("Byte Constructor failed.", "testing", str.toString());
    }

    @Test
    public void testStringConstructor() {
        EncodedString str = new EncodedString("testing", CharacterEncoding.UTF_8);
        assertEquals("Byte Constructor failed.", "testing", str.toString());
    }

    @Test
    public void testStaticBytesToString() {
        byte[] bytes = new byte[] {116,101,115,116,105,110,103};
        String str = EncodedString.bytesToString(bytes, CharacterEncoding.UTF_8);
        assertNotNull(str);
        assertEquals("Static bytesToString failed", "testing", str);

        str = EncodedString.bytesToString(null, CharacterEncoding.UTF_8);
        assertNull("String was non-null", str);
    }

    @Test
    public void testStaticGetBytes() {
        byte[] bytes = EncodedString.getBytes("testing", CharacterEncoding.UTF_8);
        assertNotNull(bytes);
        // TODO: compare the byte array

        bytes = EncodedString.getBytes(null, CharacterEncoding.UTF_8);
        assertNull("Byte array was non-null", bytes);
    }
}
