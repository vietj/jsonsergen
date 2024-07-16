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

  private JsonObject tiny;
  private JsonObject small;
  private JsonObject wide;
  private JsonObject deep;
  private JsonCodec jacksonCodec;
  private JsonCodec databindCodec;
  private JsonCodec fastJsonCodec;
  private JsonCodec dslJsonCodec;

  @Setup
  public void setup() throws Exception {
    ClassLoader classLoader = getClass().getClassLoader();
    tiny = loadJson(classLoader.getResource("tiny_bench.json"));
    small = loadJson(classLoader.getResource("small_bench.json"));
    wide = loadJson(classLoader.getResource("wide_bench.json"));
    deep = loadJson(classLoader.getResource("deep_bench.json"));
    jacksonCodec = new JacksonCodec();
    databindCodec = new DatabindCodec();
    fastJsonCodec = new FastJsonCodec();
    dslJsonCodec = new DslJsonCodec();
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
  public void tinyStringJackson(Blackhole blackhole) throws Exception {
    stringJackson(tiny, blackhole);
  }

  @Benchmark
  public void tinyStringDatabind(Blackhole blackhole) throws Exception {
    stringDatabind(tiny, blackhole);
  }

  @Benchmark
  public void tinyStringFastJson(Blackhole blackhole) throws Exception {
    stringFastJson(tiny, blackhole);
  }

  @Benchmark
  public void tinyStringDslJson(Blackhole blackhole) throws Exception {
    stringDslJson(tiny, blackhole);
  }

  @Benchmark
  public void smallStringJackson(Blackhole blackhole) throws Exception {
    stringJackson(small, blackhole);
  }

  @Benchmark
  public void smallStringDatabind(Blackhole blackhole) throws Exception {
    stringDatabind(small, blackhole);
  }

  @Benchmark
  public void smallStringFastJson(Blackhole blackhole) throws Exception {
    stringFastJson(small, blackhole);
  }

  @Benchmark
  public void smallStringDslJson(Blackhole blackhole) throws Exception {
    stringDslJson(small, blackhole);
  }

  @Benchmark
  public void wideStringJackson(Blackhole blackhole) throws Exception {
    stringJackson(wide, blackhole);
  }

  @Benchmark
  public void wideStringDatabind(Blackhole blackhole) throws Exception {
    stringDatabind(wide, blackhole);
  }

  @Benchmark
  public void wideStringFastJson(Blackhole blackhole) throws Exception {
    stringFastJson(wide, blackhole);
  }

  @Benchmark
  public void wideStringDslJson(Blackhole blackhole) throws Exception {
    stringDslJson(wide, blackhole);
  }

  @Benchmark
  public void deepStringJackson(Blackhole blackhole) throws Exception {
    stringJackson(deep, blackhole);
  }

  @Benchmark
  public void deepStringDatabind(Blackhole blackhole) throws Exception {
    stringDatabind(deep, blackhole);
  }

  @Benchmark
  public void deepStringFastJson(Blackhole blackhole) throws Exception {
    stringFastJson(deep, blackhole);
  }

  @Benchmark
  public void deepStringDslJson(Blackhole blackhole) throws Exception {
    stringDslJson(deep, blackhole);
  }

  private void stringJackson(JsonObject jsonObject, Blackhole blackhole) throws Exception {
    blackhole.consume(jacksonCodec.toBuffer(jsonObject));
  }

  private void stringDatabind(JsonObject jsonObject, Blackhole blackhole) throws Exception {
    blackhole.consume(databindCodec.toString(jsonObject));
  }

  private void stringFastJson(JsonObject jsonObject, Blackhole blackhole) throws Exception {
    blackhole.consume(fastJsonCodec.toString(jsonObject));
  }

  private void stringDslJson(JsonObject jsonObject, Blackhole blackhole) throws Exception {
    blackhole.consume(dslJsonCodec.toString(jsonObject));
  }

  @Benchmark
  public void smallBufferJackson(Blackhole blackhole) throws Exception {
    bufferJackson(small, blackhole);
  }

  @Benchmark
  public void smallBufferDatabind(Blackhole blackhole) throws Exception {
    bufferDatabind(small, blackhole);
  }

  @Benchmark
  public void smallBufferFastJson(Blackhole blackhole) throws Exception {
    bufferFastJson(small, blackhole);
  }

  @Benchmark
  public void smallBufferDslJson(Blackhole blackhole) throws Exception {
    bufferDslJson(small, blackhole);
  }

  @Benchmark
  public void deepBufferJackson(Blackhole blackhole) throws Exception {
    bufferJackson(deep, blackhole);
  }

  @Benchmark
  public void deepBufferDatabind(Blackhole blackhole) throws Exception {
    bufferDatabind(deep, blackhole);
  }

  @Benchmark
  public void deepBufferFastJson(Blackhole blackhole) throws Exception {
    bufferFastJson(deep, blackhole);
  }

  @Benchmark
  public void deepBufferDslJson(Blackhole blackhole) throws Exception {
    bufferDslJson(deep, blackhole);
  }

  @Benchmark
  public void wideBufferJackson(Blackhole blackhole) throws Exception {
    bufferJackson(wide, blackhole);
  }

  @Benchmark
  public void wideBufferDatabind(Blackhole blackhole) throws Exception {
    bufferDatabind(wide, blackhole);
  }

  @Benchmark
  public void wideBufferFastJson(Blackhole blackhole) throws Exception {
    bufferFastJson(wide, blackhole);
  }

  @Benchmark
  public void wideBufferDslJson(Blackhole blackhole) throws Exception {
    bufferDslJson(wide, blackhole);
  }

  private void bufferJackson(JsonObject jsonObject, Blackhole blackhole) throws Exception {
    blackhole.consume(jacksonCodec.toBuffer(jsonObject));
  }

  private void bufferDatabind(JsonObject jsonObject, Blackhole blackhole) throws Exception {
    blackhole.consume(databindCodec.toBuffer(jsonObject));
  }

  private void bufferFastJson(JsonObject jsonObject, Blackhole blackhole) throws Exception {
    blackhole.consume(fastJsonCodec.toBuffer(jsonObject));
  }

  private void bufferDslJson(JsonObject jsonObject, Blackhole blackhole) throws Exception {
    blackhole.consume(dslJsonCodec.toBuffer(jsonObject));
  }
}
