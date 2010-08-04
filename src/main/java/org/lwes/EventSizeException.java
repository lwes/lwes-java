package org.lwes;
/**
 * This exception is thrown when the Event is too large.
 * @author fmaritato
 */

public class EventSizeException extends EventSystemException {

    public EventSizeException(Throwable e) {
        super(e);
    }

    public EventSizeException(String s) {
        super(s);
    }

    public EventSizeException(String s, Throwable e) {
        super(s, e);
    }

}
