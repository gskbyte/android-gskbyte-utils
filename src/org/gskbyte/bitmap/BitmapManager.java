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

import org.gskbyte.util.IOUtils;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * BitmapManager class
 * 
 * A bitmap manager stores bitmaps and allows referencing them using their path.
 * The bitmaps are loaded only when they are requested for the first time.
 * */

public class BitmapManager
extends AbstractBitmapManager
{

public BitmapManager(Context context)
{
    super(context);
}

@Override
protected BitmapRef initializeReference(int location, String path)
{
    return new BitmapReference(location, path);
}


@Override
public int countLoadedBitmaps()
{
    // this could be optimized, but it's not likely to be called often
    int count = 0;
    for(BitmapRef r : references.values()) {
        if(((BitmapReference)r).bitmap != null)
            ++count;
    }
    
    return count;
}

@Override
public void freeResources()
{
    for(BitmapRef r : references.values()) {
        r.freeResources();
    }
}


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
final class BitmapReference
extends AbstractBitmapManager.BitmapRef
{

Bitmap bitmap;

public BitmapReference(int location, String path)
{
    super(location, path);
}

@Override
public Bitmap getBitmap()
{
    if(bitmap == null) {
        bitmap = loadBitmap(path);
    }
    
    return bitmap;
    
}

@Override
public void freeResources()
{
    if(bitmap!=null) {
        //bitmap.recycle();
        bitmap = null;
    }
}

}

}
