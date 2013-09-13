/*======================================================================*
 * Copyright (c) 2008, Yahoo! Inc. All rights reserved.                 *
 *                                                                      *
 * Licensed under the New BSD License (the "License"); you may not use  *
 * this file except in compliance with the License.  Unless required    *
 * by applicable law or agreed to in writing, software distributed      *
 * under the License is distributed on an "AS IS" BASIS, WITHOUT        *
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     *
 * See the License for the specific language governing permissions and  *
 * limitations under the License. See accompanying LICENSE file.        *
 *======================================================================*/

package org.lwes.listener;

import org.lwes.EventSystemException;

public abstract class ThreadedEventListener<E extends ThreadedEnqueuer, D extends ThreadedDequeuer> implements EventListener {
    /* the processor for handling events */
    protected ThreadedProcessor processor = new ThreadedProcessor();

    /* the event enqueuer and dequeuer */
    protected E enqueuer = null;
    protected D dequeuer = null;

    private int queueSize = -1;

    /**
     * Default constructor.
     */
    public ThreadedEventListener() {
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    /**
     * Gets the enqueuer being used by this listener
     *
     * @return the enqueuer
     */
    public E getEnqueuer() {
        return enqueuer;
    }

    /**
     * Sets the enqueuer to use for this listener
     *
     * @param enqueuer the enqueuer to set
     */
    public void setEnqueuer(E enqueuer) {
        this.enqueuer = enqueuer;
    }

    /**
     * Gets the dequeuer being used by this listener
     *
     * @return the dequeuer
     */
    public D getDequeuer() {
        return dequeuer;
    }

    /**
     * Sets the dequeuer used by this listener
     *
     * @param dequeuer the dequeuer to set
     */
    public void setDequeuer(D dequeuer) {
        this.dequeuer = dequeuer;
    }

    /**
     * Add an EventHandler to handle events for processing.
     *
     * @param handler the EventHandler to add
     */
    public void addHandler(EventHandler handler) {

    }

    /**
     * Initializes this listener, and starts the processor threads
     *
     * @throws EventSystemException if there is a problem initializing the listener
     */
    public void initialize() throws EventSystemException {
        if (processor == null) {
            throw new EventSystemException("No processor exists");
        }
        if (enqueuer == null) {
            throw new EventSystemException("No enqueuer exists");
        }
        if (dequeuer == null) {
            throw new EventSystemException("No dequeuer exists");
        }

        processor.setQueueSize(queueSize);
        processor.setEnqueuerPriority(Thread.MAX_PRIORITY);
        processor.setDequeuerPriority(Thread.NORM_PRIORITY);
        processor.setEnqueuer(enqueuer);
        processor.setDequeuer(dequeuer);
        processor.initialize();
    }

    public void shutdown() throws EventSystemException {
        if (processor == null) {
            throw new EventSystemException("No processor exists");
        }
        processor.shutdown();
    }

}
