package org.gskbyte.view;

import org.gskbyte.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class FontableTextView extends TextView
{
public FontableTextView(Context context) 
{ super(context); }

public FontableTextView(Context context, AttributeSet attrs)
{
    super(context, attrs);
    FontUtil.setCustomFont(this,context,attrs,
            R.styleable.org_gskbyte_view_FontableTextView,
            R.styleable.org_gskbyte_view_FontableTextView_font);
}

public FontableTextView(Context context, AttributeSet attrs, int defStyle)
{
    super(context, attrs, defStyle);
    FontUtil.setCustomFont(this,context,attrs,
            R.styleable.org_gskbyte_view_FontableTextView,
            R.styleable.org_gskbyte_view_FontableTextView_font);
}
}
