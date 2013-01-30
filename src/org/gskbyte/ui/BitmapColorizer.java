package org.gskbyte.ui;

import org.gskbyte.util.Logger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public class BitmapColorizer
{
    protected final Paint colorPaint =          new Paint();
    protected final ColorMatrix colorMatrix =   new ColorMatrix();
    protected final Canvas colorCanvas =        new Canvas();
    
    protected final Bitmap        baseBitmap;
    protected final int           bitmapWidth, bitmapHeight;
    protected final Bitmap.Config bitmapConfig;

    public BitmapColorizer(Context context, int drawableResource, Bitmap.Config bitmapConfig)
    {
        this(context, BitmapFactory.decodeResource(context.getResources(), drawableResource), bitmapConfig);
    }
    
    public BitmapColorizer(Context context, Bitmap bitmap, Bitmap.Config bitmapConfig)
    {
        this.baseBitmap = bitmap;
        this.bitmapWidth = this.baseBitmap.getWidth();
        this.bitmapHeight = this.baseBitmap.getHeight();
        this.bitmapConfig = bitmapConfig;
    }
    
    public Bitmap colorize(int color)
    {
        int a = Color.alpha(color),
            r = Color.red(color),
            g = Color.green(color),
            b = Color.blue(color);
        return colorize(a, r, g, b, Color.TRANSPARENT);
    }

    public Bitmap colorize(int color, int bgcolor)
    {
        int a = Color.alpha(color),
            r = Color.red(color),
            g = Color.green(color),
            b = Color.blue(color);
        return colorize(a, r, g, b, bgcolor);
    }

    public Bitmap colorize(int a, int r, int g, int b)
    {
        return colorize(a, r, g, b, Color.TRANSPARENT);
    }
    
    public Bitmap colorize(int a, int r, int g, int b, int bgcolor)
    {
        try {
            final float fa = a/256.0f, fr = r / 256.0f, fg = g / 256.0f, fb = b / 256.0f;

            colorMatrix.setScale(fr, fg, fb, fa);
            colorPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            final Bitmap ret = Bitmap.createBitmap(bitmapWidth, bitmapHeight, bitmapConfig);
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
