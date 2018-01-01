package com.androidadvance.zcryptowallet.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.OnClick;
import com.androidadvance.zcryptowallet.BaseFragment;
import com.androidadvance.zcryptowallet.R;
import com.androidadvance.zcryptowallet.activities.MainActivity;
import com.androidadvance.zcryptowallet.data.local.PreferencesHelper;
import com.androidadvance.zcryptowallet.data.remote.TheAPI;
import com.androidadvance.zcryptowallet.utils.DUtils;
import com.androidadvance.zcryptowallet.utils.DialogFactory;
import com.androidadvance.zcryptowallet.utils.SecurityHolder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.socks.library.KLog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewAccountFragment extends BaseFragment {

  @BindView(R.id.button_new_account) Button button_new_account;
  private ProgressDialog progressDialog;

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
  }

  @OnClick(R.id.button_new_account) public void onClickNewAccount() {

    progressDialog = DialogFactory.createProgressDialog(getActivity(), "Creating a new user account... Please wait");
    progressDialog.show();

    PreferencesHelper preferencesHelper = new PreferencesHelper(getActivity());

    JsonObject newUserJsonObject = new JsonObject();
    newUserJsonObject.add("deviceid", new JsonPrimitive(preferencesHelper.getDeviceID()));
    newUserJsonObject.add("deviceinfo", new JsonPrimitive(DUtils.getDeviceInfo()));

    TheAPI theAPI = TheAPI.Factory.getIstance(getActivity());
    theAPI.createnewuser(newUserJsonObject).enqueue(new Callback<JsonObject>() {
      @Override public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
        if (response.code() == 200 || response.code() == 201) {
          JsonObject jsonObjectKeys = response.body();
          String public_address = jsonObjectKeys.get("public_address").getAsString();
          String priv_key_public_address = jsonObjectKeys.get("priv_key_public_address").getAsString();
          String private_address = jsonObjectKeys.get("private_address").getAsString();
          String priv_key_private_address = jsonObjectKeys.get("priv_key_private_address").getAsString();

          SecurityHolder.public_address = public_address;
          SecurityHolder.private_address = private_address;

          SecurityHolder.storePublicAddress(getActivity(), public_address);
          SecurityHolder.storePrivateAddress(getActivity(), private_address);

          showPrivateKeysDialog(getActivity(), "ZCryptoWallet Account: Public Address "
              + public_address
              + " with private key: "
              + priv_key_public_address
              + "  #### and ### Private Address: "
              + private_address
              + " with private key: "
              + priv_key_private_address);
          progressDialog.dismiss();
        } else {
          DialogFactory.createGenericErrorDialog(getActivity(),
              "Due to the large amount of users, registration of new accounts is currently closed. Please try again later.").show();
        }
      }

      @Override public void onFailure(Call<JsonObject> call, Throwable t) {

        progressDialog.dismiss();
      }
    });
  }

  @Override public void onPause() {
    super.onPause();
    if ((progressDialog != null) && progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
  }

  public static void showPrivateKeysDialog(Context context, String textToCopyToClipboard) {

    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.dialog_private_keys, null);
    alertDialogBuilder.setView(view);
    alertDialogBuilder.setCancelable(false);
    final AlertDialog dialog = alertDialogBuilder.create();
    dialog.show();

    Button btn_dlg_clipboard = view.findViewById(R.id.btn_dlg_clipboard);
    Button btn_dlg_continue = view.findViewById(R.id.btn_dlg_continue);
    EditText editText_dlg_keys = view.findViewById(R.id.editText_dlg_keys);

    editText_dlg_keys.setText(textToCopyToClipboard);

    btn_dlg_clipboard.setOnClickListener(v -> {

      android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
      android.content.ClipData clip = android.content.ClipData.newPlainText("zcrypto", textToCopyToClipboard);
      if (clipboard != null) {
        clipboard.setPrimaryClip(clip);
        DialogFactory.success_toast(context, "Text has been copied to clipboard.").show();
      }
    });

    btn_dlg_continue.setOnClickListener(v -> {
      Intent i_main = new Intent(context, MainActivity.class);
      dialog.dismiss();
      context.startActivity(i_main);
    });
  }
}