package org.lwes.db;
/**
 * This class can be useful when trying to maintain a fairly large ESF file. It uses log4j for
 * all messages and the description below shows the log level for each check.
 *
 * Features:
 *
 * - If a field exists in all events, it will suggest you add it to MetaEventInfo. (INFO)
 * - If you change types for a field of the same name, you will get a warning. (WARN)
 *
 * User: frank.maritato
 * Date: 7/2/12
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.lwes.BaseType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ESFAnalyzer {

    private static transient Log log = LogFactory.getLog(ESFAnalyzer.class);

    @Option(name = "--file", aliases = "-f", usage = "File name", required = true)
    private String file;

    public void run(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        }
        catch (CmdLineException e) {
            System.err.println("Usage: ");
            parser.printUsage(System.err);
            throw new RuntimeException(e.getMessage(), e);
        }
        EventTemplateDB db = null;
        try {
            db = parseFile(file);
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            return;
        }

        Map<String, BaseType> metaFields = db.getMetaFields();
        Map<String, MutableInt> freq = new HashMap<String, MutableInt>();
        Map<String, BaseType> typeMap = new HashMap<String, BaseType>();
        Map<String, Map<String, BaseType>> events = db.getEvents();
        int eventCount = 0;
        for (String evt : events.keySet()) {
            eventCount++;
            Map<String, BaseType> fields = events.get(evt);
            for (String key : fields.keySet()) {
                if (typeMap.containsKey(key)) {
                    BaseType thisType = fields.get(key);
                    BaseType thatType = typeMap.get(key);
                    if (!thisType.getType().equals(thatType.getType())) {
                        log.warn("Event: " + evt + " Key: " + key + " has a different type: (" +
                                 thisType.getType() + "/" + thatType.getType() + ")");
                    }
                }
                else {
                    typeMap.put(key, fields.get(key));
                }
                MutableInt count = freq.get(key);
                if (count == null) {
                    freq.put(key, new MutableInt());
                }
                else {
                    count.increment();
                }
            }
        }
        for (String key : freq.keySet()) {
            MutableInt count = freq.get(key);
            if (count != null &&
                count.get() == eventCount &&
                !metaFields.containsKey(key)) {
                log.info("Field: " + key + " can be moved to MetaEventInfo");
            }
        }
    }

    public EventTemplateDB parseFile(String file) {
        EventTemplateDB db = new EventTemplateDB();
        db.setESFFile(new File(file));
        db.initialize();
        return db;
    }

    public static void main(String[] args) {
        new ESFAnalyzer().run(args);
    }

    class MutableInt {
        int value = 1;

        public void increment() {
            ++value;
        }

        public int get() {
            return value;
        }
    }
}
