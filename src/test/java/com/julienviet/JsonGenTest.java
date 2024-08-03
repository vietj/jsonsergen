package com.julienviet;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import com.julienviet.support.test.Address;
import com.julienviet.support.test.User;
import com.julienviet.support.test.UserJsonSerializer;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class JsonGenTest {

  private User user;
  private JsonObject expected;

  public JsonGenTest() {
    user = new User();
    user.setFirstName("Marcel");
    user.setLastName("Pagnol");
    user.setAge(3);
    Address address = new Address();
    address.setStreet("15 rue des Lilas");
    address.setCity("Marseille");
    address.setZip(13001);
    user.setAddress(address);
    user.setAliases(Arrays.asList("foo", "bar", "juu"));
    expected = new JsonObject()
      .put("firstName", "Marcel")
      .put("lastName", "Pagnol")
      .put("age", 3)
      .put("aliases", new JsonArray().add("foo").add("bar").add("juu"))
      .put("address", new JsonObject()
        .put("street", "15 rue des Lilas")
        .put("city", "Marseille")
        .put("zip", 13001));
  }

  @Test
  public void testJackson() throws IOException {
    for (int i = 0;i < 2;i++) {
      Buffer buff = UserJsonSerializer.Jackson.toJsonBuffer(user);
      JsonObject json = new JsonObject(buff);
      assertEquals(expected, json);
    }
  }

  @Test
  public void testFastJson() throws IOException {
    for (int i = 0;i < 2;i++) {
      Buffer buff = UserJsonSerializer.FastJson.toJsonBuffer(user);
      JsonObject json = new JsonObject(buff);
      assertEquals(expected, json);
    }
  }

  @Test
  public void testDslJson() throws IOException {
    for (int i = 0;i < 2;i++) {
      Buffer buff = UserJsonSerializer.DslJson.toJsonBuffer(user);
      JsonObject json = new JsonObject(buff);
      assertEquals(expected, json);
    }
  }
}
