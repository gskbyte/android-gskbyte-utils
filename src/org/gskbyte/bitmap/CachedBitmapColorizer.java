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
package org.gskbyte.bitmap;

import lombok.Getter;

import org.gskbyte.util.Logger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

/**
 * A class that produces colorized version of Bitmaps, but keeps them in
 * a cache in order to improve speed.
 * */
public final class CachedBitmapColorizer extends BitmapColorizer
{

/**
 * Optimizd version of the BitmapCache for fixed bitmap sizes and configurations.
 * */
private final class FixedSizeBitmapCache
extends LRUBitmapCache<Integer> // int color value used as key
{
@Getter
private final int fixedBitmapByteCount;

public FixedSizeBitmapCache(float memoryRate, int fixedBitmapByteCount)
{ super(memoryRate); this.fixedBitmapByteCount = fixedBitmapByteCount;}

public FixedSizeBitmapCache(int maxSize, int fixedBitmapByteCount)
{ super(maxSize); this.fixedBitmapByteCount = fixedBitmapByteCount;}

protected int sizeOf(Integer key, Bitmap value)
{ return fixedBitmapByteCount; }

protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue)
{ Logger.info(getClass(), "Entry removed from cache, "+size()/fixedBitmapByteCount+" elements use " + cache.size()/1024 +"/" + getMaxSize()/1024 + "KB used"); }
}

private final FixedSizeBitmapCache cache;
private final int backgroundColor;

/**
 * Constructor.
 * @param bitmap The source bitmap to which to apply color.
 * @param outputConfig The bitmapConfig to use for the resulting bitmaps.
 * @param backgroundColor The background color for all generated bitmaps.
 * @param memoryRate Maximum memory rate to use, respect to the maximum allowed per application
 * */

public CachedBitmapColorizer(Bitmap bitmap, Bitmap.Config outputConfig, int backgroundColor, float memoryRate)
{
    super(bitmap, outputConfig);
    this.backgroundColor = backgroundColor;
    int bitmapMemorySize = LRUBitmapCache.BitmapMemorySize(bitmap.getWidth(), bitmap.getHeight(), outputConfig);
    cache = new FixedSizeBitmapCache(memoryRate, bitmapMemorySize);
}

/**
 * Constructor to load a bitmap from resources
 * @param Context the context from which to load the bitmap resource
 * @param drawableResource The drawable id to load the bitmap
 * @param outputConfig The bitmapConfig to use for the resulting bitmaps.
 * @param backgroundColor The background color for all generated bitmaps.
 * @param memoryRate Maximum memory rate to use, respect to the maximum allowed per application
 * */
public CachedBitmapColorizer(Context context, int drawableResource, Bitmap.Config outputConfig, int backgroundColor, float memoryRate)
{
    this(BitmapFactory.decodeResource(context.getResources(), drawableResource), outputConfig, backgroundColor, memoryRate);
}


/**
 * Applies the color given with the integer to a copy of the source bitmap,
 * which is returned. This bitmap has a transparent background (if the current
 * bitmapConfig supports it).
 * If the entry already exists in the cache, returns it.
 * @param color The color to apply to the bitmap.
 * @return A colorized copy of the source bitmap.
 * */
public Bitmap colorize(int color)
{
    Bitmap bitmap = cache.get(color);
    if(bitmap == null || bitmap.isRecycled()) {
        bitmap = super.colorize(color, backgroundColor);
        cache.put(color, bitmap);
    }
    
    return bitmap;
}

/**
 * Applies the color given with the integer to a copy of the source bitmap,
 * which is returned. This bitmap has a transparent background (if the current
 * bitmapConfig supports it).
 * If the entry already exists in the cache, returns it.
 * @param a alpha component of the color, in the range [0,255]
 * @param r red component of the color, in the range [0,255]
 * @param g green component of the color, in the range [0,255]
 * @param b blue component of the color, in the range [0,255]
 * @return A colorized copy of the source bitmap.
 * */
public Bitmap colorize(int a, int r, int g, int b)
{
    final int color = Color.argb(a, r, g, b);
    return colorize(color);
}
}
