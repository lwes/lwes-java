package org.lwes.util;

import org.apache.commons.logging.LogFactory;

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
	private static org.apache.commons.logging.Log logger = LogFactory.getLog(Log.class);

    /**
     * Check if debug logging is turned on. This should *always* be called
     * before calling Log.debug.
     *
     * @return true if debug logging is on.
     */
    public static boolean isLogDebug() {
        return logger.isDebugEnabled();
    }

    /**
     * Check if trace logging is turned on. This should *always* be called
     * before calling Log.trace.
     *
     * @return true if trace logging is on.
     */
    public static boolean isLogTrace() {
        return logger.isTraceEnabled();
    }

    /**
     * Check if info logging is turned on. This should *always* be called
     * before calling Log.info.
     *
     * @return true if info logging is on.
     */
    public static boolean isLogInfo() {
        return logger.isInfoEnabled();
    }

	/**
	 * Log a trace level message
	 * @param message the message to be logged
	 */
	public static void trace(String message) {
		logger.trace(message);
	}

	/**
	 * Log a debug level message
	 * @param message the message to be logged
	 */
	public static void debug(String message) {
		logger.debug(message);
	}

	/**
	 * Log a info level message
	 * @param message the message to be logged
	 */
	public static void info(String message) {
		logger.info(message);
	}

	/**
	 * Log a warning level message
	 * @param message the message to be logged
	 */
	public static void warning(String message) {
		logger.warn(message);
	}

	/**
	 * Log an warning level message, with associated Throwable information
	 * @param message the message to be logged
	 * @param t the Throwable associated with the log message
	 */
	public static void warning(String message, Throwable t) {
		logger.warn(message, t);
	}


	/**
	 * Log an error level message
	 * @param message the message to be logged
	 */
	public static void error(String message) {
		logger.error(message);
	}

	/**
	 * Log an error level message, with associated Throwable information
	 * @param message the message to be logged
	 * @param t the Throwable associated with the log message
	 */
	public static void error(String message, Throwable t) {
		logger.error(message, t);
	}
}
