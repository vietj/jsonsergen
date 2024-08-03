package com.julienviet.jsonsergen;

import com.julienviet.jsonsergen.dsljson.DslJsonWriterPlugin;
import com.julienviet.jsonsergen.fastjson.FastJsonWriterPlugin;
import com.julienviet.jsonsergen.jackson.JacksonWriterPlugin;

/**
 * The backend generating the JSON buffer.
 */
public enum Backend {

  /**
   * Jackson.
   */
  JACKSON(new JacksonWriterPlugin()),

  /**
   * FastJson
   */
  FAST_JSON(new FastJsonWriterPlugin()),

  /**
   * FastJson
   */
  DSL_JSON(new DslJsonWriterPlugin());

  final WriterPlugin plugin;

  Backend(WriterPlugin plugin) {
    this.plugin = plugin;
  }
}
