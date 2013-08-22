package org.gskbyte.drawable;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

/**
 * Drawable layer to be used by AutoBackgroundButton and
 * AutoBackgroundImageButton
 * */
public class AutoBackgroundButtonDrawable
extends LayerDrawable
{

protected static final int  DISABLED_ALPHA        = 100,
                            FULL_ALPHA            = 255;
public    static final int  DEFAULT_COLOR_FILTER = Color.GRAY;

protected final ColorFilter pressedFilter;

public AutoBackgroundButtonDrawable(Drawable d, int colorFilter)
{
    super(new Drawable[] { d });
    pressedFilter = new LightingColorFilter(colorFilter, 1);
}

/*
private boolean gravitySwitched = false;
@Override
public void draw(Canvas canvas)
{
    Drawable d = getDrawable(0);
    if(!gravitySwitched && (d instanceof BitmapDrawable)) {
        BitmapDrawable bd = (BitmapDrawable) d;
        int gravity = bd.getGravity();
        int oppositeGravity = (gravity != Gravity.NO_GRAVITY) ? Gravity.NO_GRAVITY : Gravity.CENTER;
        bd.setGravity(oppositeGravity);
        bd.setGravity(gravity);
        gravitySwitched = true;
    }
    
    super.draw(canvas);
}*/

@Override
protected boolean onStateChange(int[] states)
{
    boolean enabled = false;
    boolean pressed = false;

    for (int state : states) {
        if (state == android.R.attr.state_enabled)
            enabled = true;
        else if (state == android.R.attr.state_pressed)
            pressed = true;
    }

    mutate();
    if (enabled && pressed) {
        setColorFilter(pressedFilter);
    } else if (!enabled) {
        setColorFilter(null);
        setAlpha(DISABLED_ALPHA);
    } else {
        setColorFilter(null);
        setAlpha(FULL_ALPHA);
    }

    invalidateSelf();

    return super.onStateChange(states);
}

@Override
public boolean isStateful()
{ return true; }

}
