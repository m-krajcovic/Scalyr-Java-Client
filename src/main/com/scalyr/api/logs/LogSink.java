package com.scalyr.api.logs;

import javax.annotation.Nullable;

public interface LogSink {
  void accept(Severity severity, EventAttributes attrs, @Nullable Throwable e);
}
