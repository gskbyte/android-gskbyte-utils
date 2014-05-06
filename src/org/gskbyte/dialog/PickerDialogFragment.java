package org.gskbyte.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.gskbyte.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PickerDialogFragment
extends DialogFragment
implements DialogInterface.OnMultiChoiceClickListener, OnCheckedChangeListener
{

public interface ActionListener
{
    public void onPickerDialogFragmentCompleted(PickerDialogFragment dialog, List<String> options, Set<Integer> selectedIndices);
    public void onPickerDialogFragmentCanceled(PickerDialogFragment dialog);
}

protected static final String KEY_TITLE = "title";
protected static final String KEY_TITLE_RES = "titleRes";
protected static final String KEY_ICON_RES = "iconRes";
protected static final String KEY_OPTIONS = "options";
protected static final String KEY_SELECTED_INDICES = "selected";
protected static final String KEY_ALL_ELEMENTS_TITLE = "allElements";

protected String title, allElements;
protected int iconRes;
protected ArrayList<String> options;
protected Set<Integer> selectedIndices;
protected boolean allOptionsSelected;

protected PickerTitle dialogTitleView;
protected AlertDialog dialog;

private ActionListener actionListener = null;

public static PickerDialogFragment newInstance(String title, int iconRes, List<String> options, Set<Integer> selectedIndices)
{
    PickerDialogFragment f = new PickerDialogFragment();
    
    Bundle args = new Bundle();
    args.putString(KEY_TITLE, title);
    args.putInt(KEY_ICON_RES, iconRes);
    args.putStringArrayList(KEY_OPTIONS, new ArrayList<String>(options));
    args.putIntegerArrayList(KEY_SELECTED_INDICES, new ArrayList<Integer>(selectedIndices));
    f.setArguments(args);
    
    return f;
}

public void setAllElementsTitle(String s)
{ this.allElements = s; }

public boolean areAllOptionsSelected()
{ return allOptionsSelected; }

public void setActionListener(ActionListener listener)
{
    actionListener = listener;
}

@Override
public void onCreate(Bundle savedInstanceState)
{
    super.onCreate(savedInstanceState);
    
    // could this be done on onCreateDialog()?
    Bundle args = getArguments();
    iconRes = args.getInt(KEY_ICON_RES, 0);
    
    int titleRes = args.getInt(KEY_TITLE_RES);
    if(titleRes != 0) {
        title = getString(titleRes);
    } else {
        title = args.getString(KEY_TITLE);
    }
    
    if(allElements == null) {
        allElements = getString(R.string.all_elements);
    }
    
    options = args.getStringArrayList(KEY_OPTIONS);
    selectedIndices = new TreeSet<Integer>( args.getIntegerArrayList(KEY_SELECTED_INDICES) );
}

public Dialog onCreateDialog(Bundle savedInstanceState)
{
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
    
    boolean [] selectedOptions = new boolean[ options.size() ];
    for(int i : selectedIndices) {
        selectedOptions[i] = true;
    }
    alertDialogBuilder.setMultiChoiceItems(options.toArray(new String[options.size()]), selectedOptions, this);
    
    dialogTitleView = new PickerTitle(getActivity(), null);
    dialogTitleView.setIconResource(iconRes);
    dialogTitleView.setTitle( title );
    dialogTitleView.setAllElementsTitle( allElements );
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
    
    dialog = alertDialogBuilder.create();
    updateCheckStatus();
    return dialog;
}

protected void onOkButtonPressed()
{
    if(actionListener != null)
        actionListener.onPickerDialogFragmentCompleted(this, options, selectedIndices);
}

protected void onCancelButtonPressed()
{
    dialog.dismiss();
    if(actionListener != null)
        actionListener.onPickerDialogFragmentCanceled(this);
}

protected void updateCheckStatus()
{    
    this.allOptionsSelected = options.size()==selectedIndices.size();
    
    dialogTitleView.allElements.setOnCheckedChangeListener(null);
    dialogTitleView.setChecked(this.allOptionsSelected);
    ListView listView = dialog.getListView();
    for(int i=0; i<options.size(); ++i) {
        listView.setItemChecked(i, selectedIndices.contains(i));
    }
    dialogTitleView.allElements.setOnCheckedChangeListener(this);
}

public void onClick(DialogInterface dialog, int which, boolean isChecked)
{
    if(isChecked) {
        selectedIndices.add(which);
    } else {
        selectedIndices.remove(which);
    }
    updateCheckStatus();
}

@Override
public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
{
    if(isChecked) {
        for(int i=0; i<options.size(); ++i) {
            selectedIndices.add(i);
        }
    } else {
        selectedIndices.clear();
    }
    
    updateCheckStatus();
}

@SuppressWarnings("unused")
private static class PickerTitle
extends RelativeLayout
{
    ImageView icon;
    TextView title;
    CheckBox allElements;
    
    public PickerTitle(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_data_picker_title, this);
        icon = (ImageView) findViewById(R.id.icon);
        title = (TextView) findViewById(R.id.title);
        allElements = (CheckBox) findViewById(R.id.all_elements);
        
        setIconResource(0);
    }
    
    public void setTitle(CharSequence text)
    { title.setText(text); }
    
    public void setAllElementsTitle(CharSequence text)
    { allElements.setText(text); }
    
    public void setChecked(boolean checked)
    { allElements.setChecked(checked); }
    
    public boolean isChecked(boolean checked)
    { return allElements.isChecked(); }
    
    public void setIconResource(int resId)
    {
        if(resId != 0) {
            icon.setVisibility(VISIBLE); icon.setImageResource(resId);
        } else {
            icon.setVisibility(GONE); 
        }
    }
    
}

}
