package com.androidadvance.zcryptowallet.utils;

import android.content.Context;

import com.androidadvance.zcryptowallet.data.local.PreferencesHelper;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

public class SecurityHolder {
  public static String pin = "";

  //So it uses the random generated deviceID to encrypt the pin.
  //Using a rooted phone is not recommended :)
  public static void storePIN(Context contxt, String pin) {
    try {
      PreferencesHelper preferencesHelper = new PreferencesHelper(contxt);
      AesCbcWithIntegrity.SecretKeys key = AesCbcWithIntegrity.generateKeyFromPassword(SecurityHolder.pin, preferencesHelper.getDeviceID());
      AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = AesCbcWithIntegrity.encrypt(pin, key, "UTF-8");
      preferencesHelper.setPIN(cipherTextIvMac.toString());
    } catch (GeneralSecurityException | UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  public static String getPIN(Context contxt) {
    try {
      PreferencesHelper preferencesHelper = new PreferencesHelper(contxt);
      AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = new AesCbcWithIntegrity.CipherTextIvMac(preferencesHelper.getPIN());
      AesCbcWithIntegrity.SecretKeys key = AesCbcWithIntegrity.generateKeyFromPassword(SecurityHolder.pin, preferencesHelper.getDeviceID());
      String plainText = AesCbcWithIntegrity.decryptString(cipherTextIvMac, key);
      return plainText;
    } catch (GeneralSecurityException | UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }




}
