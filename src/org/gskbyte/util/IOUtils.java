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
public class IOUtils
{

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
 * Checks if a file exists. Only accepts the following exact locations:
 *  LOCATION_ASSETS, LOCATION_PRIVATE and LOCATION_EXTERNAL.
 * Does not accept combinations of those using bitwise operations.
 * @param location Location, can not be a combination of the default values.
 * @param path The path of the file.
 * @param context The context needed to access files.
 * */
public static boolean ExistsFile(int location, String path, Context context)
throws IllegalArgumentException
{
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
        f = new File(Environment.getExternalStoragePublicDirectory(null), path);
        return f.exists();
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
 * Get a FileInputStream given a location and a file.
 * @param location A single location for the file. Can not be LOCATION_RESOURCES nor a combination of locations.
 * @param path The path for the file.
 * @param context The context used to open the file.
 * @throws IOException if the file does not exist.
 * @throws IllegalArgumentException If the location is set to resources, or is a combination.
 * */
public static FileInputStream GetInputStream(int location, String path, Context context)
        throws IOException
{
    switch(location) {
    case LOCATION_RESOURCES:
        throw new IllegalArgumentException("Can not get resource id from path");
    case LOCATION_ASSETS:
        return (FileInputStream) context.getAssets().open(path);
    case LOCATION_PRIVATE:
        return context.openFileInput(path);
    case LOCATION_EXTERNAL:
        File f = new File(Environment.getExternalStoragePublicDirectory(null), path);
        return new FileInputStream(f);
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
public static FileOutputStream GetOutputStream(int location, String path, Context context)
        throws FileNotFoundException
{
    switch(location) {
    case LOCATION_PRIVATE:
        return context.openFileOutput(path, Context.MODE_PRIVATE);
    case LOCATION_EXTERNAL:
        File f = new File(Environment.getExternalStoragePublicDirectory(null), path);
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
    FileOutputStream fos = GetOutputStream(destinationLocation, destinationPath, context);
    CopyFileStream(fis, fos);
}

/**
 * Copies a file to the app's private folder or the external storage.
 * @param originLocation The origin location. Can not be LOCATION_RESOURCES, see the previous method.
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
    FileInputStream fis = GetInputStream(originLocation, originPath, context);
    FileOutputStream fos = GetOutputStream(destinationLocation, destinationPath, context);
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
public static void MoveFile(int location, String originPath,
        String destinationPath, Context context)
    throws IOException
{
    
    File inputFile = null, outputFile = null;
    switch(location) {
    case LOCATION_PRIVATE:
        inputFile = context.getFileStreamPath(originPath);
        outputFile = context.getFileStreamPath(destinationPath);
        break;
    case LOCATION_EXTERNAL:
        inputFile = new File(Environment.getExternalStoragePublicDirectory(null),
                originPath);
        outputFile = new File(Environment.getExternalStoragePublicDirectory(null), 
                destinationPath);
        break;
    }
    
    if(inputFile != null) {
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
public static String LastPathComponent(String path)
{
    int index = path.lastIndexOf("/");
    if(index>=0 && index<path.length()) {
        return path.substring(index+1, path.length());
    } else {
        return path;
    }
}

}