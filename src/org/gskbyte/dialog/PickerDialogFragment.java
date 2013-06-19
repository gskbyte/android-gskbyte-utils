package org.gskbyte.dialog;

import java.lang.ref.WeakReference;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;

import org.gskbyte.R;

public class PickerDialogFragment
extends SherlockDialogFragment
implements DialogInterface.OnMultiChoiceClickListener, OnCheckedChangeListener
{

public interface OnButtonPressedListener
{
    public void onPickerDialogFragmentCompleted(PickerDialogFragment dialog, String [] options, boolean [] selectedOptions);
    public void onPickerDialogFragmentCanceled(PickerDialogFragment dialog);
}

protected static final String KEY_TITLE = "title";
protected static final String KEY_OPTIONS = "options";
protected static final String KEY_SELECTED_OPTIONS = "selected";

protected String title;
protected String [] options;
protected boolean [] selectedOptions;
protected boolean allOptionsSelected;

private PickerTitle dialogTitleView;
private AlertDialog dialog;

private WeakReference<OnButtonPressedListener> buttonListenerRef = new WeakReference<OnButtonPressedListener>(null);

public static PickerDialogFragment newInstance(String title, String [] options, boolean [] selectedOptions)
{
    PickerDialogFragment f = new PickerDialogFragment();
    
    Bundle args = new Bundle();
    args.putString(KEY_TITLE, title);
    args.putStringArray(KEY_OPTIONS, options);
    args.putBooleanArray(KEY_SELECTED_OPTIONS, selectedOptions);
    f.setArguments(args);
    
    return f;
}

public boolean areAllOptionsSelected()
{ return allOptionsSelected; }

public void setOnButtonPressedListener(OnButtonPressedListener listener)
{
    buttonListenerRef = new WeakReference<PickerDialogFragment.OnButtonPressedListener>(listener);
}

public OnButtonPressedListener getOnButtonPressedListener()
{
    return buttonListenerRef.get();
}

@Override
public void onCreate(Bundle savedInstanceState)
{
    super.onCreate(savedInstanceState);
    
    // could this be done on onCreateDialog()?
    Bundle args = getArguments();
    title = args.getString(KEY_TITLE);
    options = args.getStringArray(KEY_OPTIONS);
    selectedOptions = args.getBooleanArray(KEY_SELECTED_OPTIONS);
}

public Dialog onCreateDialog(Bundle savedInstanceState)
{
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            
    alertDialogBuilder.setMultiChoiceItems(options, selectedOptions, this);
    
    dialogTitleView = new PickerTitle(getActivity(), null);
    dialogTitleView.setTitle( title );
    dialogTitleView.allElements.setOnCheckedChangeListener(this);
    
    alertDialogBuilder.setCustomTitle(dialogTitleView);
    
    alertDialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            onOkButtonPressed();
        }
    });
    
    alertDialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            onCancelButtonPressed();
        }
    });
    
    updateCheckStatus();
    dialog = alertDialogBuilder.create();
    return dialog;
}

protected void onOkButtonPressed()
{
    OnButtonPressedListener listener = getOnButtonPressedListener();
    if(listener != null)
        listener.onPickerDialogFragmentCompleted(this, options, selectedOptions);
}

protected void onCancelButtonPressed()
{
    dialog.dismiss();
    OnButtonPressedListener listener = getOnButtonPressedListener();
    if(listener != null)
        listener.onPickerDialogFragmentCanceled(this);
}

protected void updateCheckStatus()
{
    allOptionsSelected = true;
    for(boolean b : selectedOptions) {
        if(!b) {
            allOptionsSelected = false;
            break;
        }
    }
}

public void onClick(DialogInterface dialog, int which, boolean isChecked)
{
    selectedOptions[which] = isChecked;
    updateCheckStatus();
    
    dialogTitleView.allElements.setOnCheckedChangeListener(null);
    dialogTitleView.setChecked(allOptionsSelected);
    dialogTitleView.allElements.setOnCheckedChangeListener(this);
}

@Override
public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
{
    ListView listView = dialog.getListView();
    for(int i=0; i<selectedOptions.length; ++i) {
        listView.setItemChecked(i, isChecked);
        selectedOptions[i] = isChecked;
    }
    updateCheckStatus();
}

private static class PickerTitle
extends RelativeLayout
{
    private TextView title;
    private CheckBox allElements;
    
    public PickerTitle(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_data_picker_title, this);
        title = (TextView) findViewById(R.id.title);
        allElements = (CheckBox) findViewById(R.id.all_elements);
    }
    
    public void setTitle(CharSequence text)
    { title.setText(text); }
    
    public void setChecked(boolean checked)
    { allElements.setChecked(checked); }
    
    public boolean isChecked(boolean checked)
    { return allElements.isChecked(); }
}

}
