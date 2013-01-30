package org.gskbyte.ui.ColorDialog;

import java.util.HashMap;

import android.graphics.Color;

class Defaults
{
    private static final HashMap<String, Integer> colors = new HashMap<String, Integer>();

    static {
        colors.put("backgroundColor", Color.BLACK);
        colors.put("textColor", Color.WHITE);
    }

    public static int getColor(final String key)
    {
        final Integer ret = colors.get(key);
        if (ret != null)
            return ret.intValue();
        else
            return 0;
    }
}
