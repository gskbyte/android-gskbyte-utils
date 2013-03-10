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

import org.gskbyte.util.Logger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/**
 * Utility class to get colorized copies of a given bitmap.
 * */
public class BitmapColorizer
{
protected final Paint colorPaint =          new Paint();
protected final ColorMatrix colorMatrix =   new ColorMatrix();
protected final Canvas colorCanvas =        new Canvas();

protected final Bitmap        baseBitmap;
protected final int           bitmapWidth, bitmapHeight;
protected final Bitmap.Config outputConfig;

/**
 * Constructor.
 * @param bitmap The source bitmap to which to apply color.
 * @param outputConfig The bitmapConfig to use for the resulting bitmaps.
 * */
public BitmapColorizer(Bitmap bitmap, Bitmap.Config outputConfig)
{
    this.baseBitmap = bitmap;
    this.bitmapWidth = this.baseBitmap.getWidth();
    this.bitmapHeight = this.baseBitmap.getHeight();
    this.outputConfig = outputConfig;
}

/**
 * Constructor to load a bitmap from resources
 * @param Context the context from which to load the bitmap resource
 * @param drawableResource The drawable id to load the bitmap
 * @param outputConfig The bitmapConfig to use for the resulting bitmaps.
 * */
public BitmapColorizer(Context context, int drawableResource, Bitmap.Config outputConfig)
{
    this(BitmapFactory.decodeResource(context.getResources(), drawableResource), outputConfig);
}

/**
 * Applies the color given with the integer to a copy of the source bitmap,
 * which is returned. This bitmap has a transparent background (if the current
 * bitmapConfig supports it)
 * @param color The color to apply to the bitmap.
 * @return A colorized copy of the source bitmap.
 * */
public Bitmap colorize(int color)
{
    int a = Color.alpha(color),
        r = Color.red(color),
        g = Color.green(color),
        b = Color.blue(color);
    return colorize(a, r, g, b, Color.TRANSPARENT);
}

/**
 * Applies the color given with the integer to a copy of the source bitmap,
 * which is returned.
 * @param color The color to apply to the bitmap.
 * @param bgcolor The background color for the returned copy.
 * @return A colorized copy of the source bitmap with a defined background color.
 * */
public Bitmap colorize(int color, int bgcolor)
{
    int a = Color.alpha(color),
        r = Color.red(color),
        g = Color.green(color),
        b = Color.blue(color);
    
    return colorize(a, r, g, b, bgcolor);
}

/**
 * Applies the color given with the integer to a copy of the source bitmap,
 * which is returned. This bitmap has a transparent background (if the current
 * bitmapConfig supports it)
 * @param a alpha component of the color, in the range [0,255]
 * @param r red component of the color, in the range [0,255]
 * @param g green component of the color, in the range [0,255]
 * @param b blue component of the color, in the range [0,255]
 * @return A colorized copy of the source bitmap.
 * */
public Bitmap colorize(int a, int r, int g, int b)
{
    return colorize(a, r, g, b, Color.TRANSPARENT);
}


/**
 * Applies the color given with the integer to a copy of the source bitmap,
 * which is returned. This bitmap has a transparent background (if the current
 * bitmapConfig supports it)
 * @param a alpha component of the color, in the range [0,255]
 * @param r red component of the color, in the range [0,255]
 * @param g green component of the color, in the range [0,255]
 * @param b blue component of the color, in the range [0,255]
 * @param bgcolor The background color for the returned copy.
 * @return A colorized copy of the source bitmap.
 * */
public Bitmap colorize(int a, int r, int g, int b, int bgcolor)
{
    try {
        final float fa = a/256.0f, fr = r / 256.0f, fg = g / 256.0f, fb = b / 256.0f;

        colorMatrix.setScale(fr, fg, fb, fa);
        colorPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        final Bitmap ret = Bitmap.createBitmap(bitmapWidth, bitmapHeight, outputConfig);
        colorCanvas.setBitmap(ret);
        if(bgcolor != Color.TRANSPARENT)
            colorCanvas.drawColor(bgcolor);
        
        colorCanvas.drawBitmap(baseBitmap, 0, 0, colorPaint);

        return ret;
    } catch(Exception e) {
        Logger.except(getClass(), e);
        return baseBitmap;
    }
}
}
