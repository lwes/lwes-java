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

package org.lwes.emitter;

import java.io.IOException;
import java.io.InputStream;

import org.lwes.Event;
import org.lwes.EventSystemException;

/**
 * EventEmitter is the interface that defines a component that takes an Event and performs an emit task
 * on that event.  This may emit the event to the network, to a disk, or to a database.	
 * 
 * @author      Michael P. Lum
 * @author      Anthony Molinaro
 */
public interface EventEmitter {
	/**
	 * Sets the ESF file used for event validation.
	 * @param esfFilePath the path of the ESF file
	 */
	public void setESFFilePath(String esfFilePath);
	
	/**
	 * Gets the ESF file used for event validation
	 * @return the ESF file path
	 */
	public String getESFFilePath();
	
	/**
	 * Sets an InputStream to be used for event validation.
	 * @param esfInputStream an InputStream used for event validation
	 */
	public void setESFInputStream(InputStream esfInputStream);
	
	/**
	 * Gets the InputStream being used for event validation.
	 * @return the InputStream of the ESF validator
	 */
	public InputStream getESFInputStream();
	
	/**
	 * Called before the system is started.  Allows for initialization of data and creation
	 * of network sockets, where applicable.
	 * 
	 * @throws IOException if an I/O error occurs during initialization
	 */
	public void initialize() throws IOException;
	
	/**
	 * Called before the system is shut down.  Allows for cleanup of data and destruction of
	 * network sockets, where applicable.
	 * 
	 * @throws IOException if an I/O error occurs during initialization
	 */
	public void shutdown() throws IOException;
	
	/**
	 * Create an event with name <tt>eventName</tt>
	 * @param eventName the name of the event to create
	 * @return the Event object
	 * @throws EventSystemException if an error occurs during event creation
	 */
	public Event createEvent(String eventName) throws EventSystemException;
	
	/**
	 * Create an event with name <tt>eventName</tt>, optionally validating it against an EventTemplateDB
	 * @param eventName the name of the event
	 * @param validate whether or not to validate the event against the EventTemplateDB
	 * @return the Event object
	 * @throws EventSystemException if an error occurs during event creation
	 */
	public Event createEvent(String eventName, boolean validate) throws EventSystemException;
	
	/**
	 * Emits an Event object to the destination
	 * 
	 * @param  event  the event being emitted
	 * @throws IOException if an I/O error occurs
	 */	
	public void emit(Event event) throws IOException;
}
