package com.androidadvance.zcryptowallet.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.androidadvance.zcryptowallet.BaseFragment;
import com.androidadvance.zcryptowallet.R;
import com.androidadvance.zcryptowallet.data.local.PreferencesHelper;
import com.androidadvance.zcryptowallet.data.remote.TheAPI;
import com.androidadvance.zcryptowallet.qrscanner.QRScannerActivity;
import com.androidadvance.zcryptowallet.utils.SecurityHolder;
import com.socks.library.KLog;

public class SendFragment extends BaseFragment {
  private boolean isjustcreated;

  @BindView(R.id.send_editText_to) EditText send_editText_to;
  @BindView(R.id.send_editText_amount) EditText send_editText_amount;
  @BindView(R.id.send_button_send) Button send_button_send;
  @BindView(R.id.send_imageButton_scanqr) ImageView send_imageButton_scanqr;
  @BindView(R.id.send_radioButtonPublic) RadioButton send_radioButtonPublic;
  @BindView(R.id.send_radioButtonPrivate) RadioButton send_radioButtonPrivate;
  @BindView(R.id.send_textView_amount) TextView send_textView_amount;
  @BindView(R.id.send_textView_zen) TextView send_textView_zen;

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

    if(send_radioButtonPublic.isChecked()) {
      send_textView_amount.setText("Amount (current balance: " + String.valueOf(SecurityHolder.current_balance_public)+" ZEN)");
    }else{
      send_textView_amount.setText("Amount (current balance: " + String.valueOf(SecurityHolder.current_balance_private)+" ZEN)");
    }
  }

  private void updateBalances() {

    PreferencesHelper preferencesHelper = new PreferencesHelper(getActivity());
    TheAPI theAPI = TheAPI.Factory.getIstance(getActivity());
  }

  @Override public void onResume() {
    super.onResume();
    KLog.d("ON RESUME CALLED!");
    if(!SecurityHolder.lastScanAddress.isEmpty()){
      send_editText_to.setText(SecurityHolder.lastScanAddress);
    }
  }

  @OnClick(R.id.send_imageButton_scanqr) public void onClickScanQR(){
    Intent iScan = new Intent(getActivity(), QRScannerActivity.class);
    iScan.putExtra("type","address");
    startActivity(iScan);
  }
}