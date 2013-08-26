package org.gskbyte.view;

import org.gskbyte.R;

import lombok.Getter;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * Extended SeekBar that shows thumbs for the available positions of the thumb.
 * This means, that if a Seekbar has a maximum of 4, it will display four points in the positions where the thumb could
 * be positioned.
 * 
 * By default, the view uses a grayed version of the defined secondary thumb. Another bitmap can be defined using the
 * attribute "secondaryThumbOffset" in Layout XML files.
 * 
 * The thumb and the secondary thumb must have the same size for the view to be correctly drawn. This should be workarounded in
 * following revisions of the view.
 * */
public class StepSeekBar
extends SeekBar
{

@Getter
protected Drawable secondaryThumb;
protected int secondaryThumbOffset; // half of the drawable width, not yet configurable

public StepSeekBar(Context context, AttributeSet attrs)
{
    super(context, attrs);
    initAttributes(context, attrs);
}

public StepSeekBar(Context context, AttributeSet attrs, int defStyle)
{
    super(context, attrs, defStyle);
    initAttributes(context, attrs);
}

protected void initAttributes(Context context, AttributeSet attrs)
{
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.org_gskbyte_view_StepSeekBar);
    
    Drawable d = a.getDrawable(R.styleable.org_gskbyte_view_StepSeekBar_secondaryThumb);
    if( d == null) {
        Bitmap b = bitmapFromDrawable( getThumb() );
        if(b != null) {
            d = new BitmapDrawable(getResources(), b);
            
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(cm);
            d.setColorFilter( filter );
        }
    }
    
    if(d!=null)
        setSecondaryThumb( d );
    
    a.recycle();
}

protected static Bitmap bitmapFromDrawable(Drawable d)
{
    if(d instanceof BitmapDrawable) {
        BitmapDrawable bd = (BitmapDrawable) d;
        return bd.getBitmap();
    } else if (d instanceof StateListDrawable) {
        StateListDrawable sld = (StateListDrawable) d;
        return bitmapFromDrawable(sld.getCurrent());
    } else {
        return null;
    }
}

public void setSecondaryThumb(Drawable d)
{
    if(d == secondaryThumb)
        return;
    
    if(secondaryThumb != null)
        secondaryThumb.setCallback(null);
    
    secondaryThumb = d;
    if(secondaryThumb != null) {
        secondaryThumb.setCallback(this);
        secondaryThumb.setBounds(0, 0, secondaryThumb.getIntrinsicWidth(), secondaryThumb.getIntrinsicHeight());
        secondaryThumbOffset = secondaryThumb.getIntrinsicWidth() / 2;
        
        requestLayout();
    } else {
        
    }
    
    invalidate();
}

@Override
protected synchronized void onDraw(Canvas canvas)
{
    super.onDraw(canvas);
    if (secondaryThumb != null) {
        final int paddingTop = getPaddingTop();
        final int increment = (getWidth()-secondaryThumb.getIntrinsicWidth()) / getMax();
        final int progress = getProgress();
                
        canvas.save();
        
        for(int i=0; i<getMax()+1; ++i) {
            if(i!=0)
                canvas.translate(increment, paddingTop);
            if(i!=progress)
                secondaryThumb.draw(canvas);
        }
        
        canvas.restore();
    }
}

}
