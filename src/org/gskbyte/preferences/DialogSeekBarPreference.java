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
package org.gskbyte.preferences;

import org.gskbyte.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class DialogSeekBarPreference extends DialogPreference implements
        SeekBar.OnSeekBarChangeListener
{
    private static final String NS_ANDROID = "http://schemas.android.com/apk/res/android";

    private SeekBar seekBar;
    private TextView splashText, mValueText;
    private Context context;

    private String dialogMessage, suffix;
    private int minValue, maxValue, defaultValue;
    
    private int currentValue = 0;

    public DialogSeekBarPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        
        suffix = attrs.getAttributeValue(NS_ANDROID, "text");
        defaultValue = attrs.getAttributeIntValue(NS_ANDROID, "defaultValue", 0);
        maxValue = attrs.getAttributeIntValue(NS_ANDROID, "max", 100);
         
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.DialogSeekBarPreference);
        
        minValue = ta.getInteger(0, 0);
        dialogMessage = ta.getString(1);
    }

    @Override
    protected View onCreateDialogView()
    {
        LinearLayout.LayoutParams params;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(6, 6, 6, 6);

        splashText = new TextView(context);
        if (dialogMessage != null)
            splashText.setText(dialogMessage);
        layout.addView(splashText);

        mValueText = new TextView(context);
        mValueText.setGravity(Gravity.CENTER_HORIZONTAL);
        mValueText.setTextSize(32);
        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(mValueText, params);

        seekBar = new SeekBar(context);
        layout.addView(seekBar, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        if (shouldPersist())
            currentValue = getPersistedInt(defaultValue);

        seekBar.setMax(maxValue-minValue);
        seekBar.setProgress(currentValue-minValue);
        
        updateText();
        seekBar.setOnSeekBarChangeListener(this);
        return layout;
    }

    @Override
    protected void onBindDialogView(View v)
    {
        super.onBindDialogView(v);
        seekBar.setMax(maxValue-minValue);
        seekBar.setProgress(currentValue-minValue);
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValueObj)
    {
        super.onSetInitialValue(restore, defaultValueObj);
        
        int defVal = 0; 
        if(defaultValueObj instanceof Integer)
            defVal = ((Integer)defaultValueObj).intValue();
        
        if (restore)
            currentValue = shouldPersist() ? minValue+getPersistedInt(defVal) : 0;
        else
            currentValue = (Integer) defaultValue;
    }

    public void onClick(DialogInterface dialog, int which)
    {
        super.onClick(dialog, which);
        if(which == Dialog.BUTTON_POSITIVE) {
            persistInt(currentValue);
        }
    }
    
    private void updateText()
    {
        String t = String.valueOf(currentValue);
        mValueText.setText(suffix == null ? t : t.concat(suffix));

    }
    
    public void onProgressChanged(SeekBar seek, int value, boolean fromTouch)
    {
        currentValue = value + minValue;
        updateText();
        callChangeListener(Integer.valueOf(currentValue));
    }

    public void onStartTrackingTouch(SeekBar seek)
    {
    }

    public void onStopTrackingTouch(SeekBar seek)
    {
    }
}
