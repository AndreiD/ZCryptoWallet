package com.androidadvance.zcryptowallet.utils;

import android.content.Context;

import com.androidadvance.zcryptowallet.data.local.PreferencesHelper;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

public class SecurityHolder {
  public static String pin = "";
  public static String public_address = "";
  public static String private_address = "";
  public static double current_balance_public = 0;
  public static double current_balance_private = 0;
  public static String lastScanAddress = "";

  //Using a rooted phone is not recommended :)
  public static void storePIN(Context ctx, String pin) {
    try {
      PreferencesHelper preferencesHelper = new PreferencesHelper(ctx);
      AesCbcWithIntegrity.SecretKeys key = AesCbcWithIntegrity.generateKeyFromPassword(SecurityHolder.pin, preferencesHelper.getDeviceID());
      AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = AesCbcWithIntegrity.encrypt(pin, key, "UTF-8");
      preferencesHelper.setPIN(cipherTextIvMac.toString());
    } catch (GeneralSecurityException | UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  public static String getPIN(Context ctx) {
    try {
      PreferencesHelper preferencesHelper = new PreferencesHelper(ctx);
      AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = new AesCbcWithIntegrity.CipherTextIvMac(preferencesHelper.getPIN());
      AesCbcWithIntegrity.SecretKeys key = AesCbcWithIntegrity.generateKeyFromPassword(SecurityHolder.pin, preferencesHelper.getDeviceID());
      String plainText = AesCbcWithIntegrity.decryptString(cipherTextIvMac, key);
      return plainText;
    } catch (GeneralSecurityException | UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void storePublicAddress(Context ctx, String publicAddress) {
    try {
      PreferencesHelper preferencesHelper = new PreferencesHelper(ctx);
      AesCbcWithIntegrity.SecretKeys key =
          AesCbcWithIntegrity.generateKeyFromPassword(SecurityHolder.pin, "x3c" + preferencesHelper.getDeviceID());
      AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = AesCbcWithIntegrity.encrypt(publicAddress, key, "UTF-8");
      preferencesHelper.setPublicAddress(cipherTextIvMac.toString());
    } catch (GeneralSecurityException | UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  public static String getPublicAddress(Context ctx) {
    try {
      PreferencesHelper preferencesHelper = new PreferencesHelper(ctx);
      AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = new AesCbcWithIntegrity.CipherTextIvMac(preferencesHelper.getPublicAddress());
      AesCbcWithIntegrity.SecretKeys key =
          AesCbcWithIntegrity.generateKeyFromPassword(SecurityHolder.pin, "x3c" + preferencesHelper.getDeviceID());
      String plainText = AesCbcWithIntegrity.decryptString(cipherTextIvMac, key);
      return plainText;
    } catch (GeneralSecurityException | UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void storePrivateAddress(Context ctx, String privateAddress) {
    try {
      PreferencesHelper preferencesHelper = new PreferencesHelper(ctx);
      AesCbcWithIntegrity.SecretKeys key =
          AesCbcWithIntegrity.generateKeyFromPassword(SecurityHolder.pin, "3x3" + preferencesHelper.getDeviceID());
      AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = AesCbcWithIntegrity.encrypt(privateAddress, key, "UTF-8");
      preferencesHelper.setPrivateAddress(cipherTextIvMac.toString());
    } catch (GeneralSecurityException | UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  public static String getPrivateAddress(Context ctx) {
    try {
      PreferencesHelper preferencesHelper = new PreferencesHelper(ctx);
      AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = new AesCbcWithIntegrity.CipherTextIvMac(preferencesHelper.getPrivateAddress());
      AesCbcWithIntegrity.SecretKeys key =
          AesCbcWithIntegrity.generateKeyFromPassword(SecurityHolder.pin, "3x3" + preferencesHelper.getDeviceID());
      String plainText = AesCbcWithIntegrity.decryptString(cipherTextIvMac, key);
      return plainText;
    } catch (GeneralSecurityException | UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }
}
