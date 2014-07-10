package org.gskbyte.remote;

import java.io.IOException;
import java.io.InputStream;

import org.gskbyte.util.IOUtils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

public class AsyncURLRequest
extends URLRequest
{

protected InputStream inputStream;

public interface CompletionListener
{
    public void onCompletion(AsyncURLRequest request, boolean success, IOException exception);
}

public interface RateListener
{
    public void onRate(AsyncURLRequest request, float rate);
}

public AsyncURLRequest(String urlBase)
{
    super(urlBase);
}

public AsyncURLRequest(String urlBase, String requestMethod)
{
    super(urlBase, requestMethod);
}

public InputStream getInputStream()
{ return this.inputStream; }

public String getString()
        throws IOException
{ return IOUtils.InputStreamToString(inputStream); }

public String getStringSafe()
{
    try {
        return IOUtils.InputStreamToString(inputStream);
    } catch (IOException e) {
        return null;
    }
}

@SuppressLint("NewApi")
public boolean executeAsync(CompletionListener listener, RateListener rateListener)
{
    if( this.isRunning() ) {
        return false;
    }
    
    DownloadTask task = new DownloadTask(listener, rateListener);
    
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    } else {
        task.execute();
    }
    
    return true;
}

private class DownloadTask
extends AsyncTask<Void, Float, Integer>
implements ProgressListener
{

protected final CompletionListener completionListener;
protected final RateListener rateListener;

public DownloadTask(CompletionListener listener, RateListener rateListener)
{
    this.completionListener = listener;
    this.rateListener = rateListener;
}

@Override
protected Integer doInBackground(Void... params)
{
    try {
        AsyncURLRequest.this.inputStream = AsyncURLRequest.this.execute(this);
    } catch (IOException e) {
        completionListener.onCompletion(AsyncURLRequest.this, false, e);
    }
    return AsyncURLRequest.this.getTotalBytes();
}

@Override
public void onProgressChanged(int readBytes, int totalBytes)
{
    if(rateListener != null)
        publishProgress(readBytes / (float)totalBytes);
}

@Override
protected void onProgressUpdate(Float ... values)
{
    float rate = values[0];
    rateListener.onRate(AsyncURLRequest.this, rate);
}

@Override
protected void onPostExecute(Integer bytes)
{
    completionListener.onCompletion(AsyncURLRequest.this, true, null);
}

}


}
