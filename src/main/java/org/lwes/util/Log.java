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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a wrapper class for logging.  Instead of embedding log4j or JDK logging
 * statements in the code, we have a wrapper around JDK logging so this could be changed
 * easily in the future.  Also, since we prefer the distribution to be simple and
 * standalone, we aren't using Commons Logging so that we don't have to bundle that in
 * the distribution.  JDK logging could easily be redirected to the destination of the
 * library user's choice.
 *
 * @author Michael P. Lum
 */
public class Log {
	private static Logger logger = Logger.getLogger("org.lwes");

    /**
     * Check if debug logging is turned on. This should *always* be called
     * before calling Log.debug.
     *
     * @return true if debug logging is on.
     */
    public static boolean isLogDebug() {
        return logger.isLoggable(Level.FINER);
    }

    /**
     * Check if trace logging is turned on. This should *always* be called
     * before calling Log.trace.
     *
     * @return true if trace logging is on.
     */
    public static boolean isLogTrace() {
        return logger.isLoggable(Level.FINEST);
    }

    /**
     * Check if info logging is turned on. This should *always* be called
     * before calling Log.info.
     *
     * @return true if info logging is on.
     */
    public static boolean isLogInfo() {
        return logger.isLoggable(Level.FINE);
    }

	/**
	 * Log a trace level message
	 * @param message the message to be logged
	 */
	public static void trace(String message) {
		logger.log(Level.FINEST, message);
	}

	/**
	 * Log a debug level message
	 * @param message the message to be logged
	 */
	public static void debug(String message) {
		logger.log(Level.FINER, message);
	}

	/**
	 * Log a info level message
	 * @param message the message to be logged
	 */
	public static void info(String message) {
		logger.log(Level.FINE, message);
	}

	/**
	 * Log a warning level message
	 * @param message the message to be logged
	 */
	public static void warning(String message) {
		logger.log(Level.WARNING, message);
	}

	/**
	 * Log an warning level message, with associated Throwable information
	 * @param message the message to be logged
	 * @param t the Throwable associated with the log message
	 */
	public static void warning(String message, Throwable t) {
		logger.log(Level.WARNING, message, t);
	}


	/**
	 * Log an error level message
	 * @param message the message to be logged
	 */
	public static void error(String message) {
		logger.log(Level.SEVERE, message);
	}

	/**
	 * Log an error level message, with associated Throwable information
	 * @param message the message to be logged
	 * @param t the Throwable associated with the log message
	 */
	public static void error(String message, Throwable t) {
		logger.log(Level.SEVERE, message, t);
	}
}
