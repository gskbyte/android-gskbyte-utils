package org.gskbyte.bitmap;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.gskbyte.util.IOUtils;
import org.gskbyte.util.Logger;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public abstract class AbstractBitmapManager
{

protected final Context context;
protected final Map<String, BitmapRef> references = new HashMap<String, BitmapRef>();

/**
 * Default constructor.
 * @param context The manager's context. Recommended to be the Application context
 * */
public AbstractBitmapManager(Context context)
{
    this.context = context;
}

/**
 * Shortcut method to add a reference to a bitmap located under the specified location.
 * @param location Integer value specifying location (@see IOUtils)
 * @param path The file's path.
 * */
public void addPath(int location, String path)
{
    references.put(path, initializeReference(location, path));
}

protected abstract BitmapRef initializeReference(int location, String path);

/**
 * Clears all references to bitmaps and frees memory.
 * */
public void clear()
{ references.clear(); }

/**
 * Returns the number of references stored in the manager.
 * */
public int size()
{ return references.size(); }

/**
 * Returns the number of loaded bitmaps
 * */
public abstract int countLoadedBitmaps();

/**
 * Returns a bitmap given a path.
 * @param path The bitmaps' path, used as a key to retrieve it.
 * */
public Bitmap get(String path)
{
    BitmapRef ref = references.get(path);
    if(ref != null) {
        return ref.getBitmap();
    } else {
        Logger.error(getClass(), "Trying to retrieve bitmap without reference: "+path);
        return null;
    }
}

/**
 * Frees the memory occupied by a given bitmap, if loaded.
 * @param path The path for the given Bitmap
 * */
public void freeBitmap(String path)
{
    BitmapRef ref = references.get(path);
    if(ref != null)
        ref.freeResources();
}
/**
 * Frees memory by releasing all bitmaps.
 * */
public abstract void freeResources();


/**
 * BitmapReference is the internal class used by the BitmapManager to store information
 * about the managed Bitmaps.
 * 
 * Bitmaps can be loaded from different locations. The locations can be combined,
 * and the BitmapManager will try to load in the following sequence:
 * external > private > assets > resources
 * 
 * See {@link IOUtils} to check more details about file locations
 * */
protected abstract class BitmapRef
{

final int location;
final String path;

public BitmapRef(int location, String path)
{
    this.location = location;
    this.path = path;
}

public abstract Bitmap getBitmap();

protected final Bitmap loadBitmap(String path)
{
    InputStream is;
    try {
        is = IOUtils.GetInputStreamForDrawable(location, path, context);
        return BitmapFactory.decodeStream(is);
    } catch (NotFoundException e) {
        // should we say anything?
    } catch (IOException e) {
        // should we say anything?
    }
    return null;
}

/**
 * Free memory occupied by the bitmap, if it's loaded
 * */
public abstract void freeResources();

}

}
