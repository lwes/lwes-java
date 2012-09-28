// Copyright OpenX Limited 2010. All Rights Reserved.
package org.lwes.emitter;

import java.util.concurrent.atomic.AtomicInteger;

import org.lwes.Event;

/**
 * A nesting of {@link EmitterGroup} that emits events
 * using the same strategy as {@link MOfNEmitterGroup}
 * 
 */
public class NestedEmitterGroup extends EmitterGroup {
	private EmitterGroup[] emitterGroups;
	private AtomicInteger i;
	private int m;
	private int n;

	/**
	 * @param emitters
	 */
	public NestedEmitterGroup(EmitterGroup[] emittergroups, int m, EmitterGroupFilter filter) {
	  super(filter);
		this.m = m;
		this.n = emittergroups.length;
		this.emitterGroups = emittergroups;
		i = new AtomicInteger();
	}

	@Override
	protected void emit(Event e) {
    int start = i.getAndIncrement();
    int index = 0;
    for (int j = 0; j < m; j++) {
    	index = Math.abs((start + j) % n);
	    emitterGroups[index].emit(e);
	  }
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder().append("NestedEmitterGroup [m=" + m + ", n=" + n + "]:\n");
		for (EmitterGroup g : emitterGroups) {
			sb.append("\t").append(g).append("\n");
		}

		return sb.toString();
	}
}
