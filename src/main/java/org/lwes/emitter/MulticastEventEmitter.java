package org.lwes.emitter;

import org.lwes.Event;
import org.lwes.EventSystemException;
import org.lwes.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * MulticastEventEmitter emits events to multicast groups on the network.  This is the most common
 * class used by users of the Light Weight Event System.
 * <p/>
 * Example code:
 * <pre>
 * MulticastEventEmitter emitter = new MulticastEventEmitter();
 * emitter.setESFFilePath("/path/to/esf/file");
 * emitter.setMulticastAddress(InetAddress.getByName("224.0.0.69"));
 * emitter.setMulticastPort(9191);
 * emitter.initialize();
 * <p/>
 * Event e = emitter.createEvent("MyEvent", false);
 * e.setString("key","value");
 * emitter.emit(e);
 * </pre>
 *
 * @author Michael P. Lum
 * @author Anthony Molinaro
 * @since 0.0.1
 */
public class MulticastEventEmitter extends AbstractEventEmitter {

    /* the actual multicast socket being used */
    private MulticastSocket socket = null;

    /* the multicast address */
    private InetAddress address = null;

    /* the multicast port */
    private int port = 9191;

    /* the multicast interface */
    private InetAddress iface = null;

    /* the multicast time-to-live */
    private int ttl = 31;

    /* a lock variable to synchronize events */
    private final Object lock = new Object();

    /**
     * Default constructor.
     */
    public MulticastEventEmitter() {
    }

    /**
     * Sets the multicast address for this emitter.
     *
     * @param address the multicast address
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
     * @param port the multicast port
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
     * Sets the network interface for this emitter.
     *
     * @param iface the network interface
     */
    public void setInterface(InetAddress iface) {
        this.iface = iface;
    }

    /**
     * Gets the network interface for this emitter.
     *
     * @return the interface address
     */
    public InetAddress getInterface() {
        return this.iface;
    }

    /**
     * Sets the multicast time-to-live for this emitter.
     *
     * @param ttl the time to live
     */
    public void setTimeToLive(int ttl) {
        this.ttl = ttl;
    }

    /**
     * Gets the multicast time-to-live for this emitter.
     *
     * @return the time to live
     */
    public int getTimeToLive() {
        return this.ttl;
    }

    /**
     * Sets the ESF file used for event validation.
     *
     * @param esfFilePath the path of the ESF file
     */
    public void setESFFilePath(String esfFilePath) {
        if (getFactory() != null) {
            getFactory().setESFFilePath(esfFilePath);
        }
    }

    /**
     * Gets the ESF file used for event validation
     *
     * @return the ESF file path
     */
    public String getESFFilePath() {
        if (getFactory() != null) {
            return getFactory().getESFFilePath();
        }
        else {
            return null;
        }
    }

    /**
     * Sets an InputStream to be used for event validation.
     *
     * @param esfInputStream an InputStream used for event validation
     */
    public void setESFInputStream(InputStream esfInputStream) {
        if (getFactory() != null) {
            getFactory().setESFInputStream(esfInputStream);
        }
    }

    /**
     * Gets the InputStream being used for event validation.
     *
     * @return the InputStream of the ESF validator
     */
    public InputStream getESFInputStream() {
        if (getFactory() != null) {
            return getFactory().getESFInputStream();
        }
        else {
            return null;
        }
    }

    /**
     * Initializes the emitter.
     */
    public void initialize() throws IOException {
        socket = new MulticastSocket();

        if (iface != null) {
            socket.setInterface(iface);
        }

        socket.setTimeToLive(ttl);
        super.initialize();
    }

    /**
     * Shuts down the emitter.
     */
    public void shutdown() throws IOException {
        socket.close();
        super.shutdown();
    }

    /**
     * Creates a new event named <tt>eventName</tt>.
     *
     * @param eventName the name of the event to be created
     * @return a new Event
     * @throws EventSystemException if there is a problem creating the event
     */
    public Event createEvent(String eventName) throws EventSystemException {
        return createEvent(eventName, true);
    }

    /**
     * Creates a new event named <tt>eventName</tt>.
     *
     * @param eventName the name of the event to be created
     * @param validate  whether or not to validate the event against the EventTemplateDB
     * @return a new Event
     * @throws EventSystemException if there is a problem creating the event
     */
    public Event createEvent(String eventName, boolean validate) throws EventSystemException {
        if (getFactory() != null) {
            return getFactory().createEvent(eventName, validate);
        }
        else {
            throw new EventSystemException("EventFactory not initialized");
        }
    }

    /**
     * Emits the event to the network.
     *
     * @param event the event to emit
     * @throws IOException throws an IOException is there is a network error.
     */
    public void emit(Event event) throws IOException {
        byte[] msg = event.serialize();

        synchronized (lock) {
            emit(msg);
            try {
                collectStatistics();
            }
            catch (EventSystemException e) {
                Log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Emits a byte array to the network.
     *
     * @param bytes the byte array to emit
     * @throws IOException throws an IOException if there is a network error.
     */
    protected void emit(byte[] bytes) throws IOException {
        /* don't bother with empty arrays */
        if (bytes == null) {
            return;
        }

        /* construct a datagram */
        DatagramPacket dp = new DatagramPacket(bytes, bytes.length, address, port);
        socket.send(dp);
    }
}
