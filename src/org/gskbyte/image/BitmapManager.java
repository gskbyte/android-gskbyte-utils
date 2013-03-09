package org.gskbyte.image;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.gskbyte.util.Logger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapManager
{

protected final Context context;
protected final Map<String, Bitmap> bitmaps = new HashMap<String, Bitmap>();

public BitmapManager(Context context)
{
    this.context = context;
}

public void addPath(String path)
{
    if(!bitmaps.containsKey(path))
        bitmaps.put(path, null);
}

public int size()
{
    return bitmaps.size();
}

public Bitmap get(String path)
{
    Bitmap bmp = null;
    if(bitmaps.containsKey(path)) {
        bmp = bitmaps.get(path);
        if(bmp == null || bmp.isRecycled()) {
            try {
                InputStream is = context.openFileInput(path);
                bmp = BitmapFactory.decodeStream(is);
                bitmaps.put(path, bmp);
            } catch (FileNotFoundException e) {
                Logger.debug(getClass(), "Bitmap not found: "+path);
            }
        }
    }
    
    return bmp;
}

public void freeResources()
{
    Iterator< Map.Entry<String, Bitmap> > it = bitmaps.entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry<String, Bitmap> pair = (Map.Entry<String, Bitmap>)it.next();
        pair.setValue(null);
    }
}

}
