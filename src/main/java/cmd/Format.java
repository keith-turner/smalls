package cmd;

import com.google.common.base.Strings;

public class Format {
  public static String toHex(long l) {
    String s = Long.toHexString(l);
    return Strings.padStart(s, 16, '0');
  }
  
  public static String toHex(int i) {
    String s = Integer.toHexString(i);
    return Strings.padStart(s, 8, '0');
  }
  
  public static String toHex(int i, int len) {
    String s = Integer.toHexString(i);
    return Strings.padStart(s, len, '0');
  }
}
