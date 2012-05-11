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

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import com.scalyr.api.internal.Logging;
import com.scalyr.api.internal.ScalyrUtil;

/**
 * Interface for recording events in the Scalyr Logs service.
 */
public class Events {
  private static AtomicReference<EventUploader> uploaderInstance = new AtomicReference<EventUploader>();
  
  /**
   * Initialize the Events reporting system. If this method has already been called, subsequent calls
   * are ignored.
   * 
   * @param apiToken The API authorization token to use when communicating with the Scalyr Logs server.
   * @param memoryLimit If not null, then we limit memory usage (for buffering events to be uploaded)
   *     to approximately this many bytes.
   */
  public static synchronized void init(String apiToken, Integer memoryLimit) {
    init(apiToken, memoryLimit, null);
  }
  
  /**
   * Variant which allows specifying a nonstandard server to send events to.
   * 
   * @param apiToken The API authorization token to use when communicating with the Scalyr Logs server.
   * @param memoryLimit If not null, then we limit memory usage (for buffering events to be uploaded)
   *     to approximately this many bytes.
   * @param serverAddress URL on which we invoke the Scalyr Logs API. If null, we use the standard
   *     production server (currently https://log.scalyr.com).
   */
  public static synchronized void init(String apiToken, Integer memoryLimit, String serverAddress) {
    init(apiToken, memoryLimit, serverAddress, null);
  }
  
  /**
   * Variant which allows specifying attributes to identify this event stream.
   * 
   * @param apiToken The API authorization token to use when communicating with the Scalyr Logs server.
   * @param memoryLimit If not null, then we limit memory usage (for buffering events to be uploaded)
   *     to approximately this many bytes.
   * @param serverAddress URL on which we invoke the Scalyr Logs API. If null, we use the standard
   *     production server (currently https://log.scalyr.com).
   * @param serverAttributes Attributes to associate with this event stream. All events in the stream
   *     inherit these attributes. Can be null.
   */
  public static synchronized void init(String apiToken, Integer memoryLimit, String serverAddress,
      EventAttributes serverAttributes) {
    if (uploaderInstance.get() != null)
      return;
    
    LogService logService = new LogService(apiToken);
    if (serverAddress != null)
      logService.setServerAddress(serverAddress);
    
    uploaderInstance.set(new EventUploader(logService, memoryLimit,
        "sess_" + UUID.randomUUID(), true, serverAttributes));
  }
  
  /**
   * Record an event at "finest" severity.
   * 
   * @param attributes Attributes for this event.
   */
  public static void finest(EventAttributes attributes) {
    event(Severity.finest, attributes);
  }
  
  /**
   * Record an event at "finer" severity.
   * 
   * @param attributes Attributes for this event.
   */
  public static void finer(EventAttributes attributes) {
    event(Severity.finer, attributes);
  }
  
  /**
   * Record an event at "fine" severity.
   * 
   * @param attributes Attributes for this event.
   */
  public static void fine(EventAttributes attributes) {
    event(Severity.fine, attributes);
  }
  
  /**
   * Record an event at "info" severity.
   * 
   * @param attributes Attributes for this event.
   */
  public static void info(EventAttributes attributes) {
    event(Severity.info, attributes);
  }
  
  /**
   * Record an event at "warning" severity.
   * 
   * @param attributes Attributes for this event.
   */
  public static void warning(EventAttributes attributes) {
    event(Severity.warning, attributes);
  }
  
  /**
   * Record an event at "error" severity.
   * 
   * @param attributes Attributes for this event.
   */
  public static void error(EventAttributes attributes) {
    event(Severity.error, attributes);
  }
  
  /**
   * Record an event at "fatal" severity.
   * 
   * @param attributes Attributes for this event.
   */
  public static void fatal(EventAttributes attributes) {
    event(Severity.fatal, attributes);
  }
  
  /**
   * Record an event at "finest" severity. This event marks the beginning of a span; at the
   * end of the span, call end(span). Best practice is to place the end() call in a "finally"
   * clause, so that spans are never left dangling.
   * 
   * @param attributes Attributes for this event.
   */
  public static Span startFinest(EventAttributes attributes) {
    return start(Severity.finest, attributes);
  }
  
  /**
   * Record an event at "finer" severity. This event marks the beginning of a span; at the
   * end of the span, call end(span). Best practice is to place the end() call in a "finally"
   * clause, so that spans are never left dangling.
   * 
   * @param attributes Attributes for this event.
   */
  public static Span startFiner(EventAttributes attributes) {
    return start(Severity.finer, attributes);
  }
  
  /**
   * Record an event at "fine" severity. This event marks the beginning of a span; at the
   * end of the span, call end(span). Best practice is to place the end() call in a "finally"
   * clause, so that spans are never left dangling.
   * 
   * @param attributes Attributes for this event.
   */
  public static Span startFine(EventAttributes attributes) {
    return start(Severity.fine, attributes);
  }
  
  /**
   * Record an event at "info" severity. This event marks the beginning of a span; at the
   * end of the span, call end(span). Best practice is to place the end() call in a "finally"
   * clause, so that spans are never left dangling.
   * 
   * @param attributes Attributes for this event.
   */
  public static Span startInfo(EventAttributes attributes) {
    return start(Severity.info, attributes);
  }
  
  /**
   * Record an event at "warning" severity. This event marks the beginning of a span; at the
   * end of the span, call end(span). Best practice is to place the end() call in a "finally"
   * clause, so that spans are never left dangling.
   * 
   * @param attributes Attributes for this event.
   */
  public static Span startWarning(EventAttributes attributes) {
    return start(Severity.warning, attributes);
  }
  
  /**
   * Record an event at "error" severity. This event marks the beginning of a span; at the
   * end of the span, call end(span). Best practice is to place the end() call in a "finally"
   * clause, so that spans are never left dangling.
   * 
   * @param attributes Attributes for this event.
   */
  public static Span startError(EventAttributes attributes) {
    return start(Severity.error, attributes);
  }
  
  /**
   * Record an event at "fatal" severity. This event marks the beginning of a span; at the
   * end of the span, call end(span). Best practice is to place the end() call in a "finally"
   * clause, so that spans are never left dangling.
   * 
   * @param attributes Attributes for this event.
   */
  public static Span startFatal(EventAttributes attributes) {
    return start(Severity.fatal, attributes);
  }
  
  /**
   * Record an event at the specified severity.
   * 
   * @param attributes Attributes for this event.
   * @param severity Severity for this event.
   */
  public static void event(Severity severity, EventAttributes attributes) {
    try {
      EventUploader instance = uploaderInstance.get();
      if (instance != null)
        instance.threadEvents.get().event(severity, attributes);
    } catch (Exception ex) {
      Logging.warn("Caught exception in com.scalyr.api.logs.Events.event()", ex);
    }
  }
  
  /**
   * Record an event at the specified severity. This event marks the beginning of a span; at the
   * end of the span, call end(span). Best practice is to place the end() call in a "finally"
   * clause, so that spans are never left dangling.
   * 
   * @param attributes Attributes for this event.
   * @param severity Severity for this event.
   */
  public static Span start(Severity severity, EventAttributes attributes) {
    try {
      EventUploader instance = uploaderInstance.get();
      if (instance != null)
        return instance.threadEvents.get().start(severity, attributes);
      else
        return new Span(ScalyrUtil.nanoTime(), severity);
    } catch (Exception ex) {
      Logging.warn("Caught exception in com.scalyr.api.logs.Events.start()", ex);
      
      return new Span(ScalyrUtil.nanoTime(), severity);
    }
  }
  
  /**
   * Record an event, marking the end of a span initiated previously. You should call end()
   * exactly once for each span, and in the same thread as the start() call.
   * 
   * @param span Object returned by the corresponding call to start().
   */
  public static void end(Span span) {
    end(span, null);
  }
  
  /**
   * Record an event, marking the end of a span initiated previously. Attach the specified
   * attributes to the event. You should call end() exactly once for each span, and in the
   * same thread as the start() call.
   * 
   * @param span Object returned by the corresponding call to start().
   * @param attributes Attributes for this event.
   */
  public static void end(Span span, EventAttributes attributes) {
    try {
      EventUploader instance = uploaderInstance.get();
      if (instance != null)
        instance.threadEvents.get().end(span, attributes);
    } catch (Exception ex) {
      Logging.warn("Caught exception in com.scalyr.api.logs.Events.end()", ex);
    }
  }
  
  /**
   * Force all events recorded so far to be uploaded to the server.
   * <p>
   * It is not normally necessary to call this method, as events are automatically uploaded
   * every few seconds. However, you may wish to call it when the process terminates, to ensure
   * that any trailing events reach the server. Note that, unlike most Scalyr API methods, this
   * method will block until a response is received from the server (or a fairly lengthy timeout
   * expires).
   */
  public static synchronized void flush() {
    EventUploader instance = uploaderInstance.get();
    if (instance != null)
      instance.flush();
  }
  
  /**
   * Wipe the state of the Events reporting system. Should only be used for internal tests.
   */
  public static synchronized void _reset(String artificialSessionId,
      LogService logService, int memoryLimit, boolean autoUpload) {
    if (uploaderInstance.get() != null)
      uploaderInstance.get().terminate();
    
    uploaderInstance.set(new EventUploader(logService, memoryLimit, artificialSessionId, autoUpload, null));
  }
}
