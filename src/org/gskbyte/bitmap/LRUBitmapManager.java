package org.gskbyte.bitmap;

import org.gskbyte.util.Logger;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;

/**
 * LRUBitmapManager class
 * 
 * This implementation of AbstractBitmapManager uses an LRUCache to store bitmaps
 * and refer to them. This allows the programmer not to abuse of memory usage, while not
 * keeping too much attention on it.
 * 
 * */
public class LRUBitmapManager
extends AbstractBitmapManager
{

public static final float DEFAULT_MEMORY_RATE = 0.25f;

private final LRUBitmapCache<String> bitmapCache;

/**
 * Constructor. Uses a memory rate of 25%, which is a good value when this
 * is the only LRUBitmapCache present in an application.
 * @param context The application context, needed to load files.
 * */
public LRUBitmapManager(Context context)
{
    this(context, DEFAULT_MEMORY_RATE);
}

/**
 * Constructor. Allows to specify a maximum memory rate. Low values will cause the Bitmaps to load
 * frequently, which can lead to a slow application. Too high values can provoke an OutOfMemoryError.
 * @param context The application context, needed to load files.
 * @param memoryRate The maximum application's memory rate to be used by this manager.
 * */
public LRUBitmapManager(Context context, float memoryRate)
{
    super(context);
    final int maxSize = MaxMemorySizeForRate(context, memoryRate);
    this.bitmapCache = new LRUBitmapCache<String>(maxSize);
}

public LRUBitmapManager(Context context, int numLoadThreads, float memoryRate)
{
    super(context, numLoadThreads);
    final int maxSize = MaxMemorySizeForRate(context, memoryRate);
    this.bitmapCache = new LRUBitmapCache<String>(maxSize);
}

public static final int MaxMemorySizeForRate(Context context, float memoryRate)
{
    ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    final int totalMemory = activityManager.getMemoryClass();
    final int memoryAfterRate = (int) (totalMemory * 1024 * 1024 * memoryRate);
    return memoryAfterRate;
}

protected BitmapRef initializeReference(int location, String path)
{
    return new LRUBitmapRef(location, path);
}


@Override
public void clear()
{
    super.clear();
    bitmapCache.evictAll();
}

/**
 * This could be optimized having a counter, but this method is not very likely
 * to be used, we keep it simple.
 * */
@Override
public int countLoadedBitmaps()
{
    int count = 0;
    for(String s : references.keySet()) {
        if(bitmapCache.get(s) != null)
            ++count;
    }
    
    return count;
}

@Override
public void releaseAllBitmaps()
{
    bitmapCache.evictAll();
}

/**
 * Specialization of a BitmapReference. Refers to Bitmaps from the Manager's LRUCache.
 * */
final class LRUBitmapRef
extends AbstractBitmapManager.BitmapRef
{

public LRUBitmapRef(int location, String path)
{
    super(location, path);
}

@Override
public boolean isLoaded()
{
    return bitmapCache.get(path) != null;
}

@Override
public Bitmap getBitmap(ScaleMode scaleMode, int maxWidth, int maxHeight)
{
    Bitmap bitmap = bitmapCache.get(path);
    if(bitmap == null) {
        bitmap = loadBitmap(scaleMode, maxWidth, maxHeight);
        if(bitmap != null) {
            bitmapCache.put(path, bitmap);
        } else {
            Logger.error(getClass(), "Bitmap not found: " + path);
        }
    }
    return bitmap;
}

@Override
public void freeResources()
{
    bitmapCache.remove(path);
}

}

}
