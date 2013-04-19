package org.gskbyte.bitmap;

import lombok.Getter;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * An LRU Cache for bitmaps.
 * Contains helper methods to compute bitmap memory size, because they are active
 * only in newer Android versions
 * */
public class LRUBitmapCache<KeyClass>
extends LruCache<KeyClass, Bitmap>
{

@Getter
private final int maxSize;

/**
 * Constructs an LRU cache with the given max size
 * @param maxSize The maximum cache size, in bytes
 * */
public LRUBitmapCache(int maxSize)
{ super(maxSize); this.maxSize = maxSize; }

/**
 * Constructs an LRU cache with the given max memory rate, respect to
 * the maximum available memory for the application. Good values are around 20%,
 * if this is the only LRU cache present in the application.
 * @param memoryRate The maximum memory rate to use
 * */
public LRUBitmapCache(float memoryRate)
{ this( (int) (Runtime.getRuntime().maxMemory() * memoryRate)); }

/**
 * Returns the size of a bitmap
 * */
@Override
protected int sizeOf(KeyClass key, Bitmap value)
{ return BitmapMemorySize(value); }

/**
 * Returns number of bytes per pixel given a bitmap config.
 * */
// Computation is done "by hand" becase .getByteCount() is available only from API 12
public final static int BytesPerPixel(Bitmap.Config config)
{
    switch(config) {
    case ALPHA_8: return 1;
    case ARGB_4444: return 2;
    case ARGB_8888: return 4;
    case RGB_565: return 2;
    }
    // should not happen
    return 2;
}

/**
 * Returns the memory required for a bitmap.
 * @param bitmap The bitmap for which to calculate its size.
 * @return Its size, in bytes.
 * */
public static int BitmapMemorySize(Bitmap bitmap)
{ return bitmap.getWidth() * bitmap.getHeight() * BytesPerPixel(bitmap.getConfig()); }

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
