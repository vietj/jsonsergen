package com.julienviet.jsonsergen;

import io.vertx.codegen.format.Case;
import io.vertx.codegen.format.LowerCamelCase;

/**
 * Annotate a Vert.x json object for json generation.
 */
public @interface JsonSerGen {

  /**
   * @return the generated backend(s), the order matters, the first backend will be the default backend.
   */
  Backend[] backends();

  Class<? extends Case> format() default LowerCamelCase.class;

}
