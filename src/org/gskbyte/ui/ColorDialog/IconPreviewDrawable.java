package org.gskbyte.ui.ColorDialog;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

class IconPreviewDrawable extends Drawable
{
    private Bitmap mBitmap;
    private Bitmap mTmpBitmap;
    private Canvas mTmpCanvas;
    private int mTintColor;

    public IconPreviewDrawable(final Resources res, final int id)
    {
        /*
        Bitmap b;
        try {
            b = BitmapFactory.decodeResource(res, id);
            if (b == null) {
                b = BitmapFactory.decodeResource(res,
                        R.drawable.color_picker_bg);
            }
        } catch (final NotFoundException e) {
            b = BitmapFactory.decodeResource(res, R.drawable.color_picker_bg);
        }
        mBitmap = b;
        mTmpBitmap = Bitmap.createBitmap(b.getWidth(), b.getHeight(),
                Config.ARGB_8888);
        mTmpCanvas = new Canvas(mTmpBitmap);*/
    }

    @Override
    public void draw(final Canvas canvas)
    {
        final Rect b = getBounds();
        final float x = (b.width() - mBitmap.getWidth()) / 2.0f;
        final float y = 0.75f * b.height() - mBitmap.getHeight() / 2.0f;

        mTmpCanvas.drawColor(0, Mode.CLEAR);
        mTmpCanvas.drawBitmap(mBitmap, 0, 0, null);
        /*
         * RectF r = new RectF(0, 0, mTmpCanvas.getWidth(),
         * mTmpCanvas.getHeight()); Paint p = new Paint();
         * p.setColor(mTintColor); mTmpCanvas.drawRoundRect(r, 5, 5, p);
         */
        mTmpCanvas.drawColor(mTintColor, Mode.SRC_ATOP);
        canvas.drawBitmap(mTmpBitmap, x, y, null);
    }

    @Override
    public int getOpacity()
    {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(final int alpha)
    {
    }

    @Override
    public void setColorFilter(final ColorFilter cf)
    {
    }

    @Override
    public void setColorFilter(final int color, final Mode mode)
    {
        mTintColor = color;
    }
}
