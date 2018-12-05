package org.lwes;

import static org.lwes.Event.MAX_MESSAGE_SIZE;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * this object maintains a memory pool for
 *
 */
public class MemoryPool {

  // Buffer pool is a queue of <Buffer>
  static protected ConcurrentLinkedQueue<Buffer> bufferPool = new ConcurrentLinkedQueue<Buffer>();

  /**
   * Buffer object to keep a ByteBuffer and CharBuffer object
   */
  public static class Buffer {
    private boolean    inPool = false;      // used to make sure one buffer is not
                                            // put in to the pool more than once
    private ByteBuffer byteBuffer;
    private CharBuffer charBuffer;

    /**
     * @return the {@code CharBuffer} object of the buffer used as the input
     *         for encoder
     */
    public CharBuffer getEncoderInputBuffer() {
      return charBuffer;
    }
    /**
     * @return the {@code ByteBuffer} object of the Buffer used as the output
     *         for encoder
     */
    public ByteBuffer getEncoderOutputBuffer() {
      return byteBuffer;
    }

    /**
     * @return the {@code CharBuffer} object of the buffer used as the output
     *         for decoder
     */
    public CharBuffer getDecoderOutputBuffer() {
      return charBuffer;
    }
  }

  /**
   * returns a {@code Buffer} object, it is either a newly allocated object
   * (if the Buffer pool is empty) or is coming from the pool.
   *
   * @return a {@code Buffer} object
   */
  static public Buffer get() {
    // get a Buffer object from the pool
    Buffer buffer = bufferPool.poll();
    if(buffer == null) {
      // queue was empty (may not be empty when we get here, but it is ok),
      // create a buffer and return it
      buffer = new Buffer();
      buffer.byteBuffer = ByteBuffer.allocate( MAX_MESSAGE_SIZE );
      buffer.charBuffer = CharBuffer.allocate( MAX_MESSAGE_SIZE );
    }
    // reset buffer (for when it is a used one coming from queue)
    buffer.byteBuffer.clear();
    buffer.charBuffer.clear();
    buffer.inPool = false;
    return buffer;
  }

  /**
   * returns a {@code ByteBuffer} object back in to the pool of ByteBuffers if it
   * is not already in the pool
   *
   * @param buffer - buffer object to be put back in the pool
   */
  static public void putBack(Buffer buffer) {
    if(buffer != null) {
      if(buffer.inPool) {
        throw new IllegalArgumentException("Trying to put a buffer that is already in the pool back in to the pool!");
      }
      buffer.inPool = true;
      bufferPool.add(buffer);
    }
  }

  /**
   * @return size of the pool
   */
  static public int size() {
    return bufferPool.size();
  }

}
