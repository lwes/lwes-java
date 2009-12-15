package org.lwes;
/**
 * @author fmaritato
 */

public class AttributeSizeException extends EventSystemException {

    public AttributeSizeException(Throwable e) {
        super(e);
    }

    public AttributeSizeException(String s) {
        super(s);
    }

    public AttributeSizeException(String s, Throwable e) {
        super(s, e);
    }

    public AttributeSizeException(String attribute, int size, int expectedSize) {
        super("Attribute "+attribute+" size is incorrect. Expected "+expectedSize+" but was "+size);
    }

    public AttributeSizeException(String attribute, int size, int expectedSize, Throwable e) {
        super("Attribute "+attribute+" size is incorrect. Expected "+expectedSize+" but was "+size, e);
    }
}
