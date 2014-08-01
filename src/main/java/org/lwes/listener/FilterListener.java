/*======================================================================*
 * Licensed under the New BSD License (the "License"); you may not use  *
 * this file except in compliance with the License.  Unless required    *
 * by applicable law or agreed to in writing, software distributed      *
 * under the License is distributed on an "AS IS" BASIS, WITHOUT        *
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     *
 * See the License for the specific language governing permissions and  *
 * limitations under the License. See accompanying LICENSE file.        *
 *======================================================================*/
package org.lwes.listener;
/**
 * This is a filtering multicast listener. It can filter by event name or any attribute of an event.
 * Specifying an attribute name more than once with multiple values creates a whitelist.
 *
 * Show only Test::Event events
 * java org.lwes.listener.FilterListener -m 224.0.0.1 -p 6001 -e "Test::Event"
 *
 * Show only events with eid=1234 AND aid=someapp
 * java org.lwes.listener.FilterListener -m 224.0.0.1 -p 6001 -a "eid=1234,aid=someapp"
 *
 * Show only events with aid=someapp OR aid=anotherapp
 * java org.lwes.listener.FilterListener -m 224.0.0.1 -p 6001 -a "eid=1234,aid=someapp,aid=anotherapp"
 *
 * Events are printed to stdout one per line.
 *
 * User: frank.maritato
 * Date: 4/9/12
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.lwes.Event;
import org.lwes.EventSystemException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class FilterListener implements EventHandler {

    private static transient Log log = LogFactory.getLog(FilterListener.class);

    @Option(name = "-m", aliases = "--multicast-address", usage = "The multicast address", required = true)
    private String multicastAddress;

    @Option(name = "-p", aliases = "--multicast-port", usage = "The multicast port", required = true)
    private int multicastPort;

    @Option(name = "-q", aliases = "--queue-size", usage = "The size of the internal queue to use.")
    private int queueSize = 5000;

    @Option(name = "-e", aliases = "--event-names", usage = "Comma separated list of event names")
    private String eventNamesList;

    @Option(name = "-a", aliases = "--eventAttrs", usage = "field=value,field2=value2 to filter on")
    private String attrList = null;

    private List<String> eventNames;
    private Map<String, Set<String>> eventAttrs;

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
        if (attrList != null && !attrList.isEmpty()) {
            eventAttrs = new HashMap<String, Set<String>>();
            String[] kvList = attrList.split(",");
            for (String kv : kvList) {
                String[] pairs = kv.trim().split("=");
                if (pairs.length == 2) {
                    if (eventAttrs.containsKey(pairs[0].trim())) {
                        eventAttrs.get(pairs[0].trim()).add(pairs[1].trim());
                    }
                    else {
                        HashSet<String> hashSet = new HashSet<String>();
                        hashSet.add(pairs[1].trim());
                        eventAttrs.put(pairs[0].trim(), hashSet);
                    }
                }
                else {
                    log.warn(kv + " not a valid k=v pair");
                }
            }
        }
        if (eventNamesList != null && !eventNamesList.isEmpty()) {
            eventNames = new LinkedList<String>();
            String[] eList = eventNamesList.split(",");
            Collections.addAll(eventNames, eList);
        }
    }

    public void run(String[] args) throws UnknownHostException,
                                          EventSystemException {

        processArguments(args);

        FilterListener l = new FilterListener();
        l.setEventNames(eventNames);
        l.setEventAttrs(eventAttrs);

        DatagramEventListener listener = new DatagramEventListener();
        listener.setAddress(InetAddress.getByName(multicastAddress));
        listener.setPort(multicastPort);
        listener.addHandler(l);
        listener.setQueueSize(queueSize);
        listener.initialize();

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
        new FilterListener().run(args);
    }

    public Map<String, Set<String>> getEventAttrs() {
        return eventAttrs;
    }

    /**
     * You can still use setEventAttrs the old way by passing an int as the second parameter,
     * that way you would not have to rewrite your code if you were using this method
     */
    public void setEventAttrs(Map<String, String> eventAttrs, int ignored) {
        this.eventAttrs = new HashMap<String, Set<String>>();
        for (String key : eventAttrs.keySet()) {
            Set<String> valSet = new HashSet<String>(1);
            valSet.add(eventAttrs.get(key));
            this.eventAttrs.put(key, valSet);
        }
    }

    public void setEventAttrs(Map<String, Set<String>> eventAttrSets) {
        eventAttrs = eventAttrSets;
    }

    public List<String> getEventNames() {
        return eventNames;
    }

    public void setEventNames(List<String> eventNames) {
        this.eventNames = eventNames;
    }

    public void destroy() {
        eventAttrs = null;
        eventNames = null;
    }

    public Event match(Event event) {
        if (eventNames != null && !eventNames.contains(event.getEventName())) {
            if (log.isDebugEnabled()) {
                log.debug("event ignored: " + event);
            }
            return null;
        }

        if (eventAttrs != null) {
            for (String key : eventAttrs.keySet()) {
                Object o = event.get(key);
                Set<String> vals = eventAttrs.get(key);
                // If more than one kv pair is submitted, events must meet
                // all conditions. If any of them fail, return.
                if (!(o != null && vals.contains(o.toString()))) {
                    return null;
                }
            }
        }

        return event;
    }

    public void handleEvent(Event event) {
        Event evt = match(event);
        if (evt != null) {
            System.out.println(evt.toOneLineString());
        }
    }
}
