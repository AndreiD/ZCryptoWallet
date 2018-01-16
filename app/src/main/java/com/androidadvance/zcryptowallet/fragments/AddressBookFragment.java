package com.androidadvance.zcryptowallet.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.OnClick;
import com.androidadvance.zcryptowallet.BaseApplication;
import com.androidadvance.zcryptowallet.BaseFragment;
import com.androidadvance.zcryptowallet.R;
import com.androidadvance.zcryptowallet.adapters.ContactsAdapter;
import com.androidadvance.zcryptowallet.data.local.Contact;
import com.androidadvance.zcryptowallet.events.ContactClickedEvent;
import com.androidadvance.zcryptowallet.qrscanner.QRScannerActivity;
import com.androidadvance.zcryptowallet.utils.DUtils;
import com.androidadvance.zcryptowallet.utils.DialogFactory;
import com.androidadvance.zcryptowallet.utils.SecurityHolder;
import com.socks.library.KLog;
import io.objectbox.Box;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class AddressBookFragment extends BaseFragment {

  @BindView(R.id.editText_add_new_contact_name) EditText editText_add_new_contact_name;
  @BindView(R.id.edittext_add_new_contact_address) EditText edittext_add_new_contact_address;
  @BindView(R.id.send_imageButton_scanqr_addressbook) ImageView send_imageButton_scanqr_addressbook;
  @BindView(R.id.send_imageButton_save_addressbook) ImageView send_imageButton_save_addressbook;
  @BindView(R.id.recyclerview_addressbook) RecyclerView recyclerview_addressbook;
  private Box<Contact> contactBox;
  private ContactsAdapter mAdapter;

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

    contactBox = ((BaseApplication) getActivity().getApplication()).getBoxStore().boxFor(Contact.class);


    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
    recyclerview_addressbook.setLayoutManager(mLayoutManager);
    recyclerview_addressbook.setItemAnimator(new DefaultItemAnimator());


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

    contactBox.put(new Contact(editText_add_new_contact_name.getText().toString().trim(), edittext_add_new_contact_address.getText().toString().trim()));
    DialogFactory.simple_toast(getActivity(), "Saved").show();

    editText_add_new_contact_name.setText("");
    edittext_add_new_contact_address.setText("");
    DUtils.hideKeyboard(getActivity());
    refreshContactsList();
  }

  @Override public void onResume() {
    super.onResume();
    if (!SecurityHolder.lastScanAddress.isEmpty()) {
      edittext_add_new_contact_address.setText(SecurityHolder.lastScanAddress);
    }

    refreshContactsList();


  }

  private void refreshContactsList() {

    List<Contact> contactList = contactBox.getAll();
    KLog.d(">>>> LISTING CONTACTS <<<<<<<");
    KLog.d(contactList.toString());
    KLog.d(">>>> LISTING CONTACTS <<<<<<<");

    if(contactList.size() < 1){
      return;
    }

    mAdapter = new ContactsAdapter(contactList,getActivity());
    recyclerview_addressbook.setAdapter(mAdapter);
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onContactClickedEvent(ContactClickedEvent event) {

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
    builder.setTitle(event.contact.getName());
    builder.setItems(new CharSequence[]
            {"Copy to Clipboard", "Delete", "Cancel"}, (dialog, which) -> {
              switch (which) {
                case 0:
                  android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                  android.content.ClipData clip = android.content.ClipData.newPlainText("address",event.contact.getAddress());
                  if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    DialogFactory.simple_toast(getActivity(),"Address copied in your clipboard").show();
                  }

                  break;
                case 1:
                  contactBox.remove(event.contact);
                  DialogFactory.simple_toast(getActivity(),"Deleted").show();
                  refreshContactsList();
                  break;
                case 2:
                  dialog.dismiss();
                  break;
                default:
                  KLog.d("no event...");
                  break;
              }
            });
    builder.create().show();


  }


  @Override
  public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onStop() {
    super.onStop();
    EventBus.getDefault().unregister(this);
  }
}