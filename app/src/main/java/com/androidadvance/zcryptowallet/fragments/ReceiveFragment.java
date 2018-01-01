package com.androidadvance.zcryptowallet.fragments;

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
import com.androidadvance.zcryptowallet.utils.SecurityHolder;
import com.socks.library.KLog;
import com.squareup.picasso.Picasso;

public class ReceiveFragment extends BaseFragment {

  @BindView(R.id.radioButtonPrivate) RadioButton radioButtonPrivate;
  @BindView(R.id.radioButtonPublic) RadioButton radioButtonPublic;
  @BindView(R.id.btn_receive_share) Button btn_receive_share;
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
      KLog.d("PRIVATE IS CHECKED!");
      String address = SecurityHolder.getPrivateAddress(getActivity()).replaceAll("\"", "");
      textView_receive_zenaddress.setText(address);
      Picasso.with(getActivity())
          .load("http://chart.apis.google.com/chart?cht=qr&chs=500x500&chl=" + address + "&chld=H|0")
          .into(imageView_qrcode_receive);
    }
  }

  @OnCheckedChanged(R.id.radioButtonPublic) public void onCheckedChangedPublic() {
    if (radioButtonPublic.isChecked()) {
      KLog.d("PUBLIC IS CHECKED!");
      String address = SecurityHolder.getPublicAddress(getActivity()).replaceAll("\"", "");
      textView_receive_zenaddress.setText(address);
      Picasso.with(getActivity())
          .load("http://chart.apis.google.com/chart?cht=qr&chs=500x500&chl=" + address + "&chld=H|0")
          .into(imageView_qrcode_receive);
    }
  }

  @OnClick(R.id.btn_receive_share) public void onClickBtnShare() {

    //Intent sharingIntent = new Intent(Intent.ACTION_SEND);
    //sharingIntent.setType("text/plain");
    //sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Please send BTC to this address");
    //sharingIntent.putExtra(Intent.EXTRA_TEXT, SecurityHolder.btcAddress + " <img src=\"http://chart.apis.google.com/chart?cht=qr&chs=500x500&chl="+  SecurityHolder.btcAddress.replaceAll("\"","")+"&chld=H|0\"");
    //startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
  }
}