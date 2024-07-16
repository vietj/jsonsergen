package com.julienviet;

import io.vertx.core.json.JsonObject;
import com.julienviet.support.test.Address;
import com.julienviet.support.test.User;
import com.julienviet.support.test.UserJsonSerializer;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class JsonGenTest {

  @Test
  public void testSimple() throws IOException {
    User user = new User();
    user.setFirstName("Marcel");
    user.setLastName("Pagnol");
    user.setAge(3);
    Address address = new Address();
    address.setStreet("15 rue des Lilas");
    address.setCity("Marseille");
    address.setZip(13001);
    user.setAddress(address);
    String s = UserJsonSerializer.toJsonBuffer(user).toString();
    JsonObject json = new JsonObject(s);
    assertEquals(new JsonObject()
        .put("firstName", "Marcel")
        .put("lastName", "Pagnol")
        .put("age", 3)
        .put("address", new JsonObject()
          .put("street", "15 rue des Lilas")
          .put("city", "Marseille")
          .put("zip", 13001))
      , json);
  }
}
