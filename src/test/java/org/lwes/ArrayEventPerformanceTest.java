package org.lwes;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lwes.ArrayEvent.ArrayEventStats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArrayEventPerformanceTest {
  private static final Log                 LOG        = LogFactory.getLog(ArrayEventPerformanceTest.class);
  private static final int                 NUM_EVENTS = 100, NUM_PASSES = 1000;
  private static double                    CPU_SCALE;  // used to reduce CPU-dependent effects
  private static final double              TOLERANCE  = 1.5;
  private ArrayEvent[]                     events;
  private int                              numFields;
  private static ThreadMXBean              tmx;
  private long                             t0;
  private Map<ArrayEventStats, Integer>    stats0;
  
  // Change these values as performance shifts. If ArrayEvent gets faster, lower
  // them.  If we are forced to accept it getting slower, lower them.
  private static final double DIRECT_GET_CPU_TIME = 140000;
  
  @BeforeClass
  public static void beforeClass() {
    tmx = ManagementFactory.getThreadMXBean();
    // Microbenchmark this machine to provide a scale factor
    // for later performance testing.
    final String sample  = "string of moderate size to check string comparison speed";
    final String sample2 = sample+" with a difference";
    final long ct0 = tmx.getCurrentThreadCpuTime(), N = 100000000;
    long       num = 0;
    for (long i=0; i<N; ++i) {
      if (sample.equals(sample2)) ++num;
    }
    assertEquals(0, num);
    final long ct1 = tmx.getCurrentThreadCpuTime();
    CPU_SCALE = (ct1-ct0)/(double) (N*sample.length());
  }

  @Before
  public void before() {
    final RandomEventGenerator generator = new RandomEventGenerator();
    events = new ArrayEvent[NUM_EVENTS];
    numFields = 0;
    for (int i=0; i<NUM_EVENTS; ++i) {
      events[i] = new ArrayEvent();
      generator.fillRandomEvent(events[i]);
      events[i].setInt32("the_trax_time", 1300000000);
      events[i].setString("the_trax_id", "deadbeefdeadbeef");
      events[i].setInt64("the_event_id", 9);
      numFields += events[i].getNumEventAttributes();
    }
    t0     = tmx.getCurrentThreadCpuTime();
    stats0 = ArrayEvent.getStatsSnapshot();
  }
  
  @After
  public void after() {
    events = null;
  }
  
  @AfterClass
  public static void afterClass() {
    tmx = null;
  }

  @Test
  public void direct() throws Exception {
    t0  = tmx.getCurrentThreadCpuTime();
    
    for (int p=0; p<NUM_PASSES; ++p) {
      for (ArrayEvent event : events) {
        event.getInt32("the_trax_time");
        event.getString("the_trax_id");
        event.getInt64("the_event_id");
      }
    }
    
    // Compute "stats" as the number of ArrayEvent operations performed during the test.
    final Map<ArrayEventStats, Integer> stats = getStatsChanges(stats0);
    assertEquals(0, stats.get(ArrayEventStats.CREATIONS).intValue());
    assertEquals(0, stats.get(ArrayEventStats.COPIES).intValue());
    assertEquals(0, stats.get(ArrayEventStats.SWAPS).intValue());
    assertEquals(3*(numFields-NUM_EVENTS)*NUM_PASSES, stats.get(ArrayEventStats.PARSES).intValue());
    assertEquals(3*NUM_PASSES*NUM_EVENTS, stats.get(ArrayEventStats.FINDS).intValue());
    
    final long dt = tmx.getCurrentThreadCpuTime() - t0;
    LOG.info(String.format(
        "get() unpacked %d fields from %d events averaging %1.1f fields %d times in %f seconds, or %f ns/event, or %f ns/field",
        3, NUM_EVENTS, numFields/(double)NUM_EVENTS, NUM_PASSES,
        dt / 1000000000., dt / (double) (NUM_EVENTS * NUM_PASSES),
        dt / (double) (numFields * NUM_PASSES)));
    final double scaledTime         = dt / (numFields * NUM_PASSES * CPU_SCALE);
    final String message = String.format(
        "Scaled CPU time was %f, and was expected to be around %f",
        scaledTime,DIRECT_GET_CPU_TIME);
    LOG.info(message);
    assertTrue(message, scaledTime <= DIRECT_GET_CPU_TIME * TOLERANCE);
  }

  private static Map<ArrayEventStats, Integer> getStatsChanges(Map<ArrayEventStats, Integer> stats0) {
    final Map<ArrayEventStats, Integer> stats = ArrayEvent.getStatsSnapshot();
    for (Entry<ArrayEventStats, Integer> entry : stats0.entrySet()) {
      stats.put(entry.getKey(), stats.get(entry.getKey()) - entry.getValue());
    }
    return stats;
  }
}
