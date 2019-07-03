package com.jzsk.seriallib.util;

/**
 * Utility class for Android LogCat.
 *
 */
public class LogUtils {

  @SuppressWarnings("unchecked")
  public static String makeTag(Class cls) {
    return "SerialClient_" + cls.getSimpleName();
  }

}
