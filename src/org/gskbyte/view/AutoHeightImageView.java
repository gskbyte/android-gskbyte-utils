package org.gskbyte.view;

import org.gskbyte.R;
import org.gskbyte.util.Logger;

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

protected float minHeightProportion, maxHeightProportion;

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
    this.minHeightProportion = a.getFloat(R.styleable.org_gskbyte_view_AutoHeightImageView_minHeightProportion, Float.MIN_VALUE);
    this.maxHeightProportion = a.getFloat(R.styleable.org_gskbyte_view_AutoHeightImageView_maxHeightProportion, Float.MAX_VALUE);
    a.recycle();
}

public float getMinHeightProportion()
{ return minHeightProportion; }

public float getMaxHeightProportion()
{ return maxHeightProportion; }

public void setMinHeightProportion(float minHeightProportion)
{
    this.minHeightProportion = minHeightProportion;
    requestLayout();
}

public void setMaxHeightProportion(float maxHeightProportion)
{
    this.maxHeightProportion = maxHeightProportion;
    requestLayout();
}

@Override
public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
{
    final Drawable drawable = getDrawable();
    int width = MeasureSpec.getSize(widthMeasureSpec);
    if(drawable == null || width == 0) {
        setMeasuredDimension(200, 200);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    } else {
        
        float bitmapWidth = getDrawable().getIntrinsicWidth();
        float bitmapHeight = getDrawable().getIntrinsicHeight();
        
        float proportion = Math.min(bitmapHeight / bitmapWidth, this.maxHeightProportion);
        proportion = Math.max(proportion, this.minHeightProportion);
        
        int height =  (int) (width*proportion);
        setMeasuredDimension(width, height);
    }
}

}
