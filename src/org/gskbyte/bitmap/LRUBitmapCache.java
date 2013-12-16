package org.gskbyte.bitmap;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;

/**
 * An LRU Cache for bitmaps.
 * Contains helper methods to compute bitmap memory size, because they are active
 * only in newer Android versions
 * */
public class LRUBitmapCache<KeyClass>
extends LruCache<KeyClass, Bitmap>
{

private final int maxSize;

/**
 * Constructs an LRU cache with the given max size
 * @param maxSize The maximum cache size, in bytes
 * */
public LRUBitmapCache(int maxSize)
{ super(maxSize); this.maxSize = maxSize; }

/**
 * Returns the maximum size of the whole cache, in bytes.
 * @return the maximum cache size in bytes
 * */
public int getMaxSize()
{ return maxSize; }

/**
 * Returns the size of a bitmap given its key. Check the method sizeOf in Android's LRUCache class documentation
 * @param key the key of the saved bitmap
 * @param value the bitmap for which to get the size
 * */
@Override
protected int sizeOf(KeyClass key, Bitmap value)
{ return BitmapMemorySize(value); }

/**
 * Returns the size of a Bitmap given its key
 * @param key The key of the Bitmap to retrieve
 * @return the size in bytes, or -1 if the bitmap is not managed
 * */
public int sizeOf(KeyClass key)
{
    Bitmap b = get(key);
    if(b != null) {
        return BitmapMemorySize( get(key) );
    } else {
        return -1;
    }
}

/**
 * Returns number of bytes per pixel given a bitmap config.
 * */
public final static int BytesPerPixel(Bitmap.Config config)
{
    switch(config) {
    case ALPHA_8:
        return 1;
    case ARGB_8888:
        return 4;
    
    default:
    case ARGB_4444:
    case RGB_565:
        return 2;
    }
}

/**
 * Returns the memory required for a bitmap.
 * @param bitmap The bitmap for which to calculate its size.
 * @return Its size, in bytes.
 * */
@SuppressLint("NewApi")
public static int BitmapMemorySize(Bitmap bitmap)
{
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
        return bitmap.getByteCount();
    } else {
        return bitmap.getWidth() * bitmap.getHeight() * BytesPerPixel(bitmap.getConfig());
    }
}

/**
 * Returns the memory required for a bitmap configuration
 * @param width The bitmap's width.
 * @param height The bitmap's height.
 * @param config The bitmap's color configuration.
 * @return Its size, in bytes.
 * */
public final static int BitmapMemorySize(int width, int height, Bitmap.Config config)
{ return width * height * BytesPerPixel(config); }


}
