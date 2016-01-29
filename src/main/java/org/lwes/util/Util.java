package org.lwes.util;

public class Util {

  /**
   * compares two arrays of bytes
   *
   * @param b1 - first array
   * @param len1 - length of the first array
   * @param b2 - second array
   * @param len2 - length of the second array
   * @return true if two arrays have identical contents, false otherwise.
   */
  public static boolean compareByteArrays(byte[] b1, int len1, byte[] b2, int len2) {
    if (len1 != len2) {
      return false;
    }
    if (b1 == b2) {
      return true;
    }
    if (b1==null || b2==null) {
      return false;
    }

    for (int i=0; i<len1; i++) {
      if (b1[i] != b2[i]) {
        return false;
      }
    }

    return true;
  }

}
