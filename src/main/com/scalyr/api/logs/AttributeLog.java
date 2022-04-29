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

/**
 * An interface for key-value logging. Example calls:
 * <pre>
 *   log.info(ev("cmd", "send", "details", "Sent batch", "elapsedMs", elapsedMs).add(batch::annot));
 *   log.error(ev("cmd", "send", "details", "Call failed"), e));
 *   log.log(Severity.error, ev("cmd", "send", "details", "Network error"), e));
 *   log.carp(warnLimit, ev("cmd", "send", "details", "Network error"), e));
 * </pre>
 */
public interface AttributeLog {

  /** Write a log event at `debug` severity. */
  void debug(EventAttributes attrs);

  /** Write a log event at `info` severity. */
  void info(EventAttributes attrs);

  /** Write a log event at `warn` severity. */
  void warn(EventAttributes attrs, Throwable e);

  /** Write a log event at `error` severity. */
  void error(EventAttributes attrs, Throwable e);

  /** Write a log event at given severity. */
  void log(Severity severity, EventAttributes attrs, Throwable e);

  /** Call `warn` if limit function allows, otherwise call `error`. */
  default void carp(RateLimiter limiter, EventAttributes attrs, Throwable e) {
    if (limiter.tryAcquire()) {
      warn(attrs, e);
    } else {
      error(attrs, e);
    }
  }

  // --- Same w/ no exception ---

  /** Write a log event at `warn` severity. */
  default void warn(EventAttributes attrs) {
    warn(attrs, null);
  }

  /** Write a log event at `error` severity. */
  default void error(EventAttributes attrs) {
    error(attrs, null);
  }

  /** Write a log event at given severity. */
  default void log(Severity severity, EventAttributes attrs) {
    log(severity, attrs, null);
  }

  /** Call `warn` if limit function allows, otherwise call `error`. */
  default void carp(RateLimiter limiter, EventAttributes attrs) {
    carp(limiter, attrs, null);
  }

  interface RateLimiter {
    boolean tryAcquire();
  }
}
