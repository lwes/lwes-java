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

package org.lwes;

import org.lwes.db.EventTemplateDB;
import org.lwes.serializer.JsonDeserializer;

import java.io.File;
import java.io.InputStream;

public class EventFactory {

    /* the DB for type checking and validation */
    private EventTemplateDB eventTemplateDB = null;

    /* the references to the ESF files for validation */
    File esfFile = null;
    String esfFilePath = null;
    InputStream esfInputStream = null;

    boolean eventTemplateDBInit = false;

    /**
     * EventFactory constructor.  Creates an empty event template database.
     */
    public EventFactory() {
        eventTemplateDB = new EventTemplateDB();
    }

    /**
     * Gets the ESF file used for validation.
     *
     * @return the File object
     */
    public File getESFFile() {
        return esfFile;
    }

    /**
     * Sets an ESF file for validation.
     *
     * @param esfFile the File object to set
     */
    public void setESFFile(File esfFile) {
        this.esfFile = esfFile;
    }

    /**
     * Gets the path of an ESF file used for validation.
     *
     * @return the ESF file path
     */
    public String getESFFilePath() {
        return this.esfFilePath;
    }

    /**
     * Sets the path of an ESF file to use for validation.
     *
     * @param path the path to the ESF file
     */
    public void setESFFilePath(String path) {
        this.esfFilePath = path;
    }

    /**
     * Gets an InputStream of the ESF file to use for validation.
     *
     * @return the ESF InputStream object
     */
    public InputStream getESFInputStream() {
        return this.esfInputStream;
    }

    /**
     * Sets the InputStream of an ESF file to use for validation
     *
     * @param input the InputStream to use
     */
    public void setESFInputStream(InputStream input) {
        this.esfInputStream = input;
    }

    /**
     * Initializes the EventFactory along with pointers to the ESF file
     *
     * @throws EventSystemException if there is an exception with setting the ESF files
     */
    public void initialize() throws EventSystemException {
        if (esfFilePath != null) {
            File esfFile = new File(esfFilePath);
            eventTemplateDB.setESFFile(esfFile);
        }

        if (esfFile != null) {
            eventTemplateDB.setESFFile(esfFile);
        }

        if (esfInputStream != null) {
            eventTemplateDB.setESFInputStream(esfInputStream);
        }
        eventTemplateDBInit = eventTemplateDB.initialize();
    }

    /**
     * Creates a validated event named <tt>eventName</tt>.
     *
     * @param eventName the name of the event
     * @return the Event object
     * @throws EventSystemException if there is a problem creating the event
     */
    public Event createEvent(String eventName) throws EventSystemException {
        return createEvent(eventName, Event.DEFAULT_ENCODING);
    }

    /**
     * Create a validated event named <tt>eventName</tt> with specified encoding.
     *
     * @param eventName the name of the event
     * @param encoding  the encoding to use
     * @return the Event object
     * @throws EventSystemException if there is a problem creating the event
     */
    public Event createEvent(String eventName, short encoding) throws EventSystemException {
        return createEvent(eventName, true, encoding);
    }

    /**
     * Create an event named <tt>eventName</tt> and optionally validate the event.
     *
     * @param eventName the name of the event
     * @param validate  whether or not to validate the event against the EventTemplateDB
     * @return the Event object
     * @throws EventSystemException if there is a problem creating the event
     */
    public Event createEvent(String eventName, boolean validate) throws EventSystemException {
        return createEvent(eventName, validate, Event.DEFAULT_ENCODING);
    }

    /**
     * Create an event named <tt>eventName</tt> with optional validation and specified encoding
     *
     * @param eventName the name of the event
     * @param validate  whether or not to validate the event against the EventTemplateDB
     * @param encoding  the encoding to use
     * @return the Event object
     * @throws EventSystemException if there is a problem creating the event
     */
    public Event createEvent(String eventName, boolean validate, short encoding) throws EventSystemException {
        if (validate && !eventTemplateDBInit) {
            throw new EventSystemException("Event template db not initialized");
        }
        return new MapEvent(eventName, validate, eventTemplateDB, encoding);
    }

    /**
     * Create an event from an array of bytes
     *
     * @param bytes the byte array
     * @return the Event object
     * @throws EventSystemException if there is a problem creating the event
     */
    public Event createEvent(byte[] bytes) throws EventSystemException {
        Event e = null;
        e = new MapEvent(bytes, eventTemplateDB);
        return e;
    }

    /**
     * Create an event from an array of bytes, with optional validation
     *
     * @param bytes    the byte array
     * @param validate whether or not to validate this event against the EventTemplateDB
     * @return the Event object
     * @throws EventSystemException if there is a problem creating the event
     */
    public Event createEvent(byte[] bytes, boolean validate) throws EventSystemException {
        if (validate && !eventTemplateDBInit) {
            throw new EventSystemException("Event template db not initialized");
        }
        return new MapEvent(bytes, validate, eventTemplateDB);
    }
    
    /**
     * Create an event from its json representation
     * @param json
     * @param type
     * @return
     */
    public Event createEventFromJson(String json, EventImplementation type){
        JsonDeserializer deSerializer = JsonDeserializer.getInstance();
        switch (type) {
        case MAP_EVENT:
            return deSerializer.fromJson(json, new MapEvent());
        case ARRAY_EVENT:
            return deSerializer.fromJson(json, new ArrayEvent());
        }
        return null;
    }

}
