package com.scalyr.api.tests;

import com.scalyr.api.logs.EventAttributes;
import org.junit.Test;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.scalyr.api.logs.EventAttributes.ev;
import static com.scalyr.api.logs.EventAttributes.fromArray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for EventAttributes
 * @author oliver@scalyr.com
 */
public class EventAttributesTest {

  private final int THREAD_START_WAIT_MS =500;

  @Test
  public void test_ev() {
    assertEquals(0, ev().size());
    assertEquals(1, ev("a", 1).size());
    assertEquals(2, ev("a", 1, "b", 2).size());
    assertEquals(3, ev("a", 1, "b", 2, "c", 3).size());
    assertEquals(4, ev("a", 1, "b", 2, "c", 3, "d", 4).size());
    assertEquals(5, ev("a", 1, "b", 2, "c", 3, "d", 4, "e", 5).size());
    assertEquals(6, ev("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6).size());
    assertEquals(2, ev(new EventAttributes("a", 1, "b", 2)).size());  // copy ctor
  }

  @Test
  public void test_add() {
    assertEquals(2, ev("z", 1).add(ev("a", 1)).size());  // copy ctor
    assertEquals(2, ev("z", 1).add("a", 1).size());
    assertEquals(3, ev("z", 1).add("a", 1, "b", 2).size());
    assertEquals(4, ev("z", 1).add("a", 1, "b", 2, "c", 3).size());
    assertEquals(5, ev("z", 1).add("a", 1, "b", 2, "c", 3, "d", 4).size());
    assertEquals(6, ev("z", 1).add("a", 1, "b", 2, "c", 3, "d", 4, "e", 5).size());
    assertEquals(7, ev("z", 1).add("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6).size());
    assertEquals(2, ev("z", 1).add(() -> ev("a", 1)).size());            // supplier
    assertEquals(2, ev("z", 1).add(attrs -> attrs.add("a", 1)).size());  // consumer
  }

  @Test
  public void test_fromArray() {
    assertEquals(0, fromArray(new Object[] {}).size());
    assertEquals(1, fromArray(new Object[] {"a", 1}).size());
    assertEquals(2, fromArray(new Object[] {"a", 1, "b", 2}).size());
    try {
      fromArray(new Object[] {"a", 1, "b"});  // missing 2nd value
      fail("odd-sized array should fail");
    } catch (RuntimeException e) {
      assertTrue(true);
    }
  }

  /**
   * Test concurrent modification support of EventAttributes by adding attributes to EventAttributes
   * during iteration
   */
  @Test
  public void eventAttrConcurrentModifyDuringIteration() {
    EventAttributes eventAttributes = new EventAttributes("key1", "val1");

    Iterator<Map.Entry<String, Object>> eventAttrIterator = eventAttributes.getEntries().iterator();
    eventAttributes.put("key2", "val2");
    eventAttrIterator.next();

  }

  /**
   * Test concurrent modification support of EventAttributes by adding attributes to EventAttributes
   * during event attributes names iteration
   */
  @Test
  public void eventAttrNameConcurrentModifyDuringIteration() {
    EventAttributes eventAttributes = new EventAttributes("key1", "val1");

    Iterator<String> attrName = eventAttributes.getNames().iterator();
    eventAttributes.put("key2", "val2");
    attrName.next();

  }

  /**
   * Test create new EventAttributes object while source EventAttributes object is modified by another thread
   */
  @Test
  public void concurrentConstructorTest() {
    EventAttributes sourceEventAttributes = new EventAttributes();
    for (int i = 1; i <= 50; i++) {
      sourceEventAttributes.put("key"+i, "val"+i);
    }

    AtomicBoolean isDone = new AtomicBoolean(false);
    ExecutorService executorService = Executors.newFixedThreadPool(1);
    executorService.execute(() -> {
      int i = 51;
      while (!isDone.get()) {
        sourceEventAttributes.put("key"+i, "val"+i);
        i++;
      }
    });

    // sleep to allow thread to start
    try{
      Thread.sleep(THREAD_START_WAIT_MS);
    } catch (InterruptedException ie ){

    }

    // Create new EventAttributes while source EventAttributes is being modified by another thread
    EventAttributes newEventAttrs = new EventAttributes(sourceEventAttributes);
    isDone.set(true);

    executorService.shutdown();
  }


  /**
   * Test underwriteFrom source EventAttributes object while source EventAttributes object is modified by another thread
   */
  @Test
  public void concurrentUnderwriteFromTest() {
    EventAttributes sourceEventAttributes = new EventAttributes();
    for (int i = 1; i <= 50; i++) {
      sourceEventAttributes.put("key"+i, "val"+i);
    }

    AtomicBoolean isDone = new AtomicBoolean(false);
    ExecutorService executorService = Executors.newFixedThreadPool(1);
    executorService.execute(() -> {
      int i = 51;
      while (!isDone.get()) {
        sourceEventAttributes.put("key"+i, "val"+i);
        i++;
      }
    });

    // sleep to allow thread to start
    try{
      Thread.sleep(THREAD_START_WAIT_MS);
    } catch (InterruptedException ie ){

    }

    // Create new EventAttributes while source EventAttributes is being modified by another thread
    EventAttributes newEventAttrs = new EventAttributes();
    newEventAttrs.underwriteFrom(sourceEventAttributes);
    isDone.set(true);

    executorService.shutdown();

  }

}
