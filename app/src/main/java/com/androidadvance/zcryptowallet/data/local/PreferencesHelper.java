package com.androidadvance.zcryptowallet.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {

  private static SharedPreferences mPref;

  public static final String PREF_FILE_NAME = "myapp_shared_prefs";
  private static final String KEY_PIN = "KEY_PIN";
  private static final String KEY_PUBLIC_ADDRESS = "KEY_PUBLIC_ADDRESS";
  private static final String KEY_PRIVATE_ADDRESS = "KEY_PRIVATE_ADDRESS";

  public PreferencesHelper(Context context) {
    mPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
  }

  public void clear() {
    mPref.edit().clear().apply();
  }

  //encrypted
  public String getPIN() {
    return mPref.getString(KEY_PIN, null);
  }

  public void setPIN(String pin) {
    mPref.edit().putString(KEY_PIN, pin).apply();
  }

}

