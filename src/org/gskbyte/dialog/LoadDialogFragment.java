package org.gskbyte.dialog;

import java.lang.ref.WeakReference;

import org.gskbyte.R;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadDialogFragment
extends DialogFragment
{

public static final String DEFAULT_TAG = "LOAD_DIALOG";
public static final String KEY_ARG_SIZE_MODE = "SIZE_MODE";

protected int windowGravity = Gravity.CENTER;

protected boolean dimsBackground = true;

protected float displayedRate;
protected boolean showsRate;

protected Drawable customBackground;

protected String customTitle, subtitleText;
protected CancelMode cancelMode = CancelMode.Invisible;
protected SizeMode sizeMode = SizeMode.Normal;
protected WeakReference<LoadDialogCancelListener> cancelListener = null;
protected boolean dismissOnCancel = true;

protected ViewGroup rootView;
protected TextView title, subtitle, progressText;
protected ProgressBar horizontalProgressBar;
protected Button cancel;


public static enum CancelMode
{
    Invisible,  // button not visible, can't cancel pressing back
    Disabled,   // button visible but disabled, can't cancel pressing back
    Enabled     // button visible and enabled, can cancel pressing back
}

public static enum SizeMode
{
    Normal,
    Compact
}

public static interface LoadDialogCancelListener
{
    public void onLoadDialogCanceled(LoadDialogFragment dialog);
}

@Deprecated
public static LoadDialogFragment newInstance()
{
    return newInstance(SizeMode.Normal);
}

public static LoadDialogFragment newInstance(SizeMode sizeMode)
{
    LoadDialogFragment fragment = new LoadDialogFragment();
    
    Bundle args = new Bundle();
    args.putSerializable(KEY_ARG_SIZE_MODE, sizeMode);
    fragment.setArguments(args);
    
    return fragment;
}

public void dismissAfter(long millis, Runnable runAfterDismiss)
{
    final Runnable finalRunnable = runAfterDismiss;
    getView().postDelayed(new Runnable() {
        @Override
        public void run()
        {
            dismiss();
            if(finalRunnable != null) {
                finalRunnable.run();
            }
        }
    }, millis);
}

@Override
public void onCreate(Bundle savedInstanceState)
{
	super.onCreate(savedInstanceState);
	
	// Disable title
	setStyle(STYLE_NO_TITLE, 0);
	
	Bundle args = getArguments();
	if(args != null) {
	    SizeMode szm = (SizeMode) args.getSerializable(KEY_ARG_SIZE_MODE);
	    if(szm != null) {
	        this.sizeMode = szm;
	    }
	}
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
    final int layoutRes = (sizeMode==SizeMode.Compact) ? R.layout.dialog_load_compact : R.layout.dialog_load;
    
    View v = inflater.inflate(layoutRes, container, false);
    
    rootView = (ViewGroup) v.findViewById(R.id.root);

    title = (TextView) v.findViewById(R.id.loading_title);
    subtitle = (TextView) v.findViewById(R.id.loading_subtitle);
    progressText = (TextView) v.findViewById(R.id.progressText);
    horizontalProgressBar = (ProgressBar) v.findViewById(R.id.determinateProgressBar);
    cancel = (Button) v.findViewById(R.id.cancel);
    cancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v)
        { onCancel( getDialog() );}
    });
    
    updateView();
    
    return v;
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
    
    title.setText( getTitle() );
    
    if(subtitleText == null || subtitleText.isEmpty()) {
        subtitle.setVisibility(View.GONE);
    } else {
        subtitle.setText(subtitleText);
        subtitle.setVisibility(View.VISIBLE);
    }
    
    updateDialog( getDialog() );
    
    progressText.setVisibility( showsRate ? View.VISIBLE : View.GONE);
    horizontalProgressBar.setVisibility( showsRate ? View.VISIBLE : View.GONE );
    
    cancel.setEnabled( (cancelMode==CancelMode.Enabled) );
    cancel.setVisibility( (cancelMode!=CancelMode.Invisible) ? View.VISIBLE : View.GONE );
    
    if(showsRate) {
        float percentRate = displayedRate * 100;
        horizontalProgressBar.setProgress( Math.round( percentRate ) );
        progressText.setText( String.format("%.2f %%", percentRate) );
    }
}

@Override
public void onCancel(DialogInterface dialog)
{
    super.onCancel(dialog);
    if(cancelListener != null && cancelListener.get() != null) {
        cancelListener.get().onLoadDialogCanceled(this);
    }
    
    if(dismissOnCancel) {
        dismiss();
    }
}

public CancelMode getCancelMode()
{ return cancelMode; }

public void setCancelMode(CancelMode m)
{
    this.cancelMode = m;
    setCancelable((m == CancelMode.Enabled));
    updateView();
}

public SizeMode getSizeMode()
{ return this.sizeMode; }

public void setCancelListener(LoadDialogCancelListener l)
{ this.cancelListener = new WeakReference<LoadDialogCancelListener>(l); }

// dismiss the dialog automatically when canceled
public void setDismissOnCancel(boolean d)
{ dismissOnCancel = d; }

public int getWindowGravity()
{ return windowGravity; }

public void setGravity(int g)
{
    if(g != windowGravity) {
        windowGravity = g;
        updateView();
    }
}

public boolean dimsBackground()
{ return dimsBackground; }

public void setDimsBackground(boolean d)
{
    if(d != dimsBackground) {
        dimsBackground = d;
        updateView();
    }
}


public float getProgressRate()
{ return displayedRate; }

public void setProgressRate(float rate)
{
    displayedRate = rate;
    updateView();
}


public boolean showsRate()
{ return showsRate; }

public void setShowsRate(boolean b)
{
    if(showsRate != b) {
        showsRate = b;
        updateView();
    }
}


public Drawable getBackground()
{ return customBackground; }

public void setBackground(Drawable d)
{
    if(d != customBackground) {
        customBackground = d;
        updateView();
    }
}

public String getDefaultTitle()
{ return getString(R.string.load_dialog_loading); }

public String getTitle()
{
    if(customTitle == null)
        return getDefaultTitle();
    else
        return customTitle;
}

public void setTitle(String customTitle)
{
    this.customTitle = customTitle;
    updateView();
}

public String getSubtitle()
{ return customTitle; }

public void setSubtitle(String subtitle)
{
    this.subtitleText = subtitle;
    updateView();
}
	
}
