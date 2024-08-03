package com.julienviet.jsonsergen.fastjson;

import com.julienviet.jsonsergen.IndentableWriter;
import com.julienviet.jsonsergen.WriterPlugin;

public class FastJsonWriterPlugin implements WriterPlugin {

  private static final String visibility = "public";

  @Override
  public String scope() {
    return "FastJson";
  }

  @Override
  public void beginClass(IndentableWriter writer, String fqn) {
    writer.print("private static final java.io.Writer NULL_WRITER = new  java.io.Writer() {\n");
    writer.print("  public void write(char[] cbuf, int off, int len) {}\n");
    writer.print("  public void flush() {}\n");
    writer.print("  public void close() {}\n");
    writer.print("};\n");
    writer.print("private static final io.netty.util.concurrent.FastThreadLocal<com.alibaba.fastjson2.JSONWriter> WRITER_LOCAL = new io.netty.util.concurrent.FastThreadLocal<>();\n");
    writer.print("private static com.alibaba.fastjson2.JSONWriter getWriter() {\n");
    writer.print("  com.alibaba.fastjson2.JSONWriter writer = WRITER_LOCAL.get();\n");
    writer.print("  if (writer == null) {\n");
    writer.print("    writer = com.alibaba.fastjson2.JSONWriter.ofUTF8();\n");
    writer.print("    WRITER_LOCAL.set(writer);\n");
    writer.print("  }\n");
    writer.print("  return writer;\n");
    writer.print("}\n");
    writer.print("public static io.vertx.core.buffer.Buffer toJsonBuffer(" + fqn + " obj) {\n");
    writer.print("  com.alibaba.fastjson2.JSONWriter writer = getWriter();\n");
    writer.print("  return toJsonBuffer(obj, writer);\n");
    writer.print("}\n");
    writer.print("public static io.vertx.core.buffer.Buffer toJsonBuffer(" + fqn + "[] obj) {\n");
    writer.print("  com.alibaba.fastjson2.JSONWriter writer = getWriter();\n");
    writer.print("  writer.startArray();\n");
    writer.print("  for (int i = 0;i < obj.length;i++) {\n");
    writer.print("    " + fqn + " elt = obj[i];\n");
    writer.print("    if (i > 0) {\n");
    writer.print("      writer.writeComma();\n");
    writer.print("    }\n");
    writer.print("    if (elt != null) {\n");
    writer.print("      toJson(elt, writer);\n");
    writer.print("    } else {\n");
    writer.print("      writer.writeNull();\n");
    writer.print("    }\n");
    writer.print("  }\n");
    writer.print("  writer.endArray();\n");
    writer.print("  try {\n");
    writer.print("    return io.vertx.core.buffer.Buffer.buffer(writer.getBytes());\n");
    writer.print("  } finally {\n");
    writer.print("    writer.flushTo(NULL_WRITER);\n");
    writer.print("  }\n");
    writer.print("}\n");
    writer.print("public static io.vertx.core.buffer.Buffer toJsonBuffer(java.util.List<" + fqn + "> obj) {\n");
    writer.print("  com.alibaba.fastjson2.JSONWriter writer = getWriter();\n");
    writer.print("  writer.startArray();\n");
    writer.print("  int len = obj.size();\n");
    writer.print("  for (int i = 0;i < len;i++) {\n");
    writer.print("    " + fqn + " elt = obj.get(i);\n");
    writer.print("    if (i > 0) {\n");
    writer.print("      writer.writeComma();\n");
    writer.print("    }\n");
    writer.print("    if (elt != null) {\n");
    writer.print("      toJson(elt, writer);\n");
    writer.print("    } else {\n");
    writer.print("      writer.writeNull();\n");
    writer.print("    }\n");
    writer.print("  }\n");
    writer.print("  writer.endArray();\n");
    writer.print("  try {\n");
    writer.print("    return io.vertx.core.buffer.Buffer.buffer(writer.getBytes());\n");
    writer.print("  } finally {\n");
    writer.print("    writer.flushTo(NULL_WRITER);\n");
    writer.print("  }\n");
    writer.print("}\n");
    writer.print("public static io.vertx.core.buffer.Buffer toJsonBuffer("  + fqn + " obj, com.alibaba.fastjson2.JSONWriter generator) {\n");
    writer.print("  toJson(obj, generator);\n");
    writer.print("  try {\n");
    writer.print("    return io.vertx.core.buffer.Buffer.buffer(generator.getBytes());\n");
    writer.print("  } finally {\n");
    writer.print("    generator.flushTo(FastJson.NULL_WRITER);\n");
    writer.print("  }\n");
    writer.print("}\n");
  }

  @Override
  public void beginObject(IndentableWriter writer, String fqn) {
    writer.print(visibility + " static void toJson(" + fqn + " obj, com.alibaba.fastjson2.JSONWriter generator) {\n");
  }

  @Override
  public void endObject(IndentableWriter writer, String fqn) {
    writer.print("}\n");
  }

  @Override
  public void genWriteObjectSeparator(IndentableWriter writer) {
  }

  @Override
  public void genWriteArraySeparator(IndentableWriter writer) {
    writer.print("generator.writeComma();\n");
  }

  @Override
  public void genWriteBeginArray(IndentableWriter writer) {
    writer.print("generator.startArray();\n");
  }

  @Override
  public void genWriteEndArray(IndentableWriter writer) {
    writer.print("generator.endArray();\n");
  }

  @Override
  public void genWriteBeginObject(IndentableWriter writer) {
    writer.print("generator.startObject();\n");
  }

  @Override
  public void genWriteEndObject(IndentableWriter writer) {
    writer.print("generator.endObject();\n");
  }

  @Override
  public void genWriteFieldName(IndentableWriter writer, String expression) {
    writer.print("generator.writeName(" + expression + ");\n");
    writer.print("generator.writeColon();\n");
  }

  @Override
  public void genWriteNumber(IndentableWriter writer, String expression, String type) {
    switch (type) {
      case "java.lang.Byte":
      case "byte":
        writer.print("generator.writeInt8(" + expression + ");\n");
        break;
      case "java.lang.Short":
      case "short":
        writer.print("generator.writeInt16(" + expression + ");\n");
        break;
      case "java.lang.Integer":
      case "int":
        writer.print("generator.writeInt32(" + expression + ");\n");
        break;
      case "java.lang.Long":
      case "long":
        writer.print("generator.writeInt64(" + expression + ");\n");
        break;
      case "java.lang.Float":
      case "float":
        writer.print("generator.writeFloat(" + expression + ");\n");
        break;
      case "java.lang.Double":
      case "double":
        writer.print("generator.writeDouble(" + expression + ");\n");
        break;
      default:
        throw new UnsupportedOperationException();
    }
  }

  @Override
  public void genWriteBoolean(IndentableWriter writer, String expression) {
    writer.print("generator.writeBool(" + expression + ");\n");
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
    writer.print("com.julienviet.jsonsergen.fastjson.FastJsonCodec.encodeJson(" + expression + ", generator);\n");
  }
}
