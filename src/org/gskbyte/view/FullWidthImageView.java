package org.gskbyte.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Solves the problem of some ImageViews being limited by their container's height.
 * */

public class FullWidthImageView
extends ImageView
{

public FullWidthImageView(Context context, AttributeSet attrs)
{
    super(context, attrs);
}

public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
{
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    
    Drawable d = getDrawable();
    if(d != null) {
        final float drawableWidth = d.getIntrinsicWidth();
        final float drawableHeight = d.getIntrinsicHeight();
        float proportion = drawableHeight/drawableWidth;
        
        final int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int viewHeight = (int)(viewWidth * proportion);
        
        setMeasuredDimension(viewWidth, viewHeight);
    }
}

}
