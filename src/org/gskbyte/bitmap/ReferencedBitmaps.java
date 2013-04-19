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

protected final Set<String> paths = new HashSet<String>();

@Getter
protected final AbstractBitmapManager manager;

@Getter
protected final int locationForBitmaps;

/**
 * Constructor.
 * @param manager The referenced manager
 * @param locationForBitmaps Default location for added paths
 * */
public ReferencedBitmaps(AbstractBitmapManager manager, int locationForBitmaps)
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
    manager.addPath(locationForBitmaps, path);
    paths.add(path);
}

/**
 * Returns true if the given Bitmap is present in memory. Asks the underlying manager.
 * @param The bitmap's path
 * @returns true if a Bitmap for the given path is loaded into memory
 * */
public boolean isBitmapLoaded(String path)
{
    return manager.isBitmapLoaded(path);
}

/**
 * Returns true if the given Bitmap's file is present in the file system. Asks the underlying manager.
 * @param The bitmap's path
 * @returns true if a file for the given path exists
 * */
public boolean existsBitmapFile(String path)
{
    return manager.existsBitmapFile(path);
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
