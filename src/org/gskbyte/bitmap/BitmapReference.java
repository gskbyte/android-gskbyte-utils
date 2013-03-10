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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.gskbyte.util.IOUtils;

import lombok.Getter;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
public class BitmapReference
{


@Getter
protected final int location;
@Getter
protected final String path, pathWithoutSeparators;
@Getter
protected Bitmap bitmap;

/**
 * Constructor
 * @param location Represents the location of the file. Can be a bitwise OR of the default enums.
 * @path The file's path. Depending on the location, it may have separators or not, refer to the Android documentation on file management.
 * */
public BitmapReference(int location, String path)
{
    this.location = location;
    this.path = path;
    int lastSlash = path.lastIndexOf('/');
    if(lastSlash < 0) {
        pathWithoutSeparators = path;
    } else {
        pathWithoutSeparators = path.substring(lastSlash+1);
    }
}

/**
 * Load bitmap if it's not already loaded.
 * @param context The current context
 * */
void loadBitmapIfNecessary(Context context, String externalBasePath)
{
    if(bitmap == null || bitmap.isRecycled()) {
        if((location & IOUtils.LOCATION_EXTERNAL) != 0 && loadExternal(context, externalBasePath))
            return;
        if((location & IOUtils.LOCATION_PRIVATE) != 0 && loadPrivate(context) )
            return;
        if((location & IOUtils.LOCATION_ASSETS) != 0 && loadAsset(context) )
            return;
        if((location & IOUtils.LOCATION_RESOURCES) != 0 && loadResource(context) )
            return;
    }
}

/**
 * Load bitmap from the external storage. Uses the path with separators.
 * @param context The current context
 * */
protected boolean loadExternal(Context context, String externalBasePath)
{
    try {
        InputStream is = new FileInputStream(externalBasePath+path);
        bitmap = BitmapFactory.decodeStream(is);
        return (bitmap != null);
    } catch (FileNotFoundException e) {
        return false;
    }
}


/**
 * Load bitmap from the app's private folder. Uses the path without separators.
 * @param context The current context
 * */
protected boolean loadPrivate(Context context)
{
    try {
        InputStream is = context.openFileInput(pathWithoutSeparators);
        bitmap = BitmapFactory.decodeStream(is);
        return (bitmap != null);
    } catch (FileNotFoundException e) {
        return false;
    }
}

/**
 * Load bitmap from assets (assets/ folder). Uses the path with separators.
 * @param context The current context
 * */
protected boolean loadAsset(Context context)
{
    try {
        InputStream is = context.getAssets().open(path);
        bitmap = BitmapFactory.decodeStream(is);
        return (bitmap != null);
    } catch (IOException e) {
        return false;
    }
}

/**
 * Load bitmap from resource (res/ folder). Uses the path without separators.
 * @param context The current context
 * */
protected boolean loadResource(Context context)
{
    final Resources resources = context.getResources();
    int id = resources.getIdentifier(pathWithoutSeparators, "drawable", context.getPackageName());
    bitmap = BitmapFactory.decodeResource(resources, id);
    return (bitmap != null);
}

/**
 * Free memory occupied by the bitmap, if it's loaded
 * */
public void freeResources()
{
    if(bitmap!=null) {
        bitmap.recycle();
        bitmap = null;
    }
}


}
