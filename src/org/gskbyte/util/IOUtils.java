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
package org.gskbyte.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.Scanner;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;

/**
 * Utility class with some methods to ease working with files in Android.
 * 
 * This class defines four constant values that represent the location of a file.
 * They are represented as integers so they can be combined using bitwise operations.
 * 
 * The file path can have path separator ("/") depending on the location:
 * 
 * For resource files, only the file name is needed.
 * For assets, only the relative path.
 * For files in the private folder, can not contain path separators.
 * For external files, the relative path into the external folder is needed. The absolute path is computed by the BitmapManager, check its constructors to know how.
 * */
public final class IOUtils
{

/** File does not exist. */
public static final int LOCATION_NONEXISTENT  = 0x0000;
/** File is located under the res/ folder of the app. */
public static final int LOCATION_RESOURCES  = 0x0001;
/** File is located under the assets/ folder of the app. */
public static final int LOCATION_ASSETS     = 0x0010;
/** File is located under the app's private folder. */
public static final int LOCATION_PRIVATE    = 0x0100;
/** File is located in the external storage. Can be an SD Card or the emulated external folder. */
public static final int LOCATION_EXTERNAL   = 0x1000;

/** Some common combined location configurations  */
public static final int LOCATION_ASSETS_UPDATABLE =         LOCATION_ASSETS | LOCATION_PRIVATE | LOCATION_EXTERNAL;
public static final int LOCATION_ASSETS_UPDATABLE_PRIVATE = LOCATION_ASSETS | LOCATION_PRIVATE;
public static final int LOCATION_FOREIGN =                  LOCATION_PRIVATE | LOCATION_EXTERNAL;

@SuppressWarnings("deprecation") // just from API 17
public static int PRIVATE_FILES_MODE = Context.MODE_WORLD_READABLE; 

/**
 * Utility method to print a location integer in a pretty way.
 * @param location The location for which to have a nice String.
 * @return A String representing the passed location.
 * */
public static String StringForLocation(int location)
{
    String ret = "";
    if((location & LOCATION_RESOURCES) != 0)
        ret += "LOCATION_RESOURCES|";
    if((location & LOCATION_ASSETS) != 0)
        ret += "LOCATION_ASSETS|";
    if((location & LOCATION_PRIVATE) != 0)
        ret += "LOCATION_PRIVATE|";
    if((location & LOCATION_EXTERNAL) != 0)
        ret += "LOCATION_EXTERNAL|";
    
    if(ret.length()>0)
        return ret.substring(0, ret.length()-1);
    else
        return ret;
}

/**
 * Reads an inputStream into a String. Be careful with this using slow streams,
 * such as remote connections.
 * @param is The InputStream to read from
 * @return String A string with the contents of the InputStream.
 * */
public static String InputStreamToString( InputStream is ) throws IOException
{
    Scanner s = new Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
}

/**
 * Returns true if the external storage (wether if it's emulated or not) can be
 * written
 * @return true if we can write to the external storage
 * */
public static boolean IsExternalStorageMounted()
{
    return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
}

/**
 * Checks if a file exists. Only accepts the following exact locations:
 *  LOCATION_ASSETS, LOCATION_PRIVATE and LOCATION_EXTERNAL.
 * Does not accept combinations of those using bitwise operations.
 * @param location Location, can not be a combination of the default values.
 * @param path The path of the file.
 * @param context The context needed to access files.
 * @throws IllegalArgumentException If the location is invalid
 * */
public static boolean ExistsFile(int location, String path, Context context)
{
	if(path.length()==0)
		return false;
	
    File f = null;
    switch(location) {
    case LOCATION_ASSETS:
        try {
            context.getAssets().open(path);
            return true;
        } catch (IOException e) {
            return false;
        }
    case LOCATION_PRIVATE:
        f = context.getFileStreamPath(path);
        return f.exists();
    case LOCATION_EXTERNAL:
        f = new File(Environment.getExternalStorageDirectory(), path);
        return f.exists();
    }
    
    throw new IllegalArgumentException("Invalid location was supplied: "+StringForLocation(location));
}

/**
 * Get an absolute path for the given file and location. Especially useful for WebViews & Co.
 * @param location A single location for the file. Can not be a combination of locations.
 * @param rel_path The relative path for the file.
 * @param context The context used to open the file.
 * @throws IOException if the file does not exist.
 * @throws IllegalArgumentException If the location is a combination of locations
 * */
public static String GetAbsolutePathForFilename(int location, String rel_path, Context context)
        throws IOException
{
    switch(location) {
    case LOCATION_RESOURCES:
        return "file:///android_res/"+rel_path;
    case LOCATION_ASSETS:
        return "file:///android_asset/"+rel_path;
    case LOCATION_PRIVATE:
        return "file://"+context.getFilesDir().getAbsolutePath() + "/" + rel_path;
    case LOCATION_EXTERNAL:
        File externalDir = Environment.getExternalStorageDirectory();
        return "file://"+externalDir + "/" + rel_path;
    }
    throw new IllegalArgumentException("Invalid origin location: "+StringForLocation(location));
}


/**
 * Deletes a file on writable space. Does not delete directories if they are not empty.
 * @param location Location, can be only LOCATION_PRIVATE and LOCATION_EXTERNAL
 * @param path The path of the file.
 * @param context The context needed to access files.
 * @throws IllegalArgumentException If the location is invalid
 * @return true if the file was successfully deleted
 * */
public static boolean DeleteFile(int location, String path, Context context)
{
	boolean success = false;
    switch(location) {
    case LOCATION_PRIVATE:
        success = context.deleteFile( LastPathComponent(path) );
        break;
    case LOCATION_EXTERNAL:
        File f = new File(Environment.getExternalStorageDirectory(), path);
        success =  f.delete();
        break;
    default:
        throw new IllegalArgumentException("Invalid location was supplied: "+StringForLocation(location));
    }
    
	//Logger.info("IOUtils", "Deleting file: "+path + "(sucess: "+success+")");
	return success;
}

/**
 * Deletes a file on writable space, including the files included in it if it's a directory.
 * @param location Location, can be only LOCATION_PRIVATE and LOCATION_EXTERNAL. Actually, it does make only sense it to be LOCATION_EXTERNAL, as LOCATION_PRIVATE doesn't allow subdirectories.
 * @param path The path of the file.
 * @param context The context needed to access files.
 * @throws IllegalArgumentException If the location is invalid
 * @return true if all files were successfully deleted
 * */
public static boolean DeleteFileRecursive(int location, String path, Context context)
{
    boolean totalSuccess = true;
    if(location == LOCATION_EXTERNAL) {
        final File f = new File(Environment.getExternalStorageDirectory(), path);
        if(f.isDirectory()) {
            String [] children = f.list();
            for(String child : children) {
                String childPath = path + "/" + child;
                totalSuccess &= DeleteFileRecursive(location, childPath, context);
            }
        }
    }
    
    return totalSuccess & DeleteFile(location, path, context);
}

/**
 * Gets a file given a path.
 * @param location Location, can be only LOCATION_PRIVATE and LOCATION_EXTERNAL
 * @param path The path of the file.
 * @param context The context needed to access files.
 * @throws IllegalArgumentException If the location is invalid
 * */
public static File GetFile(int location, String path, Context context)
{
    switch(location) {
    case LOCATION_PRIVATE:
        return context.getFileStreamPath(path);
    case LOCATION_EXTERNAL:
        return new File(Environment.getExternalStorageDirectory(), path);
    }
    
    throw new IllegalArgumentException("Invalid location was supplied: "+StringForLocation(location));
}

/**
 * Copies the contents of a FileInputStream to a FileOutputStream.
 * @param fis The FileInputStream to read from.
 * @param fos The FileOutputStream to write on.
 * @throws IOException If the operation fails.
 * */
public static void CopyFileStream(FileInputStream fis, FileOutputStream fos)
        throws IOException
{
    FileChannel inputChannel = fis.getChannel();
    inputChannel.transferTo(0, inputChannel.size(), fos.getChannel());
    fis.close();
    fos.close();
}

/**
 * Get an InputStream given a location and a file.
 * @param location A single location for the file. Can not be LOCATION_RESOURCES nor a combination of locations.
 * @param path The path for the file.
 * @param context The context used to open the file.
 * @throws IOException if the file does not exist.
 * @throws IllegalArgumentException If the location is set to resources, or is a combination.
 * */
public static InputStream GetInputStream(int location, String path, Context context)
        throws IOException
{
    switch(location) {
    case LOCATION_ASSETS:
        return context.getAssets().open(path);
    case LOCATION_PRIVATE:
        return context.openFileInput( LastPathComponent(path) );
    case LOCATION_EXTERNAL:
        File externalDir = Environment.getExternalStorageDirectory();
        File f = new File(externalDir, path);
        return new FileInputStream(f);
    }
    throw new IllegalArgumentException("Invalid origin location: "+StringForLocation(location));
}


/**
 * Get an InputStream given a location and a file. The location can be a logic combination of the
 * default ones. The file is searched from the outside to the outside, this means the following
 * order: external -> private -> assets. Resources is not explored.
 * @param location A single location for the file. Can be a combination of locations.
 * @param path The path for the file.
 * @param context The context used to open the file.
 * @throws IOException if the file does not exist in any of the given locations.
 * */
public static InputStream GetInputStreamCombinedLocation(int p_location, String path, Context context)
        throws IOException
{
    final int [] validLocations = {LOCATION_EXTERNAL, LOCATION_PRIVATE, LOCATION_ASSETS};

    InputStream is = null;
    IOException lastException = null;
    for(int validLocation : validLocations) {
        if( (p_location & validLocation) != 0 ) {
            try {
                is = GetInputStream(validLocation, path, context);
                return is;
            } catch (IOException e) {
                lastException = e;
            }
        }
    }
    
    throw lastException;
}

/**
 * Returns the integer id for a given drawable name.
 * @param name The image name, without path separators
 * @param context The context used to search the file
 * @return The id for the requested resource, 0 if not found
 * */
public static int GetResourceIdentifierForDrawable(String name, Context context)
{
    final Resources resources = context.getResources();
    int id = resources.getIdentifier( LastPathComponent(name) , "drawable", context.getPackageName());
    return id;
}

/**
 * Returns an InputStream for a resource with the given name
 * @param name The image name, without path separators
 * @param context The context used to search the file
 * @return an InputStream to the drawable file
 * @throws Resources.NotFoundException if the file does not exist.
 * */
private static InputStream GetInputStreamForDrawable(String name, Context context)
        throws Resources.NotFoundException
{
    int identifier = GetResourceIdentifierForDrawable(name, context);
    final Resources resources = context.getResources();
    return resources.openRawResource(identifier);
}


/**
 * Get an InputStream given a location and a file. The location can be a logic combination of the
 * default ones. The file is searched from the outside to the outside, this means the following
 * order: external -> private -> assets -> resources (under res/drawable)
 * @param location A single location for the file. Can be a combination of locations.
 * @param path The path for the file.
 * @param context The context used to open the file.
 * @throws IOException if the file does not exist in any of the given locations.
 * */
public static InputStream GetInputStreamForDrawable(int location, String path, Context context)
        throws IOException, Resources.NotFoundException
{
    InputStream is = null;
    try {
        is = GetInputStream(location, path, context);
        return is;
    } catch (IOException e) {
        if((location & LOCATION_RESOURCES) != 0) {
            return GetInputStreamForDrawable(LastPathComponent(path), context);
        } else {
            throw e;
        }
    } catch (IllegalArgumentException e) {
        if((location & LOCATION_RESOURCES) != 0) {
            return GetInputStreamForDrawable(LastPathComponent(path), context);
        } else {
            throw e;
        }
    }
}

/**
 * Get an InputStream given a location and a file.
 * @param location A single location for the file. Can only be LOCATION_PRIVATE or LOCATION_EXTERNAL
 * @param path The path for the file.
 * @param context The context used to open the file.
 * @throws IOException if the file does not exist.
 * @throws IllegalArgumentException If the location is set to resources, or is a combination.
 * */
public static FileInputStream GetFileInputStream(int location, String path, Context context)
        throws IOException
{
    switch(location) {
    case LOCATION_PRIVATE:
    case LOCATION_EXTERNAL:
        return (FileInputStream) GetInputStream(location, path, context);
    }
    throw new IllegalArgumentException("Invalid origin location: "+StringForLocation(location));
}

/**
 * Get a FileOutputStream given a location and a file. In Android, we can write
 * only to the app's private folder and the external storage.
 * @param location A single location for the file. Can only be LOCATION_PRIVATE or LOCATION_EXTERNAL.
 * @param path The path for the file.
 * @param context The context used to open the file.
 * @throws IOException if the file can not be created.
 * @throws IllegalArgumentException If the location is not LOCATION_PRIVATE or LOCATION_EXTERNAL.
 * */
public static FileOutputStream GetFileOutputStream(int location, String path, Context context)
        throws FileNotFoundException
{
    switch(location) {
    case LOCATION_PRIVATE:
        return context.openFileOutput(LastPathComponent(path), PRIVATE_FILES_MODE);
    case LOCATION_EXTERNAL:
        File externalDir = Environment.getExternalStorageDirectory();
        
        File containingFolder = new File(externalDir, DirectoryPath(path));
        containingFolder.mkdirs();
        
        File f = new File(externalDir, path);
        return new FileOutputStream(f);
    }
    throw new IllegalArgumentException("Invalid destination location: "+StringForLocation(location));
}

/**
 * Copies a file from resources given its resource identifier to the app's
 * private folder or the external storage.
 * @param resourceId The resource id of the file to copy.
 * @param destinationLocation A single location for the file. Can only be LOCATION_PRIVATE or LOCATION_EXTERNAL.
 * @param destinationPath The path for the output file.
 * @param context The context used to open the file.
 * @throws IOException if the file can not be created.
 * @throws IllegalArgumentException If the destination location is not LOCATION_PRIVATE or LOCATION_EXTERNAL.
 * */
public static void CopyFileFromResources(int resourceId, int destinationLocation,
        String destinationPath, Context context)
                throws IOException
{
    FileInputStream fis = (FileInputStream) context.getResources().openRawResource(resourceId);
    FileOutputStream fos = GetFileOutputStream(destinationLocation, destinationPath, context);
    CopyFileStream(fis, fos);
}

/**
 * Copies a file to the app's private folder or the external storage.
 * @param originLocation The origin location. Can not only be LOCATION_PRIVATE or LOCATION_EXTERNAL.
 * @param originPath The path for the origin file.
 * @param destinationLocation A single location for the file. Can only be LOCATION_PRIVATE or LOCATION_EXTERNAL.
 * @param destinationPath The path for the output file.
 * @param context The context used to open the file.
 * @throws IOException if the file can not be created.
 * @throws IllegalArgumentException If the origin location is LOCATION_RESOURCES, or the destination location is not LOCATION_PRIVATE or LOCATION_EXTERNAL.
 * */
public static void CopyFile(int originLocation, String originPath,
    int destinationLocation, String destinationPath, Context context)
    throws IOException
{
    FileInputStream fis = GetFileInputStream(originLocation, originPath, context);
    FileOutputStream fos = GetFileOutputStream(destinationLocation, destinationPath, context);
    CopyFileStream(fis, fos);
}

/**
 * Copies a file to the app's private folder or the external storage. Shortcut for the previous method.
 * @param origin The location for both the origin and the destination. Can not only be LOCATION_PRIVATE or LOCATION_EXTERNAL.
 * @param originPath The path for the origin file.
 * @param destinationPath The path for the output file.
 * @param context The context used to open the file.
 * @throws IOException if the file can not be created.
 * @throws IllegalArgumentException If the location is LOCATION_RESOURCES or LOCATION_ASSETS
 * */
public static void CopyFile(int location, String originPath, String destinationPath, Context context)
    throws IOException
{
    FileInputStream fis = GetFileInputStream(location, originPath, context);
    FileOutputStream fos = GetFileOutputStream(location, destinationPath, context);
    CopyFileStream(fis, fos);
}

/**
 * Moves a file. Origin and destination must be in the same location.
 * @param location The file location. Can only be LOCATION_PRIVATE or LOCATION_EXTERNAL, because they are the two only locations where we can write in Android.
 * @param originPath The path for the origin file.
 * @param destinationPath The path for the output file.
 * @param context The context used to open the file.
 * @throws IOException if the origin file can not be read or the destination file can not be created.
 * @throws IllegalArgumentException If the location is other than LOCATION_PRIVATE or LOCATION_EXTERNAL.
 * */
public static void MoveFile(int location, String originPath, String destinationPath, Context context)
    throws IOException
{
    File inputFile = null, outputFile = null;
    switch(location) {
    case LOCATION_PRIVATE:
        inputFile = context.getFileStreamPath( LastPathComponent(originPath) );
        outputFile = context.getFileStreamPath( LastPathComponent(destinationPath) );
        break;
    case LOCATION_EXTERNAL:
        inputFile = new File(Environment.getExternalStorageDirectory(),
                originPath);
        outputFile = new File(Environment.getExternalStorageDirectory(), 
                destinationPath);
        break;
    }
    
    if(inputFile != null) {
        // needs outputFile.mkdirs()?
        boolean result = inputFile.renameTo(outputFile);
        if(!result)
            throw new IOException("Failed moving file "+originPath +" to "+destinationPath + " in location: "+StringForLocation(location));
    } else {
        throw new IllegalArgumentException("Invalid location: "+StringForLocation(location));
    }
}

/**
 * Returns a path's last path component, or the file name.
 * @param path A path from where to retrieve the file name.
 * @return The contents of the string after the last "/" character.
 * */
public final static String LastPathComponent(String path)
{
    final int index = path.lastIndexOf("/");
    if(index>=0/* && index<path.length()*/) {
        if(index == path.length()-1)
            return "";
        else
            return path.substring(index+1, path.length());
    } else {
        return path;
    }
}

/**
 * Returns a extension for the filename.
 * @param path A path from where to retrieve the extension.
 * @return A String containing the extension, "" if it has no extension, or null if it's a folder
 * */
public final static String Extension(String path)
{
    String lastComponent = LastPathComponent(path);
    if(lastComponent.length() > 0) {
        int point = lastComponent.lastIndexOf('.');
        if(point > 0) {
            return lastComponent.substring(point+1, lastComponent.length());
        } else {
            return "";
        }
    } else {
        return null;
    }
}

/**
 * Returns a path's directory path (without the file name)
 * @param path A path from where to retrieve the file directory.
 * @return The contents of the string before the last "/" character, including it
 * */
public final static String DirectoryPath(String path)
{
    final int index = path.lastIndexOf("/");
    if(index>=0/* && index<path.length()*/) {
        return path.substring(0, index);
    } else {
        return path;
    }
}

}
