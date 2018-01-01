package com.androidadvance.zcryptowallet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import com.androidadvance.zcryptowallet.BaseActivity;
import com.androidadvance.zcryptowallet.R;
import com.androidadvance.zcryptowallet.data.local.PreferencesHelper;
import com.socks.library.KLog;

public class SplashActivity extends BaseActivity {

  private ImageView imageView_logo;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_splash);

    imageView_logo = findViewById(R.id.imageView_logo);

    Animation fadeIn = new AlphaAnimation(0, 1);
    fadeIn.setInterpolator(new DecelerateInterpolator());
    fadeIn.setDuration(300);

    Animation fadeOut = new AlphaAnimation(1, 0);
    fadeOut.setInterpolator(new AccelerateInterpolator());
    fadeOut.setDuration(300);

    fadeIn.setAnimationListener(new Animation.AnimationListener() {
      @Override public void onAnimationStart(Animation animation) {

      }

      @Override public void onAnimationEnd(Animation animation) {
        imageView_logo.setVisibility(View.VISIBLE);
        new CountDownTimer(600, 600) {

          @Override public void onTick(long l) {

          }

          @Override public void onFinish() {
            imageView_logo.startAnimation(fadeOut);
          }
        }.start();
      }

      @Override public void onAnimationRepeat(Animation animation) {

      }
    });

    fadeOut.setAnimationListener(new Animation.AnimationListener() {
      @Override public void onAnimationStart(Animation animation) {

      }

      @Override public void onAnimationEnd(Animation animation) {
        imageView_logo.setVisibility(View.INVISIBLE);
        check_wallet_present();
      }

      @Override public void onAnimationRepeat(Animation animation) {

      }
    });

    new CountDownTimer(200, 200) {
      @Override public void onTick(long l) {
      }

      @Override public void onFinish() {
        imageView_logo.startAnimation(fadeIn);
      }
    }.start();
  }

  private void check_wallet_present() {

    PreferencesHelper preferencesHelper = new PreferencesHelper(SplashActivity.this);

    if (preferencesHelper.getPIN() == null) {
      KLog.d("we didn't detect any pin present");
      startActivity(new Intent(this, NewPinActivity.class));
    } else {
      KLog.d("a pin is already present. show enter pin activity");
      startActivity(new Intent(this, EnterPinActivity.class));
    }
  }
}
