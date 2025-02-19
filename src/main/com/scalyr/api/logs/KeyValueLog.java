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

import java.util.function.*;

/**
 * A key-value log interface that uses call chaining. Example calls:
 *
 * <pre>
 *   log.ev().add("cmd", "send", "details", "Sent batch", "elapsedMs", elapsedMs).add(batch::annot).info();
 *   log.ev().add("cmd", "send", "details", "Call failed").err(e).error();
 *   log.ev().add("cmd", "send", "details", "Network error").err(e).carp(warnLimit);
 *   log.limit(statsLimit, () -> log.ev().add("cmd", "send", "details", "Stats").add(stats::annot).info());
 * </pre>
 *
 * @deprecated Replaced by {@link AttributeLog} interface.
 */
@Deprecated
public interface KeyValueLog<T extends EventLog.AbstractBuilder<T>> {

  /** Begin a log message. */
  T ev();

  /** Evaluate rate-limiter and call log function if rate limiter returns `true`. */
  default void limit(RateLimiter limiter, Runnable logFn) {
    if (limiter.tryAcquire()) {
      logFn.run();
    }
  }

  /**
   * A functional log interface. Use this IN FAVOR OF the class-based interface.
   * It allows called-by tracking. Example use: `log.ev().add(obj::annot).info()`.
   */
  interface AnnotFn extends Function<EventAttributes, EventAttributes> {}

  /**
   * A class-based log interface. Use this when it is not possible to use the functional interface.
   * It does not allow called-by tracking. Example use: `log.ev().incl(obj).info()`.
   */
  interface Annot {
    AnnotFn annotFn();
  }

  interface RateLimiter {
    boolean tryAcquire();
  }
}
