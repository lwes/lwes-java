package org.lwes.emitter;
/**
 * User: frank.maritato
 * Date: 9/17/12
 */

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.lwes.BaseType;
import org.lwes.Event;
import org.lwes.EventSystemException;
import org.lwes.RandomEventGenerator;
import org.lwes.db.EventTemplateDB;

public class RandomEventEmitter {

    private static transient Log log = LogFactory.getLog(RandomEventEmitter.class);

    @Option(name = "-f", aliases = "--esf-file", usage = "ESF File to validate against.", required = true)
    protected String esf;

    @Option(name = "-m", aliases = "--address", usage = "The multicast address", required = true)
    protected String address;

    @Option(name = "-p", aliases = "--port", usage = "The multicast port", required = true)
    protected int port;

    @Option(name = "-n", aliases = "--num", usage = "The number of random events to send")
    protected int num = 1000;

    private EventTemplateDB db = new EventTemplateDB();
    private String[] eventNames;

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
        Set<String> names = db.getEventNameSet();
        eventNames = new String[names.size()];
        names.toArray(eventNames);
    }

    public void run(String[] args) throws IOException,
                                          EventSystemException {

        processArguments(args);

        MulticastEventEmitter emitter = new MulticastEventEmitter();
        emitter.setMulticastAddress(InetAddress.getByName(address));
        emitter.setMulticastPort(port);
        emitter.initialize();

        RandomEventGenerator reg = new RandomEventGenerator();

        for (int i=0; i<num; i++) {
            Event event = reg.getRandomEvent(eventNames);
            Map<String, BaseType> fields = db.getBaseTypesForEvent(event.getEventName());
            reg.fillRandomFields(event, fields, 10, true);
            log.info(event.toOneLineString());
            emitter.emit(event);
        }
    }

    public static void main(String[] args) throws IOException,
                                                  EventSystemException {
        new RandomEventEmitter().run(args);
    }
}
