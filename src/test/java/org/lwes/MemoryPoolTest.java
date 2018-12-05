package org.lwes;

import static org.junit.Assert.*;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Before;
import org.junit.Test;
import org.lwes.MemoryPool.Buffer;

import static org.lwes.Event.MAX_MESSAGE_SIZE;

/**
 * unit test for {@code MemoryPool}
 *
 */
public class MemoryPoolTest {

  Random rnd = new Random();

  @Before
  public void setUp() {
    MemoryPool.bufferPool = new ConcurrentLinkedQueue<Buffer>();
  }

  /**
   * test all operations in one thread only
   */
  @Test
  public void singleThreadTest() {

    final int bufferCount = rnd.nextInt(100) + 100;
    Buffer[] buffers = new Buffer[bufferCount];

    // first test only getting buffers
    for(int i = 0; i<bufferCount; ++i) {
      buffers[i] = MemoryPool.get();
      // pool for each usage type should be empty after getting the buffer
      // since we do not release the buffer yet
      assertEquals(MemoryPool.bufferPool.size(), 0);
      // verify buffer's size
      assertEquals(buffers[i].getEncoderOutputBuffer().array().length, MAX_MESSAGE_SIZE);
      assertEquals(buffers[i].getEncoderInputBuffer().array().length, MAX_MESSAGE_SIZE);
      assertEquals(buffers[i].getDecoderOutputBuffer().array().length, MAX_MESSAGE_SIZE);
    }
    // now put the buffers back in to the pools
    for(int i = 0; i<bufferCount; ++i) {
      MemoryPool.putBack(buffers[i]);
      // pool size should now increase
      assertEquals(MemoryPool.size(), (i+1));
    }

    // now get the buffers back from the pools, they should be identical to the
    // ones that we get the first time
    Buffer[] newBuffers = new Buffer[bufferCount];
    for(int i = 0; i<bufferCount; ++i) {
      newBuffers[i] = MemoryPool.get();
      assertEquals(buffers[i], newBuffers[i]);
      assertEquals(buffers[i].getEncoderOutputBuffer(), newBuffers[i].getEncoderOutputBuffer());
      assertEquals(buffers[i].getEncoderInputBuffer(), newBuffers[i].getEncoderInputBuffer());
      assertEquals(buffers[i].getDecoderOutputBuffer(), newBuffers[i].getEncoderInputBuffer());
    }
    // we got all the buffers we fetched from the pool, so the pool should be empty now
    assertEquals(MemoryPool.size(), 0);

    // try to put a buffer in the pool while it is already in the pool
    Buffer buffer = MemoryPool.get();
    try {
      MemoryPool.putBack(buffer);
    } catch (IllegalArgumentException e) {
      // should not throw exception
      fail();
    }
    try {
      // trying to put the buffer back in the pool for the second time
      // should throw exception
      MemoryPool.putBack(buffer);
      fail();
    } catch (IllegalArgumentException e) {}
  }


  /**
   * test all operations in multiple threads
   * @throws InterruptedException
   */
  @Test
  public void multipleThreadsTest() throws InterruptedException {

    final int bufferCount = rnd.nextInt(10) + 10;
    final int threadCount = rnd.nextInt(10) + 10;
    Thread[] allThreads = new Thread[threadCount];

    final ConcurrentLinkedQueue<Buffer> allBuffers =
        new ConcurrentLinkedQueue<Buffer>();

    // create threads that get buffers from pool for the first time
    for(int i=0; i<threadCount; ++i) {
      allThreads[i] = new Thread("") {
        public void run() {
          for(int i=0; i<bufferCount; ++i) {
            // get a buffer
            Buffer buffer = MemoryPool.get();
            allBuffers.add(buffer);
          }
        }
      };
    }
    // start all threads
    for(Thread thread: allThreads) {
      thread.start();
    }
    for(Thread thread: allThreads) {
      thread.join();
    }

    // check the sizes
    assertEquals(allBuffers.size(), threadCount * bufferCount);

    // put the buffers back in to the pools
    for(Buffer buffer: allBuffers) {
      MemoryPool.putBack(buffer);
    }
    // make sure all buffers are inserted back in to the pools
    assertEquals(MemoryPool.size(), threadCount * bufferCount);

    // try to put null back in to the pool, there should be no change in
    // the size of the pool
    MemoryPool.putBack(null);
    assertEquals(MemoryPool.size(), threadCount * bufferCount);

  }

}
