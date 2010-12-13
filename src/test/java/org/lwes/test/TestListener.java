/*======================================================================*
 * Copyright (c) 2010, Frank Maritato All rights reserved.              *
 *                                                                      *
 * Licensed under the New BSD License (the "License"); you may not use  *
 * this file except in compliance with the License.  Unless required    *
 * by applicable law or agreed to in writing, software distributed      *
 * under the License is distributed on an "AS IS" BASIS, WITHOUT        *
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     *
 * See the License for the specific language governing permissions and  *
 * limitations under the License. See accompanying LICENSE file.        *
 *======================================================================*/

package org.lwes.test;

import org.junit.Ignore;
import org.lwes.listener.DatagramEventListener;
import org.lwes.listener.EventHandler;
import org.lwes.listener.EventPrintingHandler;

import java.net.InetAddress;

// Make sure junit ignores this file. It really should be located somewhere else.
@Ignore
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
