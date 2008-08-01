package org.lwes;

import java.io.File;
import java.io.InputStream;

import org.lwes.db.EventTemplateDB;

public class EventFactory {
	/* the DB for type checking and validation */
	private EventTemplateDB eventTemplateDB = null;
	
	/* the references to the ESF files for validation */
	File esfFile = null;
	String esfFilePath = null;
	InputStream esfInputStream = null;
	
	/**
	 * EventFactory constructor.  Creates an empty event template database.
	 *
	 */
	public EventFactory() {
		eventTemplateDB = new EventTemplateDB();
	}

	/**
	 * Gets the ESF file used for validation.
	 * @return the File object
	 */
	public File getESFFile() {
		return esfFile;
	}	
	
	/**
	 * Sets an ESF file for validation.
	 * @param esfFile the File object to set
	 */
	public void setESFFile(File esfFile) {
		this.esfFile = esfFile;
	}
	
	/**
	 * Gets the path of an ESF file used for validation. 
	 * @return the ESF file path
	 */
	public String getESFFilePath() {
		return this.esfFilePath;
	}
	
	/**
	 * Sets the path of an ESF file to use for validation.
	 * @param path the path to the ESF file
	 */
	public void setESFFilePath(String path) {
		this.esfFilePath = path;
	}
	
	/**
	 * Gets an InputStream of the ESF file to use for validation.
	 * @return the ESF InputStream object
	 */
	public InputStream getESFInputStream() {
		return this.esfInputStream;
	}
	
	/**
	 * Sets the InputStream of an ESF file to use for validation
	 * @param input the InputStream to use
	 */
	public void setESFInputStream(InputStream input) {
		this.esfInputStream = input;
	}
	
	public void initialize() throws EventSystemException {
		if(esfFilePath != null) {
			File esfFile = new File(esfFilePath);
			eventTemplateDB.setESFFile(esfFile);
		}
		
		if(esfFile != null) {
			eventTemplateDB.setESFFile(esfFile);
		}
		
		if(esfInputStream != null) {
			eventTemplateDB.setESFInputStream(esfInputStream);
		}
	}
	
	public Event createEvent(String eventName) throws EventSystemException {
		return createEvent(eventName, Event.DEFAULT_ENCODING);
	}

	public Event createEvent(String eventName, short encoding) throws EventSystemException {
		return createEvent(eventName, true, encoding);
	}
	
	public Event createEvent(String eventName, boolean validate) throws EventSystemException {
		return createEvent(eventName, validate, Event.DEFAULT_ENCODING);
	}
	
	public Event createEvent(String eventName, boolean validate, short encoding) throws EventSystemException {
		Event e = null;
		e = new Event(eventName, validate, eventTemplateDB, encoding);
		return e;		
	}
	
	public Event createEvent(byte[] bytes) throws EventSystemException {
		Event e = null;
		e = new Event(bytes, eventTemplateDB);
		return e;
	}
	
	public Event createEvent(byte[] bytes, boolean validate) throws EventSystemException {
		Event e = null;
		e = new Event(bytes, validate, eventTemplateDB);
		return e;
	}
	
}
