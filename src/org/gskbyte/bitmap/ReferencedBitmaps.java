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

protected final Set<String> keys = new HashSet<String>();

protected int uniqueCounter = 0;

@Getter
protected final AbstractBitmapManager bitmapManager;

@Getter
protected final int locationForBitmaps;

/**
 * Constructor.
 * @param manager The referenced manager
 * @param locationForBitmaps Default location for added paths
 * */
public ReferencedBitmaps(AbstractBitmapManager manager, int locationForBitmaps)
{
    this.bitmapManager = manager;
    this.locationForBitmaps = locationForBitmaps;
}

/**
 * Returns the underlying bitmap manager.
 * @deprecated Use getBitmapManager instead.
 * */
@Deprecated
public AbstractBitmapManager getManager()
{
    return bitmapManager;
}

/**
 * Adds a path to a bitmap, depending on the initial default location.
 * @param path A path to a bitmap.
 * @param alias An extra alias to a bitmap. Must have length() > 0.
 * */
public void addPath(String path, String ... aliases)
{
    bitmapManager.addPath(locationForBitmaps, path, aliases);
    if(!keys.contains(path))
        ++uniqueCounter;
    keys.add(path);
    for(String alias : aliases)
        keys.add(alias);
}

/**
 * Adds an alias to an already referenced bitmap, depending on the initial default location.
 * @param path A path to a bitmap.
 * @param alias An extra alias to a bitmap. Must have length() > 0.
 * */
public void addAliases(String path, String ... aliases)
{
    bitmapManager.addAliases(path, aliases);
    keys.add(path);
    for(String alias : aliases)
        keys.add(alias);
}

/**
 * @Deprecated Use containsKey, its name is more adequated
 * Indicates if this contains the given path.
 * @return true if there is a bitmap reference for the given path
 * */
@Deprecated
public boolean containsPath(String path)
{ return containsKey(path); }

/**
 * Indicates if this references the given key.
 * @return true if there is a bitmap reference for the given key
 * */
public boolean containsKey(String key)
{ return keys.contains(key); }

/**
 * Returns true if the given Bitmap is present in memory. Asks the underlying manager.
 * @param The bitmap's path
 * @returns true if a Bitmap for the given path is loaded into memory
 * */
public boolean isBitmapLoaded(String key)
{ return bitmapManager.isBitmapLoaded(key); }

/**
 * Returns true if the given Bitmap file is present in the file system. Asks the underlying manager.
 * @param The bitmap's path
 * @returns true if a file for the given path exists
 * */
public boolean existsBitmapFile(String key)
{ return bitmapManager.existsBitmapFile(key); }

/**
 * Checks the presence of all bitmaps in the file system 
 * @returns true if all contained Bitmaps are present in the file system. Asks the underlying manager.
 * */
public boolean existAllBitmaps()
{
    for(String key : keys) {
        if(!bitmapManager.existsBitmapFile(key))
            return false;
    }
    
    return true;
}

/**
 * Returns the number of references.
 * */
public int size()
{ return uniqueCounter; }

/**
 * Returns the number of existing bitmap files from the referenced by this.
 * @return the number of existing referenced bitmap files
 * */
public int countExistingBitmapFiles()
{
    int counter = 0;
    for(String k : keys) {
        if(bitmapManager.existsBitmapFile(k)) {
            ++counter;
        }
    }
    return counter;
}

/**
 * Returns a bitmap given its path.
 * @param key The key for the bitmap,
 * */
public Bitmap get(String key)
{ return bitmapManager.get(key); }

/**
 * @Deprecated Use getFirstExistingKey()
 * Returns the path for the first existing path.
 * @return The path for the fist existing bitmap file
 * */
@Deprecated
protected String getFirstExistingFilePath()
{
    return getFirstExistingKey();
}

/**
 * Returns the path for the first existing path.
 * @return The path for the fist existing bitmap file
 * */
protected String getFirstExistingKey()
{
    for(String key : keys) {
        boolean exists = existsBitmapFile(key);
        if(exists)
            return key;
    }
    
    return null;
}

/**
 * Clears all references to bitmaps, but does not release them.
 * */
public void clear()
{ clear(false); }

/**
 * Clears all references to bitmaps.
 * @param releaseBitmaps Wether the referenced bitmaps should be cleared or not.
 * */
public void clear(boolean releaseBitmaps)
{
    if(releaseBitmaps)
        freeResources();
    keys.clear();
}

/**
 * Frees memory by releasing all referenced bitmaps.
 * */
public void freeResources()
{
    for(String s : keys) {
        bitmapManager.freeBitmap(s);
    }
}

}
