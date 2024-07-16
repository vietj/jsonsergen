package com.julienviet.jsonsergen.fastjson;

import io.vertx.core.spi.JsonFactory;
import io.vertx.core.spi.json.JsonCodec;

public class FastJsonFactory implements JsonFactory {

  @Override
  public JsonCodec codec() {
    return new FastJsonCodec();
  }
}
