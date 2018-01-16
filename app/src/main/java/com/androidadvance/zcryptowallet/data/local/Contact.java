package com.androidadvance.zcryptowallet.data.local;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity public class Contact {

  @Id public long id;
  String name;
  String address;

  public Contact() {
  }

  public Contact(String name, String address) {
    this.name = name;
    this.address = address;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  @Override public String toString() {
    return "Contact{'name='" + name + '\'' + ", address='" + address + '\'' + '}';
  }
}
