package com.androidadvance.zcryptowallet.data.local;

import com.google.gson.annotations.SerializedName;

public class Contact {

  @SerializedName("name") private String name;

  @SerializedName("address") private String address;

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
