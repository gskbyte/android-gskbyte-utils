package org.gskbyte.dialog;

import org.gskbyte.R;
import org.gskbyte.download.DiskDownload;
import org.gskbyte.download.Download;
import org.gskbyte.download.DownloadManager;
import org.gskbyte.download.MemoryDownload;
import org.gskbyte.util.Logger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

public class DownloadDialogFragment
extends LoadDialogFragment
implements Download.Listener
{

private static final String KEY_DOWNLOAD_REQUEST = "downloadRequest";

private Download download;
private Listener listener = null;

public static interface Listener
{
    public void downloadDialogDidDismiss(DownloadDialogFragment dialog, boolean downloadSucceeded);
}

public static DownloadDialogFragment NewInstanceForDownload(Download.Request d)
{
    DownloadDialogFragment fragment = new DownloadDialogFragment();
    
    Bundle args = new Bundle();
    args.putSerializable(KEY_DOWNLOAD_REQUEST, d);
    fragment.setArguments(args);
    
    return fragment;
}
    
@Override
public void onCreate(Bundle savedInstanceState)
{
    super.onCreate(savedInstanceState);    
    Download.Request request = (Download.Request) getArguments().getSerializable(KEY_DOWNLOAD_REQUEST);
    
    if(request != null) {
        if(request instanceof MemoryDownload.Request) {
            download = new MemoryDownload((MemoryDownload.Request)request);
        } else if(request instanceof DiskDownload.Request) {
            download = new DiskDownload((DiskDownload.Request)request);
        } else {
            Logger.error(getClass(), "Download Request class not recognized: " + request.getClass());
        }
    }
    if(download != null) {
        download.addListener(this);
        download.resume();
    } else {
        Logger.error(getClass(), "No download to start with, dismissing");
        onDownloadFailed(null);
    }
}

@Override
public void onDestroy()
{
    super.onDestroy();
    download = null;
    listener = null;
}

public Download getDownload()
{ return download; }

public void setDownload(Download d)
{ download = d; }

public void setListener(Listener listener)
{ this.listener = listener; }

public void show(FragmentManager manager, String tag)
{
    throw new IllegalStateException("Call showAndBeginDownload() to show this");
}

public int show(FragmentTransaction ft, String tag)
{
    throw new IllegalStateException("Call showAndBeginDownload() to show this");
}

public void showAndBeginDownload(FragmentManager fm, String tag)
{
    super.show(fm, getClass().toString());
}

public void showAndBeginDownloadCheckingWiFi(Context context, final FragmentManager fm, final String tag)
{
    if( DownloadManager.IsConnectionActive(context) ) {
        if( DownloadManager.IsConnectionWifi(context) ) {
            showAndBeginDownload(fm, tag);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false);
            builder.setTitle(R.string.no_wifi_detected);
            builder.setMessage(R.string.no_wifi_detected_description);
            builder.setNegativeButton(R.string.no, null);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    showAndBeginDownload(fm, tag);
                }
            });
            builder.create().show();
        }
    } else {
        Toast.makeText(context, R.string.error_connection_not_available, Toast.LENGTH_SHORT).show();
    }
}

@Override
public void onDownloadStarted(Download download)
{
    
}

@Override
public void onDownloadFailed(Download download)
{
    dismiss();
    if(listener != null)
        listener.downloadDialogDidDismiss(this, false);
}

@Override
public void onDownloadRate(Download download, float rate)
{
    setProgressRate(rate);
}

@Override
public void onDownloadFinished(Download download)
{
    dismiss();
    if(listener != null)
        listener.downloadDialogDidDismiss(this, true);
}

}
