/*******************************************************************************
 * Copyright (c) 2013 Jose Alcalá Correa.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * Contributors:
 *     Jose Alcalá Correa - initial API and implementation
 ******************************************************************************/
package org.gskbyte.util;

/**
 * Logger class that simplifies the usage of {@link android.util.Log}.
 * 
 * Allows to configure globally if messages with info and debug levels will be
 * printed.
 * */
public final class Logger
{    
public static boolean LOG_INFO    = true;
public static boolean LOG_DEBUG   = true;

/**
 * Log an info message, if LOG_INFO is enabled.
 * @param tag The log tag.
 * @param message The message to print.
 * */
public static void info(String tag, String message)
{
    if(LOG_INFO) {
        android.util.Log.i(tag, message);
    }
}

/**
 * Log an info message, if LOG_INFO is enabled.
 * @param clazz The caller's class, whose simpleName will be used as tag.
 * @param message The message to print.
 * */
public static void info(Class<?> clazz, String message)
{ info(clazz.getSimpleName(), message); }


/**
 * Log a debug message, if LOG_DEBUG is enabled.
 * @param tag The log tag.
 * @param message The message to print.
 * */
public static void debug(String tag, String message)
{
    if(LOG_INFO) {
        android.util.Log.d(tag, message);
    }
}

/**
 * Log a debug message, if LOG_DEBUG is enabled.
 * @param clazz The caller's class, whose simpleName will be used as tag.
 * @param message The message to print.
 * */
public static void debug(Class<?> clazz, String message)
{ debug(clazz.getSimpleName(), message); }

/**
 * Log an error message
 * @param tag The log tag.
 * @param message The message to print.
 * */
public static void error(String tag, String message)
{
    if(LOG_INFO) {
        android.util.Log.e(tag, message);
    }
}
/**
 * Log an error message.
 * @param clazz The caller's class, whose simpleName will be used as tag.
 * @param message The message to print.
 * */
public static void error(Class<?> clazz, String message)
{ error(clazz.getSimpleName(), message); }

/**
 * Log an error message generated by an exception.
 * @param tag The log tag.
 * @param e The exception whose message will be printed.
 * */
public static void except(String tag, Exception e)
{ android.util.Log.e(tag, ""+e.getMessage()); }

/**
 * Log an error message generated by an exception.
 * @param clazz The caller's class, whose simpleName will be used as tag.
 * @param e The exception whose message will be printed.
 * */
public static void except(Class<?> clazz, Exception e)
{ except(clazz.getSimpleName(), e); }

/**
 * Utility method to start a time measurement. Returns a long value with the current time millis.
 * This can be used later in logTimeMeasurement()
 * @return the integer id used to log the time
 * */
public static synchronized long startTimeMeasurement()
{ return System.currentTimeMillis(); }

/**
 * Given the a long millis value (for example, given by startTimeMeasurement), logs the time passed between now and the given time.
 * Logs are printed using debug level, so they depend on LOG_DEBUG's value
 * @param startMillis The time when the measurement started
 * @param tag The log tag.
 * @param messagePrefix The base message to print with the format #message -> #time ms
 * @return the computed time difference, in milliseconds
 * */
public static synchronized long logTimeMeasurement(long startMillis, String tag, String messagePrefix)
{
    final long differenceMillis = System.currentTimeMillis()-startMillis;
    if(!LOG_DEBUG) {
        android.util.Log.d(tag, messagePrefix + " -> " + differenceMillis + " ms");
    }
    return differenceMillis;
}

/**
 * Given the a long millis value (for example, given by startTimeMeasurement), logs the time passed between now and the given time.
 * Logs are printed using debug level, so they depend on LOG_DEBUG's value
 * @param timeId The time id provided previously by startTimeMeasurement()
 * @param clazz The caller's class, whose simpleName will be used as tag.
 * @param messagePrefix The base message to print with the format #message -> #time ms
 * @return the computed time difference, in milliseconds
 * */
public static synchronized long logTimeMeasure(int timeId, Class<?> clazz, String messagePrefix)
{ return logTimeMeasurement(timeId, clazz.getSimpleName(), messagePrefix); }

}
