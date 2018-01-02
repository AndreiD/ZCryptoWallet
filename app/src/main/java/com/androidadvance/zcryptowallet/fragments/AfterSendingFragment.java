package com.androidadvance.zcryptowallet.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.androidadvance.zcryptowallet.BaseFragment;
import com.androidadvance.zcryptowallet.R;
import com.androidadvance.zcryptowallet.data.remote.TheAPI;
import com.androidadvance.zcryptowallet.utils.DialogFactory;
import com.google.gson.JsonObject;
import com.socks.library.KLog;
import java.util.Timer;
import java.util.TimerTask;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AfterSendingFragment extends BaseFragment {
  private String opid;
  private final static int INTERVAL = 1000 * 60 * 2; //2 minutes
  Timer myTimer = new Timer("MyTimer", true);

  public AfterSendingFragment() {
  }

  public static AfterSendingFragment newInstance(String opid) {
    Bundle args = new Bundle();
    args.putString("opid", opid);
    AfterSendingFragment fragment = new AfterSendingFragment();
    fragment.setArguments(args);
    fragment.setRetainInstance(true);
    return fragment;
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_aftersend, container, false);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    opid = getArguments().getString("opid", null);

    myTimer.scheduleAtFixedRate(new MyTask(), INTERVAL, INTERVAL);
  }

  private class MyTask extends TimerTask {

    public void run() {
      KLog.d("checking for txid for opid: " + opid);
      TheAPI theAPI = TheAPI.Factory.getIstance(getActivity());
      theAPI.checkOpid(opid).enqueue(new Callback<JsonObject>() {
        @Override public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
          JsonObject jsonObject = response.body();
          if (response.code() > 299) {
            DialogFactory.createGenericErrorDialog(getActivity(), jsonObject.toString()).show();
            return;
          }

          if (jsonObject.has("txid")) {
            myTimer.cancel();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Success!");
            builder.setMessage("Transaction Completed with txid: " + jsonObject.get("txid"))
                .setCancelable(false)
                .setPositiveButton("View Transaction", (dialog, id) -> {
                  Intent browserIntent =
                      new Intent(Intent.ACTION_VIEW, Uri.parse("https://explorer.zensystem.io/tx/" + jsonObject.get("txid").getAsString()));
                  startActivity(browserIntent);
                  dialog.dismiss();
                  getActivity().getSupportFragmentManager().popBackStack();
                })
                .setNegativeButton("Exit", (dialog, id) -> {
                  dialog.dismiss();
                  getActivity().getSupportFragmentManager().popBackStack();
                });
            AlertDialog alert = builder.create();
            alert.show();
          } else {
            DialogFactory.loading_toast(getActivity(), "Still Processing...").show();
          }
        }

        @Override public void onFailure(Call<JsonObject> call, Throwable t) {
          myTimer.cancel();
          DialogFactory.createGenericErrorDialog(getActivity(), t.getLocalizedMessage()).show();
        }
      });
    }
  }

  @Override public void onPause() {
    super.onPause();
    myTimer.cancel();
  }
}