package com.scalyr.api.tests;

import com.scalyr.api.knobs.ConfigurationFile;
import com.scalyr.api.knobs.Knob;
import com.scalyr.api.logs.EventAttributes;
import com.scalyr.api.logs.EventUploader;
import com.scalyr.api.logs.Events;
import com.scalyr.api.logs.LogService;


import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests for Gzip compression. These are marked as @Ignore b/c they are dependent on a local server instance
 * or a remote Scalyr environment. You must manually check that the test logs are received and decompressed
 * correctly at the destination.
 */
@Ignore public class GzipTest extends LogsTestBase {

  // Put appropriate log write token here.
  String apiLogWriteKey = "";

  // Either put localhost here, or a particular Scalyr environment. Remember to use "https" for staging and prod.
  String serverAddress = "https://scalyr.com";

  @Test public void testGzipOnApacheHttpClient() {
    Knob.setDefaultFiles(new ConfigurationFile[0]);

    LogService testService = new LogService(apiLogWriteKey);
    testService = testService.setServerAddress(serverAddress);

    Events._reset("testGzipSession", testService, 999999, false, true);
    // Enable Gzip in case it's not enabled by default
    Events.setCompressionType(EventUploader.CompressionType.Gzip);

    Events.info(new EventAttributes("tag", "testWithGzipApache", "foo1", "bla1", "foo2", "bla2"));
    Events.flush();
  }
}
