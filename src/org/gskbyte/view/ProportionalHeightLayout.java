package org.gskbyte.view;

import org.gskbyte.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class ProportionalHeightLayout
extends FrameLayout
{

private float heightProportion = 1;

public ProportionalHeightLayout(Context context, AttributeSet attrs)
{
    super(context, attrs);
    initAttributes(context, attrs);
}

public ProportionalHeightLayout(Context context, AttributeSet attrs, int defStyle)
{
    super(context, attrs);
    initAttributes(context, attrs);
}

private void initAttributes(Context context, AttributeSet attrs)
{
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.org_gskbyte_view_ProportionalHeightLayout);
    this.heightProportion = a.getFloat(R.styleable.org_gskbyte_view_ProportionalHeightLayout_heightProportion, 1);
    a.recycle();
}

public float getHeightProportion()
{ return heightProportion; }

public void setHeightProportion(float p)
{
    heightProportion = p;
    requestLayout();
}

@Override
public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
{
    int width = MeasureSpec.getSize(widthMeasureSpec);
    int height = (int)(width*heightProportion);
    setMeasuredDimension(width, height);
    for (int i = 0; i < getChildCount(); i++) {
        final View child = getChildAt(i);
        int wChildMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, width);
        int hChildMeasureSpec = getChildMeasureSpec(heightMeasureSpec, 0, height);
        child.measure(wChildMeasureSpec, hChildMeasureSpec);
    }
}

@Override
public void addView(View child)
{
    if (getChildCount() > 0) {
        throw new IllegalStateException("ProportionalHeightLayout can host only one direct child");
    }

    super.addView(child);
}

@Override
public void addView(View child, int index)
{
    if (getChildCount() > 0) {
        throw new IllegalStateException("ProportionalHeightLayout can host only one direct child");
    }

    super.addView(child, index);
}

@Override
public void addView(View child, ViewGroup.LayoutParams params)
{
    if (getChildCount() > 0) {
        throw new IllegalStateException("ProportionalHeightLayout can host only one direct child");
    }

    super.addView(child, params);
}

@Override
public void addView(View child, int index, ViewGroup.LayoutParams params)
{
    if (getChildCount() > 0) {
        throw new IllegalStateException("ProportionalHeightLayout can host only one direct child");
    }

    super.addView(child, index, params);
}


}
