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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.gskbyte.util.Logger;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * BitmapManager class
 * 
 * A bitmap manager stores bitmaps and allows referencing them using their path.
 * The bitmaps are loaded only when they are requested for the first time.
 * 
 * TODO: add support for the BitmapCache in order to stabilize memory usage.
 * */

public class BitmapManager
{

protected final Context context;
protected final Map<String, BitmapReference> references = new HashMap<String, BitmapReference>();
protected final String externalBasePath;

/**
 * Creates external folder with the package name
 * @param context The manager's context. Recommended to be the Application context
 * */
public BitmapManager(Context context)
{
    this(context, context.getPackageName());
}

/**
 * Creates suggested external folder, if not exists.
 * @param context The manager's context. Recommended to be the Application context
 * */
public BitmapManager(Context context, String externalFolderName)
{
    this.context = context;
    File external = new File( context.getExternalFilesDir(null), externalFolderName);
    external.mkdir();
    externalBasePath = external.getAbsolutePath();
}

/**
 * Adds a bitmap reference to the manager.
 * @param reference The bitmap reference to add.
 * */
public void addReference(BitmapReference reference)
{
    references.put(reference.path, reference);
}

/**
 * Shortcut method to add a reference to a bitmap located under the specified location.
 * @param location Integer value specifying location (@see IOUtils)
 * @param path The file's path.
 * */
public void addPath(int location, String path)
{
    references.put(path, new BitmapReference(location, path));
}

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
public int loadedBitmaps()
{
    // this could be optimized, but it's not likely to be called often
    int count = 0;
    for(BitmapReference r : references.values()) {
        if(r.bitmap != null)
            ++count;
    }
    
    return count;
}

/**
 * Returns a bitmap reference given a path. Loads the bitmap if necessary.
 * @param path The bitmaps' path, used as a key to retrieve it.
 * */
public BitmapReference getReference(String path)
{
    BitmapReference ref = references.get(path);
    if(ref == null) {
        Logger.error(getClass(), "Trying to retrieve bitmap without reference: "+path);
        return null;
    } else {
        ref.loadBitmapIfNecessary(context, externalBasePath);
        return ref;
    }
}

/**
 * Returns a bitmap given a path.
 * @param path The bitmaps' path, used as a key to retrieve it.
 * */
public Bitmap get(String path)
{
    BitmapReference ref = getReference(path);
    if(ref != null) {
        return ref.getBitmap();
    } else {
        return null;
    }
}

public void freeBitmap(String path)
{
    BitmapReference ref = references.get(path);
    if(ref != null)
        ref.freeResources();
}

/**
 * Frees memory by releasing all bitmaps.
 * */
public void freeResources()
{
    for(BitmapReference r : references.values()) {
        r.freeResources();
    }
}

}
