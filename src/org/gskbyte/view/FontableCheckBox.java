package org.gskbyte.view;

import org.gskbyte.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

public class FontableCheckBox
extends CheckBox
{

public FontableCheckBox(Context context, AttributeSet attrs)
{
    super(context, attrs);
    initAttributes(context, attrs);
}

public FontableCheckBox(Context context, AttributeSet attrs, int defStyle)
{
    super(context, attrs, defStyle);
    initAttributes(context, attrs);
}

private void initAttributes(Context context, AttributeSet attrs)
{
    if(!isInEditMode()) {
        FontUtil.setCustomFont(this, context, attrs,
                R.styleable.org_gskbyte_view_FontableCheckBox,
                R.styleable.org_gskbyte_view_FontableCheckBox_font);
        TextViewUtil.setHtmlTextFromAttributes(this, attrs,
                R.styleable.org_gskbyte_view_FontableCheckBox,
                R.styleable.org_gskbyte_view_FontableCheckBox_html_text);
    }
}
public void setHtmlText(String html)
{
    TextViewUtil.setHtmlText(this, html);
}

}
