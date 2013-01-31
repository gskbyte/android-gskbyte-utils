package org.gskbyte.ui;

import lombok.Getter;

import org.gskbyte.util.Logger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.util.LruCache;

public final class CachedBitmapColorizer extends BitmapColorizer
{
    private final class BitmapCache extends LruCache<Integer, Bitmap>
    {
        public BitmapCache(int maxSize)
        { super(maxSize); }
        
        protected int sizeOf(Integer key, Bitmap value)
        { return bitmapByteCount; }
        
        protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue)
        { Logger.info(getClass(), "Entry removed from cache, "+cache.size()/bitmapByteCount+" elements use " + cache.size()/1024 +"/" + maxCacheSize/1024 + "KB used"); }
    }
    
    private final BitmapCache cache;
    
    @Getter
    private final int maxCacheSize;
    
    @Getter
    private final int bitmapByteCount;
    
    public CachedBitmapColorizer(Context context, int drawableResource, Bitmap.Config bitmapConfig, float memoryRate)
    {
        this(context, BitmapFactory.decodeResource(context.getResources(), drawableResource), bitmapConfig, memoryRate);
    }
    
    public CachedBitmapColorizer(Context context, Bitmap bitmap, Bitmap.Config bitmapConfig, float memoryRate)
    {
        super(context, bitmap, bitmapConfig);
        int bbc = baseBitmap.getWidth() * baseBitmap.getHeight();
        
        // Computation is done "by hand" becase .getByteCount() is available only from API 12
        switch(bitmapConfig) {
        case ALPHA_8: bbc*= 1; break;
        case ARGB_4444: bbc*= 2; break;
        case ARGB_8888: bbc*=4; break;
        case RGB_565: bbc*=2; break;
        }
        bitmapByteCount = bbc;
        maxCacheSize    = (int) (Runtime.getRuntime().maxMemory() * memoryRate);
        
        cache = new BitmapCache(maxCacheSize);
    }
    
    public Bitmap colorize(int r, int g, int b, int bgcolor)
    {
        final int colorKey = Color.argb(255, r, g, b);

        Bitmap bitmap = cache.get(colorKey);
        if(bitmap == null || bitmap.isRecycled()) {
            bitmap = super.colorize(r, g, b, bgcolor);
            cache.put(colorKey, bitmap);
        }
        
        return bitmap;
    }
}
