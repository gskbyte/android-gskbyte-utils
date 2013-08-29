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

import android.content.Context;
import android.graphics.Bitmap;

/**
 * BitmapManager class
 * 
 * A bitmap manager stores bitmaps and allows referencing them using their path.
 * The bitmaps are loaded only when they are requested for the first time.
 * 
 * This is a simple implementation that doesn't care too much about memory usage.
 * LRUBitmapManager is much more interesting.
 * */

public class BitmapManager
extends AbstractBitmapManager
{

public BitmapManager(Context context)
{
    super(context);
}

public BitmapManager(Context context, int numLoadThreads)
{
    super(context, numLoadThreads);
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
public void releaseAllBitmaps()
{
    for(BitmapRef r : references.values()) {
        r.freeResources();
    }
}


/**
 * Simple specialization of a BitmapReference. Just stores a bitmap in it.
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
public Bitmap getBitmap(ScaleMode scaleMode, int maxWidth, int maxHeight)
{
    if(bitmap == null) {
        bitmap = loadBitmap(scaleMode, maxWidth, maxHeight);
    }
    
    return bitmap;
}

@Override
public boolean isLoaded()
{
    return bitmap != null;
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
