package com.androidadvance.zcryptowallet.activities;

import android.content.Intent;
import android.os.Build;
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
import com.androidadvance.zcryptowallet.utils.DialogFactory;
import com.androidadvance.zcryptowallet.utils.SecurityHolder;

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

    if(BuildConfig.DEBUG) {
      editText_pin1.setText("123123");
      btn_verify_pin.performClick();
    }

  }

  @OnClick(R.id.btn_verify_pin) public void onClickSaveVerify() {
    String pin1 = editText_pin1.getText().toString();

    //try to decrypt with the entered pin
    SecurityHolder.pin = pin1;
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

    startActivity(new Intent(mContext, MainActivity.class));
  }
}
