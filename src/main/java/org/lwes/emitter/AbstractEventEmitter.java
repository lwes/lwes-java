package org.lwes.emitter;
/**
 * @author fmaritato
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwes.Event;
import org.lwes.EventFactory;
import org.lwes.EventSystemException;

import java.io.IOException;

public abstract class AbstractEventEmitter implements EventEmitter {

    private static transient Log log = LogFactory.getLog(AbstractEventEmitter.class);

    private EventFactory factory = new EventFactory();

    private boolean emitHeartbeat = false;
    private long eventCount = 0;
    private long totalEventCount = 0;
    private long frequency = 60000;
    private long lastBeatTime = 0;
    private long sequence = 0;

    public void initialize() throws IOException {
        try {
            factory.initialize();
            lastBeatTime = System.currentTimeMillis();
            Event e = factory.createEvent("System::Startup", false);
            emit(e);
        }
        catch (EventSystemException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void shutdown() throws IOException {
        try {
            Event e = factory.createEvent("System::Shutdown", false);
            long time = System.currentTimeMillis();
            long freqThisPeriod = time - lastBeatTime;
            sendEventWithStatistics(e, freqThisPeriod);
        }
        catch (EventSystemException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void collectStatistics() throws EventSystemException, IOException {

        eventCount++;
        totalEventCount++;
        long time = System.currentTimeMillis();
        long freqThisPeriod = time - lastBeatTime;

        if (emitHeartbeat && (freqThisPeriod >= frequency)) {
            Event e = factory.createEvent("System::Heartbeat", false);
            sendEventWithStatistics(e, freqThisPeriod);
            eventCount = 0;
            lastBeatTime = time;
        }
    }

    public void sendEventWithStatistics(Event e, long freq)
            throws EventSystemException, IOException {
        e.setInt64("freq", freq);
        e.setInt64("seq", ++sequence);
        e.setInt64("count", eventCount);
        e.setInt64("total", totalEventCount);
        emit(e.serialize());
    }

    protected abstract void emit(byte[] bytes) throws IOException;

    public boolean isEmitHeartbeat() {
        return emitHeartbeat;
    }

    public void setEmitHeartbeat(boolean emitHeartbeat) {
        this.emitHeartbeat = emitHeartbeat;
    }

    public long getEventCount() {
        return eventCount;
    }

    public EventFactory getFactory() {
        return factory;
    }

    public void setFactory(EventFactory factory) {
        this.factory = factory;
    }

    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    public long getLastBeatTime() {
        return lastBeatTime;
    }

    public long getSequence() {
        return sequence;
    }

    public long getTotalEventCount() {
        return totalEventCount;
    }

}
