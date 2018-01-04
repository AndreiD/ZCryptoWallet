package com.androidadvance.zcryptowallet.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import com.androidadvance.zcryptowallet.BaseActivity;
import com.androidadvance.zcryptowallet.R;
import com.androidadvance.zcryptowallet.data.local.PreferencesHelper;
import com.androidadvance.zcryptowallet.data.remote.TheAPI;
import com.androidadvance.zcryptowallet.utils.DialogFactory;
import com.google.gson.JsonObject;
import com.socks.library.KLog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        checkNews();
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

  private void checkNews() {

    //TODO: add should update related to the app version number...

    TheAPI theAPI = TheAPI.Factory.getIstance(SplashActivity.this);
    theAPI.getNews().enqueue(new Callback<JsonObject>() {
      @Override public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
        if (response.code() > 299) {
          KLog.e("an error occurred while trying to get the news");
          check_wallet_present();
          return;
        }
        JsonObject jsonObject = response.body();
        String news = jsonObject.get("news").getAsString();

        if ((news != null) && (!news.isEmpty())) {

          AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
          builder.setTitle("News");
          builder.setCancelable(false);
          builder.setMessage(news).setCancelable(false).setPositiveButton("OK", (dialog, id) -> check_wallet_present());
          AlertDialog alert = builder.create();
          alert.show();
        }
      }

      @Override public void onFailure(Call<JsonObject> call, Throwable t) {
        check_wallet_present();
      }
    });
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
