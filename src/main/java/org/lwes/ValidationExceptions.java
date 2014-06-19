/*======================================================================*
 * Copyright (c) 2010, Frank Maritato All rights reserved.              *
 *                                                                      *
 * Licensed under the New BSD License (the "License"); you may not use  *
 * this file except in compliance with the License.  Unless required    *
 * by applicable law or agreed to in writing, software distributed      *
 * under the License is distributed on an "AS IS" BASIS, WITHOUT        *
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     *
 * See the License for the specific language governing permissions and  *
 * limitations under the License. See accompanying LICENSE file.        *
 *======================================================================*/

package org.lwes;
/**
 * @author fmaritato
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.lwes.db.EventTemplateDB;

public class ValidationExceptions extends EventSystemException {

    List<EventSystemException> allExceptions = new LinkedList<EventSystemException>();

    @Deprecated // unused
    public ValidationExceptions(Throwable e) {
        super(e);
    }

    @Deprecated // unused
    public ValidationExceptions(EventSystemException e) {
        super(e);
        allExceptions.add(e);
    }

    public ValidationExceptions(String s) {
        super(s);
    }

    @Deprecated // unused
    public ValidationExceptions(String s, Throwable e) {
        super(s, e);
    }

    public void addException(EventSystemException e) {
        allExceptions.add(e);
    }
    public List<EventSystemException> getAllExceptions() {
        return new ArrayList<EventSystemException>(allExceptions);
    }
    public boolean hasExceptions() {
        return allExceptions.size() > 0;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("ValidationExceptions {\n");
        for (Exception e : allExceptions) {
               buf.append(e.toString()).append("\n");
        }
        buf.append("\n}");
        return buf.toString();
    }
    
    /**
     * To avoid some boilerplate code in {@link EventTemplateDB} and yet defer
     * consruction of an {@link ValidationExceptions} object, allow the following
     * pattern:
     * 
     * <pre><code>
     * ValidationExceptions ve = null;
     * if (condition1) {
     *   ve = ValidationExceptions.append(ve, new EventSystemException("label 1");
     * }
     * if (condition2) {
     *   ve = ValidationExceptions.append(ve, new EventSystemException("label 2");
     * }
     * </code></pre>
     */
    public static ValidationExceptions append(ValidationExceptions existing, EventSystemException... exceptions) {
        final ValidationExceptions result;
        if (existing == null) {
            result = new ValidationExceptions("Unable to validate event");
        } else {
            result = existing;
        }
        for (EventSystemException e : exceptions) {
            result.addException(e);
        }
        return result;
    }
}
