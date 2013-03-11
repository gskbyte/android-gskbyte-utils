package org.gskbyte.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import org.gskbyte.R;

public class FontableButton extends TextView
{
    public FontableButton(Context context) 
    { super(context); }

    public FontableButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        FontUtil.setCustomFont(this,context,attrs,
                R.styleable.org_gskbyte_view_FontableButton,
                R.styleable.org_gskbyte_view_FontableButton_font);
    }

    public FontableButton(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        FontUtil.setCustomFont(this,context,attrs,
                R.styleable.org_gskbyte_view_FontableButton,
                R.styleable.org_gskbyte_view_FontableButton_font);
    }
}
