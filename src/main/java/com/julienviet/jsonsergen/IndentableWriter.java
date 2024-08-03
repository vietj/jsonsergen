package com.julienviet.jsonsergen;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class IndentableWriter extends PrintWriter {
  private final InternalWriter indentable;

  public IndentableWriter(Writer out) {
    super(new InternalWriter(out));
    indentable = (InternalWriter) super.out;
  }

  void indent() {
    indentable.indent += 2;
  }

  void unindent() {
    indentable.indent -= 2;
    if (indentable.indent < 0) {
      throw new IllegalStateException();
    }
  }

  static class InternalWriter extends Writer {
    int indent = 0;
    boolean written;
    private Writer actual;

    public InternalWriter(Writer actual) {
      this.actual = actual;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
      while (len-- > 0) {
        char c = cbuf[off++];
        if (c == '\n') {
          written = false;
        } else {
          if (!written) {
            written = true;
            for (int i = 0; i < indent; i++) {
              actual.write(' ');
            }
          }
        }
        actual.write(c);
      }
    }

    @Override
    public void flush() throws IOException {
      actual.flush();
    }

    @Override
    public void close() throws IOException {
      actual.close();
    }
  }
}
