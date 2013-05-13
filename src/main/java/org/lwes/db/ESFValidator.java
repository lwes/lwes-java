package org.lwes.db;

/**
 * This class validates a given ESF. If it is parsable, it returns 0, otherwise, 1.
 * User: frank.maritato
 * Date: 9/14/12
 */

import java.io.File;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class ESFValidator {

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

        if (!parseFile(file)) {
            System.exit(1);
        }
        System.exit(0);
    }


    public boolean parseFile(String file) {
        EventTemplateDB db = new EventTemplateDB();
        db.setESFFile(new File(file));
        return db.initialize();
    }

    public static void main(String[] args) {
        new ESFValidator().run(args);
    }
}
