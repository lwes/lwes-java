package org.lwes;
/**
 * @author fmaritato
 */

public class AttributeRequiredException extends EventSystemException {

    public AttributeRequiredException(Throwable e) {
        super(e);
    }

    public AttributeRequiredException(String attrName) {
        super("Attribute "+attrName+" is required");
    }

    public AttributeRequiredException(String attrName, Throwable e) {
        super("Attribute "+attrName+" is required", e);
    }

}
