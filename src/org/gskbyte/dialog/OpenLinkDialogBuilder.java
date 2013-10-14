package org.gskbyte.dialog;

import org.gskbyte.R;
import org.gskbyte.util.FrequentIntents;

import lombok.Getter;
import lombok.Setter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class OpenLinkDialogBuilder
{

protected AlertDialog.Builder internalBuilder;

// must be saved for API < 11
private final Context context;

@Getter @Setter
protected boolean displayLink = true;

@Getter @Setter
protected String description = "";

public OpenLinkDialogBuilder(Context context)
{
    this.context = context;
    internalBuilder = new AlertDialog.Builder(context);
}

public void setTitle(String s)
{
    internalBuilder.setTitle(s);
}

public void setTitle(int stringRes)
{
    internalBuilder.setTitle(stringRes);
}

public AlertDialog create(final String url)
{
    internalBuilder.setCancelable(true);
    internalBuilder.setTitle(R.string.openlinkdialog_title);
    
    String message = description;
    if(displayLink) {
        if(message.length() > 0) {
            message += "\n\n(" + url + ")";
        } else {
            message += url;
        }
        internalBuilder.setMessage(message);
    }
    
    internalBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            FrequentIntents.OpenURL(context, url);
        }
    });
    internalBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            dialog.dismiss();
        }
    });
    return internalBuilder.create();
}

}
