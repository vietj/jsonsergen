package com.julienviet.jsonsergen.fastjson;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.EncodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.JacksonCodec;
import io.vertx.core.spi.json.JsonCodec;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static io.vertx.core.json.impl.JsonUtil.BASE64_ENCODER;
import static java.time.format.DateTimeFormatter.ISO_INSTANT;

public class FastJsonCodec implements JsonCodec {

  @Override
  public <T> T fromString(String json, Class<T> clazz) throws DecodeException {
    JSONReader reader = JSONReader.of(json);
    reader.next();

    return null;
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
    try (JSONWriter writer = encodeJson(object, pretty)) {
      return writer.toString();
    }
  }

  @Override
  public Buffer toBuffer(Object object, boolean pretty) throws EncodeException {
    try (JSONWriter writer = encodeJson(object, pretty)) {
      ByteBuf bbuf = Unpooled.buffer(writer.size());
      // There is no need to use a try with resources here as jackson
      // is a well-behaved and always calls the closes all streams in the
      // "finally" block bellow.
      try (ByteBufOutputStream out = new ByteBufOutputStream(bbuf)) {
        writer.flushTo(out);
        @SuppressWarnings("deprecation")
        Buffer buff = Buffer.buffer(bbuf);
        return buff;
      } catch (IOException e) {
        throw new EncodeException(e.getMessage(), e);
      }
    }
  }

  private JSONWriter encodeJson(Object object, boolean pretty) {
    JSONWriter jsonWriter;
    if (pretty) {
      jsonWriter = JSONWriter.ofPretty(JSONWriter.ofUTF8());
    } else {
      jsonWriter = JSONWriter.ofUTF8();
    }
    encodeJson(object, jsonWriter);
    return jsonWriter;
  }

  private static void encodeJson(Object json, JSONWriter generator) throws EncodeException {
    if (json instanceof JsonObject) {
      json = ((JsonObject)json).getMap();
    } else if (json instanceof JsonArray) {
      json = ((JsonArray)json).getList();
    }
    if (json instanceof Map) {
      generator.startObject();
      for (Map.Entry<String, ?> e : ((Map<String, ?>)json).entrySet()) {
        generator.writeName(e.getKey());
        generator.writeColon();
        encodeJson(e.getValue(), generator);
      }
      generator.endObject();
    } else if (json instanceof List) {
      generator.startArray();
      boolean first = true;
      for (Object item : (List<?>) json) {
        if (first) {
          first = false;
        } else {
          generator.writeComma();
        }
        encodeJson(item, generator);
      }
      generator.endArray();
    } else if (json instanceof String) {
      generator.writeString((String) json);
    } else if (json instanceof Number) {
      if (json instanceof Short) {
        generator.writeInt16((Short) json);
      } else if (json instanceof Integer) {
        generator.writeInt32((Integer) json);
      } else if (json instanceof Long) {
        generator.writeInt64((Long) json);
      } else if (json instanceof Float) {
        generator.writeFloat((Float) json);
      } else if (json instanceof Double) {
        generator.writeDouble((Double) json);
      } else if (json instanceof Byte) {
        generator.writeInt8((Byte) json);
      } else if (json instanceof BigInteger) {
        generator.writeBigInt((BigInteger) json);
      } else if (json instanceof BigDecimal) {
        generator.writeDecimal((BigDecimal) json);
      } else {
        generator.writeDouble(((Number) json).doubleValue());
      }
    } else if (json instanceof Boolean) {
      generator.writeBool((Boolean)json);
    } else if (json instanceof Instant) {
      // RFC-7493
      generator.writeString((ISO_INSTANT.format((Instant)json)));
    } else if (json instanceof byte[]) {
      // RFC-7493
      generator.writeString(BASE64_ENCODER.encodeToString((byte[]) json));
    } else if (json instanceof Buffer) {
      // RFC-7493
      generator.writeString(BASE64_ENCODER.encodeToString(((Buffer) json).getBytes()));
    } else if (json instanceof Enum) {
      // vert.x extra (non standard but allowed conversion)
      generator.writeString(((Enum<?>) json).name());
    } else if (json == null) {
      generator.writeNull();
    } else {
      throw new EncodeException("Mapping " + json.getClass().getName() + "  is not available without Jackson Databind on the classpath");
    }
  }
}
