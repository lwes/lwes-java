package org.lwes;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RandomEventSerializationTest {
    @Test
    public void test() throws InstantiationException, IllegalAccessException {
        // To test another Event implementation, add it here.
        final List<Class<? extends Event>> eventClasses = new ArrayList<Class<? extends Event>>();
        eventClasses.add(MapEvent.class);
        eventClasses.add(ArrayEvent.class);
        
        final int E = eventClasses.size();
        final int N = 1000;
        final RandomEventGenerator[] generators = new RandomEventGenerator[E];
        for (int i=0; i<E; ++i) {
            generators[i] = new RandomEventGenerator();
        }
        
        final ThreadMXBean tmx = ManagementFactory.getThreadMXBean();
        final long t0 = tmx.getCurrentThreadCpuTime();
        for (int n=0; n<N; ++n) {
            // Generate random events and serialize them.
            final Event[]  randomEvents  = new Event[E];
            final byte[][] serialization = new byte[E][];
            for (int i=0; i<E; ++i) {
                randomEvents[i] = eventClasses.get(i).newInstance();
                generators[i].fillRandomEvent(randomEvents[i]);
                serialization[i] = randomEvents[i].serialize();
            }
            
            // Ensure that the random events are equal.
            for (int i=1; i<E; ++i) {
                assertEquals(randomEvents[0], randomEvents[i]);
            }
                
            // Ensure that serializing any event type and deserializing it as
            // any event type is still equal.
            final Event[][] deserializedEvents = new Event[E][E];
            for (int i=0; i<E; ++i) {
                for (int j=0; j<E; ++j) {
                    deserializedEvents[i][j] = eventClasses.get(j).newInstance();
                    deserializedEvents[i][j].deserialize(serialization[i]);
                    assertEquals(randomEvents[0], deserializedEvents[i][j]);
                }
            }
        }
        final long t1 = tmx.getCurrentThreadCpuTime();
        System.out.printf("Tested %d random events at %f ms each\n", N, (t1-t0)/(1000000. * N));
    }
}
