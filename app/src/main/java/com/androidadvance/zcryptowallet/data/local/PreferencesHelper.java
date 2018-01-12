package com.androidadvance.zcryptowallet.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {

  private static SharedPreferences mPref;

  public static final String PREF_FILE_NAME = "myapp_shared_prefs";
  private static final String KEY_NEW_ACCOUNT = "NEW_ACCOUNT";
  private static final String KEY_CONTACTS = "KEY_CONTACTS";

  public PreferencesHelper(Context context) {
    mPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
  }

  public void clear() {
    mPref.edit().clear().apply();
  }


  public boolean getIsNewAccount() {
    return mPref.getBoolean(KEY_NEW_ACCOUNT, true);
  }

  public void setIsNewAccount(boolean isNewAccount) {
    mPref.edit().putBoolean(KEY_NEW_ACCOUNT, isNewAccount).commit();
  }


  public String getContacts() {
    return mPref.getString(KEY_CONTACTS, null);
  }

  public void setContacts(String contacts) {
    mPref.edit().putString(KEY_CONTACTS, contacts).commit();
  }

}

