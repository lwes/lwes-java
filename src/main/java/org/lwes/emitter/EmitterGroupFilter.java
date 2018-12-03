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

import java.util.Set;
import java.util.HashSet;
import java.util.Properties;
import java.util.Collections;

public class EmitterGroupFilter {
  public static enum FilterType {
    Inclusion,
    Exclusion
  }

  protected final FilterType type;
  protected final Set<String> filtered;

  public EmitterGroupFilter(FilterType type, Set<String> eventNames) {
    this.type = type;
    this.filtered = eventNames;
  }

  public boolean shouldEmit(String eventName) {
    boolean inFilter = filtered.contains(eventName);
    return type == FilterType.Inclusion ? inFilter : !inFilter;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(type.toString());
    sb.append(" filter for ");
    for (String e : filtered) sb.append(e).append(" ");
    return sb.toString();
  }


  // Helper method
  public static EmitterGroupFilter fromProperties(Properties props, String prefix) {
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

}
