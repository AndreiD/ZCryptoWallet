package com.androidadvance.zcryptowallet;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import com.androidadvance.zcryptowallet.data.local.MyObjectBox;
import com.androidadvance.zcryptowallet.utils.DUtils;
import com.crashlytics.android.Crashlytics;
import com.socks.library.KLog;
import io.objectbox.BoxStore;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class BaseApplication extends Application {
  private BoxStore boxStore;

  @Override public void onCreate() {
    super.onCreate();

    boolean isDebuggable = (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));

    if (isDebuggable) {
      KLog.init(true);
    } else {
      KLog.init(false);
    }

    //fonts init
    CalligraphyConfig.initDefault(
        new CalligraphyConfig.Builder().setDefaultFontPath("fonts/AdelleSansLight.ttf").setFontAttrId(R.attr.fontPath).build());

    Crashlytics.setUserIdentifier(DUtils.getShortID());

    boxStore = MyObjectBox.builder().androidContext(BaseApplication.this).build();
  }

  public BoxStore getBoxStore() {
    return boxStore;
  }

  public static BaseApplication get(Context context) {
    return (BaseApplication) context.getApplicationContext();
  }
}
