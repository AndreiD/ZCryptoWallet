package com.androidadvance.zcryptowallet.events;

import com.androidadvance.zcryptowallet.data.local.Contact;

public class ContactClickedEvent {
  public Contact contact;
  public ContactClickedEvent(Contact contact) {
    this.contact = contact;

  }
}
