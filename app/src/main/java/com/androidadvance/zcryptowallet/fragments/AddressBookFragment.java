package com.androidadvance.zcryptowallet.fragments;

import android.content.Context;
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
import com.androidadvance.zcryptowallet.data.local.Contact;
import com.androidadvance.zcryptowallet.qrscanner.QRScannerActivity;
import com.androidadvance.zcryptowallet.utils.DialogFactory;
import com.androidadvance.zcryptowallet.utils.SecurityHolder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.socks.library.KLog;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    if ((editText_add_new_contact_name.getText().toString().isEmpty()) || (edittext_add_new_contact_address.getText().toString().length() < 25)) {
      DialogFactory.warning_toast(getActivity(), "Please enter contact name and it's ZEN address.").show();
      return;
    }
  }

  @Override public void onResume() {
    super.onResume();
    if (!SecurityHolder.lastScanAddress.isEmpty()) {
      edittext_add_new_contact_address.setText(SecurityHolder.lastScanAddress);
    }

    // populateContacts();

    test_contacts();
  }

  private void test_contacts() {

    //save two

    JSONArray jsonArray = new JSONArray();
    try {
      jsonArray = new JSONArray(readFromFile());
    } catch (JSONException e) {
      e.printStackTrace();
    }

    Gson gson = new Gson();
    String json = gson.toJson(new Contact("ne", "address"));
    jsonArray.put(json);
    SecurityHolder.storeContacts(getActivity(), json);

    json = gson.toJson(new Contact("ne2", "address2"));
    jsonArray.put(json);

    writeToFile(jsonArray.toString());


    try {
      JSONArray jarrayNew = new JSONArray(readFromFile());
      KLog.d(jarrayNew.toString());
      KLog.d("Length: " + jarrayNew.length());
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void populateContacts() {

    SecurityHolder.storeContacts(getActivity(), "");

    String contactsRaw = SecurityHolder.getContacts(getActivity());
    try {
      JSONArray jsonArray = new JSONArray(contactsRaw);
      KLog.d("WE HAVE A TOTAL OF : " + jsonArray.length() + " CONTACTS!");

      JsonObject contactJsonObject = new JsonObject();
      contactJsonObject.addProperty("entry_name", "xyz...");
      contactJsonObject.addProperty("address", "address here...");
      jsonArray.put(contactJsonObject);

      JsonObject contactJsonObject2 = new JsonObject();
      contactJsonObject2.addProperty("entry_name", "xyz2...");
      contactJsonObject2.addProperty("address", "address here2...");
      jsonArray.put(contactJsonObject2);

      JsonObject contactJsonObject3 = new JsonObject();
      contactJsonObject3.addProperty("entry_name", "xyz3...");
      contactJsonObject3.addProperty("address", "address here3...");
      jsonArray.put(contactJsonObject3);

      SecurityHolder.storeContacts(getActivity(), jsonArray.toString());
    } catch (JSONException e) {
      KLog.e(e);
    }

    KLog.d(" AND GET THEM : " + SecurityHolder.getContacts(getActivity()));
  }

  private void writeToFile(String data) {
    try {
      OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getActivity().openFileOutput("addressbook.txt", Context.MODE_PRIVATE));
      outputStreamWriter.write(data);
      outputStreamWriter.close();
    } catch (IOException e) {
      KLog.e("Exception", "File write failed: " + e.toString());
    }
  }

  private String readFromFile() {

    String ret = "";

    try {
      InputStream inputStream = getActivity().openFileInput("addressbook.txt");

      if (inputStream != null) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String receiveString = "";
        StringBuilder stringBuilder = new StringBuilder();

        while ((receiveString = bufferedReader.readLine()) != null) {
          stringBuilder.append(receiveString);
        }

        inputStream.close();
        ret = stringBuilder.toString();
      }
    } catch (FileNotFoundException e) {
      KLog.e("login activity", "File not found: " + e.toString());
    } catch (IOException e) {
      KLog.e("login activity", "Can not read file: " + e.toString());
    }

    return ret;
  }
}