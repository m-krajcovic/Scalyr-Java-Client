/*
 * Scalyr client library
 * Copyright 2012 Scalyr, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scalyr.api.logs;

import javax.annotation.Nullable;

/**
 * A basic {@link AttributeLog} implementation that routes directly to an event sink.
 *
 * This class is not normally used directly, but is instead used by a `getLogger` factory method.
 */
public class AttributeLogImpl implements AttributeLog {

  private final Severity minimum;
  private final LogSink sink;

  public AttributeLogImpl(Severity minimum, LogSink sink) {
    this.minimum = minimum;
    this.sink = sink;
  }

  /** Write a log event at `debug` severity. */
  @Override
  public void debug(EventAttributes attrs) {
    if (Severity.fine.ordinal() >= minimum.ordinal())  // 2
      sink.accept(Severity.fine, attrs, null);
  }

  /** Write a log event at `info` severity. */
  @Override
  public void info(EventAttributes attrs) {
    if (Severity.info.ordinal() >= minimum.ordinal())  // 3
      sink.accept(Severity.info, attrs, null);
  }

  /** Write a log event at `warn` severity. */
  @Override
  public void warn(EventAttributes attrs, @Nullable Throwable e) {
    if (Severity.warning.ordinal() >= minimum.ordinal())  // 4
      sink.accept(Severity.warning, attrs, e);
  }

  /** Write a log event at `error` severity. */
  @Override
  public void error(EventAttributes attrs, @Nullable Throwable e) {
    if (Severity.error.ordinal() >= minimum.ordinal())  // 5
      sink.accept(Severity.error, attrs, e);
  }

  /** Write a log event at given severity. */
  @Override
  public void log(Severity severity, EventAttributes attrs, @Nullable Throwable e) {
    if (severity.ordinal() >= minimum.ordinal())
      sink.accept(severity, attrs, e);
  }

  /** Call `warn` if limit function allows, otherwise call `error`. */
  @Override
  public void carp(RateLimiter limiter, EventAttributes attrs, @Nullable Throwable e) {
    if (limiter.tryAcquire()) {
      warn(attrs, e);
    } else {
      error(attrs, e);
    }
  }
}
