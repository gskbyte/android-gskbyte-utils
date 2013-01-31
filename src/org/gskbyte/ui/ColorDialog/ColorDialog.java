package org.gskbyte.ui.ColorDialog;

import lombok.Getter;
import lombok.Setter;

import org.gskbyte.R;
import org.gskbyte.ui.BitmapColorizer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public final class ColorDialog extends AlertDialog implements
        OnSeekBarChangeListener, OnClickListener
{
    private final SeekBar redSeek,  greenSeek, blueSeek, alphaSeek;
    private final TextView redValue, greenValue, blueValue, alphaValue;
    
    private final ImageView colorPreview, imagePreview;
    
    private int currentColor;
    private final BitmapColorizer colorPreviewColorizer, imagePreviewColorizer;
    
    public static interface ColorDialogListener
    {
        public void onColorChosen(ColorDialog dialog, int color);
    }
    @Getter @Setter
    private ColorDialogListener listener;

    public ColorDialog(Context context, boolean useAlpha, int color)
    {
        this(context, useAlpha, color, null);
    }

    public ColorDialog(Context context, boolean useAlpha, int color, Bitmap imageBitmap)
    {
        super(context);
        this.currentColor = color;

        final Resources res = context.getResources();
        
        setButton(BUTTON_POSITIVE, res.getText(android.R.string.ok), this);
        setButton(BUTTON_NEGATIVE, res.getText(android.R.string.cancel), this);
        
        final View root = LayoutInflater.from(context).inflate(R.layout.dialog_color_picker, null);
        setView(root);

        this.colorPreview = (ImageView) root.findViewById(R.id.colorPreview);
        Bitmap colorBitmap = BitmapFactory.decodeResource(res, R.drawable.color_preview_fg);
        this.colorPreviewColorizer = new BitmapColorizer(context, colorBitmap, Bitmap.Config.ARGB_8888);
        
        imagePreview = (ImageView) root.findViewById(R.id.image);
        if(imageBitmap == null) {
            imagePreview.setVisibility(View.GONE);
            this.imagePreviewColorizer = null;
        } else {
            imagePreview.setImageBitmap(imageBitmap);
            this.imagePreviewColorizer = new BitmapColorizer(context, imageBitmap, Bitmap.Config.ARGB_8888);
        }
        
        redSeek = (SeekBar) root.findViewById(R.id.red);
        redValue = (TextView) root.findViewById(R.id.redValue);
        
        greenSeek = (SeekBar) root.findViewById(R.id.green);
        greenValue = (TextView) root.findViewById(R.id.greenValue);
        
        blueSeek = (SeekBar) root.findViewById(R.id.blue);
        blueValue = (TextView) root.findViewById(R.id.blueValue);
        
        alphaSeek = (SeekBar) root.findViewById(R.id.alpha);
        alphaValue = (TextView) root.findViewById(R.id.alphaValue);

        currentColor = color;
        setupSeekBar(redSeek,  Color.red(color));
        setupSeekBar(greenSeek, Color.green(color));
        setupSeekBar(blueSeek, Color.blue(color));

        if (useAlpha) {
            setupSeekBar(alphaSeek, Color.alpha(color));
        } else {
            alphaSeek.setVisibility(View.GONE);
            alphaSeek.setProgress(255);
            alphaValue.setVisibility(View.GONE);
        }
        
        updatePreview(color);
    }

    @Override
    public void onClick(final DialogInterface dialog, final int which)
    {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            listener.onColorChosen(this, currentColor);
        }
        dismiss();
    }

    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress,
            final boolean fromUser)
    {
        update();
    }

    @Override
    public void onStartTrackingTouch(final SeekBar seekBar)
    {
    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar)
    {
    }

    private void setupSeekBar(SeekBar seekBar, int value)
    {
        seekBar.setProgress(value);
        seekBar.setOnSeekBarChangeListener(this);
    }

    private void update()
    {
        int a = alphaSeek.getProgress();
        int r = redSeek.getProgress();
        int g = greenSeek.getProgress();
        int b = blueSeek.getProgress();

        alphaValue.setText(String.valueOf(a));
        redValue.setText(String.valueOf(r));
        greenValue.setText(String.valueOf(g));
        blueValue.setText(String.valueOf(b));
        
        currentColor = Color.argb(a, r, g, b);
        updatePreview(currentColor);
    }

    private void updatePreview(int color)
    {
        if(imagePreviewColorizer != null) {
            imagePreview.setImageBitmap(imagePreviewColorizer.colorize(color));
        }
        colorPreview.setImageBitmap(colorPreviewColorizer.colorize(color));
    }
}
