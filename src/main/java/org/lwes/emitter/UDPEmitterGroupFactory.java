package org.lwes.emitter;

import org.lwes.Event;
import org.lwes.EventFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;


public class UDPEmitterGroupFactory implements EmitterGroupFactory {
  private static final Logger LOG = Logger.getLogger(EmitterGroupBuilder.class);

  private static String STRATEGY_ALL = "all";
  private static Pattern STRATEGY_M_OF_N = Pattern.compile("([\\d]*)ofN", Pattern.CASE_INSENSITIVE);
  private static Pattern STRATEGY_NESTED = Pattern.compile("(([\\d|]*)ofN|all)_(([\\d]*)ofN|all)", Pattern.CASE_INSENSITIVE);
  private static Pattern nestedTuplePattern = Pattern.compile("^((\\([^\\(]+?\\))(,)*)+$", Pattern.CASE_INSENSITIVE);
  private static Pattern tuplePattern = Pattern.compile("\\([^\\(]+?\\)", Pattern.CASE_INSENSITIVE);

  @Override
  public EmitterGroup create(Properties props, String groupName, String prefix, EventFactory factory) throws IOException {
    String defaultPortStr = props.getProperty(prefix + "port");
    int defaultPort = (defaultPortStr != null) ? Integer.parseInt(defaultPortStr) : -1;

    String hostsStr = props.getProperty(prefix + "hosts");
    String strategy = props.getProperty(prefix + "strategy");
    boolean emitHeartbeat =
      Boolean.parseBoolean(props.getProperty(prefix + "emit_heartbeat"));

    String rateStr = props.getProperty(prefix + "sample_rate");
    double defaultSampleRate = null == rateStr ? 1.0 : Double.parseDouble(rateStr);

    EmitterGroupFilter filter = EmitterGroupFilter.fromProperties(props, prefix);
    if (filter != null) {
      LOG.info(String.format("Emitter group %s : %s", prefix, filter));
    }

    if (STRATEGY_NESTED.matcher(strategy).matches()) {
      return buildNestedEmitterGroup(prefix, hostsStr, strategy, defaultPort, filter, defaultSampleRate, emitHeartbeat, factory);
    }

    DatagramSocketEventEmitter<?>[] emitters =
      createEmitters(groupName, prefix, hostsStr, defaultPort, emitHeartbeat, factory);

    if (strategy == null || strategy.isEmpty()) {
      throw new RuntimeException(
          String.format(
              "No strategy specified for emitter group %s - not set in %s property",
              groupName,
              prefix + "strategy"));
    }

    Matcher mOfN = STRATEGY_M_OF_N.matcher(strategy);

    if (STRATEGY_ALL.equalsIgnoreCase(strategy)) {
      return new BroadcastEmitterGroup(emitters, filter, defaultSampleRate, factory);
    } else if (mOfN.matches()) {
      return new MOfNEmitterGroup(emitters, Integer.parseInt(mOfN.group(1)), filter, defaultSampleRate, factory);
    } else {
      throw new RuntimeException(
          String.format(
              "Invalid strategy '%s' given for emitter group %s in property %s.",
              strategy,
              prefix,
              prefix + "strategy"));
    }
  }

  private static EmitterGroup buildNestedEmitterGroup(String prefix, String hostsStr, String strategyStr, int port, EmitterGroupFilter filter, double sampleRate, boolean emitHeartbeat, EventFactory factory) throws IOException {
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
      DatagramSocketEventEmitter<?>[] emitters =
        createEmitters(group, prefix, groupHosts, port, emitHeartbeat, factory);
      MOfNEmitterGroup meg = new MOfNEmitterGroup(emitters, hostEmitCount == -1 ? emitters.length : hostEmitCount, filter, factory);
      emitterGroups.add(meg);
    }

    int groupEmitCount = getEmitCount(ratioConfig[0]);
    EmitterGroup[] emitterGroupsArray = emitterGroups.toArray(new EmitterGroup[emitterGroups.size()]);
    return new NestedEmitterGroup(emitterGroupsArray, groupEmitCount == -1 ? emitterGroupsArray.length : groupEmitCount, filter, sampleRate, factory);
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

  private static DatagramSocketEventEmitter<?>[] createEmitters(String groupName, String prefix, String hostsStr, int defaultPort, boolean emitHeartbeat, EventFactory factory) throws IOException {
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
        MulticastEventEmitter mee =
          (factory == null ? new MulticastEventEmitter() :
                             new MulticastEventEmitter(factory));

        mee.setInterface(iface);

        if (ttl > 0) {
          mee.setTimeToLive(ttl);
        }

        emitters[i] = mee;
      } else {
        emitters[i] =
          (factory == null ? new UnicastEventEmitter() :
                             new UnicastEventEmitter(factory));
      }

      emitters[i].setAddress(address);
      emitters[i].setPort(port);
      emitters[i].setEmitHeartbeat(emitHeartbeat);
      emitters[i].initialize();
    }

    return emitters;
  }

}
