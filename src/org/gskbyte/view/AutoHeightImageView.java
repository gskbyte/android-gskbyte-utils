package org.gskbyte.view;

import org.gskbyte.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * ImageView that computes its height depending on the given width.
 * */
public class AutoHeightImageView
extends ImageView
{

protected float maxHeightProportion;

public AutoHeightImageView(Context context, AttributeSet attrs)
{
    super(context, attrs);
    initAttributes(context, attrs);
}

public AutoHeightImageView(Context context, AttributeSet attrs, int defStyle)
{
    super(context, attrs, defStyle);
    initAttributes(context, attrs);
}

private void initAttributes(Context context, AttributeSet attrs)
{
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.org_gskbyte_view_AutoHeightImageView);
    this.maxHeightProportion = a.getFloat(R.styleable.org_gskbyte_view_AutoHeightImageView_maxHeightProportion, Float.MAX_VALUE);
    a.recycle();
}

public float getMaxHeightProportion()
{ return maxHeightProportion; }

public void setMaxHeightProportion(float maxHeightProportion)
{
    this.maxHeightProportion = maxHeightProportion;
    requestLayout();
}


@Override
public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
{
    final Drawable drawable = getDrawable();
    if(drawable == null) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    } else {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        
        float bitmapWidth = getDrawable().getIntrinsicWidth();
        float bitmapHeight = getDrawable().getIntrinsicHeight();
        
        float proportion = Math.min(bitmapHeight / bitmapWidth, this.maxHeightProportion);
        
        setMeasuredDimension(width, (int) (width*proportion));
    }
}

}
