package com.julienviet.jsonsergen;

public interface WriterPlugin {

  String scope();

  void beginClass(IndentableWriter writer, String fqn);

  void beginObject(IndentableWriter writer, String fqn);

  void endObject(IndentableWriter writer, String fqn);

  void genWriteBeginArray(IndentableWriter writer);

  void genWriteArraySeparator(IndentableWriter writer);

  void genWriteObjectSeparator(IndentableWriter writer);

  void genWriteEndArray(IndentableWriter writer);

  void genWriteBeginObject(IndentableWriter writer);

  void genWriteEndObject(IndentableWriter writer);

  void genWriteFieldName(IndentableWriter writer, String expression);

  void genWriteNumber(IndentableWriter writer, String expression, String type);

  void genWriteBoolean(IndentableWriter writer, String expression);

  void genWriteString(IndentableWriter writer, String expression);

  void genWriteNull(IndentableWriter writer);

  void genWriteJsonSerGen(IndentableWriter writer, String fqn, String expression);

  void genWriteJson(IndentableWriter writer, String expression);

}
