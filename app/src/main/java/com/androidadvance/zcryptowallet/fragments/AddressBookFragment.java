package com.androidadvance.zcryptowallet.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.OnClick;
import com.androidadvance.zcryptowallet.BaseFragment;
import com.androidadvance.zcryptowallet.R;
import com.androidadvance.zcryptowallet.qrscanner.QRScannerActivity;
import com.androidadvance.zcryptowallet.utils.DialogFactory;
import com.androidadvance.zcryptowallet.utils.SecurityHolder;

public class AddressBookFragment extends BaseFragment {

  @BindView(R.id.editText_add_new_contact_name) EditText editText_add_new_contact_name;
  @BindView(R.id.edittext_add_new_contact_address) EditText edittext_add_new_contact_address;
  @BindView(R.id.send_imageButton_scanqr_addressbook) ImageView send_imageButton_scanqr_addressbook;
  @BindView(R.id.send_imageButton_save_addressbook) ImageView send_imageButton_save_addressbook;
  @BindView(R.id.recyclerview_addressbook) RecyclerView recyclerview_addressbook;

  public AddressBookFragment() {
  }

  public static AddressBookFragment newInstance() {
    AddressBookFragment fragment = new AddressBookFragment();
    fragment.setRetainInstance(true);
    return fragment;
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_addressbook, container, false);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);


  }

  @OnClick(R.id.send_imageButton_scanqr_addressbook) public void onClickScanQRCode() {
    Intent iScan = new Intent(getActivity(), QRScannerActivity.class);
    iScan.putExtra("type", "address");
    startActivity(iScan);
  }

  @OnClick(R.id.send_imageButton_save_addressbook) public void onClickSave() {
    if( (editText_add_new_contact_name.getText().toString().isEmpty()) || (edittext_add_new_contact_address.getText().toString().length() < 25)){
      DialogFactory.warning_toast(getActivity(),"Invalid contact name or address.").show();
      return;
    }

  }

  @Override public void onResume() {
    super.onResume();

    if (!SecurityHolder.lastScanAddress.isEmpty()) {
      edittext_add_new_contact_address.setText(SecurityHolder.lastScanAddress);
    }
  }
}