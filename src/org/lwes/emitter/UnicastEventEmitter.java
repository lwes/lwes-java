package org.lwes.emitter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.lwes.Event;
import org.lwes.util.Log;
import org.lwes.util.NumberCodec;

/**
 * UnicastEventEmitter emits events as unicast datagrams on the network.	
 * 
 * @author      Michael P. Lum
 * @author      Anthony Molinaro
 * @version     %I%, %G%
 * @since       0.0.1
 */

public class UnicastEventEmitter implements EventEmitter {
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
	 * Sets the multicast address for this emitter.
	 * 
	 * @param address the multicast address
	 * 
	 */
	public void setMulticastAddress(InetAddress address) {
		this.address = address;
	}
	
	/**
	 * Gets the multicast address for this emitter.
	 * 
	 * @return the address
	 */
	public InetAddress getMulticastAddress() {
		return this.address;
	}

	/**
	 * Sets the multicast port for this emitter.
	 * 
	 * @param address the multicast port
	 * 
	 */
	public void setMulticastPort(int port) {
		this.port = port;
	}
	
	/**
	 * Gets the multicast port for this emitter.
	 * 
	 * @return the multicast port
	 */
	public int getMulticastPort() {
		return this.port;
	}
	
	/**
	 * Initializes the emitter.
	 */
	public void initialize() throws IOException {		
		try {
			socket = new DatagramSocket();		
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
