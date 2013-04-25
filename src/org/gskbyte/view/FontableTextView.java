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
    initAttributes(context, attrs);
}

public FontableTextView(Context context, AttributeSet attrs, int defStyle)
{
    super(context, attrs, defStyle);
    initAttributes(context, attrs);
}

private void initAttributes(Context context, AttributeSet attrs)
{
    FontUtil.setCustomFont(this, context, attrs,
            R.styleable.org_gskbyte_view_FontableTextView,
            R.styleable.org_gskbyte_view_FontableTextView_font);
    TextViewUtil.setHtmlTextFromAttributes(this, attrs,
            R.styleable.org_gskbyte_view_FontableTextView,
            R.styleable.org_gskbyte_view_FontableTextView_font);
}
public void setHtmlText(String html)
{
    TextViewUtil.setHtmlText(this, html);
}

}
