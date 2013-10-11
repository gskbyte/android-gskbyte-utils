package org.gskbyte.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Just a Layout to force an inner item to be squared.
 * 
 * Possible use cases:
 * 
 * - to have a view centered in a square (having layout_height and layout_width="wrap_content" and layout_gravity="center")
 * - to have a squared background (setting the background to this parent layout, instead of the child)
 * - to force a view to be squared (having layout_height and layout_width="match_parent")
 * 
 * */
public class SquaredLayout
extends FrameLayout
{

public SquaredLayout(Context context, AttributeSet attrs)
{
    super(context, attrs);
}

@Override
public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
{
    if(widthMeasureSpec != 0) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    } else {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }
}
}
