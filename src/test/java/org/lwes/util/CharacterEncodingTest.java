package org.lwes.util;
/**
 * @author fmaritato
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

public class CharacterEncodingTest {

    @Test
    public void testCharEncodeInstance() {
        CharacterEncoding utf8 = null;
        try {
            utf8 = CharacterEncoding.getInstance("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            fail(e.getMessage());
        }
        assertNotNull(utf8);

        CharacterEncoding iso88591 = null;
        try {
            iso88591 = CharacterEncoding.getInstance("ISO-8859-1");
        }
        catch (UnsupportedEncodingException e) {
            fail(e.getMessage());
        }
        assertNotNull(iso88591);

        assertFalse("utf8 = iso-8859-1", utf8.equals(iso88591));

    }

    @Test
    public void testCharEncodeStatics() {
        CharacterEncoding utf8 = CharacterEncoding.UTF_8;
        assertEquals(utf8.getEncodingString(), "UTF-8");
        CharacterEncoding ascii = CharacterEncoding.ASCII;
        assertEquals(ascii.getEncodingString(), "ASCII");
        CharacterEncoding jp = CharacterEncoding.EUC_JP;
        assertEquals(jp.getEncodingString(), "EUC_JP");
        CharacterEncoding kr = CharacterEncoding.EUC_KR;
        assertEquals(kr.getEncodingString(), "EUC_KR");
        CharacterEncoding iso8859 = CharacterEncoding.ISO_8859_1;
        assertEquals(iso8859.getEncodingString(), "ISO-8859-1");
        CharacterEncoding jis = CharacterEncoding.SHIFT_JIS;
        assertEquals(jis.getEncodingString(), "SJIS");
    }
}
