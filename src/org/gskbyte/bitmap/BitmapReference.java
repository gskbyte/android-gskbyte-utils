package org.gskbyte.bitmap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
 * */

public class BitmapReference
{
    
/** Bitmap is located under the res/ folder of the app. */
public static final int LOCATION_RESOURCES  = 1;
/** Bitmap is located under the assets/ folder of the app. */
public static final int LOCATION_ASSETS     = 2;
/** Bitmap is located under the app's private folder. */
public static final int LOCATION_PRIVATE    = 4;
/** Bitmap is located in the external storage. Can be an SD Card or the emulated external folder. */
public static final int LOCATION_EXTERNAL   = 8;

/** Some common location configurations  */
public static final int LOCATION_ASSETS_UPDATABLE =         LOCATION_ASSETS | LOCATION_PRIVATE | LOCATION_EXTERNAL;
public static final int LOCATION_ASSETS_UPDATABLE_PRIVATE = LOCATION_ASSETS | LOCATION_PRIVATE;
public static final int LOCATION_FOREIGN =                  LOCATION_PRIVATE | LOCATION_EXTERNAL;

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
 * 
 * For resource files, only the file name is needed.
 * For assets, only the relative path.
 * For files in the private folder, can not contain path separators.
 * For external files, the relative path into the external folder is needed. The absolute path is computed by the BitmapManager, check its constructors to know how.
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
        if((location & LOCATION_EXTERNAL) != 0 && loadExternal(context, externalBasePath))
            return;
        if((location & LOCATION_PRIVATE) != 0 && loadPrivate(context) )
            return;
        if((location & LOCATION_ASSETS) != 0 && loadAsset(context) )
            return;
        if((location & LOCATION_RESOURCES) != 0 && loadResource(context) )
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
