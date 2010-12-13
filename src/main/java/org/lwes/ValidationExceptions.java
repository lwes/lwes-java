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
