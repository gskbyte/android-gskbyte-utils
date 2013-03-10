/*******************************************************************************
 * Copyright (c) 2013 Jose Alcalá Correa.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * Contributors:
 *     Jose Alcalá Correa - initial API and implementation
 ******************************************************************************/
package org.gskbyte.ui.ColorDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ColorPreference extends DialogPreference implements
        OnSeekBarChangeListener
{
    private SeekBar mRedSeek;
    private SeekBar mGreenSeek;
    private SeekBar mBlueSeek;
    private int mColor;
    private Drawable mPreviewDrawable;
    private ImageView mImage;

    public ColorPreference(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public void onClick(final DialogInterface dialog, final int which)
    {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            final SharedPreferences p = getSharedPreferences();
            final SharedPreferences.Editor e = p.edit();
            e.putInt(getKey(), mColor);
            e.commit();
        }
    }

    @Override
    protected View onCreateDialogView()
    {
        /*
        final Context context = getContext();
        final Resources res = context.getResources();
        final int initialColor = getSharedPreferences().getInt(getKey(),
                Defaults.getColor(getKey()));

        final View root = LayoutInflater.from(context).inflate(
                R.layout.full_color_picker, null);

        final View preview = root.findViewById(R.id.preview);
        mPreviewDrawable = new GradientDrawable();
        // 2 pix more than color_picker_frame's radius
        // mPreviewDrawable.setCornerRadius(7);
        Drawable[] layers;

        layers = new Drawable[] { mPreviewDrawable,
                res.getDrawable(R.drawable.color_picker_frame), };
        preview.setBackgroundDrawable(new LayerDrawable(layers));

        mRedSeek = (SeekBar) root.findViewById(R.id.red);
        mGreenSeek = (SeekBar) root.findViewById(R.id.green);
        mBlueSeek = (SeekBar) root.findViewById(R.id.blue);
        final SeekBar alphaSeek = (SeekBar) root.findViewById(R.id.alpha);
        alphaSeek.setVisibility(View.GONE);

        mColor = initialColor;
        setupSeekBar(mRedSeek, R.string.red, Color.red(initialColor), res);
        setupSeekBar(mGreenSeek, R.string.green, Color.green(initialColor), res);
        setupSeekBar(mBlueSeek, R.string.blue, Color.blue(initialColor), res);

        mImage = (ImageView) root.findViewById(R.id.image);
        mImage.setVisibility(View.GONE);
        TransparentBitmapManager.instance().setFuntonImage(mImage, mColor, true);

        updatePreview(initialColor);

        return root;
        */
        return null;
    }

    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress,
            final boolean fromUser)
    {
        //TransparentBitmapManager.instance().setFuntonImage(mImage, mColor, true);
        //update();
    }

    @Override
    public void onStartTrackingTouch(final SeekBar seekBar)
    {
    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar)
    {
    }

    private void setupSeekBar(final SeekBar seekBar, final int id,
            final int value, final Resources res)
    {
        /*seekBar.setProgressDrawable(new TextSeekBarDrawable(res, id,
                value < seekBar.getMax() / 2));*/
        seekBar.setProgress(value);
        seekBar.setOnSeekBarChangeListener(this);
    }

    private void update()
    {
        mColor = Color.rgb(mRedSeek.getProgress(), mGreenSeek.getProgress(),
                mBlueSeek.getProgress());
        updatePreview(mColor);
    }

    private void updatePreview(final int color)
    {
        ((GradientDrawable) mPreviewDrawable).setColor(color);
        mPreviewDrawable.invalidateSelf();
    }

}
