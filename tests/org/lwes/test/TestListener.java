package org.lwes.test;

import java.net.*;
import org.lwes.listener.*;

public class TestListener {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		 EventHandler myHandler = new EventPrintingHandler();
		 
		 try {
			 InetAddress address = InetAddress.getByName("224.0.0.69");
			 DatagramEventListener listener = new DatagramEventListener();
			 listener.setAddress(address);
			 listener.setPort(9191);
			 listener.addHandler(myHandler);
			 listener.initialize();
		 } catch(Exception e) {
			e.printStackTrace();
		 }

		 // keep this thread busy
		 while(true) { try { Thread.sleep(1000);} catch(InterruptedException ie) {} }
	}
}
