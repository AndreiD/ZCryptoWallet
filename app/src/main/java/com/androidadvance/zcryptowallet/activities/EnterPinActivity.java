package com.androidadvance.zcryptowallet.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnterPinActivity extends BaseActivity {

  @BindView(R.id.editText_pin1) EditText editText_pin1;

  @BindView(R.id.btn_verify_pin) Button btn_verify_pin;

  @BindView(R.id.textView_forgot_pin) TextView textView_forgot_pin;



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

    if(editText_pin1.getText().toString().length() < 3){
      DialogFactory.error_toast(EnterPinActivity.this, "Pin should be at least 4 numbers").show();
      return;
    }

    //Check the pin
    SecurityHolder.pin = editText_pin1.getText().toString();

    TheAPI theAPI = TheAPI.Factory.getIstance(mContext);
    theAPI.getWalletInfo(DUtils.getUniqueID(), SecurityHolder.pin).enqueue(new Callback<JsonObject>() {
      @Override public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

        if (response.code() > 299) {
          DialogFactory.error_toast(mContext, "Invalid PIN").show();
          incorrectCounter = incorrectCounter + 1;
          if(incorrectCounter > 3){
            incorrectCounter = 0;
            finish();
          }
          return;
        }

        DUtils.hideKeyboard(EnterPinActivity.this);

        JsonObject jsonObject = response.body();
        if (!jsonObject.has("privateaddresskey")) {
          DialogFactory.error_toast(mContext, "Failed to get balances for your account. Device ID was not found: "  + DUtils.getShortID()).show();
          return;
        }

        SecurityHolder.publicAddress = jsonObject.get("publicaddress").getAsString();
        SecurityHolder.privateAddress = jsonObject.get("privateaddress").getAsString();
        SecurityHolder.publicAddressKey = jsonObject.get("publicaddresskey").getAsString();
        SecurityHolder.privateAddressKey = jsonObject.get("privateaddresskey").getAsString();

        Crashlytics.setString("publicaddress", SecurityHolder.publicAddress);

        startActivity(new Intent(mContext, MainActivity.class));
      }

      @Override public void onFailure(Call<JsonObject> call, Throwable t) {
        Crashlytics.logException(t);
        DialogFactory.error_toast(mContext, "Failed to get balances for your account.").show();
        finish();
      }
    });
  }


  @OnClick(R.id.textView_forgot_pin) public void onClickForgotPin(){

    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
        mContext);
    LayoutInflater inflater = (LayoutInflater) mContext
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.dialog_change_pin, null);
    alertDialogBuilder.setView(view);
    alertDialogBuilder.setCancelable(false);
    final AlertDialog dialog = alertDialogBuilder.create();
    dialog.show();
    Button btn_update_pin_change = view.findViewById(R.id.btn_update_pin_change);
    Button btn_update_pin_close = view.findViewById(R.id.btn_update_pin_close);
    EditText editText_update_pin_public_address = view.findViewById(R.id.editText_update_pin_public_address);
    EditText editText_update_pin_public_address_key = view.findViewById(R.id.editText_update_pin_public_address_key);
    EditText editText_update_pin_pin = view.findViewById(R.id.editText_update_pin_pin);

    btn_update_pin_close.setOnClickListener(v -> dialog.dismiss());

    btn_update_pin_change.setOnClickListener(view1 -> {
      String pin = editText_update_pin_pin.getText().toString().trim();
      if(pin.length() < 4){
        DialogFactory.error_toast(mContext,"Pin should be at least 4 characters").show();
        dialog.dismiss();
        return;
      }

      JsonObject updatePinJson = new JsonObject();
      updatePinJson.add("publicaddress", new JsonPrimitive(editText_update_pin_public_address.getText().toString().trim()));
      updatePinJson.add("publickey", new JsonPrimitive(editText_update_pin_public_address_key.getText().toString().trim()));
      updatePinJson.add("new_pin", new JsonPrimitive(pin));

     TheAPI theAPI = TheAPI.Factory.getIstance(mContext);
      theAPI.updateUserPin(updatePinJson).enqueue(new Callback<JsonObject>() {
        @Override public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
          if(response.code() > 299){
            DialogFactory.error_toast(mContext,"Your public address and key were incorrect. Please double-check.").show();
            return;
          }
          DialogFactory.success_toast(mContext,"PIN Successfully Updated.").show();
          dialog.dismiss();

        }

        @Override public void onFailure(Call<JsonObject> call, Throwable t) {
          DialogFactory.error_toast(mContext,"Your public address and key were incorrect. Please double-check.").show();
        }
      });


    });

  }
}

