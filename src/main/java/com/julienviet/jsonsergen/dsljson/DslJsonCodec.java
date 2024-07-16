package com.julienviet.jsonsergen.dsljson;

import com.alibaba.fastjson2.JSONWriter;
import com.dslplatform.json.BoolConverter;
import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.NumberConverter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.EncodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.JacksonCodec;
import io.vertx.core.spi.json.JsonCodec;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class DslJsonCodec implements JsonCodec {

  private DslJson<Object> dslJson = new DslJson<>();
  private JsonWriter writer = dslJson.newWriter();

  @Override
  public <T> T fromString(String json, Class<T> clazz) throws DecodeException {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> T fromBuffer(Buffer json, Class<T> clazz) throws DecodeException {
    // Temp
    return new JacksonCodec().fromBuffer(json, clazz);
  }

  @Override
  public <T> T fromValue(Object json, Class<T> toValueType) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString(Object object, boolean pretty) throws EncodeException {
    JsonWriter writer = encodeJson(object, pretty);
    return writer.toString();
  }

  @Override
  public Buffer toBuffer(Object object, boolean pretty) throws EncodeException {
    JsonWriter writer = encodeJson(object, pretty);
    int len = writer.size();
    ByteBuf bbuff = Unpooled.copiedBuffer(writer.getByteBuffer(), 0, len);
    return Buffer.buffer(bbuff);
  }


  private JsonWriter encodeJson(Object object, boolean pretty) {
    if (pretty) {
      throw new UnsupportedOperationException();
    }
    writer.reset();
    encodeJson(object, writer);
    return writer;
  }

  private static void encodeJson(Object json, JsonWriter generator) throws EncodeException {
    if (json instanceof JsonObject) {
      json = ((JsonObject)json).getMap();
    } else if (json instanceof JsonArray) {
      json = ((JsonArray)json).getList();
    }
    if (json instanceof Map) {
      generator.writeByte(JsonWriter.OBJECT_START);
      boolean first = true;
      for (Map.Entry<String, ?> e : ((Map<String, ?>)json).entrySet()) {
        if (first) {
          first = false;
        } else {
          generator.writeByte(JsonWriter.COMMA);
        }
        generator.writeString(e.getKey());
        generator.writeByte(JsonWriter.SEMI);
        encodeJson(e.getValue(), generator);
      }
      generator.writeByte(JsonWriter.OBJECT_END);;
    } else if (json instanceof List) {
      generator.writeByte(JsonWriter.ARRAY_START);;
      boolean first = true;
      for (Object item : (List<?>) json) {
        if (first) {
          first = false;
        } else {
          generator.writeByte(JsonWriter.COMMA);
        }
        encodeJson(item, generator);
      }
      generator.writeByte(JsonWriter.ARRAY_END);;
    } else if (json instanceof String) {
      generator.writeString((String) json);
    } else if (json instanceof Number) {
      if (json instanceof Short) {
        NumberConverter.serialize((Short) json, generator);
      } else if (json instanceof Integer) {
        NumberConverter.serialize((Integer) json, generator);
      } else if (json instanceof Long) {
        NumberConverter.serialize((Long) json, generator);
      } else if (json instanceof Float) {
        NumberConverter.serialize((Float) json, generator);
      } else if (json instanceof Double) {
        NumberConverter.serialize((Double) json, generator);
      } else if (json instanceof Byte) {
        NumberConverter.serialize((Byte) json, generator);
      } else if (json instanceof BigInteger) {
        NumberConverter.serialize((BigInteger) json, generator);
      } else if (json instanceof BigDecimal) {
        NumberConverter.serialize((BigDecimal) json, generator);
      } else {
        NumberConverter.serialize(((Number) json).doubleValue(), generator);
      }
    } else if (json instanceof Boolean) {
      BoolConverter.serializeNullable((Boolean) json, generator);
    } else if (json instanceof Instant) {
      // RFC-7493
      throw new UnsupportedOperationException();
    } else if (json instanceof byte[]) {
      // RFC-7493
      throw new UnsupportedOperationException();
    } else if (json instanceof Buffer) {
      // RFC-7493
      throw new UnsupportedOperationException();
    } else if (json instanceof Enum) {
      // vert.x extra (non standard but allowed conversion)
      throw new UnsupportedOperationException();
    } else if (json == null) {
      generator.writeNull();
    } else {
      throw new EncodeException("Mapping " + json.getClass().getName() + "  is not available without Jackson Databind on the classpath");
    }
  }
}
