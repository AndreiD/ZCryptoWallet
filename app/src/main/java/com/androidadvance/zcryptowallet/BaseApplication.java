package com.androidadvance.zcryptowallet;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.androidadvance.zcryptowallet.data.local.PreferencesHelper;
import com.socks.library.KLog;

import java.util.UUID;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        boolean isDebuggable = (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));

        if (isDebuggable) {
            KLog.init(true);
        } else {
            KLog.init(false);
        }

        //generate Device ID if missing
        PreferencesHelper preferencesHelper = new PreferencesHelper(this);
        if (preferencesHelper.getDeviceID() == null) {
            String uniqueID = UUID.randomUUID().toString();
            preferencesHelper.setDeviceID(uniqueID);
        }

        //fonts init
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/AdelleSansLight.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

    }


    public static BaseApplication get(Context context) {
        return (BaseApplication) context.getApplicationContext();
    }
}
