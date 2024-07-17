/*
 * Copyright (c) 2011-2019 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package com.julienviet.benchmarks;

import com.julienviet.support.test.Address;
import com.julienviet.support.test.User;
import com.julienviet.support.test.UserJsonSerializer;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.core.json.jackson.JacksonCodec;
import io.vertx.core.spi.json.JsonCodec;
import com.julienviet.jsonsergen.dsljson.DslJsonCodec;
import com.julienviet.jsonsergen.fastjson.FastJsonCodec;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.*;
import java.net.URL;

/**
 * @author Thomas Segismont
 * @author slinkydeveloper
 */
@State(Scope.Thread)
public class JsonEncodeBenchmark extends BenchmarkBase {

  User user = new User();
  JsonObject userJson;
  JsonCodec jacksonCodec;
  JsonCodec databindCodec;

  @Setup
  public void setup() throws Exception {
    ClassLoader classLoader = getClass().getClassLoader();
    Address address = new Address();
    address.setStreet("15 rue des Lilas");
    address.setCity("Marseille");
    address.setZip(13001);
    User user = new User();
    user.setFirstName("Marcel");
    user.setLastName("Pagnol");
    user.setAge(3);
    user.setAddress(address);
    this.user = user;
    this.userJson = new JsonObject(UserJsonSerializer.toJsonBuffer(user));
    this.jacksonCodec = new JacksonCodec();
    this.databindCodec = new DatabindCodec();
  }

  private JsonObject loadJson(URL url) throws Exception {
    InputStream is = url.openStream();
    byte[] tmp = new byte[256];
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    for (int l = 0;l != -1;l = is.read(tmp, 0, 256)) {
      baos.write(tmp, 0, l);
    }
    baos.flush();
    baos.close();
    byte[] bytes = baos.toByteArray();
    return new JsonObject(Buffer.buffer(bytes));
  }

  @Benchmark
  public void jackson(Blackhole blackhole) throws Exception {
    bufferJackson(userJson, blackhole);
  }

  @Benchmark
  public void databind(Blackhole blackhole) throws Exception {
    bufferDatabind(userJson, blackhole);
  }

  @Benchmark
  public void jsonSerGen(Blackhole blackhole) throws Exception {
    bufferJsonSerGen(user, blackhole);
  }

  private void bufferJackson(JsonObject user, Blackhole blackhole) throws Exception {
    blackhole.consume(jacksonCodec.toBuffer(user));
  }

  private void bufferDatabind(JsonObject user, Blackhole blackhole) throws Exception {
    blackhole.consume(databindCodec.toBuffer(user));
  }

  private void bufferJsonSerGen(User user, Blackhole blackhole) throws Exception {
    blackhole.consume(UserJsonSerializer.toJsonBuffer(user));
  }
}
