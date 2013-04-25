package org.gskbyte.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import org.gskbyte.R;

public class FontableButton
extends Button
{
public FontableButton(Context context) 
{ super(context); }

public FontableButton(Context context, AttributeSet attrs)
{
    super(context, attrs);
    initAttributes(context, attrs);
}

public FontableButton(Context context, AttributeSet attrs, int defStyle)
{
    super(context, attrs, defStyle);
    initAttributes(context, attrs);
}

private void initAttributes(Context context, AttributeSet attrs)
{
    FontUtil.setCustomFont(this, context, attrs,
            R.styleable.org_gskbyte_view_FontableButton,
            R.styleable.org_gskbyte_view_FontableButton_font);
    TextViewUtil.setHtmlTextFromAttributes(this, attrs,
            R.styleable.org_gskbyte_view_FontableButton,
            R.styleable.org_gskbyte_view_FontableButton_html_text);
}

public void setHtmlText(String html)
{
    TextViewUtil.setHtmlText(this, html);
}
}
