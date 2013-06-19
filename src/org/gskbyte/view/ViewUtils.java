package org.gskbyte.view;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.animation.AlphaAnimation;

public class ViewUtils
{


@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public static void SetBackgroundDrawable(View view, Drawable drawable)
{
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        view.setBackground(drawable);
    } else {
        view.setBackgroundDrawable(drawable);
    }
}

@SuppressLint("NewApi")
public static void SetAlpha(View view, float alpha)
{
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
        view.setAlpha(alpha);
    } else {
        AlphaAnimation a = new AlphaAnimation(alpha, alpha);
        a.setDuration(0);
        a.setFillAfter(true);
        view.startAnimation(a);
    }
}

}
