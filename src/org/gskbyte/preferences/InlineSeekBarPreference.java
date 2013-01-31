package org.gskbyte.preferences;

import org.gskbyte.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class InlineSeekBarPreference extends Preference implements
        OnSeekBarChangeListener
{

    private final String TAG = InlineSeekBarPreference.class.getSimpleName();

    private static final String NS_ANDROID = "http://schemas.android.com/apk/res/android";
    private static final String NS_GSKBYTE = "http://www.gskbyte.net/";
    private static final int DEFAULT_VALUE = 50;

    private int maxValue = 100;
    private int minValue = 0;
    private int interval = 1;
    private int currentValue;
    private String unitsLeft = "";
    private String unitsRight = "";
    private SeekBar seekBar;

    private TextView statusText;

    public InlineSeekBarPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initPreference(context, attrs);
    }

    public InlineSeekBarPreference(Context context, AttributeSet attrs,
            int defStyle)
    {
        super(context, attrs, defStyle);
        initPreference(context, attrs);
    }

    private void initPreference(Context context, AttributeSet attrs)
    {
        setValuesFromXml(attrs);
        seekBar = new SeekBar(context, attrs);
        seekBar.setMax(maxValue - minValue);
        seekBar.setOnSeekBarChangeListener(this);
    }

    private void setValuesFromXml(AttributeSet attrs)
    {
        maxValue = attrs.getAttributeIntValue(NS_ANDROID, "max", 100);
        minValue = attrs.getAttributeIntValue(NS_GSKBYTE, "min", 0);

        unitsLeft = getAttributeStringValue(attrs, NS_GSKBYTE, "unitsLeft",  "");
        String units = getAttributeStringValue(attrs, NS_GSKBYTE, "units", "");
        unitsRight = getAttributeStringValue(attrs, NS_GSKBYTE, "unitsRight", units);

        try {
            String newInterval = attrs.getAttributeValue(NS_GSKBYTE,
                    "interval");
            if (newInterval != null)
                interval = Integer.parseInt(newInterval);
        } catch (Exception e) {
            Log.e(TAG, "Invalid interval value", e);
        }

    }

    private String getAttributeStringValue(AttributeSet attrs,
            String namespace, String name, String defaultValue)
    {
        String value = attrs.getAttributeValue(namespace, name);
        if (value == null)
            value = defaultValue;

        return value;
    }

    @Override
    protected View onCreateView(ViewGroup parent)
    {

        RelativeLayout layout = null;

        try {
            LayoutInflater mInflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            layout = (RelativeLayout) mInflater.inflate(
                    R.layout.inline_seekbar_preference, parent, false);
        } catch (Exception e) {
            Log.e(TAG, "Error creating seek bar preference", e);
        }

        return layout;

    }

    @Override
    public void onBindView(View view)
    {
        super.onBindView(view);

        try {
            // move our seekbar to the new view we've been given
            ViewParent oldContainer = seekBar.getParent();
            ViewGroup newContainer = (ViewGroup) view
                    .findViewById(R.id.seekBarPrefBarContainer);

            if (oldContainer != newContainer) {
                // remove the seekbar from the old view
                if (oldContainer != null) {
                    ((ViewGroup) oldContainer).removeView(seekBar);
                }
                // remove the existing seekbar (there may not be one) and add
                // ours
                newContainer.removeAllViews();
                newContainer.addView(seekBar,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error binding view: " + ex.toString());
        }

        updateView(view);
    }

    /**
     * Update a SeekBarPreference view with our current state
     * 
     * @param view
     */
    protected void updateView(View view)
    {

        try {
            RelativeLayout layout = (RelativeLayout) view;

            statusText = (TextView) layout.findViewById(R.id.seekBarPrefValue);
            statusText.setText(String.valueOf(currentValue));
            statusText.setMinimumWidth(30);

            seekBar.setProgress(currentValue - minValue);

            TextView unitsTextRight = (TextView) layout
                    .findViewById(R.id.seekBarPrefUnitsRight);
            unitsTextRight.setText(unitsRight);

            TextView unitsTextLeft = (TextView) layout
                    .findViewById(R.id.seekBarPrefUnitsLeft);
            unitsTextLeft.setText(unitsLeft);

        } catch (Exception e) {
            Log.e(TAG, "Error updating seek bar preference", e);
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromUser)
    {
        int newValue = progress + minValue;

        if (newValue > maxValue)
            newValue = maxValue;
        else if (newValue < minValue)
            newValue = minValue;
        else if (interval != 1 && newValue % interval != 0)
            newValue = Math.round(((float) newValue) / interval) * interval;

        // change rejected, revert to the previous value
        if (!callChangeListener(newValue)) {
            seekBar.setProgress(currentValue - minValue);
            return;
        }

        // change accepted, store it
        currentValue = newValue;
        statusText.setText(String.valueOf(newValue));
        persistInt(newValue);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        notifyChanged();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray ta, int index)
    {
        int defaultValue = ta.getInt(index, DEFAULT_VALUE);
        return defaultValue;
    }
    
    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue)
    {

        if (restoreValue) {
            currentValue = getPersistedInt(currentValue);
        } else {
            int temp = 0;
            try {
                temp = (Integer) defaultValue;
            } catch (Exception ex) {
                Log.e(TAG, "Invalid default value: " + defaultValue.toString());
            }

            persistInt(temp);
            currentValue = temp;
        }

    }
}