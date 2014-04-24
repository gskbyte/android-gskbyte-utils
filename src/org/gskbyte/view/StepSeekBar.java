package org.gskbyte.view;

import java.lang.ref.WeakReference;

import org.gskbyte.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
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
implements SeekBar.OnSeekBarChangeListener
{

public static final int DEFAULT_SMOOTH_FACTOR = 128;

private Drawable primaryThumb; // needed for API<16 (JELLY_BEAN), because getThumb() is not implemented
protected Drawable secondaryThumb;
protected int secondaryThumbOffset; // half of the drawable width, not yet configurable

protected int smoothFactor;
protected WeakReference<OnSeekBarChangeListener> overridenListener = new WeakReference<SeekBar.OnSeekBarChangeListener>(null);

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

private final int smoothedValue(int realValue)
{
    int ret = realValue;
    if(smoothFactor != 1) {
        ret = Math.round((float)realValue/smoothFactor);
    }
    return ret;
}

public int getSmoothFactor()
{
    // we need the check because smoothFactor is initially zero
    return (smoothFactor>0?smoothFactor:1);
}

public void setSmoothFactor(int f)
{
    if(f==smoothFactor)
        return;
    
    if(f < 1)
        throw new IllegalArgumentException("smoothFactor must be >= 1");
    
    final int max = getSmoothedMax();
    smoothFactor = f;
    setMax(max);
    
    invalidate();
}

public int getSmoothedMax()
{
    return getMax()/getSmoothFactor();
}

@Override
public void setMax(int max)
{
    super.setMax(max*getSmoothFactor());
}

@Override
public void setThumb(Drawable d)
{
    super.setThumb(d);
    primaryThumb = d;
}

@SuppressLint("NewApi")
//@Override
public Drawable getThumb()
{
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        return super.getThumb();
    } else {
        return primaryThumb;
    }
}

public Drawable getSecondaryThumb()
{ return secondaryThumb; }

private void setDefaultSecondaryThumb()
{
    Bitmap b = bitmapFromDrawable( getThumb() );
    if(b != null) {
        Drawable d = new BitmapDrawable(getResources(), b);
        
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(cm);
        d.setColorFilter( filter );
        
        secondaryThumb = d;
    }
}

protected void initAttributes(Context context, AttributeSet attrs)
{
    super.setOnSeekBarChangeListener(this);
    
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.org_gskbyte_view_StepSeekBar);
    
    Drawable d = a.getDrawable(R.styleable.org_gskbyte_view_StepSeekBar_secondaryThumb);
    
    if(d!=null) {
        setSecondaryThumb( d );
    } else if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        setDefaultSecondaryThumb();
    }
    
    boolean isSmoothed = a.getBoolean(R.styleable.org_gskbyte_view_StepSeekBar_smoothed, false);
    final int defaultSmoothFactor = isSmoothed ? DEFAULT_SMOOTH_FACTOR : 1;
    setSmoothFactor( a.getInteger(R.styleable.org_gskbyte_view_StepSeekBar_smoothFactor, defaultSmoothFactor) );
    
    a.recycle();
    
    if( isInEditMode() ) {
        setMax(4);
    }
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

@SuppressLint("NewApi")
@Override
protected synchronized void onDraw(Canvas canvas)
{
    if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
        onDrawBefore3(canvas);
    } else {
        onDrawFrom3(canvas);
    }

}

/**
 * THIS METHOD IS LIKELY TO BE BUGGY, has benn tested in an app in which I had LOTS of time pressure
 * */

final Rect copyBounds = new Rect();
private void onDrawBefore3(Canvas canvas)
{
    final float xscale = 0.90f;
    
    Drawable d = getProgressDrawable();
    final int pl = getPaddingLeft();
    final int pt = getPaddingTop();
    final int width = getWidth();
    final int scaledpl = (int) ( width*xscale * ((1-xscale)*0.5));

    if(d != null) {
        canvas.save();
        canvas.scale(xscale, 1);
        canvas.translate(scaledpl, pt);
        d.draw(canvas);
        canvas.restore();
    }
    
    final int max = getSmoothedMax();
    final int increment = (width-secondaryThumb.getIntrinsicWidth()) / max;   
    if(secondaryThumb != null) {
        canvas.save();
        canvas.translate(pl, 0);
        for(int i=0; i<max+1; ++i) {
            secondaryThumb.draw(canvas);
            canvas.translate(increment, 0);
        }
        canvas.restore();
    }
    
    
    Drawable thumb = getThumb();
    if (thumb != null) {
        final float rate = getProgress() / (float) getMax();
                
        final int x = (int) (rate*increment*getSmoothedMax());
        final int w = thumb.getIntrinsicWidth();
        
        canvas.save();
        
        copyBounds.set( thumb.getBounds() );
        
        thumb.getBounds().left = x;
        thumb.getBounds().right = x + w;
        
        thumb.draw(canvas);
        
        thumb.setBounds(copyBounds);
        canvas.restore();
    }
}



@SuppressLint("WrongCall")
private void onDrawFrom3(Canvas canvas)
{
    super.onDraw(canvas);
    
    if (secondaryThumb != null) {
        final int max = getSmoothedMax();
        
        final int paddingTop = getPaddingTop();
        final int increment = (getWidth()-secondaryThumb.getIntrinsicWidth()) / max;                
        canvas.save();
        // draw secondary thumbs
        for(int i=0; i<max+1; ++i) {
            if(i!=0)
                canvas.translate(increment, paddingTop);
            secondaryThumb.draw(canvas);
        }
        
        canvas.restore();
        
        // thumb needs to be repainted over the secondary thumbs
        // @see AbsSeekBar#onDraw()
        
        if(getThumb() != null) {
            canvas.save();
            canvas.translate(getPaddingLeft() - getThumbOffset(), paddingTop);
            
            getThumb().draw(canvas);
            canvas.restore();
        }
    }
}


@Override
public void setOnSeekBarChangeListener(OnSeekBarChangeListener l)
{
    overridenListener = new WeakReference<SeekBar.OnSeekBarChangeListener>(l);
}

// does not exist in superclass?
//@Override
public OnSeekBarChangeListener getOnSeekBarChangeListener()
{
    return overridenListener.get();
}


public int getSmoothedProgress()
{
    return smoothedValue( super.getProgress() );
}

@Override
public void setProgress(int progress)
{
    super.setProgress(progress*getSmoothFactor());
}

@Override
public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
{
    if(getOnSeekBarChangeListener() != null)
        getOnSeekBarChangeListener().onProgressChanged(seekBar, smoothedValue(progress), fromUser);
}

@Override
public void onStartTrackingTouch(SeekBar seekBar)
{
    if(getOnSeekBarChangeListener() != null)
        getOnSeekBarChangeListener().onStartTrackingTouch(seekBar);
}

@Override
public void onStopTrackingTouch(SeekBar seekBar)
{
    if(smoothFactor != 1) {
        setProgress( smoothedValue(super.getProgress() ));
        invalidate();
    }
    if(getOnSeekBarChangeListener() != null)
        getOnSeekBarChangeListener().onStopTrackingTouch(seekBar);
}

}
