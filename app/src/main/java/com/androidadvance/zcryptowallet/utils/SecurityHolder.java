package com.androidadvance.zcryptowallet.utils;

import android.content.Context;

import com.androidadvance.zcryptowallet.data.local.PreferencesHelper;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

public class SecurityHolder {
  public static String pin = "";
  public static String publicAddress = "";
  public static String privateAddress = "";
  public static String publicAddressKey = "";
  public static String privateAddressKey = "";
  public static double current_balance_public = 0;
  public static double current_balance_private = 0;
  public static String lastScanAddress = "";

  //Using a rooted phone is not recommended :)
  public static void storePIN(Context ctx, String pin) {
    if(pin.isEmpty()){
      return;
    }
    try {
      PreferencesHelper preferencesHelper = new PreferencesHelper(ctx);
      AesCbcWithIntegrity.SecretKeys key = AesCbcWithIntegrity.generateKeyFromPassword(pin, DUtils.getUniqueID());
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
      AesCbcWithIntegrity.SecretKeys key = AesCbcWithIntegrity.generateKeyFromPassword(SecurityHolder.pin, DUtils.getUniqueID());
      String plainText = AesCbcWithIntegrity.decryptString(cipherTextIvMac, key);
      return plainText;
    } catch (GeneralSecurityException | UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }


  public static void storeContacts(Context ctx, String contacts) {
    if(contacts.isEmpty()){
      return;
    }
    try {
      PreferencesHelper preferencesHelper = new PreferencesHelper(ctx);
      AesCbcWithIntegrity.SecretKeys key = AesCbcWithIntegrity.generateKeyFromPassword(SecurityHolder.pin, DUtils.getUniqueID());
      AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = AesCbcWithIntegrity.encrypt(contacts, key, "UTF-8");
      preferencesHelper.setContacts(cipherTextIvMac.toString());
    } catch (GeneralSecurityException | UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  public static String getContacts(Context ctx) {
    try {
      PreferencesHelper preferencesHelper = new PreferencesHelper(ctx);
      AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = new AesCbcWithIntegrity.CipherTextIvMac(preferencesHelper.getContacts());
      AesCbcWithIntegrity.SecretKeys key = AesCbcWithIntegrity.generateKeyFromPassword(SecurityHolder.pin, DUtils.getUniqueID());
      return AesCbcWithIntegrity.decryptString(cipherTextIvMac, key);
    } catch (GeneralSecurityException | UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }
}
