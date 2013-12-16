package org.gskbyte.view;

import org.gskbyte.R;
import org.gskbyte.drawable.AutoBackgroundButtonDrawable;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * Applies a pressed state color filter or disabled state alpha for the button's
 * background drawable.
 * 
 * @see Original implementation: https://github.com/shiki/android-autobgbutton
 * 
 * This extension enables customization of the filter color and allows to apply it to the source image.
 * By default, if no background is enabled, it is set to null.
 * 
 * @author shiki
 * @author Jose Alcalá Correa
 */
public class AutoBackgroundImageButton
extends ImageButton
{

protected int filterColor;
protected boolean applyFilterToImage;

protected boolean filterColorLoaded = false;

public AutoBackgroundImageButton(Context context, AttributeSet attrs)
{
    super(context, attrs, 0); // setting the third attribute to 0 removes default button BG
    initAttributes(context, attrs);
}

public AutoBackgroundImageButton(Context context, AttributeSet attrs, int defStyle)
{
    super(context, attrs, defStyle);
    initAttributes(context, attrs);
}

protected void initAttributes(Context context, AttributeSet attrs)
{
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.org_gskbyte_view_AutoBackgroundImageButton);
    this.filterColor = a.getColor(R.styleable.org_gskbyte_view_AutoBackgroundImageButton_filterColor, AutoBackgroundButtonDrawable.DEFAULT_COLOR_FILTER);
    this.filterColorLoaded = true;
    setBackgroundDrawable( getBackground() );
    
    boolean apply = a.getBoolean(R.styleable.org_gskbyte_view_AutoBackgroundImageButton_applyFilterToImage, false);
    setApplyFilterToImage(apply);
    
    a.recycle();
}

public int getFilterColor()
{ return this.filterColor; }

public void setFilterColor(int filterColor)
{
    this.filterColor = filterColor;
    
    setBackground( getBackground() );
    setApplyFilterToImage( this.applyFilterToImage );
}

public boolean appliesFilterToImage()
{ return this.applyFilterToImage; }

public void setApplyFilterToImage(boolean apply)
{
    this.applyFilterToImage = apply;
    setImageDrawable( getDrawable() );
}

@Override
public void setBackground(Drawable d)
{
    setBackgroundDrawable(d);
}

@SuppressWarnings("deprecation")
@Override
public void setBackgroundDrawable(Drawable d)
{
    super.setBackgroundDrawable( filteredDrawable(d, true) );
}

@Override
public void setImageResource(int resId)
{
    setImageDrawable( filteredDrawable(getResources().getDrawable(resId), applyFilterToImage));
}

// TODO Implement a faster thing
@Override
public void setImageURI(Uri uri)
{
    super.setImageURI(uri);
    setImageDrawable( filteredDrawable(getDrawable(), applyFilterToImage) );
}

@Override
public void setImageDrawable(Drawable d)
{
    super.setImageDrawable( filteredDrawable(d, applyFilterToImage) );
}

private final Drawable filteredDrawable(Drawable d, boolean extraCondition)
{
    if( d!=null && filterColorLoaded && extraCondition && !(d instanceof AutoBackgroundButtonDrawable) ) {
        return new AutoBackgroundButtonDrawable(d, filterColor);
    } else {
        return d;
    }
}


}
