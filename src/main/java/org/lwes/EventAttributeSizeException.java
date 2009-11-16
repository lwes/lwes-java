package org.lwes;
/**
 * @author fmaritato
 */

public class EventAttributeSizeException extends EventSystemException {

    public EventAttributeSizeException(Throwable e) {
        super(e);
    }

    public EventAttributeSizeException(String s) {
        super(s);
    }

    public EventAttributeSizeException(String s, Throwable e) {
        super(s, e);
    }

    public EventAttributeSizeException(String attribute, int size, int expectedSize) {
        super("Attribute "+attribute+" size is incorrect. Expected "+expectedSize+" but was "+size);
    }

    public EventAttributeSizeException(String attribute, int size, int expectedSize, Throwable e) {
        super("Attribute "+attribute+" size is incorrect. Expected "+expectedSize+" but was "+size, e);
    }
}
