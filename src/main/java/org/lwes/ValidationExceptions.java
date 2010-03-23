package org.lwes;
/**
 * @author fmaritato
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ValidationExceptions extends EventSystemException {

    List<EventSystemException> allExceptions = new LinkedList<EventSystemException>();

    public ValidationExceptions(Throwable e) {
        super(e);
    }

    public ValidationExceptions(String s) {
        super(s);
    }

    public ValidationExceptions(String s, Throwable e) {
        super(s, e);
    }

    public void addException(EventSystemException e) {
        allExceptions.add(e);
    }
    public List<EventSystemException> getAllExceptions() {
        return new ArrayList(allExceptions);
    }
    public boolean hasExceptions() {
        return allExceptions.size() > 0;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("ValidationExceptions {\n");
        for (Exception e : allExceptions) {
               buf.append(e.toString()).append("\n");
        }
        buf.append("\n}");
        return buf.toString();
    }
}
