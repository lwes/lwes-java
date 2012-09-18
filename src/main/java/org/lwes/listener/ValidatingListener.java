package org.lwes.listener;
/**
 * This class is an event listener that will validate received events against an ESF. Results will be logged.
 *
 * User: frank.maritato
 * Date: 9/17/12
 */

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.lwes.Event;
import org.lwes.EventSystemException;
import org.lwes.ValidationExceptions;
import org.lwes.db.EventTemplateDB;

public class ValidatingListener implements EventHandler {

    private static transient Log log = LogFactory.getLog(ValidatingListener.class);

    @Option(name = "-f", aliases = "--esf-file", usage = "ESF File to validate against.", required = true)
    protected String esf;

    @Option(name = "-m", aliases = "--address", usage = "The multicast address", required = true)
    protected String address;

    @Option(name = "-p", aliases = "--port", usage = "The multicast port", required = true)
    protected int port;

    @Option(name = "-q", aliases = "--queue-size", usage = "The size of the internal queue to use.")
    protected int queueSize = 5000;

    private EventTemplateDB db = new EventTemplateDB();

    public void processArguments(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        }
        catch (CmdLineException e) {
            System.err.println("Usage: ");
            parser.printUsage(System.err);
            throw new RuntimeException(e.getMessage(), e);
        }

        db.setESFFile(new File(esf));
        db.initialize();
    }

    public void run(String[] args) throws UnknownHostException,
                                          EventSystemException {

        processArguments(args);

        DatagramEventListener listener = new DatagramEventListener();
        listener.setAddress(InetAddress.getByName(address));
        listener.setPort(port);
        listener.addHandler(this);
        listener.setQueueSize(queueSize);
        listener.initialize();

        if (log.isInfoEnabled()) {
            log.info("Address: "+listener.getAddress());
            log.info("Port: "+listener.getPort());
        }

        while (true) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws UnknownHostException,
                                                  EventSystemException {
        new ValidatingListener().run(args);
    }

    public void handleEvent(Event event) {
        try {
            db.validate(event);
            log.info("Event OK: "+event.toOneLineString());
        }
        catch (ValidationExceptions e) {
            log.error(event.toOneLineString()+" "+e.toString());
        }
    }

    public void destroy() {

    }
}
