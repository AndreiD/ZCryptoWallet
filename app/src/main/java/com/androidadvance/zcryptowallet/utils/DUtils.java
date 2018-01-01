package com.androidadvance.zcryptowallet.utils;

import android.os.Build;
import com.socks.library.KLog;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class DUtils {

  public static String getDeviceInfo() {
    String manufacturer = Build.MANUFACTURER;

    String model =
        Build.MODEL + " " + android.os.Build.BRAND + " (" + android.os.Build.VERSION.RELEASE + ")" + " API-" + android.os.Build.VERSION.SDK_INT;

    if (model.startsWith(manufacturer)) {
      return capitalize(model);
    } else {
      return capitalize(manufacturer) + " " + model;
    }
  }

  private static String capitalize(String s) {
    if (s == null || s.length() == 0) {
      return s;
    } else {
      StringBuilder phrase = new StringBuilder();
      boolean next = true;
      for (char c : s.toCharArray()) {
        if (next && Character.isLetter(c) || Character.isWhitespace(c)) next = Character.isWhitespace(c = Character.toUpperCase(c));
        phrase.append(c);
      }
      return phrase.toString();
    }
  }

  public final static boolean isValidEmail(CharSequence target) {
    if (target == null) {
      return false;
    } else {
      return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
  }

  public static String getHashForPassword(String email, String raw_password) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      String text = email + raw_password + "XYZ!159753987123654789";
      md.update(text.getBytes(Charset.forName("UTF-8")));
      return getHex(md.digest()).toLowerCase();
    } catch (Exception ex) {
      KLog.e(ex);
    }
    return null;
  }

  static final String HEXES = "0123456789ABCDEF";

  private static String getHex(byte[] raw) {
    if (raw == null) {
      return null;
    }
    final StringBuilder hex = new StringBuilder(2 * raw.length);
    for (final byte b : raw) {
      hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
    }
    return hex.toString();
  }

  public static String randomString(int len) {
    final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    SecureRandom rnd = new SecureRandom();
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++)
      sb.append(AB.charAt(rnd.nextInt(AB.length())));
    return sb.toString();
  }

}
