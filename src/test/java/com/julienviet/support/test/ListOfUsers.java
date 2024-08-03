package com.julienviet.support.test;

import com.julienviet.jsonsergen.Backend;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import com.julienviet.jsonsergen.JsonSerGen;

import java.util.ArrayList;
import java.util.List;

@DataObject
@JsonSerGen(backends = {Backend.FAST_JSON,Backend.JACKSON})
public class ListOfUsers {

  private List<User> values = new ArrayList<>();

  public ListOfUsers(JsonObject json) {
  }

  public ListOfUsers() {
  }

  public ListOfUsers(ListOfUsers that) {
  }

  public List<User> getValues() {
    return values;
  }

  public void setValues(List<User> values) {
    this.values = values;
  }

  public void addValue(User value) {
    this.values.add(value);
  }
}
