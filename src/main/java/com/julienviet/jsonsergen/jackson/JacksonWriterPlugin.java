package com.julienviet.jsonsergen.jackson;

import com.julienviet.jsonsergen.IndentableWriter;
import com.julienviet.jsonsergen.WriterPlugin;

public class JacksonWriterPlugin implements WriterPlugin {

  private static final String visibility = "public";

  @Override
  public String scope() {
    return "Jackson";
  }

  @Override
  public void beginClass(IndentableWriter writer, String fqn) {
    writer.print("private static final com.fasterxml.jackson.core.JsonFactory JSON_FACTORY = com.fasterxml.jackson.core.JsonFactory.builder().recyclerPool(new com.julienviet.jsonsergen.jackson.FastThreadLocalRecyclerPool()).build();\n");
    writer.print("private static com.fasterxml.jackson.core.JsonGenerator createGenerator(java.io.OutputStream out) throws java.io.IOException {\n");
    writer.print("  return JSON_FACTORY.createGenerator(out);\n");
    writer.print("}\n");
    writer.print("" + visibility + " static <T> io.vertx.core.buffer.Buffer toJsonBuffer(T obj, java.util.function.BiConsumer<T, com.fasterxml.jackson.core.JsonGenerator> cons) {\n");
    writer.print("  com.fasterxml.jackson.core.util.BufferRecycler br = JSON_FACTORY._getBufferRecycler();\n");
    writer.print("  try (com.fasterxml.jackson.core.util.ByteArrayBuilder bb = new com.fasterxml.jackson.core.util.ByteArrayBuilder(br)) {\n");
    writer.print("    com.fasterxml.jackson.core.JsonGenerator generator = createGenerator(bb);\n");
    writer.print("    cons.accept(obj, generator);\n");
    writer.print("    generator.close();\n");
    writer.print("    byte[] result = bb.toByteArray();\n");
    writer.print("    bb.release();\n");
    writer.print("    return io.vertx.core.buffer.Buffer.buffer(result);\n");
    writer.print("  } catch (java.io.IOException e) {\n");
    writer.print("    throw new io.vertx.core.json.EncodeException(e.getMessage(), e);\n");
    writer.print("  } finally {\n");
    writer.print("    br.releaseToPool();\n");
    writer.print("  }\n");
    writer.print("}\n");
    writer.print("\n");
    writer.print(visibility + " static io.vertx.core.buffer.Buffer toJsonBuffer(" + fqn + " obj) {\n");
    writer.print("  return toJsonBuffer(obj, (o, gen) -> toJson2(o, gen));\n");
    writer.print("}\n");
    writer.print("\n");
    writer.print(visibility + " static io.vertx.core.buffer.Buffer toJsonBuffer(" + fqn + "[] list) {\n");
    writer.print("  return toJsonBuffer(list, (o, gen) -> toJson2(o, gen));\n");
    writer.print("}\n");
    writer.print("\n");
    writer.print(visibility + " static io.vertx.core.buffer.Buffer toJsonBuffer(Iterable<" + fqn + "> list) {\n");
    writer.print("  return toJsonBuffer(list, (o, gen) -> toJson2(o, gen));\n");
    writer.print("}\n");
    writer.print("\n");
    writer.print(visibility + " static void toJson(" + fqn + "[] list, com.fasterxml.jackson.core.JsonGenerator generator) throws java.io.IOException {\n");
    writer.print("  generator.writeStartArray();\n");
    writer.print("  for (" + fqn + " obj : list) {\n");
    writer.print("    toJson(obj, generator);\n");
    writer.print("  }\n");
    writer.print("  generator.writeEndArray();\n");
    writer.print("}\n");
    writer.print("\n");
    writer.print(visibility + " static void toJson(Iterable<" + fqn + "> list, com.fasterxml.jackson.core.JsonGenerator generator) throws java.io.IOException {\n");
    writer.print("  generator.writeStartArray();\n");
    writer.print("  for (" + fqn + " obj : list) {\n");
    writer.print("    toJson(obj, generator);\n");
    writer.print("  }\n");
    writer.print("  generator.writeEndArray();\n");
    writer.print("}\n");
    writer.print("\n");
    writer.print(visibility + " static void toJson2(" + fqn + " obj, com.fasterxml.jackson.core.JsonGenerator generator) {\n");
    writer.print("  try {\n");
    writer.print("    toJson(obj, generator);\n");
    writer.print("  }\n");
    writer.print("  catch(java.io.IOException e) {\n");
    writer.print("    throw new io.vertx.core.json.EncodeException(e.getMessage(), e);\n");
    writer.print("  }\n");
    writer.print("}\n");
    writer.print("\n");
    writer.print(visibility + " static void toJson2(" + fqn + "[] list, com.fasterxml.jackson.core.JsonGenerator generator) {\n");
    writer.print("  try {\n");
    writer.print("    toJson(list, generator);\n");
    writer.print("  }\n");
    writer.print("  catch(java.io.IOException e) {\n");
    writer.print("    throw new io.vertx.core.json.EncodeException(e.getMessage(), e);\n");
    writer.print("  }\n");
    writer.print("}\n");
    writer.print("\n");
    writer.print(visibility + " static void toJson2(Iterable<" + fqn + "> list, com.fasterxml.jackson.core.JsonGenerator generator) {\n");
    writer.print("  try {\n");
    writer.print("    toJson(list, generator);\n");
    writer.print("  }\n");
    writer.print("  catch(java.io.IOException e) {\n");
    writer.print("    throw new io.vertx.core.json.EncodeException(e.getMessage(), e);\n");
    writer.print("  }\n");
    writer.print("}\n");
  }

  @Override
  public void beginObject(IndentableWriter writer, String fqn) {
    writer.print(visibility + " static void toJson(" + fqn + " obj, com.fasterxml.jackson.core.JsonGenerator generator) throws java.io.IOException {\n");
  }

  @Override
  public void endObject(IndentableWriter writer, String fqn) {
    writer.print("}\n");
  }

  @Override
  public void genWriteBeginArray(IndentableWriter writer) {
    writer.print("generator.writeStartArray();\n");
  }

  @Override
  public void genWriteArraySeparator(IndentableWriter writer) {
    // NOOP
  }

  @Override
  public void genWriteObjectSeparator(IndentableWriter writer) {
    // NOOP
  }

  @Override
  public void genWriteEndArray(IndentableWriter writer) {
    writer.print("generator.writeEndArray();\n");
  }

  @Override
  public void genWriteBeginObject(IndentableWriter writer) {
    writer.print("generator.writeStartObject();\n");
  }

  @Override
  public void genWriteEndObject(IndentableWriter writer) {
    writer.print("generator.writeEndObject();\n");
  }

  @Override
  public void genWriteFieldName(IndentableWriter writer, String expression) {
    writer.print("generator.writeFieldName(" + expression + ");\n");
  }

  @Override
  public void genWriteNumber(IndentableWriter writer, String expression, String type) {
    writer.print("generator.writeNumber(" + expression + ");\n");
  }

  @Override
  public void genWriteBoolean(IndentableWriter writer, String expression) {
    writer.print("generator.writeBoolean(" + expression + ");\n");
  }

  @Override
  public void genWriteString(IndentableWriter writer, String expression) {
    writer.print("generator.writeString(" + expression + ");\n");
  }

  @Override
  public void genWriteNull(IndentableWriter writer) {
    writer.print("generator.writeNull();\n");
  }

  @Override
  public void genWriteJsonSerGen(IndentableWriter writer, String fqn, String expression) {
    writer.print(fqn + ".toJson(" + expression + ", generator);\n");
  }

  @Override
  public void genWriteJson(IndentableWriter writer, String expression) {
    writer.print("io.vertx.core.json.jackson.JacksonCodec.encodeJson(" + expression + ", generator);\n");
  }
}
