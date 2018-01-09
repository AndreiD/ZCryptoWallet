package com.androidadvance.zcryptowallet.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import com.androidadvance.zcryptowallet.BaseFragment;
import com.androidadvance.zcryptowallet.R;
import com.androidadvance.zcryptowallet.utils.DialogFactory;
import com.androidadvance.zcryptowallet.utils.SecurityHolder;
import com.socks.library.KLog;
import com.squareup.picasso.Picasso;

public class ReceiveFragment extends BaseFragment {

  @BindView(R.id.radioButtonPrivate) RadioButton radioButtonPrivate;
  @BindView(R.id.radioButtonPublic) RadioButton radioButtonPublic;
  @BindView(R.id.btn_receive_share) Button btn_receive_share;
  @BindView(R.id.btn_copy_clipboard) Button btn_copy_clipboard;
  @BindView(R.id.imageView_qrcode_receive) ImageView imageView_qrcode_receive;
  @BindView(R.id.textView_receive_zenaddress) TextView textView_receive_zenaddress;

  public ReceiveFragment() {
  }

  public static ReceiveFragment newInstance() {
    ReceiveFragment fragment = new ReceiveFragment();
    fragment.setRetainInstance(true);
    return fragment;
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_receive, container, false);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    onCheckedChangedPrivate();
  }

  @OnCheckedChanged(R.id.radioButtonPrivate) public void onCheckedChangedPrivate() {
    if (radioButtonPrivate.isChecked()) {
      String address = SecurityHolder.privateAddress;
      textView_receive_zenaddress.setText(address);
      Picasso.with(getActivity())
          .load("http://chart.apis.google.com/chart?cht=qr&chs=500x500&chl=" + address + "&chld=H|0")
          .into(imageView_qrcode_receive);
    }
  }

  @OnCheckedChanged(R.id.radioButtonPublic) public void onCheckedChangedPublic() {
    if (radioButtonPublic.isChecked()) {
      String address = SecurityHolder.publicAddress;
      textView_receive_zenaddress.setText(address);
      Picasso.with(getActivity())
          .load("http://chart.apis.google.com/chart?cht=qr&chs=500x500&chl=" + address + "&chld=H|0")
          .into(imageView_qrcode_receive);
    }
  }

  @OnClick(R.id.btn_receive_share) public void onClickBtnShare() {

    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
    sharingIntent.setType("text/plain");
    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Please send ZEN to this address");
    sharingIntent.putExtra(Intent.EXTRA_TEXT, textView_receive_zenaddress.getText().toString());
    startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
  }

  @OnClick(R.id.btn_copy_clipboard) public void onClickCopyClipboaord() {
    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
    android.content.ClipData clip = android.content.ClipData.newPlainText("zen address",textView_receive_zenaddress.getText().toString());
    clipboard.setPrimaryClip(clip);
    DialogFactory.simple_toast(getActivity(),"Address copied to clipboard").show();
  }
}