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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.gskbyte.bitmap.AbstractBitmapManager.ScaleMode;

import lombok.Getter;

import android.graphics.Bitmap;

/**
 * Class used to reference a subset of a bitmap manager.
 * Provides the same functionality as ReferencedBitmaps, but also allows to
 * access the bitmaps in the order their paths were added.
 * */

public class IndexedBitmaps
extends ReferencedBitmaps
{

@Getter
protected final List<String> keyList = new ArrayList<String>();

/**
 * Constructor.
 * @param manager The referenced manager
 * @param locationForBitmaps Default location for added paths
 * */
public IndexedBitmaps(AbstractBitmapManager manager, int locationForBitmaps)
{ super(manager, locationForBitmaps); }

/**
 * Adds a path to a bitmap, depending on the initial default location, and adds
 * its path to the end of the list.
 * @param path A path to a bitmap.
 * @param aliases Aliases for the given bitmap file. All must have length() > 0.
 * */
@Override
public void addPath(String path, String ... aliases)
{
    super.addPath(path, aliases);
    keyList.add(path);
}

/**
 * 
 * */
@Override
public int size()
{ return keyList.size(); }

/**
 * Adds a list of paths to a bitmap, depending on the initial default location, and adds
 * their path to the end of the list.
 * @param paths A list of paths to bitmaps
 * */
public void addPaths(List<String> path)
{
    for(String s : path) {
        addPath(s);
    }
}

/**
 * @deprecated Use getKeyAt()
 * Returns a path given the index in which it was added.
 * @param index The path's index.
 * */
@Deprecated
public String getPathAt(int index)
{ return keyList.get(index); }

/**
 * Returns a path given the index in which it was added.
 * @param index The key's index.
 * */
public String getKeyAt(int index)
{ return keyList.get(index); }

/**
 * Returns a Bitmap given the index in which its path it was added.
 * @param index The bitmap's path index.
 * */
public Bitmap getAt(int index)
{
    String path = keyList.get(index);
    return bitmapManager.get(path);
}

/**
 * Returns a Bitmap given the index in which its path it was added.
 * @param index The bitmap's path index.
 * */
public Bitmap getAt(int index, ScaleMode scaleMode, int maxWidth, int maxHeight)
{
    String path = keyList.get(index);
    return bitmapManager.get(path, scaleMode, maxWidth, maxHeight);
}

/**
 * Returns true if the given Bitmap is present in memory
 * @param The bitmap's path index
 * @returns true if a Bitmap for the given path is loaded into memory
 * */
public boolean isBitmapLoadedAt(int index)
{
    return bitmapManager.isBitmapLoaded( keyList.get(index) );
}

/**
 * Returns true if the given Bitmap's file is present in the file system. Asks the underlying manager.
 * @param The bitmap's path index
 * @returns true if a file for the given path index exists
 * */
public boolean existsBitmapFileAt(int index)
{ return bitmapManager.existsBitmapFile( keyList.get(index) ); }

/**
 * @deprecated
 * Returns the path for the first existing path. The paths are iterated in insertion order.
 * @return The path for the fist existing bitmap file
 * */
@Deprecated
@Override
public String getFirstExistingFilePath()
{ return getFirstExistingKey(); }

/**
 * Returns the path for the first existing path. The paths are iterated in insertion order.
 * @return The path for the fist existing bitmap file
 * */
@Override
public String getFirstExistingKey()
{
    for(int i=0; i<size(); ++i) {
        boolean exists = existsBitmapFileAt(i);
        if(exists) {
            return getPathAt( i );
        }
    }
    
    return null;
}

/**
 * @deprecated Use getRandomExistingKey()
 * Returns the file path for a random bitmap.
 * @return A file path for an existing bitmap file
 * */
@Deprecated
public String getRandomExistingFilePath()
{ return getRandomExistingKey(); }

/**
 * Returns the file path for a random bitmap.
 * @return A file path for an existing bitmap file
 * */
public String getRandomExistingKey()
{
    final int numBitmaps = size();
    if(numBitmaps > 1) {
        Random r = new Random();
        int startindex = r.nextInt( numBitmaps );
        for(int i=0; i<numBitmaps; ++i) {
            int index = (startindex+i) %numBitmaps;
            boolean exists = existsBitmapFileAt(index);
            if(exists) {
                return getPathAt( index );
            }
        }
        return null;
    } else {
        return getFirstExistingKey();
    }
}



/**
 * Clears all references to bitmaps.
 * @param releaseBitmaps Wether the referenced bitmaps should be cleared or not.
 * */
@Override
public void clear(boolean releaseBitmaps)
{
    super.clear(releaseBitmaps);
    keyList.clear();
}

}
