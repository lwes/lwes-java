package org.lwes.emitter;
/**
 * @author fmaritato
 */

import org.lwes.Event;
import org.lwes.EventSystemException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class MockMulticastEventEmitter extends MulticastEventEmitter {

    private LinkedList<Event> events = new LinkedList();

    @Override
    protected void emit(byte[] bytes) throws IOException {
        try {
            events.add(getFactory().createEvent(bytes, false));
        }
        catch (EventSystemException e) {
            e.printStackTrace();
        }
    }

    public List<Event> getEvents() {
        List l = new LinkedList();
        l.addAll(events);
        return l;
    }
}
