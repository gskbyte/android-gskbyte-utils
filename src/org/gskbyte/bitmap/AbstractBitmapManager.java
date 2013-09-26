package org.gskbyte.bitmap;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import lombok.Getter;

import org.gskbyte.util.IOUtils;
import org.gskbyte.util.Logger;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;

/**
 * AbstractBitmapManager class
 * 
 * A bitmap manager stores bitmaps and allows referencing them using their path.
 * The bitmaps are loaded only when they are requested for the first time.
 * 
 * This is an abstract class that defines interface and basic methods for
 * specific implementations.
 * 
 * @see BitmapManager for a simple specialization
 * @see LRUBitmapManager for a manager using an LRU cache as backend
 * */
public abstract class AbstractBitmapManager
{

protected final Context context;
protected final Map<String, BitmapRef> references = new HashMap<String, BitmapRef>();
protected int uniqueCounter;

/* Used for background work */
private final Map<String, HashSet<BackgroundLoadListener>> backgroundListeners = new HashMap<String, HashSet<BackgroundLoadListener>>();
private final ArrayList<AsyncLoadTask> backgroundLoadTasks = new ArrayList<AsyncLoadTask>();
private final ArrayList<AsyncLoadTask> runningLoadTasks = new ArrayList<AsyncLoadTask>();

public enum ScaleMode
{
    None,
    Fast, // Only BitmapFactory.inSampleSize is used
    Full  // Fast scale mode is applied, as well as further resizing if needed
}

@Getter
private final int numLoadThreads;

@Getter
private ScaleMode automaticScaleMode = ScaleMode.None;
@Getter
private int maxBitmapWidth, maxBitmapHeight;


/**
 * Default constructor. Will establish one load thread
 * @param context The manager's context. Recommended to be the Application context.
 * */
public AbstractBitmapManager(Context context)
{
    this(context, Runtime.getRuntime().availableProcessors() - 1);
}

/**
 * Constructor specifying number of threads.
 * @param context The manager's context. Recommended to be the Application context.
 * @param numLoadThreads Maximum number of threads to be used. Must be at least 1.
 * */
public AbstractBitmapManager(Context context, int numLoadThreads)
{
    this.context = context;
    this.numLoadThreads = Math.max(numLoadThreads, 1);
    this.uniqueCounter = 0;
}

/**
 * Enables automatic bitmap scale.
 * Bitmap scaling is disabled by default because it can cause bitmap loading to be much slower. Use it carefully!
 * 
 * @param maxWidth maximum width for loaded bitmaps
 * @param maxHeight maximum height for loaded bitmaps
 * */
public void setAutomaticScale(ScaleMode scaleMode, int maxWidth, int maxHeight)
{
    if(scaleMode!=ScaleMode.None && (maxWidth < 1 || maxHeight < 1)) {
        throw new IllegalArgumentException("maxWidth and maxHeight must be > 0");
    }

    automaticScaleMode = scaleMode;
    maxBitmapWidth = maxWidth;
    maxBitmapHeight = maxHeight;
}

/**
 * Disables automatic bitmap scaling.
 * */
public void disableScaleBigBitmaps()
{
    automaticScaleMode = ScaleMode.None;
}

/**
 * Adds a reference to a bitmap located under the specified location, with the given alias
 * @param location Integer value specifying location (@see IOUtils)
 * @param path The file's path.
 * @param aliases Aliases for the given file.  All must have length() > 0.
 * @return true If the reference has just been created
 * */
public boolean addPath(int location, String filepath, String ... aliases)
{
    boolean isNewRef = false;
    BitmapRef ref = references.get(filepath);
    if(ref == null) {
        ref = initializeReference(location, filepath);
        references.put(filepath, ref);   
        isNewRef = true;
        ++uniqueCounter;
    }
    
    if(aliases.length>0)
        addAliases(filepath, aliases);
    
    return isNewRef;
}

/**
 * Adds aliases for an existing mapped filepath.
 * @param filepath The file path for which to define aliases
 * @param aliases Alisases to define
 * @throws IllegalArgumentException if the given filepath was not mapped
 * */
public void addAliases(String filepath, String ... aliases)
{
    BitmapRef ref = references.get(filepath);
    if(ref != null) {
        for(String alias : aliases) {
            references.put(alias,  ref);
        }
    } else {
        throw new IllegalArgumentException("Filepath not mapped: " + filepath);
    }
}

/**
 * Just initializes a specialization of BitmapRef, depending on the child class
 * @param location The location for the bitmap
 * @param path The path for the bitmap, given a location
 * @return A BitmapRef object to be used to look for the Bitmap 
 * */
protected abstract BitmapRef initializeReference(int location, String path);

/**
 * Clears all references to bitmaps and frees memory.
 * */
public void clear()
{ references.clear(); }

/**
 * Returns the number of different unique references stored in the manager.
 * */
public int size()
{ return uniqueCounter; }

/**
 * Returns the number of loaded bitmaps
 * */
public abstract int countLoadedBitmaps();

/**
 * Returns true if the reference is defined
 * @param key The bitmap's key or path
 * */
public boolean containsKey(String key)
{
    return references.containsKey(key);
}

/**
 * Returns true if the given Bitmap is present in memory
 * @param key The bitmap's key or path
 * @returns true if a Bitmap for the given path is loaded into memory
 * */
public boolean isBitmapLoaded(String key)
{
    BitmapRef ref = references.get(key);
    if(ref != null) {
        return ref.isLoaded();
    } else {
        Logger.error(getClass(), "Trying to retrieve presence for not referenced bitmap: "+key);
        return false;
    }
}

/**
 * Returns the size for a bitmap. If it's not loaded into memory, reads only the size
 * @param The bitmap's path
 * @returns true if a Bitmap for the given path is loaded into memory
 * */
public Point getBitmapSize(String key)
{
    BitmapRef ref = references.get(key);
    if(ref != null) {
        return ref.getBitmapSize();
    } else {
        Logger.error(getClass(), "Trying to retrieve size for not referenced bitmap: "+key);
        return null;
    }
}

/**
 * Returns true if the given Bitmap's file is present in the file system
 * @param The bitmap's path
 * @returns true if a file for the given path exists
 * */
public boolean existsBitmapFile(String key)
{
    BitmapRef ref = references.get(key);
    if(ref != null) {
        return ref.existsFile();
    } else {
        //Logger.error(getClass(), "Trying to retrieve existence for not referenced bitmap: "+key);
        return false;
    }
}

/**
 * Returns a bitmap given a path.
 * @param key The bitmap's path or alias, used as a key to retrieve it.
 * */
public synchronized Bitmap get(String key)
{
    BitmapRef ref = references.get(key);
    if(ref != null) {
        return ref.getBitmap(automaticScaleMode, maxBitmapWidth, maxBitmapHeight);
    } else {
        Logger.error(getClass(), "Trying to get(key) not referenced bitmap: "+key);
        return null;
    }
}

/**
 * Returns a bitmap given a path and fitting the given size.
 * @param key The bitmap's path or alias, used as a key to retrieve it.
 * 
 * @param scaleMode The scale mode to be used
 * @param maxWidth The bitmap's maxWidth, if not loaded
 * @param maxHeight The bitmap's maxHeight, if not loaded
 * */

public synchronized Bitmap get(String key, ScaleMode scaleMode, int maxWidth, int maxHeight)
{
    if(maxWidth <= 0 || maxHeight <= 0) {
        throw new IllegalArgumentException("maxWidth and maxHeight must be >0");
    }
    
    BitmapRef ref = references.get(key);
    if(ref != null) {
        return ref.getBitmap(scaleMode, maxWidth, maxHeight);
    } else {
        Logger.error(getClass(), "Trying to get(key, maxWidth, maxHeight) not referenced bitmap: "+key);
        return null;
    }
}

/**
 * Returns a bitmap's absolute path for a given key.
 * @param key The bitmap's path or alias, used as a key to retrieve it.
 * @return The path for the given key if found, null if not.
 * @throws IOException if an error occurs while getting the absolute path 
 * */
public String getAbsolutePathForKey(String key) throws IOException 
{
    BitmapRef ref = references.get(key);
    if(ref != null) {
        return IOUtils.GetAbsolutePathForFilename(ref.location, ref.path, context);
    } else {
        Logger.error(getClass(), "Trying to getAbsolutePath() for not referenced bitmap: "+key);
        return null;
    }
}

/**
 * Frees the memory occupied by a given bitmap, if loaded.
 * @param key The path for the given Bitmap
 * */
public void freeBitmap(String key)
{
    BitmapRef ref = references.get(key);
    if(ref != null)
        ref.freeResources();
}
/**
 * Frees memory by releasing all bitmaps.
 * */
public abstract void releaseAllBitmaps();

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
float scale;
final Point size = new Point();

public BitmapRef(int location, String path)
{
    this.location = location;
    this.path = path;
}

public abstract Bitmap getBitmap(ScaleMode scaleMode, int maxWidth, int maxHeight);
public abstract boolean isLoaded();

public boolean existsFile()
{
    return IOUtils.ExistsFile(location, path, context);
}

protected final Point getBitmapSize()
{
    if(size.x==0) {
        try {
            InputStream is = IOUtils.GetInputStreamForDrawable(location, path, context);
            final BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, opts);
            size.x = opts.outWidth;
            size.y = opts.outHeight;
        } catch (NotFoundException e) {
            Logger.except(getClass(), e);
        } catch (IOException e) {
            Logger.except(getClass(), e);
        }
    }
    
    return size;
}

protected final Bitmap loadBitmap(ScaleMode scaleMode, int maxWidth, int maxHeight)
{
    try {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        
        if(scaleMode == ScaleMode.None) {
            InputStream is = IOUtils.GetInputStreamForDrawable(location, path, context);
            Bitmap b = BitmapFactory.decodeStream(is);
            size.x = b.getWidth();
            size.y = b.getHeight();
            scale = 1;
            return b;
        } else {
            getBitmapSize();
            
            // Detect sample size
            float wfactor = (float)size.x / maxWidth;
            float hfactor = (float)size.y / maxHeight;
            final float minfactor = Math.min(wfactor, hfactor);
            scale = 1/minfactor;
            
            opts.inSampleSize = Math.max(1, (int)minfactor);
            
            InputStream is = IOUtils.GetInputStreamForDrawable(location, path, context);
            Bitmap b = BitmapFactory.decodeStream(is, null, opts);
            if(scaleMode == ScaleMode.Full) {
                // recompute factors if inSample has been used
                wfactor = (float)opts.outWidth / maxWidth;
                hfactor = (float)opts.outHeight / maxHeight;
                scale = Math.min(1, Math.min(1/wfactor, 1/hfactor) );
                if(scale<1) {
                    Logger.error(getClass(), "sizes: " + size.x + "," + size.y + " (" + maxWidth +","+maxHeight + ") -> " + minfactor + ", " + scale);
                    b = Bitmap.createScaledBitmap(b, (int)(opts.outWidth*scale), (int)(opts.outHeight*scale), false);
                }
            }
            return b;
        }
    } catch (NotFoundException e) {
        // should we say anything?
    } catch (IOException e) {
        // should we say anything?
    }  catch (java.lang.OutOfMemoryError e) {
        // should we say anything?
    } 
    return null;
}

/**
 * Free memory occupied by the bitmap, if it's loaded
 * */
public abstract void freeResources();

}

/**
 * Classes who want to load bitmaps in background must implement this interface,
 * so that they can be called.
 * */
public interface BackgroundLoadListener
{
    public void bitmapLoadedInManager(Bitmap bitmap, String path, AbstractBitmapManager manager);
}

/**
 * Returns a bitmap given a path, loading it in background if needed. In this case, no listener will be called.
 * If the Bitmap is already available, it is immediately returned.
 * @param path The bitmap's path, used as a key to retrieve it.
 * @param listener The listener class who will be called once the bitmap is loaded
 * @return the requested Bitmap, if it's already loaded
 * */

public synchronized Bitmap getInBackground(String path, BackgroundLoadListener listener)
{
    BitmapRef ref = references.get(path);
    if(ref != null) {
        if(ref.isLoaded()) {
            return ref.getBitmap(automaticScaleMode, maxBitmapWidth, maxBitmapHeight);
        } else {
            
            HashSet<BackgroundLoadListener> listeners = backgroundListeners.get(path);
            if(listeners == null) {
                listeners = new HashSet<BackgroundLoadListener>();
                backgroundListeners.put(path, listeners);
                
                AsyncLoadTask task = new AsyncLoadTask(path);
                backgroundLoadTasks.add(task);
            }
            listeners.add(listener);
            processLoadTaskQueue();
            
            return null;
        }
    } else {
        Logger.error(getClass(), "Trying to retrieve bitmap in background without reference: "+path);
        return null;
    } 
}

private synchronized void processLoadTaskQueue()
{
    while(backgroundLoadTasks.size()>0 && runningLoadTasks.size()<numLoadThreads) {
        AsyncLoadTask task = backgroundLoadTasks.get(0);
        backgroundLoadTasks.remove(0);
        runningLoadTasks.add(task);
        task.execute();
    }
}

private synchronized void callListeners(AsyncLoadTask task, Bitmap bitmap)
{
    HashSet<BackgroundLoadListener> listeners = backgroundListeners.get(task.path);
    for(BackgroundLoadListener l : listeners) {
        l.bitmapLoadedInManager(bitmap, task.path, AbstractBitmapManager.this);
    }
    
    backgroundListeners.remove(task.path);
    runningLoadTasks.remove(task);
    
    processLoadTaskQueue();
}

/**
 * AsyncTask used to load Bitmaps in background
 * */
private final class AsyncLoadTask
extends AsyncTask<Void, Void, Bitmap>
{
    final String path;
    
    public AsyncLoadTask(String path)
    {
        this.path = path;
    }
    
    @Override
    protected Bitmap doInBackground(Void... params)
    {
        return AbstractBitmapManager.this.get(path);
    }
    
    protected void onPostExecute(Bitmap bitmap)
    {
        callListeners(this, bitmap);
    }
}


}
