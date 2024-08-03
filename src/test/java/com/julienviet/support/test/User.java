package com.julienviet.support.test;

import com.julienviet.jsonsergen.Backend;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import com.julienviet.jsonsergen.JsonSerGen;

import java.util.ArrayList;
import java.util.List;

@DataObject
@JsonSerGen(backends = {Backend.FAST_JSON,Backend.JACKSON,Backend.DSL_JSON})
public class User {

  private String firstName;
  private String lastName;
  private int age;
  private Address address;
  private List<String> aliases = new ArrayList<>();

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

  public List<String> getAliases() {
    return aliases;
  }

  public void setAliases(List<String> aliases) {
    this.aliases = aliases;
  }
}
