package org.gskbyte.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.gskbyte.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.widget.ArrayAdapter;

public class OpenFileHandler
{

private final Context context;
private final String mimeContentType;
private final String [] appNames, appPackages;

protected String dialogTitle;
protected int dialogIconResource;

public OpenFileHandler(Context context, String mimeContentType, String [] appNames, String [] appPackages)
{
    this.context = context;
    this.mimeContentType = mimeContentType;
    if(appNames.length != appPackages.length)
        throw new IllegalArgumentException("You have to provide as many appPackages as appNames");
    
    this.appNames = appNames;
    this.appPackages = appPackages;
    
    this.dialogTitle = context.getString(R.string.openfilehandler_title_param, mimeContentType);
}

public String getDialogTitle()
{ return dialogTitle; }

public void setDialogTitle(String dialogTitle)
{ this.dialogTitle = dialogTitle; }

public int getDialogIconResource()
{ return dialogIconResource; }

public void setDialogIconResource(int dialogIconResource)
{ this.dialogIconResource = dialogIconResource; }

public boolean openFile(int location, String filename)
    throws IOException
{
    if(isExternalAppAvailable()) {
        return copyFileToExternalStorageAndOpenIt(location, filename);
    } else {
        if(canSuggestAppForMimeContentType()) {
            showSuggestionDialog();
        } else {
            showUnknownMimeContentTypeDialog();
        }
        return false;
    }
}

/**
 * */
public boolean canSuggestAppForMimeContentType()
{ return appNames.length>0; }

/**
 * Returns true if at least one application to open this kind of files is available
 * */
public boolean isExternalAppAvailable()
{
    PackageManager packageManager = context.getPackageManager();
    Intent testIntent = new Intent(Intent.ACTION_VIEW);
    testIntent.setType(mimeContentType);
    List<ResolveInfo> list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
    return list.size() > 0;
}

/**
 * Private internal methods
 * */
private void showSuggestionDialog()
{
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setCancelable(true);
    builder.setTitle( dialogTitle );
    
    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice, appNames);
    builder.setAdapter(adapter, new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String pdfAppPackageName = appPackages[which];
            intent.setData(Uri.parse("market://details?id=" + pdfAppPackageName));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
        }
    });
    
    
    AlertDialog alert = builder.create();
    alert.show();
}

private void showUnknownMimeContentTypeDialog()
{
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setCancelable(true);
    builder.setTitle( dialogTitle );
    builder.setMessage(R.string.openfilehandler_noapp_message);
    builder.setNegativeButton(R.string.no, new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            dialog.dismiss();
        }
    });

    builder.setPositiveButton(R.string.openfilehandler_noapp_search, new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            
            String [] mimeParts = mimeContentType.split("/");
            String fileExtension = mimeParts[mimeParts.length-1];
            
            intent.setData(Uri.parse("market://search?q="+fileExtension+"&c=apps"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
        }
    });
    
    AlertDialog alert = builder.create();
    alert.show();
}

private boolean copyFileToExternalStorageAndOpenIt(int location, String filepath)
        throws IOException
{
    String destinationFilename = "." + IOUtils.LastPathComponent(filepath);
    if(location != IOUtils.LOCATION_EXTERNAL) {
        IOUtils.DeleteFileRecursive(IOUtils.LOCATION_EXTERNAL, destinationFilename, context);
        IOUtils.CopyFile(location, filepath, IOUtils.LOCATION_EXTERNAL, destinationFilename, context);
    }
    
    File file = IOUtils.GetFile(IOUtils.LOCATION_EXTERNAL, destinationFilename, context);
    
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_VIEW);
    Uri uri = Uri.fromFile(file);
    intent.setDataAndType(uri, mimeContentType);
    context.startActivity(intent);
    return true;
}

}
