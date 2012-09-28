package org.lwes.emitter;

import java.util.Set;

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
}
