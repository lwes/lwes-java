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

import java.util.HashMap;
import java.util.Properties;
import java.io.IOException;
import org.lwes.EventFactory;

/**
 * <p>Build an array of emitter groups from a java Properties object.</p>
 * <p>A registration mechanism is available to allow plugins to
 * define new "types" of EmitterGroups.</p>
 *
 * @author Joel Meyer
 */
public class EmitterGroupBuilder {
  private static HashMap<String, EmitterGroupFactory> emitterGroupFactoryRegistry;
  static {
    registerEmitterGroupFactory("udp", new UDPEmitterGroupFactory());
  }


  /**
   * <p>Register a new 'type' of emitter.</p>
   * <p>The default emitter is 'udp'.</p>
   * <p>A 'gcs' emitter is available as a plugin.</p>
   *
   * @param type the name of the type of the emitter,
   * which must be given as lwes.{{GROUPNAME}}.type in the configuration.
   * The default type, and the only builtin type, is 'udp'.
   * @param factory An interface that spits out 
   * EmitterGroups, give a Properties object and a prefix to look under.
   */
  public synchronized static void registerEmitterGroupFactory(String type, EmitterGroupFactory factory) {
    //
    // This pattern is, of course, not thread safe, but all registration
    // should occur on the main java thread.
    //
    if (emitterGroupFactoryRegistry == null) {
      emitterGroupFactoryRegistry = new HashMap<String, EmitterGroupFactory>();
    }

    emitterGroupFactoryRegistry.put(type, factory);
  }

  public static EmitterGroup[] createGroups(Properties props,
                                            EventFactory factory)
      throws IOException {
    String groupsStr = props.getProperty("lwes.emitter_groups");
    String[] groups = groupsStr.split(",");

    EmitterGroup[] emitterGroups = new EmitterGroup[groups.length];

    for (int i = 0; i < groups.length; i++) {
      emitterGroups[i] = createGroup(props, groups[i], factory);
    }

    return emitterGroups;
  }

  public static EmitterGroup[] createGroups(Properties props) throws IOException {
    return createGroups(props, null);
  }


  public static EmitterGroup createGroup(Properties props, String groupName,
                                         EventFactory factory)
      throws IOException {
    String prefix = "lwes." + groupName + ".";
    String type = props.getProperty(prefix + "type");
    if (type == null) {
      type = "udp";
    }
    EmitterGroupFactory f = emitterGroupFactoryRegistry.get(type);
    if (f == null) {
      throw new RuntimeException("no emitter-group factory of type " + type + " known");
    }
    return f.create(props, groupName, prefix, factory);
  }


}
