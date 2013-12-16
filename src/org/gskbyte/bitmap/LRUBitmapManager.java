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

public static final float DEFAULT_MEMORY_RATE = 0.33f;

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
    final int totalMemoryInMegabytes = activityManager.getMemoryClass();
    final int maxMemoryInMegabytes = (int)(totalMemoryInMegabytes * memoryRate);
    final int maxMemoryInBytes = (int) (maxMemoryInMegabytes * 1024 * 1024);
    return maxMemoryInBytes;
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
 * Frees memory from old bitmaps, given the rate of memory that we want to free up (0 = nothing, 1 = all bitmaps)
 * @param freeUpRate The amount of memory to free up
 * */
public void releaseOldBitmaps(float freeUpRate)
{
    final int initialSize = bitmapCache.size();
    
    int maxSize = -1; // Removes all, equivalent to releaseAll
    if(freeUpRate < 0)
        return;
    if(freeUpRate < 1) {
        int cacheMaxSize = bitmapCache.getMaxSize();
        maxSize = (int)((1-freeUpRate)*cacheMaxSize);
    }
    
    bitmapCache.trimToSize(maxSize);
    final int finalSize = bitmapCache.size();
    final int sizeDif = initialSize - finalSize;
    if(sizeDif != 0) {
        Logger.info( getClass(), String.format("Freed memory: %.2f MB before, %.2f MB after (%.2f MB saved)",
                (initialSize/(1024*1024f)),
                (finalSize/(1024*1024f)),
                (sizeDif/(1024*1024f))
                ) );
    }
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
