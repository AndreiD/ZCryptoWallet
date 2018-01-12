package com.androidadvance.zcryptowallet.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import butterknife.BindView;
import butterknife.OnClick;
import com.androidadvance.zcryptowallet.BaseFragment;
import com.androidadvance.zcryptowallet.R;
import com.androidadvance.zcryptowallet.activities.SplashActivity;
import com.androidadvance.zcryptowallet.data.local.PreferencesHelper;
import com.androidadvance.zcryptowallet.data.remote.TheAPI;
import com.androidadvance.zcryptowallet.utils.DUtils;
import com.androidadvance.zcryptowallet.utils.DialogFactory;
import com.androidadvance.zcryptowallet.utils.SecurityHolder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.socks.library.KLog;
import java.util.Random;
import org.json.JSONObject;
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

    JsonObject newUserJsonObject = new JsonObject();
    newUserJsonObject.add("deviceid", new JsonPrimitive(DUtils.getUniqueID()));
    newUserJsonObject.add("deviceinfo", new JsonPrimitive(DUtils.getDeviceInfo()));
    newUserJsonObject.add("pin", new JsonPrimitive(SecurityHolder.pin));

    TheAPI theAPI = TheAPI.Factory.getIstance(getActivity());
    theAPI.createnewuser(newUserJsonObject).enqueue(new Callback<JsonObject>() {
      @Override public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
        if (response.code() < 300) {
          JsonObject jsonObjectKeys = response.body();
          String public_address = jsonObjectKeys.get("public_address").getAsString();
          String priv_key_public_address = jsonObjectKeys.get("priv_key_public_address").getAsString();
          String private_address = jsonObjectKeys.get("private_address").getAsString();
          String priv_key_private_address = jsonObjectKeys.get("priv_key_private_address").getAsString();

          SecurityHolder.publicAddress = public_address;
          SecurityHolder.publicAddressKey = priv_key_public_address;

          SecurityHolder.privateAddress = private_address;
          SecurityHolder.privateAddressKey = priv_key_private_address;

          //setup complete. this is an old account. store it in preferences.
          PreferencesHelper preferencesHelper = new PreferencesHelper(getActivity());
          preferencesHelper.setIsNewAccount(false);

          AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
          builder.setMessage("Account Created Successfully. Please restart the application")
              .setCancelable(false)
              .setPositiveButton("OK", (dialog, id) -> {
                dialog.dismiss();
                getActivity().finish();
              });
          AlertDialog alert = builder.create();
          alert.show();

          progressDialog.dismiss();
        } else {
          progressDialog.dismiss();

          try {
            JSONObject jObjError = new JSONObject(response.errorBody().string());

            if (jObjError.getString("error").contains("Seems you already have an account")) {
              SecurityHolder.pin = String.valueOf(new Random().nextInt(999999));
              PreferencesHelper preferencesHelper = new PreferencesHelper(getActivity());
              preferencesHelper.setIsNewAccount(false); //force the show enter pin screen123


              AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogErrorStyle);
              builder.setMessage("Application will restart now. Because there's already an account tied to this device please use the forgot PIN function in order to restore your account.")
                  .setCancelable(false)
                  .setPositiveButton("OK", (dialog, id) -> {
                    dialog.dismiss();
                    getActivity().finish();
                  });
              AlertDialog alert = builder.create();
              alert.show();


              return;
            } else {
              DialogFactory.error_toast(getActivity(), jObjError.getString("error")).show();
            }

            return;
          } catch (Exception e) {
            KLog.e(e);
          }
          DialogFactory.createGenericErrorDialog(getActivity(), "Error creating new account. Please try again later: ").show();
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
}