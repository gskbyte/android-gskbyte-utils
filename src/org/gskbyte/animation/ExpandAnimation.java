package org.gskbyte.animation;

import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Class to make expand/contract animations.
 * Before this, the view's margin MUST be set to minus the view height.
 * 
 * TODO improve documentation
 * */
public class ExpandAnimation
extends Animation
{

private View animatedView;
private MarginLayoutParams layoutParams;
private int marginStart, marginEnd;
private boolean isVisibleAfter = false;
private boolean wasEndedAlready = false;

/**
 * Initialize the animation
 * @param view The layout we want to animate
 * @param duration The duration of the animation, in ms
 */
public ExpandAnimation(View view, int duration)
{
    setDuration(duration);
    animatedView = view;
    layoutParams = (MarginLayoutParams) view.getLayoutParams();

    // decide to show or hide the view
    isVisibleAfter = (view.getVisibility() == View.VISIBLE);

    marginStart = layoutParams.bottomMargin;
    marginEnd = (marginStart == 0 ? (0- view.getHeight()) : 0);

    view.setVisibility(View.VISIBLE);
    setFillAfter(true);
}

@Override
protected void applyTransformation(float interpolatedTime, Transformation t)
{
    super.applyTransformation(interpolatedTime, t);

    if (interpolatedTime < 1.0f) {
        layoutParams.bottomMargin = marginStart
                + (int) ((marginEnd - marginStart) * interpolatedTime);
        animatedView.requestLayout();
    } else if (!wasEndedAlready) {
        layoutParams.bottomMargin = marginEnd;
        animatedView.requestLayout();

        if (isVisibleAfter) {
            animatedView.setVisibility(View.GONE);
        }
        wasEndedAlready = true;
    }
}
}
