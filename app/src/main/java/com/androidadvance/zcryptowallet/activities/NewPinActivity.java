package com.androidadvance.zcryptowallet.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.Button;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.androidadvance.zcryptowallet.BaseActivity;
import com.androidadvance.zcryptowallet.R;
import com.androidadvance.zcryptowallet.fragments.NewAccountFragment;
import com.androidadvance.zcryptowallet.utils.DialogFactory;
import com.androidadvance.zcryptowallet.utils.SecurityHolder;

public class NewPinActivity extends BaseActivity {

  @BindView(R.id.editText_pin1) EditText editText_pin1;

  @BindView(R.id.editText_pin2) EditText editText_pin2;

  @BindView(R.id.btn_save_new_pin) Button btn_save_new_pin;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_create_pin);
    ButterKnife.bind(this);

  }

  @OnClick(R.id.btn_save_new_pin) public void onClickSaveNewPin() {
    String pin1 = editText_pin1.getText().toString();
    String pin2 = editText_pin2.getText().toString();
    if (!pin1.equals(pin2)) {
      DialogFactory.error_toast(NewPinActivity.this, "Pin 1 is not equal to pin 2.").show();
      return;
    }
    if (pin1.length() < 3) {
      DialogFactory.error_toast(NewPinActivity.this, "Pin is too short. Type at least 4 numbers please").show();
      return;
    }

    SecurityHolder.pin = pin1;

    SecurityHolder.storePIN(this, pin1);

    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager.beginTransaction().replace(android.R.id.content, NewAccountFragment.newInstance()).addToBackStack(null).commit();
  }
}
