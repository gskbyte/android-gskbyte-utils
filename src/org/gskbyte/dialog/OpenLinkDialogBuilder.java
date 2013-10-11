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

public AlertDialog create(String url)
{
    internalBuilder.setCancelable(true);
    internalBuilder.setTitle(R.string.openlinkdialog_title);
    if(displayLink) {
        internalBuilder.setMessage(url);
    }
    
    internalBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            FrequentIntents.OpenURL(context, "http://www.medac.de/medac_international/relaunch/index.php");
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
