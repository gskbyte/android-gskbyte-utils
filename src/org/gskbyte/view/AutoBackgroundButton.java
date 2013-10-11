package org.gskbyte.view;

import lombok.Getter;
import org.gskbyte.R;
import org.gskbyte.drawable.AutoBackgroundButtonDrawable;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Applies a pressed state color filter or disabled state alpha for the button's
 * background drawable.
 * 
 * @see Original implementation: https://github.com/shiki/android-autobgbutton
 * 
 * This extension enables customization of the filter color and allows to apply it to the side drawables and the text itself.
 * By default, if no background is enabled, it is set to null.
 * 
 * @author shiki
 * @author Jose Alcalá Correa
 */
public class AutoBackgroundButton
extends Button
{

@Getter
protected int filterColor;
@Getter
protected boolean applyFilterToDrawables, applyFilterToText;

protected boolean filterColorLoaded = false;

public AutoBackgroundButton(Context context, AttributeSet attrs)
{
    super(context, attrs, 0); // setting the third attribute to 0 removes default button BG
    setClickable(true);
    initAttributes(context, attrs);
}

public AutoBackgroundButton(Context context, AttributeSet attrs, int defStyle)
{
    super(context, attrs, defStyle);
    initAttributes(context, attrs);
}

protected void initAttributes(Context context, AttributeSet attrs)
{
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.org_gskbyte_view_AutoBackgroundButton);
    this.filterColor = a.getColor(R.styleable.org_gskbyte_view_AutoBackgroundButton_filterColor, AutoBackgroundButtonDrawable.DEFAULT_COLOR_FILTER);
    filterColorLoaded = true;
    setBackgroundDrawable( getBackground() );
    
    boolean applyDrawables = a.getBoolean(R.styleable.org_gskbyte_view_AutoBackgroundButton_applyFilterToDrawables, false);
    setApplyFilterToDrawables(applyDrawables);

    boolean applyText = a.getBoolean(R.styleable.org_gskbyte_view_AutoBackgroundButton_applyFilterToText, false);
    setApplyFilterToText(applyText);
    
    a.recycle();
}

public void setFilterColor(int filterColor)
{
    this.filterColor = filterColor;
    
    setBackgroundDrawable( getBackground() );
    setApplyFilterToDrawables( this.applyFilterToDrawables );
    setApplyFilterToText( this.applyFilterToText );
}

public void setApplyFilterToDrawables(boolean apply)
{
    this.applyFilterToDrawables = apply;
    
    Drawable[] d = getCompoundDrawables();
    setCompoundDrawables(d[0], d[1], d[2], d[3]);
}

private ColorStateList originalTextColors = null;
public void setApplyFilterToText(boolean apply)
{
    this.applyFilterToText = apply;

    // should take color from state list
    if(originalTextColors == null) {
        originalTextColors = getTextColors();
    }
    final int defaultTextColor = originalTextColors.getDefaultColor();
    if(apply) {
        final int [][] states = new int[][] {
                new int[] {android.R.attr.state_enabled, android.R.attr.state_pressed},  // pressed state
                new int[] {} // default state
        };
        final int [] color_values = {
                filterColor, defaultTextColor
        };
        
        setTextColor( new ColorStateList(states, color_values) );
    } else {
        setTextColor( originalTextColors );
    }
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
    // Replace the original background drawable (e.g. image) with a
    // LayerDrawable that contains the original drawable.
    super.setBackgroundDrawable( filteredDrawable(d, true) );
}

@Override
public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom)
{
    super.setCompoundDrawables(
            filteredDrawable(left, applyFilterToDrawables),
            filteredDrawable(top, applyFilterToDrawables),
            filteredDrawable(right, applyFilterToDrawables),
            filteredDrawable(bottom, applyFilterToDrawables)
            );
    
}

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
@Override
public void setCompoundDrawablesRelative(Drawable left, Drawable top, Drawable right, Drawable bottom)
{
    super.setCompoundDrawablesRelative(
            filteredDrawable(left, applyFilterToDrawables),
            filteredDrawable(top, applyFilterToDrawables),
            filteredDrawable(right, applyFilterToDrawables),
            filteredDrawable(bottom, applyFilterToDrawables)
            );
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
