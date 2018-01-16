package com.androidadvance.zcryptowallet.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidadvance.zcryptowallet.R;
import com.androidadvance.zcryptowallet.data.local.Contact;
import com.androidadvance.zcryptowallet.events.ContactClickedEvent;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {

  private final Context context;
  private List<Contact> contactList;

  public ContactsAdapter(List<Contact> contactList, Context ctx) {
    this.contactList = contactList;
    this.context = ctx;
  }

  @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_contacts, parent, false);
    return new MyViewHolder(itemView);
  }

  @Override public void onBindViewHolder(MyViewHolder holder, int position) {
    Contact contact = contactList.get(position);
    holder.row_textView_name.setText(contact.getName());
    holder.row_textView_address.setText(contact.getAddress());

    if(position % 2 != 0){
      holder.row_linlayout_contact.setBackgroundColor(context.getResources().getColor(R.color.zen_orange_transparent));
    }

    holder.row_linlayout_contact.setOnClickListener(view -> EventBus.getDefault().post(new ContactClickedEvent(contact)));

  }

  @Override public int getItemCount() {
    return contactList.size();
  }

  public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView row_textView_name, row_textView_address;
    LinearLayout row_linlayout_contact;

    public MyViewHolder(View view) {
      super(view);
      row_textView_name = view.findViewById(R.id.row_textView_name);
      row_textView_address = view.findViewById(R.id.row_textView_address);
      row_linlayout_contact = view.findViewById(R.id.row_linlayout_contact);

    }
  }
}