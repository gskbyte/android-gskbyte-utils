package org.gskbyte.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * This class avoids the problem of a List which is too small when you use wrap_content.
 * 
 * A typical use-case for this is to have a ListView that can be embedded in a ScrollView
 * (for example, when you want to include a custom header)
 * 
 * @see ExpandedGridView, the source code is adapted from there
 * 
 * Original source http://stackoverflow.com/questions/8481844/gridview-height-gets-cut
 * */
public class ExpandedListView
extends ListView
{

public ExpandedListView(Context context, AttributeSet attrs)
{ super(context, attrs); }

public ExpandedListView(Context context, AttributeSet attrs,
        int defStyle)
{
    super(context, attrs, defStyle);
}


private boolean adapterJustSet = true;
@Override
public void setAdapter(ListAdapter adapter)
{
    super.setAdapter(adapter);
    adapterJustSet = true;
}

@SuppressLint("DrawAllocation")
@Override
public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
{
    // Calculate entire height by providing a very large height hint.
    // But do not use the highest 2 bits of this integer; those are
    // reserved for the MeasureSpec mode.
    int expandSpec = MeasureSpec.makeMeasureSpec(
            Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
    super.onMeasure(widthMeasureSpec, expandSpec);

    getLayoutParams().height = getMeasuredHeight();
    if(adapterJustSet) {
        ViewParent parent = getParent();
        if(parent instanceof ViewGroup) {
            ViewParent grandparent = parent.getParent();
            if(grandparent instanceof ScrollView) {
                final ScrollView sv = ((ScrollView) grandparent);
                sv.post(new Runnable() { 
                    public void run() { 
                        sv.scrollTo(0, 0);
                    }});
            }
        }
        adapterJustSet = false;
    }
}

}
