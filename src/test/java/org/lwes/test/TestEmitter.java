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
import org.lwes.Event;
import org.lwes.emitter.MulticastEventEmitter;

import java.net.InetAddress;

// Make sure junit ignores this file. It really should be located somewhere else.
@Ignore
public class TestEmitter {
	public static void main(String[] args) {
		try {
			MulticastEventEmitter emitter = new MulticastEventEmitter();
			emitter.setMulticastAddress(InetAddress.getByName("224.0.0.69"));
			emitter.setMulticastPort(9191);
			emitter.initialize();

			Event e = emitter.createEvent("MyEvent", false);
			e.setBoolean("boolean", true);
			e.setInt16("int16", (short) 12345);
			e.setUInt16("uint16", 56789);
			e.setInt32("int32", 55555555);
			e.setUInt32("uint32", 20392039402L);
			e.setInt64("int64", (long) 9999999);
			e.setUInt64("uint64", new java.math.BigInteger("9999999"));
			e.setIPAddress("ip", InetAddress.getByName("24.199.3.198"));
			e.setString("key", "value");
			emitter.emit(e);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
