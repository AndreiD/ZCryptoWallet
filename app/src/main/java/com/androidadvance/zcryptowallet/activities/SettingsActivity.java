package com.androidadvance.zcryptowallet.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.androidadvance.zcryptowallet.R;
import com.androidadvance.zcryptowallet.data.local.PreferencesHelper;
import com.androidadvance.zcryptowallet.data.remote.TheAPI;
import com.androidadvance.zcryptowallet.utils.DialogFactory;
import com.androidadvance.zcryptowallet.utils.SecurityHolder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    getSupportActionBar().setElevation(0);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public static class MyPreferenceFragment extends PreferenceFragment {
    @Override public void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.settings);

      Preference version = findPreference("version");
      try {
        String versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        version.setSummary(versionName);
      } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
      }

      Preference buttonfeedback = findPreference(getString(R.string.send_feedback));
      buttonfeedback.setOnPreferenceClickListener(preference -> {
        DialogFactory.simple_toast(getActivity(), "You should have an account on Discord...").show();
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://discord.gg/p96a98Z"));
        startActivity(viewIntent);

        return true;
      });

      Preference buttonReset = findPreference(getString(R.string.erase_everything));
      buttonReset.setOnPreferenceClickListener(preference -> {
        PreferencesHelper preferencesHelper = new PreferencesHelper(getActivity());
        preferencesHelper.clear();
        getActivity().finish();
        getActivity().finishAffinity();
        return true;
      });


      Preference exportWallet = findPreference(getString(R.string.export_wallet));
      exportWallet.setOnPreferenceClickListener(preference -> {

        showPrivateKeysDialog(getActivity(), "ZCryptoWallet Wallet \n\nPublic Address "
            + SecurityHolder.publicAddress
            + " with private key: "
            + SecurityHolder.publicAddressKey
            + "  \n\nPrivate Address: "
            + SecurityHolder.privateAddress
            + " with private key: "
            + SecurityHolder.privateAddressKey);


        return true;
      });


      Preference buttonChegePin = findPreference(getString(R.string.change_pin));
      buttonChegePin.setOnPreferenceClickListener(preference -> {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
            getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity()
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
            DialogFactory.error_toast(getActivity(),"Pin should be at least 4 characters").show();
            dialog.dismiss();
            return;
          }

          JsonObject updatePinJson = new JsonObject();
          updatePinJson.add("publicaddress", new JsonPrimitive(editText_update_pin_public_address.getText().toString().trim()));
          updatePinJson.add("publickey", new JsonPrimitive(editText_update_pin_public_address_key.getText().toString().trim()));
          updatePinJson.add("new_pin", new JsonPrimitive(pin));

          TheAPI theAPI = TheAPI.Factory.getIstance(getActivity());
          theAPI.updateUserPin(updatePinJson).enqueue(new Callback<JsonObject>() {
            @Override public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
              if(response.code() > 299){
                DialogFactory.error_toast(getActivity(),"Your public address and key were incorrect. Please double-check.").show();
                return;
              }
              DialogFactory.success_toast(getActivity(),"PIN Successfully Updated.").show();
              dialog.dismiss();
              SecurityHolder.pin = pin;
            }

            @Override public void onFailure(Call<JsonObject> call, Throwable t) {
              DialogFactory.error_toast(getActivity(),"Your public address and key were incorrect. Please double-check.").show();
            }
          });


        });

        return true;
      });


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
      Button btn_dlg_close = view.findViewById(R.id.btn_dlg_close);
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
      btn_dlg_close.setOnClickListener(view1 -> dialog.dismiss());


    }
  }
}