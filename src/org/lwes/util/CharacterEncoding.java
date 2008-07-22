package org.lwes.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * This is a little class to abstract the character encoding strings that
 * Java uses into classes which can be checked at compile time.
 *
 * @author Kevin Scaldeferri
 */
public abstract class CharacterEncoding {
  /*
   * Returns Java's canonical form of the encoding.
   */
  public abstract String getEncodingString();

  /*
   * Returns the official IANA name for the encoding.  Everything
   * expect Java expects this form.
   */
  public abstract String getIANAString();

  public static final CharacterEncoding ISO_8859_1 = new CharacterEncoding() {
      public String getEncodingString() { return "ISO-8859-1"; }
      public String getIANAString() { return "ISO-8859-1"; }
    };

  public static final CharacterEncoding UTF_8 = new CharacterEncoding() {
      public String getEncodingString() { return "UTF-8"; }
      public String getIANAString() { return "UTF-8"; }
    };

  public static final CharacterEncoding ASCII = new CharacterEncoding() {
      public String getEncodingString() { return "ASCII"; }
      public String getIANAString() { return "US-ASCII"; }
    };

  public static final CharacterEncoding SHIFT_JIS = new CharacterEncoding() {
      public String getEncodingString() { return "SJIS"; }
      public String getIANAString() { return "Shift_JIS"; }
    };

  public static final CharacterEncoding EUC_JP = new CharacterEncoding() {
      public String getEncodingString() { return "EUC_JP"; }
      public String getIANAString() { return "EUC-JP"; }
    };

  public static final CharacterEncoding EUC_KR = new CharacterEncoding() {
      public String getEncodingString() { return "EUC_KR"; }
      public String getIANAString() { return "EUC-KR"; }
    };

  /**
   * This is a highly limited implementation at the moment, so 
   * don't expect too much from it.
   */
  public static CharacterEncoding getInstance(String enc)
    throws UnsupportedEncodingException
  {
    if (ENCODING_HASH.containsKey(enc.toUpperCase())) {
      return (CharacterEncoding) ENCODING_HASH.get(enc.toUpperCase());
    } else {
      throw new UnsupportedEncodingException(enc);
    }
  }

  public boolean equals(Object o) {
    return (o instanceof CharacterEncoding) &&
      getEncodingString().equals(((CharacterEncoding) o).getEncodingString());
  }

  /**************************************************************
   * 
   * Loads of constants
   *
   **************************************************************/

  private static final HashMap<String,CharacterEncoding> ENCODING_HASH = 
	  new HashMap<String,CharacterEncoding>();

  private static final String CANONICAL_ASCII_NAME = "ASCII";
  private static final String CANONICAL_ISO_8859_1_NAME = "ISO8859_1";
  private static final String CANONICAL_UTF_8_NAME = "UTF8";
  private static final String CANONICAL_SHIFT_JIS_NAME = "SJIS";
  private static final String CANONICAL_EUC_JP_NAME = "EUC_JP";
  private static final String CANONICAL_EUC_KR_NAME = "EUC_KR";
  
  private static final String[] ASCII_ALIASES = {CANONICAL_ASCII_NAME,
                                                 "US-ASCII",
                                                 "ISO646-US"};
  private static final String[] ISO_8859_1_ALIASES = 
  {CANONICAL_ISO_8859_1_NAME, "ISO-8859-1", "ISO-LATIN-1", "8859_1"};
  private static final String[] UTF_8_ALIASES = {CANONICAL_UTF_8_NAME,
                                                 "UTF-8"};
  private static final String[] SHIFT_JIS_ALIASES = {CANONICAL_SHIFT_JIS_NAME,
                                                     "SHIFTJIS",
                                                     "SHIFT-JIS",
                                                     "SHIFT_JIS"};
  private static final String[] EUC_JP_ALIASES = {CANONICAL_EUC_JP_NAME,
                                                  "EUC-JP"};
  private static final String[] EUC_KR_ALIASES = {CANONICAL_EUC_KR_NAME,
                                                  "EUC-KR"};
  
  static {
    int i = 0;
    for (i = 0 ; i < ASCII_ALIASES.length ; i++) {
      ENCODING_HASH.put(ASCII_ALIASES[i], ASCII);
    }
    for (i = 0 ; i < ISO_8859_1_ALIASES.length ; i++) {
      ENCODING_HASH.put(ISO_8859_1_ALIASES[i], ISO_8859_1);
    }
    for (i = 0 ; i < UTF_8_ALIASES.length ; i++) {
      ENCODING_HASH.put(UTF_8_ALIASES[i], UTF_8);
    }
    for (i = 0 ; i < SHIFT_JIS_ALIASES.length ; i++) {
      ENCODING_HASH.put(SHIFT_JIS_ALIASES[i], SHIFT_JIS);
    }
    for (i = 0 ; i < EUC_JP_ALIASES.length ; i++) {
      ENCODING_HASH.put(EUC_JP_ALIASES[i], EUC_JP);
    }
    for (i = 0 ; i < EUC_KR_ALIASES.length ; i++) {
      ENCODING_HASH.put(EUC_KR_ALIASES[i], EUC_KR);
    }
  }
}
