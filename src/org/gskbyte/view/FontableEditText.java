package org.gskbyte.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import org.gskbyte.R;

public class FontableEditText extends EditText
{
public FontableEditText(Context context) 
{ super(context); }

public FontableEditText(Context context, AttributeSet attrs)
{
    super(context, attrs);
    FontUtil.setCustomFont(this,context,attrs,
            R.styleable.org_gskbyte_view_FontableEditText,
            R.styleable.org_gskbyte_view_FontableEditText_font);
}

public FontableEditText(Context context, AttributeSet attrs, int defStyle)
{
    super(context, attrs, defStyle);
    FontUtil.setCustomFont(this,context,attrs,
            R.styleable.org_gskbyte_view_FontableEditText,
            R.styleable.org_gskbyte_view_FontableEditText_font);
}
}
