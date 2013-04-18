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

import android.graphics.Bitmap;

/**
 * Class used to reference a subset of a bitmap manager.
 * Provides the same functionality as ReferencedBitmaps, but also allows to
 * access the bitmaps in the order their paths were added.
 * */

public class IndexedBitmaps
extends ReferencedBitmaps
{

protected final List<String> pathList = new ArrayList<String>();

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
 * */
@Override
public void addPath(String path)
{
    super.addPath(path);
    pathList.add(path);
}

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
 * Returns a path given the index in which it was added.
 * @param index The path's index.
 * */
public String getPath(int index)
{ return pathList.get(index); }

/**
 * Returns a Bitmap given the index in which its path it was added.
 * @param index The bitmap's path index.
 * */
public Bitmap get(int index)
{
    String path = pathList.get(index);
    return manager.get(path);
}

/**
 * Clears all references to bitmaps.
 * @param releaseBitmaps Wether the referenced bitmaps should be cleared or not.
 * */
@Override
public void clear(boolean releaseBitmaps)
{
    super.clear(releaseBitmaps);
    paths.clear();
}

}
