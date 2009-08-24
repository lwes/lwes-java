package org.lwes.listener;

import org.lwes.Event;

public class EventPrintingHandler implements EventHandler {
	public EventPrintingHandler() {
	}
	
	public void handleEvent(Event event) {
		System.out.println(event.toString());
	}

    public void destroy() {
        // no need to do anything here
    }
}
