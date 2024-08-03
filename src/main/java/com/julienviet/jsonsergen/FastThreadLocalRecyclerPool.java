package com.julienviet.jsonsergen;

import com.fasterxml.jackson.core.util.BufferRecycler;
import com.fasterxml.jackson.core.util.RecyclerPool;
import io.netty.util.concurrent.FastThreadLocal;

public class FastThreadLocalRecyclerPool implements RecyclerPool<BufferRecycler> {

  private final static FastThreadLocal<BufferRecycler> _recyclerRef = new FastThreadLocal<>();

  public BufferRecycler acquireAndLinkPooled() {
    return acquirePooled();
  }

  @Override
  public BufferRecycler acquirePooled() {
    BufferRecycler br = _recyclerRef.get();

    if (br == null) {
      br = new BufferRecycler();
      _recyclerRef.set(br);
    }

    return br;
  }

  @Override
  public void releasePooled(BufferRecycler pooled) {
  }
}
