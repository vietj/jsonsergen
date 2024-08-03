package com.julienviet.jsonsergen;

/**
 * Annotate a Vert.x json object for json generation.
 */
public @interface JsonSerGen {

  /**
   * @return the generated backend(s), the order matters, the first backend will be the default backend.
   */
  Backend[] backends();
}
