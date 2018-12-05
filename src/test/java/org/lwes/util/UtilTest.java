package org.lwes.util;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

public class UtilTest {

  @Test
  public void testCompareByteArrays() {
    // null arrays
    assertTrue( Util.compareByteArrays(null,  0,  null,  0));

    // passing different array lengths
    assertFalse( Util.compareByteArrays(null,  2,  null,  1));

    // one array not null
    assertFalse( Util.compareByteArrays(null,  0,  new byte[1],  0));
    assertFalse( Util.compareByteArrays(new byte[1],  0,  null,  0));

    // similar arrays
    byte[] b1 = new byte[5];
    byte[] b2 = new byte[5];
    for(int i=0; i<b1.length; ++i) {
      byte b = (byte)(new Random()).nextInt(128);
      b1[i] = b;
      b2[i] = b;
    }
    assertTrue( Util.compareByteArrays(b1,  b1.length,  b2, b2.length));

    // different arrays, same size
    b1[b1.length - 1] = (byte)1;
    b2[b2.length - 1] = (byte)0;
    assertFalse( Util.compareByteArrays(b1,  b1.length,  b2, b2.length));

    // identical arrays
    b2 = b1;
    assertTrue( Util.compareByteArrays(b1,  0,  b2,  0));
  }


}
