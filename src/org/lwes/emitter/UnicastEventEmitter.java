package org.lwes.emitter;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.lwes.Event;
import org.lwes.EventFactory;
import org.lwes.EventSystemException;
import org.lwes.util.Log;
import org.lwes.util.NumberCodec;

/**
 * UnicastEventEmitter emits events as unicast datagrams on the network.	
 * 
 * @author      Michael P. Lum
 * @author      Anthony Molinaro
 * @since       0.0.1
 * 
 * Example code:
 * <pre>
 * UnicastEventEmitter emitter = new UnicastEventEmitter();
 * emitter.setESFFilePath("/path/to/esf/file");
 * emitter.setMulticastAddress(InetAddress.getByName("224.0.0.69"));
 * emitter.setMulticastPort(9191);
 * emitter.initialize();
 *
 * Event e = emitter.createEvent("MyEvent", false);
 * e.setString("key","value");
 * emitter.emit(e);
 * </pre>
 */

public class UnicastEventEmitter implements EventEmitter {
	/* an EventFactory */
	private EventFactory factory = new EventFactory();
	
	/* the unicast socket being used */
	private DatagramSocket socket = null;
	
	/* the address */
	private InetAddress address = null;
	
	/* the port */
	private int port = 9191;
	
	/* a lock variable to synchronize events */
	private Object lock = new Object();
	
	/**
	 * Default constructor.
	 */
	public UnicastEventEmitter() {
	}
	
	/**
	 * Sets the destination address for this emitter.
	 * 
	 * @param address the multicast address
	 * 
	 */
	public void setAddress(InetAddress address) {
		this.address = address;
	}
	
	/**
	 * Gets the address for this emitter.
	 * 
	 * @return the address
	 */
	public InetAddress getAddress() {
		return this.address;
	}

	/**
	 * Sets the destination port for this emitter.
	 * 
	 * @param address the multicast port
	 * 
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * Gets the destination port for this emitter.
	 * 
	 * @return the multicast port
	 */
	public int getPort() {
		return this.port;
	}
	
	/**
	 * Sets the ESF file used for event validation.
	 * @param esfFilePath the path of the ESF file
	 */
	public void setESFFilePath(String esfFilePath) {
		if(factory != null) {
			factory.setESFFilePath(esfFilePath);
		}
	}
	
	/**
	 * Gets the ESF file used for event validation
	 * @return the ESF file path
	 */
	public String getESFFilePath() {
		if(factory != null) {
			return factory.getESFFilePath();
		} else {
			return null;
		}
	}
	
	/**
	 * Sets an InputStream to be used for event validation.
	 * @param esfInputStream an InputStream used for event validation
	 */
	public void setESFInputStream(InputStream esfInputStream) {
		if(factory != null) {
			factory.setESFInputStream(esfInputStream);
		}
	}
	
	/**
	 * Gets the InputStream being used for event validation.
	 * @return the InputStream of the ESF validator
	 */
	public InputStream getESFInputStream() {
		if(factory != null) {
			return factory.getESFInputStream();
		} else {
			return null;
		}
	}
	
	
	/**
	 * Initializes the emitter.
	 */
	public void initialize() throws IOException {		
		try {
			factory.initialize();
			socket = new DatagramSocket();		
			socket.setReuseAddress(true);
		} catch(IOException ie) {
			throw ie;
		} catch(Exception e) {
			Log.error("Unable to initialize UnicastEventEmitter", e);
		}
	}
	
	public void shutdown() throws IOException {
		socket.close();
	}

	/**
	 * Creates a new event named <tt>eventName</tt>.
	 * @param eventName the name of the event to be created
	 * @return a new Event
	 * @exception EventSystemException if there is a problem creating the event
	 */
	public Event createEvent(String eventName) throws EventSystemException {
		return createEvent(eventName, true);
	}
	
	/**
	 * Creates a new event named <tt>eventName</tt>.
	 * @param eventName the name of the event to be created
	 * @param validate whether or not to validate the event against the EventTemplateDB
	 * @return a new Event
	 * @exception EventSystemException if there is a problem creating the event
	 */
	public Event createEvent(String eventName, boolean validate) throws EventSystemException {
		if(factory != null) {
			return factory.createEvent(eventName, validate);
		} else {
			throw new EventSystemException("EventFactory not initialized");
		}
	}	
	
	/**
	 * Emits the event to the network.
	 * 
	 * @param event the event to emit
	 * @exception IOException throws an IOException is there is a network error.
	 */
	public void emit(Event event) throws IOException {
		byte[] msg = event.serialize();
		
		synchronized(lock) {
			emit(msg);
		}
	}

	/**
	 * Emits a byte array to the network.
	 * 
	 * @param bytes the byte array to emit
	 * @exception IOException throws an IOException if there is a network error.
	 */
	protected void emit(byte[] bytes) throws IOException {
		emit(bytes, this.address, this.port);
	}

	/**
	 * @param bytes the byte array to emit
	 * @param address the address to use
	 * @param port the port to use
	 * @throws IOException throws an IOException if there is a network error
	 */
	protected void emit(byte[] bytes, InetAddress address, int port) throws IOException {
		/* don't send null bytes */
		if(bytes == null) return;
		
		DatagramPacket dp = new DatagramPacket(bytes, bytes.length, address, port);
		socket.send(dp);
		
		Log.trace("Sent to network '" + NumberCodec.byteArrayToHexString(dp.getData(), 0, dp.getLength()));
	}
}
