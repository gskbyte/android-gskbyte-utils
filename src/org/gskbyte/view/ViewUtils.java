package org.gskbyte.view;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Some utility methods to cover Android API flaws or deprecations
 * */
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

public static void SetTextAndVisibility(TextView view, CharSequence text)
{ SetTextAndVisibility(view, text, View.GONE); }

public static void SetTextAndVisibility(TextView view, CharSequence text, int visibilityIfEmpty)
{
    if(text == null || text.length()==0) {
        view.setVisibility(visibilityIfEmpty);
    } else {
        view.setVisibility(View.VISIBLE);
        view.setText(text);
    }
}

public static void SetBitmapAndVisibility(ImageView view, Bitmap bitmap)
{ SetBitmapAndVisibility(view, bitmap, View.GONE); }


public static void SetBitmapAndVisibility(ImageView view, Bitmap bitmap, int visibilityIfEmpty)
{
    if(bitmap == null ) {
        view.setVisibility(visibilityIfEmpty);
    } else {
        view.setVisibility(View.VISIBLE);
        view.setImageBitmap(bitmap);
    }
}


}
