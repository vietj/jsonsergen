package com.julienviet.jsonsergen.dsljson;

import com.julienviet.jsonsergen.IndentableWriter;
import com.julienviet.jsonsergen.WriterPlugin;

public class DslJsonWriterPlugin implements WriterPlugin {

  private static final String visibility = "public";

  @Override
  public String scope() {
    return "DslJson";
  }

  @Override
  public void beginClass(IndentableWriter writer, String fqn) {
    writer.print("private static final com.dslplatform.json.DslJson<Object> DSL_JSON = new com.dslplatform.json.DslJson<>();\n");
    writer.print("private static final io.netty.util.concurrent.FastThreadLocal<com.dslplatform.json.JsonWriter> WRITER_LOCAL = new io.netty.util.concurrent.FastThreadLocal<>();\n");
    writer.print("private static com.dslplatform.json.JsonWriter getWriter() {\n");
    writer.print("  com.dslplatform.json.JsonWriter writer = WRITER_LOCAL.get();\n");
    writer.print("  if (writer == null) {\n");
    writer.print("    writer = DSL_JSON.newWriter();\n");
    writer.print("    WRITER_LOCAL.set(writer);\n");
    writer.print("  }\n");
    writer.print("  return writer;\n");
    writer.print("}\n");
    writer.print("public static io.vertx.core.buffer.Buffer toJsonBuffer(" + fqn + " obj) {\n");
    writer.print("  com.dslplatform.json.JsonWriter writer = getWriter();\n");
    writer.print("  return toJsonBuffer(obj, writer);\n");
    writer.print("}\n");
    writer.print("public static io.vertx.core.buffer.Buffer toJsonBuffer(" + fqn + "[] obj) {\n");
    writer.print("  com.dslplatform.json.JsonWriter writer = getWriter();\n");
    writer.print("  " + BEGIN_ARRAY + ";\n");
    writer.print("  for (int i = 0;i < obj.length;i++) {\n");
    writer.print("    " + fqn + " elt = obj[i];\n");
    writer.print("    if (i > 0) {\n");
    writer.print("  " + COMMA + ";\n");
    writer.print("    }\n");
    writer.print("    if (elt != null) {\n");
    writer.print("      toJson(elt, writer);\n");
    writer.print("    } else {\n");
    writer.print("      writer.writeNull();\n");
    writer.print("    }\n");
    writer.print("  }\n");
    writer.print("  " + END_ARRAY + ";\n");
    writer.print("  try {\n");
    writer.print("    return io.vertx.core.buffer.Buffer.buffer(writer.toByteArray());\n");
    writer.print("  } finally {\n");
    writer.print("    writer.reset();\n");
    writer.print("  }\n");
    writer.print("}\n");
    writer.print("public static io.vertx.core.buffer.Buffer toJsonBuffer(java.util.List<" + fqn + "> obj) {\n");
    writer.print("  com.dslplatform.json.JsonWriter writer = getWriter();\n");
    writer.print("  " + BEGIN_ARRAY +";\n");
    writer.print("  int len = obj.size();\n");
    writer.print("  for (int i = 0;i < len;i++) {\n");
    writer.print("    " + fqn + " elt = obj.get(i);\n");
    writer.print("    if (i > 0) {\n");
    writer.print("      " + COMMA + ";\n");
    writer.print("    }\n");
    writer.print("    if (elt != null) {\n");
    writer.print("      toJson(elt, writer);\n");
    writer.print("    } else {\n");
    writer.print("      writer.writeNull();\n");
    writer.print("    }\n");
    writer.print("  }\n");
    writer.print("  " + END_ARRAY + ";\n");
    writer.print("  try {\n");
    writer.print("    return io.vertx.core.buffer.Buffer.buffer(writer.toByteArray());\n");
    writer.print("  } finally {\n");
    writer.print("    writer.reset();\n");
    writer.print("  }\n");
    writer.print("}\n");
    writer.print("public static io.vertx.core.buffer.Buffer toJsonBuffer("  + fqn + " obj, com.dslplatform.json.JsonWriter writer) {\n");
    writer.print("  toJson(obj, writer);\n");
    writer.print("  try {\n");
    writer.print("    return io.vertx.core.buffer.Buffer.buffer(writer.toByteArray());\n");
    writer.print("  } finally {\n");
    writer.print("    writer.reset();\n");
    writer.print("  }\n");
    writer.print("}\n");
  }

  @Override
  public void beginObject(IndentableWriter writer, String fqn) {
    writer.print(visibility + " static void toJson(" + fqn + " obj, com.dslplatform.json.JsonWriter writer) {\n");
  }

  @Override
  public void endObject(IndentableWriter writer, String fqn) {
    writer.print("}\n");
  }

  private static final String BEGIN_ARRAY = "writer.writeByte(com.dslplatform.json.JsonWriter.ARRAY_START)";
  private static final String END_ARRAY = "writer.writeByte(com.dslplatform.json.JsonWriter.ARRAY_END)";
  private static final String COMMA = "writer.writeByte(com.dslplatform.json.JsonWriter.COMMA)";

  @Override
  public void genWriteBeginArray(IndentableWriter writer) {
    writer.print(BEGIN_ARRAY + ";\n");
  }

  @Override
  public void genWriteArraySeparator(IndentableWriter writer) {
    writer.print(COMMA + ";\n");
  }

  @Override
  public void genWriteObjectSeparator(IndentableWriter writer) {
    writer.print(COMMA + ";\n");
  }

  @Override
  public void genWriteEndArray(IndentableWriter writer) {
    writer.print(END_ARRAY + ";\n");
  }

  @Override
  public void genWriteBeginObject(IndentableWriter writer) {
    writer.print("writer.writeByte(com.dslplatform.json.JsonWriter.OBJECT_START);\n");
  }

  @Override
  public void genWriteEndObject(IndentableWriter writer) {
    writer.print("writer.writeByte(com.dslplatform.json.JsonWriter.OBJECT_END);\n");
  }

  @Override
  public void genWriteFieldName(IndentableWriter writer, String expression) {
    writer.print("writer.writeString(" + expression + ");\n");
    writer.print("writer.writeByte(com.dslplatform.json.JsonWriter.SEMI);\n");
  }

  @Override
  public void genWriteNumber(IndentableWriter writer, String expression, String type) {
    writer.print("com.dslplatform.json.NumberConverter.serialize(" + expression + ", writer);\n");
  }

  @Override
  public void genWriteBoolean(IndentableWriter writer, String expression) {
    writer.print("writer.writeBoolean(" + expression + ");\n");
  }

  @Override
  public void genWriteString(IndentableWriter writer, String expression) {
    writer.print("writer.writeString(" + expression + ");\n");
  }

  @Override
  public void genWriteNull(IndentableWriter writer) {
    writer.print("writer.writeNull();\n");
  }

  @Override
  public void genWriteJsonSerGen(IndentableWriter writer, String fqn, String expression) {
    writer.print(fqn + ".toJson(" + expression + ", writer);\n");
  }

  @Override
  public void genWriteJson(IndentableWriter writer, String expression) {
    writer.print("io.vertx.core.json.jackson.JacksonCodec.encodeJson(" + expression + ", writer);\n");
  }
}
