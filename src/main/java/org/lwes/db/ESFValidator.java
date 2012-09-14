package org.lwes.db;

/**
 * This class validates a given ESF. If it is parsable, it returns 0, otherwise, 1.
 * User: frank.maritato
 * Date: 9/14/12
 */

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class ESFValidator {

    private static transient Log log = LogFactory.getLog(ESFValidator.class);

    @Option(name = "-f", aliases = "--file", required = true, usage = "The ESF file to validate")
    protected String file;

    public void run(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        }
        catch (CmdLineException e) {
            System.err.println("Usage: ");
            parser.printUsage(System.err);
            System.exit(1);
        }

        try {
            parseFile(file);
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(1);
        }
        System.exit(0);
    }


    public EventTemplateDB parseFile(String file) {
        EventTemplateDB db = new EventTemplateDB();
        db.setESFFile(new File(file));
        db.initialize();
        return db;
    }

    public static void main(String[] args) {
        new ESFValidator().run(args);
    }
}
