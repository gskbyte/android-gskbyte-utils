package org.gskbyte.bitmap;

import org.gskbyte.util.Logger;

import android.content.Context;
import android.graphics.Bitmap;

public class LRUBitmapManager
extends AbstractBitmapManager
{

private final LRUBitmapCache<String> bitmapCache;

public LRUBitmapManager(Context context)
{
    this(context, 0.2f);
}

public LRUBitmapManager(Context context, float memoryRate)
{
    super(context);
    this.bitmapCache = new LRUBitmapCache<String>(memoryRate);
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
public void freeResources()
{
    bitmapCache.evictAll();
}

final class LRUBitmapRef
extends AbstractBitmapManager.BitmapRef
{

public LRUBitmapRef(int location, String path)
{
    super(location, path);
}

@Override
public Bitmap getBitmap()
{
    Bitmap bitmap = bitmapCache.get(path);
    if(bitmap == null) {
        bitmap = loadBitmap(path);
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
