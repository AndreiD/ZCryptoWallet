package com.androidadvance.zcryptowallet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.androidadvance.zcryptowallet.BaseActivity;
import com.androidadvance.zcryptowallet.BuildConfig;
import com.androidadvance.zcryptowallet.R;
import com.androidadvance.zcryptowallet.data.remote.TheAPI;
import com.androidadvance.zcryptowallet.utils.DUtils;
import com.androidadvance.zcryptowallet.utils.DialogFactory;
import com.androidadvance.zcryptowallet.utils.SecurityHolder;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnterPinActivity extends BaseActivity {

  @BindView(R.id.editText_pin1) EditText editText_pin1;

  @BindView(R.id.btn_verify_pin) Button btn_verify_pin;

  private EnterPinActivity mContext;
  public static int incorrectCounter = 0;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_enter_pin);
    ButterKnife.bind(this);
    mContext = EnterPinActivity.this;

    if (BuildConfig.DEBUG) { //TODO: move it to local.properties
      editText_pin1.setText("123123");
      btn_verify_pin.performClick();
    }
  }

  @OnClick(R.id.btn_verify_pin) public void onClickSaveVerify() {

    //try to decrypt with the entered pin
    SecurityHolder.pin = editText_pin1.getText().toString();
    if (SecurityHolder.getPIN(EnterPinActivity.this) == null) {
      incorrectCounter = incorrectCounter + 1;
      editText_pin1.setText("");
      if (incorrectCounter > 3) {
        finish();
      }
      new CountDownTimer(1000, 1000) {
        @Override public void onTick(long l) {
        }

        @Override public void onFinish() {
          DialogFactory.error_toast(EnterPinActivity.this, "Incorrect PIN. Please try again").show();
        }
      }.start();
      return;
    }

    TheAPI theAPI = TheAPI.Factory.getIstance(mContext);
    theAPI.getWalletInfo(DUtils.getUniqueID()).enqueue(new Callback<JsonObject>() {
      @Override public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
        if (response.code() > 299) {
          DialogFactory.error_toast(mContext, "Failed to get balances for your account.").show();
          finish();
        }

        JsonObject jsonObject = response.body();
        if (!jsonObject.has("privateaddresskey")) {
          DialogFactory.error_toast(mContext, "Failed to get balances for your account. Device ID was not found").show();
          finish();
        }

        SecurityHolder.publicAddress = jsonObject.get("publicaddress").getAsString();
        SecurityHolder.privateAddress = jsonObject.get("privateaddress").getAsString();
        SecurityHolder.publicAddressKey = jsonObject.get("publicaddresskey").getAsString();
        SecurityHolder.privateAddressKey = jsonObject.get("privateaddresskey").getAsString();

        startActivity(new Intent(mContext, MainActivity.class));
      }

      @Override public void onFailure(Call<JsonObject> call, Throwable t) {
        DialogFactory.error_toast(mContext, "Failed to get balances for your account.").show();
        finish();
      }
    });
  }
}
