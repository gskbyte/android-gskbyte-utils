package org.gskbyte.util;

import android.util.SparseArray;

public final class Logger
{    
    public static final boolean LOG_INFO    = true;
    public static final boolean LOG_DEBUG   = true;
    
    private static int CurrentTimeId = 0;
    private static final SparseArray<Long> TimeMeasurements = new SparseArray<Long>();
    
    public static void info(String tag, String message)
    {
        if(LOG_INFO) {
            android.util.Log.i(tag, message);
        }
    }
    public static void info(Class<?> clazz, String message)
    { info(clazz.getSimpleName(), message); }

    public static void debug(String tag, String message)
    {
        if(LOG_INFO) {
            android.util.Log.d(tag, message);
        }
    }
    public static void debug(Class<?> clazz, String message)
    { debug(clazz.getSimpleName(), message); }
    
    public static void error(String tag, String message)
    {
        if(LOG_INFO) {
            android.util.Log.e(tag, message);
        }
    }
    public static void error(Class<?> clazz, String message)
    { error(clazz.getSimpleName(), message); }

    public static void except(String tag, Exception exception)
    {
        android.util.Log.e(tag, ""+exception.getMessage());
    }
    public static void except(Class<?> clazz, Exception exception)
    { except(clazz.getSimpleName(), exception); }
    
    public static int startTimeMeasurement()
    {
        if(LOG_INFO) {
            final int timeId = ++CurrentTimeId;
            TimeMeasurements.put(timeId, System.currentTimeMillis());
            return timeId;
        } else {
            return -1;
        }
    }
    
    public static void endTimeMeasurement(int timeId, String tag, String message)
    {
        Long start = TimeMeasurements.get(timeId);
        if(start != null) {
            long time = System.currentTimeMillis()-start;
            TimeMeasurements.remove(timeId);
            android.util.Log.d(tag, message + " -> " + time + " ms");
        }
    }
    
    public static void endTimeMeasure(int timeId, Class<?> clazz, String message)
    { endTimeMeasurement(timeId, clazz.getSimpleName(), message); }
}
