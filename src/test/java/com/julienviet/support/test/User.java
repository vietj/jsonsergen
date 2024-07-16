package com.julienviet.support.test;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import com.julienviet.jsonsergen.JsonSerGen;

@DataObject
@JsonSerGen
public class User {

  private String firstName;
  private String lastName;
  private int age;
  private Address address;

  public User() {
  }

  public User(JsonObject json) {
  }

  public User(User that) {
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }
}
