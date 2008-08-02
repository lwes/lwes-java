package org.lwes.listener;

import org.lwes.Event;

public class EventPrintingHandler implements EventHandler {
	public EventPrintingHandler() {
	}
	
	public void handleEvent(Event event) {
		System.out.println(event.toString());
	}
}
