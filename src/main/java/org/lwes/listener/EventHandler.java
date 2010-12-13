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

import org.lwes.Event;

/**
 * This interface is implemented by any object that wishes to
 * receive incoming Events.  Once an EventListener registers
 * with an EventDispatcher, it will receive access to any new
 * Events that are sent through that EventDispatcher.
 *
 * @author Anthony Molinaro
 */
public interface EventHandler {

    /**
     * This is the method that is called when an event is caught.
     * @param event
     */
    public void handleEvent(Event event);

    /**
     * Shutdown hook
     */
    public void destroy();
}
