package com.julienviet.support.test;

import io.vertx.codegen.annotations.DataObject;
import com.julienviet.jsonsergen.JsonSerGen;

@DataObject
@JsonSerGen
public class Address {

  private String street;
  private String city;
  private int zip;

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public int getZip() {
    return zip;
  }

  public void setZip(int zip) {
    this.zip = zip;
  }
}
