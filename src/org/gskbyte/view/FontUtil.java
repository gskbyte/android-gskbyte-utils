package org.gskbyte.view;

import java.util.HashMap;
import java.util.Map;

import org.gskbyte.util.Logger;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Utility class to set custom fonts to TextViews and children (like Button, for example)
 * Fonts must be saved under the subfolder fonts/ in assets.
 * 
 * TODO improve documentation
 * */
public class FontUtil
{

public static void setCustomFont(TextView tv, Context ctx, AttributeSet attrs, int[] attributeSet, int fontId)
{
    TypedArray a = ctx.obtainStyledAttributes(attrs, attributeSet);
    String customFont = a.getString(fontId);
    setCustomFont(tv, ctx, customFont);
    a.recycle();
}

public static boolean setCustomFont(TextView tv, Context ctx, String asset)
{
    if (TextUtils.isEmpty(asset))
        return false;
    Typeface tf = null;
    try {
        tf = getFont(ctx, asset);
        tv.setTypeface(tf);
    } catch (Exception e) {
        Logger.error(FontUtil.class, "Could not get typeface: " + asset + ",  " +e);
        return false;
    }

    return true;
}

private static final Map<String, Typeface> FontCache = new HashMap<String, Typeface>();

public static Typeface getFont(Context c, String name)
{
    synchronized (FontCache) {
        Typeface typeface = FontCache.get(name);
        if (typeface == null) {
            String filename = name;
            if(!filename.endsWith(".ttf") && !name.endsWith(".otf")) {
                filename += ".ttf";
            }

            typeface = Typeface.createFromAsset( c.getAssets(), "fonts/" + filename);
            FontCache.put(name, typeface);
        }

        return typeface;
    }
}

}

