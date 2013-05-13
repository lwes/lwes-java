package org.lwes.listener;
/**
 * User: frank.maritato
 * Date: 5/1/12
 */

import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Assert;
import org.junit.Test;
import org.lwes.EventSystemException;

public class ThreadedProcessorTest {

    @Test(expected = EventSystemException.class)
    public void testNoEnqueuer() {
        ThreadedProcessor tp = new ThreadedProcessor();
        tp.initialize();
    }

    @Test(expected = EventSystemException.class)
    public void testNoDequeuer() {
        ThreadedProcessor tp = new ThreadedProcessor();
        tp.setEnqueuer(new EmptyEnqueuer());
        Assert.assertNotNull(tp.getEnqueuer());
        tp.initialize();
    }

    @Test
    public void testWithNoQueueAndSize() {
        ThreadedProcessor tp = new ThreadedProcessor();
        tp.setEnqueuer(new EmptyEnqueuer());
        tp.setDequeuer(new EmptyDequeuer());
        Assert.assertNotNull(tp.getEnqueuer());
        Assert.assertNotNull(tp.getDequeuer());
        tp.setQueueSize(10);
        tp.initialize();
        LinkedBlockingQueue q = tp.getQueue();
        Assert.assertNotNull(q);
        Assert.assertEquals(10, q.remainingCapacity());
        tp.shutdown();
    }

    @Test
    public void testWithoutQueue() {
        ThreadedProcessor tp = new ThreadedProcessor();
        tp.setEnqueuer(new EmptyEnqueuer());
        tp.setDequeuer(new EmptyDequeuer());
        Assert.assertNotNull(tp.getEnqueuer());
        Assert.assertNotNull(tp.getDequeuer());
        tp.initialize();
        LinkedBlockingQueue q = tp.getQueue();
        Assert.assertNotNull(q);
    }

    @Test
    public void testWithQueue() {
        ThreadedProcessor tp = new ThreadedProcessor();
        tp.setEnqueuer(new EmptyEnqueuer());
        tp.setDequeuer(new EmptyDequeuer());
        Assert.assertNotNull(tp.getEnqueuer());
        Assert.assertNotNull(tp.getDequeuer());
        tp.setQueue(new LinkedBlockingQueue<QueueElement>());
        tp.initialize();
        LinkedBlockingQueue q = tp.getQueue();
        Assert.assertNotNull(q);
    }


    class EmptyDequeuer extends ThreadedDequeuer {

    }
    class EmptyEnqueuer extends ThreadedEnqueuer {

    }

}
