package org.gskbyte.bitmap;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

import android.graphics.Bitmap;

/**
 * Class used to reference a subset of a bitmap manager.
 * This allows having a single BitmapManager per app, allowing just a section
 * of the app to access only to some bitmaps.
 * 
 * Eases the calls to the BitmapManager because the location for the bitmaps
 * is set at the beginning.
 * */
public class ReferencedBitmaps
{

protected final Set<String> paths = new HashSet<String>();

@Getter
protected final BitmapManager manager;

@Getter
protected final int locationForBitmaps;

/**
 * Constructor.
 * @param manager The referenced manager
 * @param locationForBitmaps Default location for added paths
 * */
public ReferencedBitmaps(BitmapManager manager, int locationForBitmaps)
{
    this.manager = manager;
    this.locationForBitmaps = locationForBitmaps;
}

/**
 * Adds a path to a bitmap, depending on the initial default location.
 * @param path A path to a bitmap.
 * */
public void addPath(String path)
{
    manager.addReference(new BitmapReference(locationForBitmaps, path));
    paths.add(path);
}

/**
 * Returns the number of references.
 * */
public int size()
{ return paths.size(); }

/**
 * Returns a bitmap given its path.
 * @param path The path to the bitmap, which depends on the default location.
 * */
public Bitmap get(String path)
{ return manager.get(path); }


/**
 * Clears all references to bitmaps, and releases them.
 * */
public void clear()
{ clear(true); }

/**
 * Clears all references to bitmaps.
 * @param releaseBitmaps Wether the referenced bitmaps should be cleared or not.
 * */
public void clear(boolean releaseBitmaps)
{
    if(releaseBitmaps)
        freeResources();
    paths.clear();
}

/**
 * Frees memory by releasing all referenced bitmaps.
 * */
public void freeResources()
{
    for(String s : paths) {
        manager.freeBitmap(s);
    }
}

}
