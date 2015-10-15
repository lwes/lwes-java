/*======================================================================*
 * Copyright OpenX Limited 2010. All Rights Reserved.                   *
 *                                                                      *
 * Licensed under the New BSD License (the "License"); you may not use  *
 * this file except in compliance with the License.  Unless required    *
 * by applicable law or agreed to in writing, software distributed      *
 * under the License is distributed on an "AS IS" BASIS, WITHOUT        *
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     *
 * See the License for the specific language governing permissions and  *
 * limitations under the License. See accompanying LICENSE file.        *
 *======================================================================*/
package org.lwes.emitter;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.lwes.emitter.EmitterGroupFilter.FilterType;

/**
 * @author Joel Meyer
 *
 */
public class EmitterGroupBuilder {
  private static final Logger LOG = Logger.getLogger(EmitterGroupBuilder.class);

  private static String STRATEGY_ALL = "all";
  private static Pattern STRATEGY_M_OF_N = Pattern.compile("([\\d]*)ofN", Pattern.CASE_INSENSITIVE);
  private static Pattern STRATEGY_NESTED = Pattern.compile("(([\\d|]*)ofN|all)_(([\\d]*)ofN|all)", Pattern.CASE_INSENSITIVE);
  private static Pattern nestedTuplePattern = Pattern.compile("^((\\([^\\(]+?\\))(,)*)+$", Pattern.CASE_INSENSITIVE);
  private static Pattern tuplePattern = Pattern.compile("\\([^\\(]+?\\)", Pattern.CASE_INSENSITIVE);

  public static EmitterGroup[] createGroups(Properties props) throws IOException {
    String groupsStr = props.getProperty("lwes.emitter_groups");
    String[] groups = groupsStr.split(",");

    EmitterGroup[] emitterGroups = new EmitterGroup[groups.length];

    for (int i = 0; i < groups.length; i++) {
      emitterGroups[i] = createGroup(props, groups[i]);
    }

    return emitterGroups;
  }

  public static EmitterGroupFilter getEmitterGroupFilter(Properties props, String prefix) {
    String filterType = props.getProperty(prefix + "filter.type");
    if (filterType == null) return null;

    FilterType type = null;

    if ("inclusion".equalsIgnoreCase(filterType) ||
        "whitelist".equalsIgnoreCase(filterType) ||
        "in".equalsIgnoreCase(filterType)) {
      type = FilterType.Inclusion;
    } else if ("exclusion".equalsIgnoreCase(filterType) ||
               "blacklist".equalsIgnoreCase(filterType) ||
               "out".equalsIgnoreCase(filterType)) {
      type = FilterType.Exclusion;
    } else {
      throw new RuntimeException(
          String.format(
              "Invalid filter type: %s. Must be one of ['inclusion', 'whitelist', 'in'] or ['exclusion','blacklist','out']",
              filterType));
    }

    String filteredNames = props.getProperty(prefix + "filter.names");
    if (filteredNames == null || filteredNames.isEmpty()) {
      return new EmitterGroupFilter(type, Collections.<String>emptySet());
    } else {
      Set<String> filtered = new HashSet<String>();
      String[] items = filteredNames.split(",");
      for (String item : items) filtered.add(item);
      return new EmitterGroupFilter(type, filtered);
    }
  }

  public static EmitterGroup createGroup(Properties props, String groupName) throws IOException {
    String prefix = "lwes." + groupName + ".";

    String defaultPortStr = props.getProperty(prefix + "port");
    int defaultPort = (defaultPortStr != null) ? Integer.parseInt(defaultPortStr) : -1;

    String hostsStr = props.getProperty(prefix + "hosts");
    String strategy = props.getProperty(prefix + "strategy");

    String rateStr = props.getProperty(prefix + "sample_rate");
    double defaultSampleRate = null == rateStr ? 1.0 : Double.parseDouble(rateStr);

    EmitterGroupFilter filter = getEmitterGroupFilter(props, prefix);
    if (filter != null) {
      LOG.info(String.format("Emitter group %s : %s", groupName, filter));
    }

    if (STRATEGY_NESTED.matcher(strategy).matches()) {
      return buildNestedEmitterGroup(prefix, hostsStr, strategy, defaultPort, filter, defaultSampleRate);
    }

    DatagramSocketEventEmitter<?>[] emitters =
      createEmitters(groupName, prefix, hostsStr, defaultPort);

    if (strategy == null || strategy.isEmpty()) {
      throw new RuntimeException(
          String.format(
              "No strategy specified for emitter group %s - not set in %s property",
              groupName,
              prefix + "strategy"));
    }

    Matcher mOfN = STRATEGY_M_OF_N.matcher(strategy);

    if (STRATEGY_ALL.equalsIgnoreCase(strategy)) {
      return new BroadcastEmitterGroup(emitters, filter, defaultSampleRate);
    } else if (mOfN.matches()) {
      return new MOfNEmitterGroup(emitters, Integer.parseInt(mOfN.group(1)), filter, defaultSampleRate);
    } else {
      throw new RuntimeException(
          String.format(
              "Invalid strategy '%s' given for emitter group %s in property %s.",
              strategy,
              groupName,
              prefix + "strategy"));
    }
  }

  private static EmitterGroup buildNestedEmitterGroup(String prefix, String hostsStr, String strategyStr, int port, EmitterGroupFilter filter, double sampleRate) throws IOException {
  String[] ratioConfig = strategyStr.split("_");
  if (null == ratioConfig || ratioConfig.length != 2) {
    throw new IllegalArgumentException("Invalid nested strategy config " + strategyStr);
  }
  if (!nestedTuplePattern.matcher(hostsStr).find()) {
    throw new IllegalArgumentException("Invalid nested hosts config " + hostsStr);
  }


  int hostEmitCount = getEmitCount(ratioConfig[1]);

  Matcher groupMatcher = tuplePattern.matcher(hostsStr);
  List<EmitterGroup> emitterGroups = new ArrayList<EmitterGroup>();
  while (groupMatcher.find()) {
    String group = groupMatcher.group();
    String groupHosts = group.replaceAll("\\(|\\)", "");
    DatagramSocketEventEmitter<?>[] emitters = createEmitters(group, prefix, groupHosts, port);
    MOfNEmitterGroup meg = new MOfNEmitterGroup(emitters, hostEmitCount == -1 ? emitters.length : hostEmitCount, filter);
    emitterGroups.add(meg);
  }

  int groupEmitCount = getEmitCount(ratioConfig[0]);
  EmitterGroup[] emitterGroupsArray = emitterGroups.toArray(new EmitterGroup[emitterGroups.size()]);
  return new NestedEmitterGroup(emitterGroupsArray, groupEmitCount == -1 ? emitterGroupsArray.length : groupEmitCount, filter, sampleRate);
  }

  /**
   * Get emit count for a MOfN type configuration string, -1 implies all of N.
   * @param config
   * @return
   */
  private static int getEmitCount(String config) {
  if (STRATEGY_ALL.equalsIgnoreCase(config)) {
    return -1;
  } else {
    Matcher mOfN = STRATEGY_M_OF_N.matcher(config);
    if (!mOfN.matches()) {
    throw new IllegalArgumentException("Unable to parse nested strategy config " + config);
    }
    return Integer.parseInt(mOfN.group(1));
    }
  }

  private static DatagramSocketEventEmitter<?>[] createEmitters(String groupName, String prefix, String hostsStr, int defaultPort) throws IOException {
    String[] hosts = hostsStr.split(",");
    DatagramSocketEventEmitter<?>[] emitters = new DatagramSocketEventEmitter<?>[hosts.length];

    for (int i = 0; i < hosts.length; i++) {
      String host = hosts[i];
      String ifaceStr = null;
      Integer port = null;
      String ttlStr = null;

      // accepted formats:
      // HOST, IFACE:HOST, HOST:PORT, IFACE:HOST:PORT, HOST:PORT:TTL, IFACE:HOST:PORT:TTL
      if (host.indexOf(":") > 0) {
        String[] parts = host.split(":");
        if (parts.length == 2) {
          try {
            port = Integer.parseInt(parts[1]);
            host = parts[0];
          } catch (NumberFormatException nfe) {
            ifaceStr = parts[0];
            host = parts[1];
          }
        } else if (parts.length == 3) {
          try {
            port = Integer.parseInt(parts[1]);
            host = parts[0];
            ttlStr = parts[2];
          } catch (NumberFormatException nfe) {
            ifaceStr = parts[0];
            host = parts[1];
            port = Integer.parseInt(parts[2]);
          }
        } else if (parts.length == 4) {
          ifaceStr = parts[0];
          host = parts[1];
          port = Integer.parseInt(parts[2]);
          ttlStr = parts[3];
        } else {
          throw new RuntimeException(
            String.format("Unable to parse LWES emitter group %s host config %s",
                          groupName, host));
        }
      }

      InetAddress address = InetAddress.getByName(host);
      InetAddress iface = (ifaceStr == null ? null : InetAddress.getByName(ifaceStr));
      int ttl = (ttlStr == null ? -1 : Integer.parseInt(ttlStr));

      if (port == null) {
        if (defaultPort < 0) {
          throw new RuntimeException(
              String.format(
                  "Unable to get port information for LWES emitter group %s - not specified " +
                  "in %s or as part of the host definition (e.g. host1:port1,host2:port2).",
                  groupName, prefix + "port"));
        } else {
          port = defaultPort;
        }
      }

      if (address.isMulticastAddress()) {
        MulticastEventEmitter mee = new MulticastEventEmitter();

        mee.setInterface(iface);

        if (ttl > 0) {
          mee.setTimeToLive(ttl);
        }

        emitters[i] = mee;
      } else {
        emitters[i] = new UnicastEventEmitter();
      }

      emitters[i].setAddress(address);
      emitters[i].setPort(port);
      emitters[i].initialize();
    }

    return emitters;
  }
}
