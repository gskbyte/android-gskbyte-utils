package org.gskbyte.view;

import android.content.res.TypedArray;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewUtil
{

static public void setHtmlTextFromAttributes(TextView textView, AttributeSet attrs,
        int [] attributeSet, int fontAttr)
{
    TypedArray a = textView.getContext().obtainStyledAttributes(attrs, attributeSet);
    String htmlText = a.getString(fontAttr);
    if(htmlText != null)
        setHtmlText(textView, htmlText);
    a.recycle();
}

static public void setHtmlText(TextView textView, String html)
{
    textView.setText( Html.fromHtml(html) );
}

}
