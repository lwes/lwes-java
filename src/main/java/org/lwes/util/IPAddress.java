/*======================================================================*
 * Copyright (c) 2008, Yahoo! Inc. All rights reserved.                 *
 *                                                                      *
 * Licensed under the New BSD License (the "License"); you may not use  *
 * this file except in compliance with the License.  Unless required    *
 * by applicable law or agreed to in writing, software distributed      *
 * under the License is distributed on an "AS IS" BASIS, WITHOUT        *
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     *
 * See the License for the specific language governing permissions and  *
 * limitations under the License. See accompanying LICENSE file.        *
 *======================================================================*/

package org.lwes.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This is a wrapper class for InetAddress, which allows the setting of
 * an InetAddress with a byte arrays.  As well as other useful functions.
 *
 * @deprecated
 * @author Anthony Molinaro
 */
public class IPAddress implements Serializable {
    private static transient Log log = LogFactory.getLog(IPAddress.class);

    /* serializable UID */
    static final long serialVersionUID = -1;

    /* inet address in network byte order */
    private byte[] inet_addr;
    private InetAddress java_inet_addr;

    /**
     * Defaut constructor
     */
    public IPAddress() {
        inet_addr = new byte[4];
        inet_addr[0] = (byte) 0;
        inet_addr[1] = (byte) 0;
        inet_addr[2] = (byte) 0;
        inet_addr[3] = (byte) 0;
        java_inet_addr = null;
    }

    /**
     * Construct an IPAddress object with a string of the form
     * "%d.%d.%d.%d" representing an InetAddress.
     * The address defaults to 0.0.0.0 if an invalid address is
     * given.
     *
     * @param anIPAddress the string representing the IPAddress
     */
    public IPAddress(String anIPAddress) {
        try {
            java_inet_addr = InetAddress.getByName(anIPAddress);
            inet_addr = java_inet_addr.getAddress();
        }
        catch (UnknownHostException e) {
            java_inet_addr = null;
            inet_addr = new byte[4];
            inet_addr[0] = (byte) 0;
            inet_addr[1] = (byte) 0;
            inet_addr[2] = (byte) 0;
            inet_addr[3] = (byte) 0;
        }
    }

    /**
     * Construct an IPAddress object with a byte array containing
     * the InetAddress in little-endian notation.
     *
     * @param anIPAddress the byte array representing the IPAddress
     */
    public IPAddress(byte[] anIPAddress) {
        if (anIPAddress.length != 4) {
            log.error("ERROR : bad inet addr\n");
            throw new RuntimeException("Bad inet address");
        }
        inet_addr = anIPAddress;
    }

    /**
     * Construct an IPAddress object with an InetAddress.
     *
     * @param anIPAddress the InetAddress to set this IPAddress to
     */
    public IPAddress(InetAddress anIPAddress) {
        java_inet_addr = anIPAddress;
        inet_addr = java_inet_addr.getAddress();
    }

    /**
     * Get the InetAddress representing this IPAddress
     *
     * @return the InetAddress representing this IPAddress
     */
    public InetAddress toInetAddress() {
        if (java_inet_addr != null) {
            return java_inet_addr;
        }
        else {
            try {
                return InetAddress.getByName(toString());
            }
            catch (UnknownHostException e) {
            }
            return null;
        }
    }

    /**
     * Return the IPAddress in a byte array in network order
     *
     * @return a byte array containing the IPAddress in network order
     */
    public byte[] getInetAddressAsBytes() {
        return inet_addr;
    }

    /**
     * Return the IPAddress in as an integer
     *
     * @return an integer representing the IPAddress in network order
     */
    public int toInt() {
        return (int) ((inet_addr[0] << 24)
                      | (inet_addr[1] << 16)
                      | (inet_addr[2] << 8)
                      | (inet_addr[3] << 0));
    }

    /**
     * return a string representation of an IPAddress in the following
     * format "%d.%d.%d.%d".
     *
     * @return a string representation of an IPAddress.
     */
    public String toString() {
        return (((int) (inet_addr[0]) & 0xff)
                + "." + ((int) (inet_addr[1]) & 0xff)
                + "." + ((int) (inet_addr[2]) & 0xff)
                + "." + ((int) (inet_addr[3]) & 0xff));
    }

    /**
     * return the size that an IPAddress takes up in a serialized byte array
     */
    public int bytesStoreSize() {
        return 4;
    }

    public boolean equals(Object o) {
        if (!(o instanceof IPAddress)) {
            /* "o" is not an IPAddress object (note: it may be null). */
            return false;
        }
        final IPAddress other = (IPAddress) o;
        final byte[] theseBytes = this.getInetAddressAsBytes();
        final byte[] otherBytes = other.getInetAddressAsBytes();
        if (theseBytes.length != otherBytes.length) {
            /* same length or else not equal */
            return false;
        }
        for (int i = 0; i < theseBytes.length; ++i) {
            if (theseBytes[i] != otherBytes[i]) {
                /* found a difference; not equal. */
                return false;
            }
        }
        return true; /* passed all tests; return true */
    }

    public int hashCode() {
        int hash = 0;
        final byte[] theseBytes = this.getInetAddressAsBytes();
        for (int i = 0; i < theseBytes.length; ++i) {
            hash ^= theseBytes[i];
        }
        return hash;
    }
}
