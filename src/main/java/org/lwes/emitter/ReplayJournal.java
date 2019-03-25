package org.lwes.emitter;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.lwes.Event;
import org.lwes.EventFactory;
import org.lwes.emitter.EmitterGroupBuilder;
import org.lwes.emitter.EmitterGroup;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.util.Properties;


public class ReplayJournal {

  @Option(name = "-i", usage = "journal file", required = true, metaVar = "FILE")
  String inputFilename;

  @Option(name = "-n", usage = "repeat count", metaVar = "COUNT")
  int repeatCount = 1;

  @Option(name = "-sleep", usage = "milliseconds to sleep between events", metaVar = "MILLISECONDS")
  int sleepMillis = 0;

  @Option(name = "-group", usage = "event emitter group name", required = true)
  String groupName;

  @Option(name = "-properties", usage = "properties file", required = true)
  String propertiesFilename;

  public static EventFactory factory = new EventFactory();

  private void run() throws Exception {
    Properties props = new Properties();
    props.load(new FileReader(propertiesFilename));
    EmitterGroup group = EmitterGroupBuilder.createGroup(props, groupName, factory);
    byte[] header = new byte[22];
    for (int i = 0; repeatCount < 0 || i < repeatCount; i++) {
      FileInputStream file = new FileInputStream(inputFilename);
      BufferedInputStream input = new BufferedInputStream(file);
      while (input.read(header) == 22) {
        int size = (((int)header[0] << 8) & 0xff00)
                 | (((int)header[1] << 0) & 0x00ff);
        byte[] eventData = new byte[size];
        if (input.read(eventData) != eventData.length) {
          throw new RuntimeException("bad journal");
        }
        Event event = factory.createEvent(eventData, false);
        group.emitToGroup(event);
        if (sleepMillis != 0)
          Thread.sleep(sleepMillis);
      }
    }
    System.err.println("no more data: returning");
  }

  public static void main(String[] args) throws Exception {
    ReplayJournal replayer = new ReplayJournal();
    CmdLineParser parser = new CmdLineParser(replayer);
    parser.parseArgument(args);
    replayer.run();
  }
}

