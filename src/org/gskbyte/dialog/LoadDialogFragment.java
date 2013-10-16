package org.gskbyte.dialog;

import org.gskbyte.R;

import lombok.Getter;
import lombok.Setter;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadDialogFragment
extends DialogFragment
{

public static final String DEFAULT_TAG = "LOAD_DIALOG";

@Getter
protected int windowGravity = Gravity.CENTER;

@Getter
protected boolean dimsBackground = true;

@Getter
protected float displayedRate;
@Getter
protected boolean showsRate;

protected Drawable customBackground;

@Getter @Setter
protected String customTitle;

protected ViewGroup rootView;
protected TextView title, progressText;
protected ProgressBar horizontalProgressBar;


public static LoadDialogFragment newInstance()
{
    LoadDialogFragment fragment = new LoadDialogFragment();
    
    Bundle args = new Bundle();
    fragment.setArguments(args);
    
    return fragment;
}

@Override
public void onCreate(Bundle savedInstanceState)
{
	super.onCreate(savedInstanceState);
	
	// Disable title
	setStyle(STYLE_NO_TITLE, 0);
}

@Override
public Dialog onCreateDialog(Bundle savedInstanceState)
{
    Dialog d = super.onCreateDialog(savedInstanceState);

    // Disable user cancellation
    d.setCanceledOnTouchOutside(false);
    setCancelable(false);
    
    updateDialog(d);
    
    return d;
}

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
{
    View v = inflater.inflate(R.layout.dialog_load, container, false);
    
    rootView = (ViewGroup) v.findViewById(R.id.root);
    
    title = (TextView) v.findViewById(R.id.loading_title);
    progressText = (TextView) v.findViewById(R.id.progressText);
    horizontalProgressBar = (ProgressBar) v.findViewById(R.id.determinateProgressBar);
    
    updateView();
    
    return v;
}

public void setGravity(int g)
{
    if(g != windowGravity) {
        windowGravity = g;
        updateView();
    }
}

public void setDimsBackground(boolean d)
{
    if(d != dimsBackground) {
        dimsBackground = d;
        updateView();
    }
}

public void setBackground(Drawable d)
{
    if(d != customBackground) {
        customBackground = d;
        updateView();
    }
}

public Drawable getBackground()
{
    return customBackground;
}

public void setProgressRate(float rate)
{
    displayedRate = rate;
    updateView();
}

public void setShowsRate(boolean b)
{
    if(showsRate != b) {
        showsRate = b;
        updateView();
    }
}

protected void updateDialog(Dialog d)
{
    if(d == null)
        return;
    
    Window w = d.getWindow();
    
    // TODO implement the positive case, when you have time!!!!
    if(!dimsBackground)
        w.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    
    if(customBackground != null)
        d.getWindow().setBackgroundDrawable(customBackground);
    
    WindowManager.LayoutParams wlp = w.getAttributes();

    wlp.gravity = windowGravity;
    wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
    
    w.setAttributes(wlp);
}

protected void updateView()
{
    if(progressText == null) // getView() is still null in onCreateView
        return;
    
    if(customTitle != null) {
        title.setText(customTitle);
    }
    
    updateDialog( getDialog() );
    
    progressText.setVisibility( showsRate ? View.VISIBLE : View.GONE);
    horizontalProgressBar.setVisibility( showsRate ? View.VISIBLE : View.GONE );

    if(showsRate) {
        float percentRate = displayedRate * 100;
        horizontalProgressBar.setProgress( Math.round( percentRate ) );
        progressText.setText( String.format("%.2f %%", percentRate) );
    }
}
	
}
