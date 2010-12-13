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

/**
 * EventListener is an interface defining an event listener that has pluggable
 * event acquisition and handling.  Events can be acquired over the network or 
 * from a file, for example.  Handlers can be registered using a callback interface 
 * that will be called as events come into the system.
 * 
 * @author      Michael P. Lum
 * @author      Anthony Molinaro
 */
public interface EventListener {
	/**
	 * Add an EventHandler to handle events for processing.
	 * @param handler the EventHandler to add
	 */
	public void addHandler(EventHandler handler);
}

