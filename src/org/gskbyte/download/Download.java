package org.gskbyte.download;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.http.util.ByteArrayBuffer;
import org.gskbyte.listener.Listenable;
import org.gskbyte.util.IOUtils;
import org.gskbyte.util.Logger;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

public abstract class Download
extends Listenable<Download.Listener>
{

public static abstract class Request implements Serializable
{
    private static final long serialVersionUID = 7957702126831831255L;
    public static final int DEFAULT_TAG = 0;
    
    protected final URL remoteURL;
    protected final int tag;
    protected String postParameters = "";
    protected String user = "", password = "";
    
    public abstract boolean savesToDisk();
    
    protected Request(URL remoteUrl, int tag)
    { this.remoteURL = remoteUrl; this.tag = tag; }
    protected Request(URL remoteUrl)
    { this(remoteUrl, DEFAULT_TAG); }

    protected Request(String remoteUrlString, int tag) throws MalformedURLException
    { this(new URL(remoteUrlString), tag); }
    protected Request(String remoteUrlString) throws MalformedURLException
    { this(new URL(remoteUrlString)); }

    protected Request(Request requestToClone)
    {
        this.remoteURL = requestToClone.remoteURL;
        this.tag = requestToClone.tag;
        this.postParameters = requestToClone.postParameters;
    }
    
    public URL getRemoteURL()
    { return remoteURL; }

    public int getTag()
    { return tag; }

    public String getPostParameters()
    { return postParameters; }

    public String getUser()
    { return user; }

    public String getPassword()
    { return password; }

    // Should be safer!
    public void addPostParameter(String name, String value)
    {
        if(postParameters.length()>0) {
            postParameters += "&";
        }
        postParameters += name + "=" + value;
    }
    
    public void setAuthentication(String user, String password)
    {
    	this.user = user;
    	this.password = password;
    }
}

// Delegate methods get always called on the main thread
public interface Listener
{
    public void onDownloadStarted(Download download);
    public void onDownloadFailed(Download download);
    public void onDownloadRate(Download download, float rate);
    public void onDownloadFinished(Download download);
}

public enum State
{ Stopped, Running, Paused, Failed, Finished}

public static final int    REMOTE_READ_BYTES           = 8 * 1024; // Read every 16 KB
public static final int    DEFAULT_BUFFER_SIZE         = 256 * 1024; // Memory cache size
public static final int    DEFAULT_NOTIFICATION_SIZE   = 64 * 1024; // Notify every 64 KB
public static final float  DEFAULT_NOTIFICATION_RATE   = 0.01f; // Notify every 1%

static volatile private int GlobalId = 1;

protected final int uniqueId;
protected final int tag;
protected final URL remoteURL;
protected String postParameters = "";
protected String user = "", password = "";

protected int totalSize;
protected int downloadedSize;

protected float rate;
protected long startTime, endTime;

protected int notificationMinSize = DEFAULT_NOTIFICATION_SIZE;
protected float notificationMinRate = DEFAULT_NOTIFICATION_RATE;

protected int numRetries = 0;

protected State state = State.Stopped;
protected transient ByteArrayBuffer byteArray;

private DownloadTask downloadTask;

protected Download(URL remoteURL)
{
    this.uniqueId = ++GlobalId;
    this.tag = Request.DEFAULT_TAG;
    this.remoteURL = remoteURL;
    resetTemporalStuff();
}

protected Download(Request request)
{
    this.uniqueId = ++GlobalId;
    this.tag = request.tag;
    this.remoteURL = request.remoteURL;
    this.postParameters = request.postParameters;
    this.user = request.user;
    this.password = request.password;
    
    resetTemporalStuff();
}

public int getUniqueId()
{ return uniqueId; }

public int getTag()
{ return tag; }

public URL getRemoteURL()
{ return remoteURL; }

public String getPostParameters()
{ return postParameters; }

public String getUser()
{ return user; }

public String getPassword()
{ return password; }

public int getTotalSize()
{ return totalSize; }

public int getDownloadedSize()
{ return downloadedSize; }

public float getRate()
{ return rate; }

public long getStartTime()
{ return startTime; }

public long getEndTime()
{ return endTime; }

public State getState()
{ return state; }

public int getNotificationMinSize()
{return notificationMinSize;}

public void setNotificationMinSize(int notificationMinSize)
{ this.notificationMinSize = notificationMinSize; }

public float getNotificationMinRate()
{ return notificationMinRate; }

public void setNotificationMinRate(float notificationMinRate)
{ this.notificationMinRate = notificationMinRate; }

public int getNumRetries()
{ return numRetries; }

public void setNumRetries(int numRetries)
{ this.numRetries = numRetries; }

@SuppressLint("NewApi")
public synchronized boolean resume()
{
    switch (state) {
    case Stopped:
    case Failed:
    case Finished:
        downloadTask = this.getDownloadTask();
    case Paused:
        state = State.Running;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
            downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else{
            downloadTask.execute();
        }

        return true;
    default: // aka Running
        return false;
    }
}

protected abstract DownloadTask getDownloadTask();
public abstract int getLocalFileLocation();
public abstract String getLocalFileName();
public abstract boolean isCorrect();
public abstract long getDataLength();

public synchronized boolean pause()
{
    switch (state) {
    case Running:
        state = State.Paused;
        return true;
    default:
        return false;
    }
}

// TODO: review this method!!!
public synchronized boolean stop()
{
    switch (state) {
    case Running:
        state = State.Stopped;
        downloadTask.cancel(true);
        downloadTask = null; // should call cancel?
        return true;
    default:
        return false;
    }
}

public synchronized boolean isFinished()
{ return state == State.Finished; }
    
public InputStream getInputStream() throws IOException
{
    return new ByteArrayInputStream(byteArray.buffer());
}

public String getDataAsString() throws IOException
{
    return IOUtils.InputStreamToString(getInputStream());
}

protected synchronized void resetTemporalStuff()
{
    totalSize = downloadedSize = 0;
    rate = 0;
    startTime = endTime = -1;
    
    state = State.Stopped;
    downloadTask = null;
}

protected abstract class DownloadTask extends AsyncTask<Void, Float, Integer>
{
    protected long lastNotificationSize, notificationSizeDif;
    protected ByteArrayBuffer buffer;
    protected HttpURLConnection connection;
    protected InputStream connectionStream;

    protected InputStream initConnectionInputStream() throws IOException
    {
        connection= (HttpURLConnection) remoteURL.openConnection();
        
    	String protocol = remoteURL.getProtocol();
    	if(protocol.equals("https")) {
    		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}});
    	}

        connection.setDoInput(true);
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        
        if(postParameters.length()>0) {
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("POST"); 
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
            wr.writeBytes(postParameters);
            wr.flush();
            wr.close();
        }
        
        if(user!=null && user.length()>0) {
	    	Authenticator au = new Authenticator() {
	    		@Override
	    		protected PasswordAuthentication getPasswordAuthentication() {
	    		    return new PasswordAuthentication (user, password.toCharArray());
	    		}
	    	};
	    	Authenticator.setDefault(au);
        }
        
        // to be able to resume downloads
        if(downloadedSize>0)
            connection.setRequestProperty("Range", "bytes=" + downloadedSize + "-");
        connection.connect();

        // Make sure response code is in the 200 range.
        int responseCode = connection.getResponseCode();
        if (responseCode / 100 != 2) {
        	Logger.debug(getClass(), "Error connecting to URL, response code: "+responseCode);
            return null;
        }

        // Check for valid content length.
        totalSize = connection.getContentLength(); // could be 0 or -1, be careful!!!
        if(totalSize > 0) {
            long percentageSize = (long) (notificationMinRate * totalSize);
            notificationSizeDif = percentageSize < notificationMinSize ? percentageSize : notificationMinSize;
        } else {
            notificationSizeDif = notificationMinSize;
        }
        
        // do some preallocation if needed
        int capacity = (int) ((totalSize<DEFAULT_BUFFER_SIZE && totalSize>0) ? totalSize : DEFAULT_BUFFER_SIZE);
        buffer = new ByteArrayBuffer(capacity);
        startTime = System.currentTimeMillis();
        
        InputStream stream = connection.getInputStream();
        
        return stream;
    }
    
    protected abstract void readFromStream() throws IOException;

    protected volatile boolean downloadSuccessInBackgroundThread = true;
    @Override
    protected Integer doInBackground(Void ... unused)
    {
        try {
            connectionStream = initConnectionInputStream();
            if(connectionStream != null) {
                readFromStream();
                // Connection close
                connectionStream.close();
            } else {
                downloadedSize = -1;
            }
            connection.disconnect();
        } catch (IOException e) {
            Logger.error(getClass(), "Error creating connection to URL "+remoteURL + ": "+e.getMessage());
            downloadSuccessInBackgroundThread = false;
        } finally {
            if(buffer!=null/* && buffer.length() >= totalSize*/) {
                byteArray = buffer;
                buffer = null;
                
                state = State.Finished;
                endTime = System.currentTimeMillis();
            }
        }
        
        if(downloadSuccessInBackgroundThread) {
            return Integer.valueOf(downloadedSize);
        } else {
            return null;
        }
        
    }
    
    protected void notifyFail()
    {
        state = State.Failed;
        cleanupListeners();
        for(WeakReference<Listener> lref : getListeners()) {
            Listener l = lref.get();
            if(l!=null) l.onDownloadFailed(Download.this);
        }
    }
            
    @Override
    protected void onPreExecute()
    {
        Download thisDownload = Download.this;
        cleanupListeners();
        for(WeakReference<Listener> lref : getListeners()) {
            Listener l = lref.get();
            if(l!=null) l.onDownloadStarted(thisDownload);
        }
    }

    @Override
    protected void onProgressUpdate(Float ... values)
    {
        Download thisDownload = Download.this;
        cleanupListeners();
        for(WeakReference<Listener> lref : getListeners()) {
            Listener l = lref.get();
            if(l!=null) l.onDownloadRate(thisDownload, values[0]);
        }
    }
    
    @Override
    protected void onPostExecute(Integer bytes)
    {
        Download thisDownload = Download.this;
        if(bytes != null && bytes >= 0 && downloadSuccessInBackgroundThread) { // only if no error
            cleanupListeners();
            for(WeakReference<Listener> lref : getListeners()) {
                Listener l = lref.get();
                if(l!=null) l.onDownloadFinished(thisDownload);
            }
        } else {
            notifyFail();
        }

        resetTemporalStuff();
    }

}

public abstract boolean savesToDisk();

public String toString()
{ return "Download #"+uniqueId+" ("+remoteURL+")"; }

public int hashCode()
{ return uniqueId; }


}
