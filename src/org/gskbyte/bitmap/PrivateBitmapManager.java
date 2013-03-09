package org.gskbyte.bitmap;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.gskbyte.util.Logger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/** 
 * A bitmap manager stores bitmaps and allows referencing them using their path
 * inside the app's private folder.
 * 
 * This class can be considered a specialization of BitmapManager, but it's
 * actually an older and less generic version of it. Could be interesting in
 * simple scenarios, but BitmapManager gives much more functionality. 
 * The interfaces are, however, very similar.
 * 
 * A BitmapManager can be used as a PrivateBitmapManager just by calling
 * addPrivatePath() instead of addPath().
 * */

public class PrivateBitmapManager
{

protected final Context context;
protected final Map<String, Bitmap> bitmaps = new HashMap<String, Bitmap>();

/**
 * Constructor.
 * @param context The manager's context. Recommended to be the Application context
 * */
public PrivateBitmapManager(Context context)
{
    this.context = context;
}

/**
 * Addsa bitmap's path located in the app's private folder.
 * @param path The file's path.
 * */
public void addPath(String path)
{
    if(!bitmaps.containsKey(path))
        bitmaps.put(path, null);
}

/**
 * Returns the number of references stored in the manager.
 * */
public int size()
{
    return bitmaps.size();
}

/**
 * Returns the number of loaded bitmaps
 * */
public int loadedBitmaps()
{
    // this could be optimized, but it's not likely to be called often
    int count = 0;
    for(Bitmap b : bitmaps.values()) {
        if(b != null)
            ++count;
    }
    
    return count;
}

/**
 * Returns a bitmap given a path.
 * @param path The bitmaps' path, used as a key to retrieve it.
 * */
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

/**
 * Frees memory by releasing all bitmaps.
 * */
public void freeResources()
{
    Iterator< Map.Entry<String, Bitmap> > it = bitmaps.entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry<String, Bitmap> pair = (Map.Entry<String, Bitmap>)it.next();
        pair.setValue(null);
    }
}

}
