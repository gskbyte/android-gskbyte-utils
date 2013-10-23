package org.gskbyte.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.gskbyte.R;

import lombok.Getter;
import lombok.Setter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.widget.ArrayAdapter;

public class OpenFileHandler
{

private final Context context;
private final String fileDataType;
private final String [] appNames, appPackages;

@Getter @Setter
protected String dialogTitle;

@Getter @Setter
protected int dialogIconResource;

public OpenFileHandler(Context context, String fileDataType, String [] appNames, String [] appPackages)
{
    this.context = context;
    this.fileDataType = fileDataType;
    if(appNames.length == 0 || appNames.length != appPackages.length)
        throw new IllegalArgumentException("You have to provide as many appPackages as appNames");
    
    this.appNames = appNames;
    this.appPackages = appPackages;
    
    this.dialogTitle = context.getString(R.string.openfilehandler_title_param, fileDataType);
}

public boolean openFile(int location, String filename)
{
    if (isExternalAppAvailable()) {
        return copyFileToExternalStorageAndOpenIt(location, filename);
    } else {
        showDialog();
        return false;
    }
}

/**
 * Returns true if at least one application to open this kind of files is available
 * */
public boolean isExternalAppAvailable()
{
    PackageManager packageManager = context.getPackageManager();
    Intent testIntent = new Intent(Intent.ACTION_VIEW);
    testIntent.setType(fileDataType);
    List<ResolveInfo> list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
    return list.size() > 0;
}

/**
 * Static handlers for common file types
 * */
private final static String [] PDFAppNames = {"Adobe Reader", "PDF Reader"};
private final static String [] PDFAppPackages = {"com.adobe.reader", "com.foobnix.pdf.reader"};

/**
 * Returns an OpenFileHandler for PDF files
 * */
public static final OpenFileHandler PDF(Context context)
{
    OpenFileHandler pdf = new OpenFileHandler(context, "application/pdf", PDFAppNames, PDFAppPackages);
    pdf.setDialogTitle( context.getString(R.string.openfilehandler_title_pdf) );
    return pdf;
}

/**
 * Private internal methods
 * */
private void showDialog()
{
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setCancelable(true);
    
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
    
    builder.setTitle( dialogTitle );
    AlertDialog alert = builder.create();
    alert.show();
}

private boolean copyFileToExternalStorageAndOpenIt(int location, String filepath)
{
    String destinationFilename = "." + IOUtils.LastPathComponent(filepath);
    if(location != IOUtils.LOCATION_EXTERNAL) {
        try {
            IOUtils.DeleteFileRecursive(IOUtils.LOCATION_EXTERNAL, destinationFilename, context);
            IOUtils.CopyFile(location, filepath, IOUtils.LOCATION_EXTERNAL, destinationFilename, context);
        } catch (IOException e) {
            Logger.except(getClass(), e, "Can't copy file to external storage");
            return false;
        }
    }
    
    File file = IOUtils.GetFile(IOUtils.LOCATION_EXTERNAL, destinationFilename, context);
    
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_VIEW);
    Uri uri = Uri.fromFile(file);
    intent.setDataAndType(uri, fileDataType);
    context.startActivity(intent);
    return true;
}

}
