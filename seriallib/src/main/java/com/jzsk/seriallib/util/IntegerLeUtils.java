package com.jzsk.seriallib.util;

/**
 * Operations on primitive integers (like {@code int}).
 * <p>
 * This class tries to handle {@code null} input gracefully. An exception will not be thrown for a
 * {@code null} array input. Each method documents its behavior.
 *
 */
public class IntegerLeUtils {

  /**
   * Converts a primitive byte to an array of bytes
   *
   * @param num a byte
   * @return a byte array
   */
  public static byte[] asBytes(byte num) {
    byte[] result = new byte[1];

    result[0] = num;

    return result;
  }

  /**
   * Converts a primitive short to an array of bytes
   *
   * @param num a short
   * @return a byte array
   */
  public static byte[] asBytes(short num) {
    byte[] result = new byte[2];

    result[1] = (byte) ((num >> 8) & 0xff);
    result[0] = (byte) (num & 0xff);

    return result;
  }

  /**
   * Converts a primitive int to an array of bytes
   *
   * @param num an int
   * @return a byte array
   */
  public static byte[] asBytes(int num) {
    byte[] result = new byte[4];

    result[3] = (byte) ((num >> 24) & 0xff);
    result[2] = (byte) ((num >> 16) & 0xff);
    result[1] = (byte) ((num >> 8) & 0xff);
    result[0] = (byte) (num & 0xff);

    return result;
  }

  /**
   * Converts a primitive long to an array of bytes
   *
   * @param num a long
   * @return a byte array
   */
  public static byte[] asBytes(long num) {
    byte[] result = new byte[8];

    result[7] = (byte) ((num >> 56) & 0xff);
    result[6] = (byte) ((num >> 48) & 0xff);
    result[5] = (byte) ((num >> 40) & 0xff);
    result[4] = (byte) ((num >> 32) & 0xff);
    result[3] = (byte) ((num >> 24) & 0xff);
    result[2] = (byte) ((num >> 16) & 0xff);
    result[1] = (byte) ((num >> 8) & 0xff);
    result[0] = (byte) (num & 0xff);

    return result;
  }

  /**
   * Converts an array of primitive bytes to a primitive byte.
   *
   * @param bytes a byte array, may be {@code null}
   * @return a byte, {@code 0} if null or empty array input
   */
  public static byte parseByte(byte[] bytes) {
    if (ArrayUtils.isEmpty(bytes)) {
      return 0;
    }

    return bytes[bytes.length - 1];
  }

  /**
   * Converts an array of primitive bytes to a primitive short.
   *
   * @param bytes a byte array, may be {@code null}
   * @return a short, {@code 0} if null or empty array input
   */
  public static short parseShort(byte[] bytes) {
    if (ArrayUtils.isEmpty(bytes)) {
      return 0;
    }

    bytes = ArrayUtils.ensureLength(bytes, 2);

    return (short) ((bytes[1] & 0xff) << 8 | (bytes[0] & 0xff));
  }

  /**
   * Converts an array of primitive bytes to a primitive int.
   *
   * @param bytes a byte array, may be {@code null}
   * @return an int, {@code 0} if null or empty array input
   */
  public static int parseInt(byte[] bytes) {
    if (ArrayUtils.isEmpty(bytes)) {
      return 0;
    }

    bytes = ArrayUtils.ensureLength(bytes, 4);

    return (bytes[3] & 0xff) << 24
           | (bytes[2] & 0xff) << 16
           | (bytes[1] & 0xff) << 8
           | (bytes[0] & 0xff);
  }

  /**
   * Converts an array of primitive bytes to a primitive long.
   *
   * @param bytes a byte array, may be {@code null}
   * @return a long, {@code 0} if null or empty array input
   */
  public static long parseLong(byte[] bytes) {
    if (ArrayUtils.isEmpty(bytes)) {
      return 0;
    }

    bytes = ArrayUtils.ensureLength(bytes, 8);

    return (bytes[7] & 0xffL) << 56
           | (bytes[6] & 0xffL) << 48
           | (bytes[5] & 0xffL) << 40
           | (bytes[4] & 0xffL) << 32
           | (bytes[3] & 0xffL) << 24
           | (bytes[2] & 0xffL) << 16
           | (bytes[1] & 0xffL) << 8
           | (bytes[0] & 0xffL);
  }

  @SuppressWarnings("unchecked")
  public static byte[] toBcd(long num) {
    int digits = 0;

    long quot = num;
    while (quot != 0) {
      quot /= 10;
      digits++;
    }

    int len = digits % 2 == 0 ? digits / 2 : digits / 2 + 1;

    byte[] bcd = new byte[len];

    byte digit;
    for (int i = digits; i > 0; i--) {
      digit = (byte) (num % 10);
      if (i % 2 == 0) {
        bcd[i / 2 - 1] = digit;
      } else {
        bcd[i / 2] |= digit << 4;
      }
      num /= 10;
    }

    return bcd;
  }
  
  
  /** 
   * @����: BCD��תΪ10���ƴ�(����������) 
   * @����: BCD�� 
   * @���: 10���ƴ� 
   */  
  public static String bcd2Str(byte[] bytes) {
      StringBuffer temp = new StringBuffer(bytes.length * 2);
      for (int i = 0; i < bytes.length; i++) {  
          temp.append((byte) ((bytes[i] & 0xf0) >>> 4));  
          temp.append((byte) (bytes[i] & 0x0f));  
      }  
      return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp  
              .toString().substring(1) : temp.toString();  
  }  

  /** 
   * @����: 10���ƴ�תΪBCD�� 
   * @����: 10���ƴ� 
   * @���: BCD�� 
   */  
  public static byte[] str2Bcd(String asc) {
      int len = asc.length();  
      int mod = len % 2;  
      if (mod != 0) {  
          asc = "0" + asc;  
          len = asc.length();  
      }  
      byte abt[] = new byte[len];  
      if (len >= 2) {  
          len = len / 2;  
      }  
      byte bbt[] = new byte[len];  
      abt = asc.getBytes();  
      int j, k;  
      for (int p = 0; p < asc.length() / 2; p++) {  
          if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {  
              j = abt[2 * p] - '0';  
          } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {  
              j = abt[2 * p] - 'a' + 0x0a;  
          } else {  
              j = abt[2 * p] - 'A' + 0x0a;  
          }  
          if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {  
              k = abt[2 * p + 1] - '0';  
          } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {  
              k = abt[2 * p + 1] - 'a' + 0x0a;  
          } else {  
              k = abt[2 * p + 1] - 'A' + 0x0a;  
          }  
          int a = (j << 4) + k;  
          byte b = (byte) a;  
          bbt[p] = b;  
      }  
      return bbt;  
  }  
  
  public static String printHexString(byte[] b)
  {
 
	  StringBuffer buffer=new StringBuffer();
      for (int i = 0; i < b.length; i++)
      {
          String hex = Integer.toHexString(b[i] & 0xFF);
          if (hex.length() == 1)
          {
              hex = '0' + hex;
          }
          buffer.append(hex.toUpperCase() + " ");
      }
     return buffer.toString();
  }
  

//  private static byte BCD_MASK = 0b00001111;
//
//  public static long parseLongFromBcd(byte[] bcd) {
//    long value = 0;
//    int digits = bcd.length;
//    for (int i = 0; i< digits; i++){
//
//      value += (bcd[i] & BCD_MASK)*1E(2*i-1));
//    }
//
//  }

}
