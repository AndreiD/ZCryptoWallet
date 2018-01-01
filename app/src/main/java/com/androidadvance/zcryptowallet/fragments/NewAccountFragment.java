package com.androidadvance.zcryptowallet.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import butterknife.BindView;
import butterknife.OnClick;
import com.androidadvance.zcryptowallet.BaseFragment;
import com.androidadvance.zcryptowallet.R;
import com.androidadvance.zcryptowallet.data.remote.TheAPI;
import com.socks.library.KLog;

public class NewAccountFragment extends BaseFragment {

  @BindView(R.id.button_new_account) Button button_new_account;

  public NewAccountFragment() {
  }

  public static NewAccountFragment newInstance() {
    NewAccountFragment fragment = new NewAccountFragment();
    fragment.setRetainInstance(true);
    return fragment;
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_new_account, container, false);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    // SecurityHolder.storePIN(this, pin1);
  }

  @OnClick(R.id.button_new_account) public void onClickNewAccount(){
    KLog.d("CLICKED NEW ACCOUNT");
    TheAPI theAPI = TheAPI.Factory.getIstance(getActivity());

  }
}