package com.androidadvance.zcryptowallet.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import com.androidadvance.zcryptowallet.BaseFragment;
import com.androidadvance.zcryptowallet.R;
import com.androidadvance.zcryptowallet.data.local.PreferencesHelper;
import com.androidadvance.zcryptowallet.data.remote.TheAPI;
import com.androidadvance.zcryptowallet.qrscanner.QRScannerActivity;
import com.androidadvance.zcryptowallet.utils.DUtils;
import com.androidadvance.zcryptowallet.utils.DialogFactory;
import com.androidadvance.zcryptowallet.utils.SecurityHolder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.socks.library.KLog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendFragment extends BaseFragment {
  private boolean isjustcreated;

  @BindView(R.id.send_editText_to) EditText send_editText_to;
  @BindView(R.id.send_linlayout_memo) LinearLayout send_linlayout_memo;
  @BindView(R.id.send_editText_memo) EditText send_editText_memo;
  @BindView(R.id.send_editText_amount) EditText send_editText_amount;
  @BindView(R.id.send_button_send) Button send_button_send;
  @BindView(R.id.send_imageButton_scanqr) ImageView send_imageButton_scanqr;
  @BindView(R.id.send_radioButtonPublic) RadioButton send_radioButtonPublic;
  @BindView(R.id.send_radioButtonPrivate) RadioButton send_radioButtonPrivate;
  @BindView(R.id.send_textView_amount) TextView send_textView_amount;
  @BindView(R.id.send_textView_zen) TextView send_textView_zen;

  public static double FEE = 0.0001;
  private ProgressDialog progressDialog;

  public SendFragment() {
  }

  public static SendFragment newInstance(boolean isjustcreated) {
    Bundle args = new Bundle();
    args.putBoolean("isjustcreated", isjustcreated);
    SendFragment fragment = new SendFragment();
    fragment.setArguments(args);
    fragment.setRetainInstance(true);
    return fragment;
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_send, container, false);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    isjustcreated = getArguments().getBoolean("isjustcreated", false);

    if (send_radioButtonPublic.isChecked()) {
      send_textView_amount.setText("Amount (current balance: " + String.valueOf(SecurityHolder.current_balance_public) + " ZEN)");
    } else {
      send_textView_amount.setText("Amount (current balance: " + String.valueOf(SecurityHolder.current_balance_private) + " ZEN)");
    }

    send_editText_to.setText("zc9CvpKMQ9P6ZFoc6UMWZ5AqLujmueTpWHzUjTmJUmMJsAT9ixEbrE4mMtn1cn463x3KpJRTMGuxorpz7ut1ppNyVAczw55");

    send_linlayout_memo.setVisibility(View.GONE);

    if (send_editText_to.getText().length() > 50) {
      send_linlayout_memo.setVisibility(View.VISIBLE);
    }
  }

  private void updateBalances() {

  }

  @Override public void onResume() {
    super.onResume();
    KLog.d("ON RESUME CALLED!");
    if (!SecurityHolder.lastScanAddress.isEmpty()) {
      send_editText_to.setText(SecurityHolder.lastScanAddress);
    }

    if (send_editText_to.getText().length() > 50) {
      send_linlayout_memo.setVisibility(View.VISIBLE);
    }
  }

  @OnClick(R.id.send_button_send) public void onClickSend() {
    double amount_to_send = 0;
    if (send_editText_amount.getText().toString().trim().length() > 0) {
      try {
        amount_to_send = Double.valueOf(send_editText_amount.getText().toString().trim());
      } catch (Exception ignored) {
      }
    }

    if (send_editText_to.getText().toString().length() < 30) {
      DialogFactory.warning_toast(getActivity(), "You need to enter the destination address.").show();
      return;
    }

    if (amount_to_send + FEE <= 0) {
      DialogFactory.warning_toast(getActivity(), "Please enter the amount you want to send").show();
      return;
    }

    if (send_radioButtonPublic.isChecked()) {
      //public
      if (SecurityHolder.current_balance_public < (amount_to_send + FEE)) {
        DialogFactory.warning_toast(getActivity(), "Seems you don't have enough ZEN for this transaction.").show();
        return;
      }
      sendTheMoney("public", send_editText_to.getText().toString().trim(), amount_to_send, FEE);
    } else {
      //private
      if (SecurityHolder.current_balance_private < (amount_to_send + FEE)) {
        DialogFactory.warning_toast(getActivity(), "Seems you don't have enough ZEN for this transaction.").show();
        return;
      }
      sendTheMoney("private", send_editText_to.getText().toString().trim(), amount_to_send, FEE);
    }
  }

  private void sendTheMoney(String typeAddress, String destinationAddress, double amount, double fee) {
    progressDialog = DialogFactory.createProgressDialog(getActivity(), "Sending...");
    progressDialog.show();

    PreferencesHelper preferencesHelper = new PreferencesHelper(getActivity());
    TheAPI theAPI = TheAPI.Factory.getIstance(getActivity());

    JsonObject sendJsonObject = new JsonObject();
    sendJsonObject.add("fromuserdevice", new JsonPrimitive(preferencesHelper.getDeviceID()));
    if (typeAddress.equals("public")) {
      sendJsonObject.add("fromaddress", new JsonPrimitive(SecurityHolder.getPublicAddress(getActivity())));
    } else {
      sendJsonObject.add("fromaddress", new JsonPrimitive(SecurityHolder.getPrivateAddress(getActivity())));
    }
    sendJsonObject.add("toaddress", new JsonPrimitive(destinationAddress));
    if (send_editText_to.getText().length() > 50) {
      if (send_editText_memo.getText().toString().trim().length() > 0) {
        sendJsonObject.add("memo", new JsonPrimitive(send_editText_memo.getText().toString().trim()));
      }
    }
    sendJsonObject.add("fee", new JsonPrimitive(fee));

    theAPI.sendMoney(sendJsonObject).enqueue(new Callback<JsonObject>() {
      @Override public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
        progressDialog.dismiss();
        JsonObject jsonObject = response.body();
        if (response.code() > 299) {
          DialogFactory.error_toast(getActivity(), "OPS! Something didn't work: " + jsonObject.toString()).show();
          return;
        }

        FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
        t.replace(android.R.id.content, AfterSendingFragment.newInstance(jsonObject.get("opid").getAsString()));
        t.addToBackStack("aftersending");
        t.commit();
      }

      @Override public void onFailure(Call<JsonObject> call, Throwable t) {
        progressDialog.dismiss();
        DialogFactory.error_toast(getActivity(), "OPS! Something didn't work: " + t.getLocalizedMessage()).show();
      }
    });
  }

  @OnClick(R.id.send_imageButton_scanqr) public void onClickScanQR() {
    Intent iScan = new Intent(getActivity(), QRScannerActivity.class);
    iScan.putExtra("type", "address");
    startActivity(iScan);
  }

  @OnCheckedChanged(R.id.send_radioButtonPublic) public void onCheckedChangedPublic() {
    if (send_radioButtonPublic.isChecked()) {
      send_textView_amount.setText("Amount (current balance: " + String.valueOf(SecurityHolder.current_balance_public) + " ZEN)");
    }
  }

  @OnCheckedChanged(R.id.send_radioButtonPrivate) public void onCheckedChangedPrivate() {
    if (send_radioButtonPrivate.isChecked()) {
      send_textView_amount.setText("Amount (current balance: " + String.valueOf(SecurityHolder.current_balance_private) + " ZEN)");
    }
  }
}