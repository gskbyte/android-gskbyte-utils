package org.gskbyte.download;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.gskbyte.download.Download.State;
import org.gskbyte.listener.Listenable;
import org.gskbyte.util.Logger;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.SparseArray;

public class DownloadManager
extends Listenable<DownloadManager.Listener>
implements Download.Listener
{
    public interface Listener
    {
        void onDownloadManagerCompleted(DownloadManager manager);
        void onDownloadManagerRate(DownloadManager manager, float rate);
        
        void onDownloadStartedInManager(Download download, DownloadManager manager);
        void onDownloadCompletedInManager(Download download, DownloadManager manager);
        void onDownloadFailedInManager(Download download, DownloadManager manager);
        void onDownloadRateInManager(Download download, float rate, DownloadManager manager);
    }
        
    public static final int   DEFAULT_NUM_THREADS           = 3;
    public static final float DEFAULT_NOTIFICATION_INTERVAL = 0.01f; // every 1%
    
    private int numThreads = DEFAULT_NUM_THREADS;
    private float rateNotificationInterval = DEFAULT_NOTIFICATION_INTERVAL;
    
    private float downloadRate;
    private Download.State state;
    
    protected float lastNotifiedRate;
    protected final Map<URL, Download> downloadsForUrls = new HashMap<URL, Download>();
    protected final List<Download> queued = new LinkedList<Download>();
    protected final SparseArray<Download> finished = new SparseArray<Download>(),
                                          failed = new SparseArray<Download>(),
                                          downloading = new SparseArray<Download>();
    
    public DownloadManager()
    {
        SSLContext sc;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            Logger.except(getClass(), e);
        }
    }
    
    /**
     * All these methods require permission android.permission.ACCESS_NETWORK_STATE
     * */
    private static NetworkInfo getNetworkInfo(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo;
    }
    
    public static boolean IsConnectionActive(Context context)
    {
        NetworkInfo netInfo = getNetworkInfo(context);
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }
    
    public static boolean IsConnectionWifi(Context context)
    {
        NetworkInfo netInfo = getNetworkInfo(context);
        if(netInfo != null) {
            return netInfo.isConnected() && netInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }
    
    public static boolean IsConnectionMobile(Context context)
    {
        NetworkInfo netInfo = getNetworkInfo(context);
        if(netInfo != null) {
            return netInfo.isConnected() && netInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }
    
    public int getNumThreads()
    { return numThreads; }

    public void setNumThreads(int numThreads)
    {
        this.numThreads = numThreads;
        updateRunningQueue();
    }

    public float getRateNotificationInterval()
    { return rateNotificationInterval; }

    public void setRateNotificationInterval(float rateNotificationInterval)
    {
        this.rateNotificationInterval = rateNotificationInterval;
        this.lastNotifiedRate = 0; // to force notification
    }

    public float getDownloadRate()
    { return downloadRate; }

    public Download.State getState()
    {
        return state;
    }

    public Download queueRequest(Download.Request r)
    { return queueRequest(r, false, false); }

    public Download queueRequest(Download.Request r, boolean downloadFirst)
    { return queueRequest(r, downloadFirst, false); }
    
    public Download queueRequest(Download.Request r, boolean downloadFirst, boolean forceStart)
    {
        Download d = downloadForUrl(r.remoteURL);
        if(d == null || forceStart) {            
            if(r instanceof MemoryDownload.Request) {
                d = new MemoryDownload((MemoryDownload.Request)r);
            } else if(r instanceof DiskDownload.Request) {
                d = new DiskDownload((DiskDownload.Request)r);
            }
            
            if(d != null) {
                if(downloadsForUrls.containsKey(r.remoteURL)) {
                    Logger.error(getClass(), "File downloaded twice " + r.remoteURL);
                }
                
                downloadsForUrls.put(r.remoteURL, d);
                d.addListener(this);
                if(downloadFirst)
                    queued.add(0, d);
                else
                    queued.add(d);
            }
        }
        
        return d;
    }
    
    public boolean isFinished()
    { return state == State.Finished; }
    
    public boolean hasFinishedDownloads()
    { return finished.size() == 0; }
    
    public int getTotalCount()
    { return queued.size() + finished.size() + failed.size() + downloading.size(); }
    
    public int getQueuedCount()
    { return queued.size(); }
    
    public int getFinishedCount()
    { return finished.size(); }
    
    public int getFailedCount()
    { return failed.size(); }
    
    public Download downloadForUrl(URL remoteUrl)
    {
        return downloadsForUrls.get(remoteUrl);
    }
    
    public boolean isDownloadingDownloadWithId(int uniqueId)
    {
        final int indexInArray = downloading.indexOfKey(uniqueId);
        return indexInArray >=0;
    }
    
    protected float computeRate()
    {
        final int total = getTotalCount();
        final int finishedAndFailedCount = getFinishedCount() + getFailedCount();
        final int queuedCount = getQueuedCount();
        if(finishedAndFailedCount == total) {
            return 1.0f;
        } else if(queuedCount == total) {
            return 0.0f;
        } else {
            float rateProFile = 1.0f / (float)total;
            float totalRate = 0;
            totalRate += rateProFile * finishedAndFailedCount;
            for(int i=0; i<downloading.size(); ++i) {
                Download d = downloading.valueAt(i);
                totalRate += rateProFile * d.getRate();
            }
            
            return totalRate;
        }
    }
    
    public synchronized boolean resume()
    {
        lastNotifiedRate = 0;
        if(state != State.Running && queued.size()>0) {
            state = State.Running;
            updateRunningQueue();
            return true;
        }
        return false;
    }
    
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
    
    public synchronized boolean cancel(boolean removeFiles)
    {
        // TODO: implement!!!
        return false;
    }
    
    public synchronized boolean retryFailed()
    {
        if(failed.size() > 0) {
            for(int i=0; i<failed.size(); ++i) {
                int key = failed.keyAt(i);
                Download d = failed.valueAt(key);
                queued.add(d);
            }
            failed.clear();
            
            state = State.Running;
            updateRunningQueue();
            
            return true;
        } else {
            return false;
        }
    }
    
    protected synchronized void removeUrlDownloadMappingForArray(SparseArray<Download> array)
    {
        final int count = array.size();
        for(int i=0; i<count; ++i) {
            final int k = array.keyAt(i);
            Download d = array.get(k);
            downloadsForUrls.remove(d.remoteURL);
        }
    }
    
    public synchronized void clearFinished(boolean includeFailed)
    {
        removeUrlDownloadMappingForArray(finished);
        finished.clear();
        if(includeFailed) {
            removeUrlDownloadMappingForArray(failed);
            failed.clear();
        }
    }
    
    public synchronized boolean clearAll()
    {
        if(state != State.Running) {
            queued.clear();
            for(int i=0; i<downloading.size(); ++i) {
                int key = downloading.keyAt(i);
                Download d = downloading.valueAt(key);
                d.removeListener(this);
            }
            downloading.clear();
            finished.clear();
            failed.clear();
            downloadsForUrls.clear();
            cleanupListeners();
            
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public void onDownloadStarted(Download download)
    {
        for(WeakReference<Listener> lref : getListeners()) {
            Listener l = lref.get();
            if(l!=null) l.onDownloadStartedInManager(download, this);
        }
    }

    @Override
    public void onDownloadFailed(Download download)
    {
        boolean isDownloading = isDownloadingDownloadWithId(download.getUniqueId());
        if(isDownloading) {
            downloading.remove(download.getUniqueId());
            
            if(download.getNumRetries()>0) {
            	Logger.info(getClass(), "Retrying download: " + download);
            	download.setNumRetries(download.getNumRetries()-1);
            	
            	queued.add(download);
            	state = State.Running;
            	updateRunningQueue();
            } else {
                failed.append(download.getUniqueId(), download);
                for(WeakReference<Listener> lref : getListeners()) {
                    Listener l = lref.get();
                    if(l!=null) l.onDownloadFailedInManager(download, this);
                }
            }
        } else {
            Logger.error(getClass(), "Download already removed failed in manager");
        }
        
        updateRunningQueue();
    }
    
    protected void notifyCurrentRate(boolean force)
    {
        downloadRate = computeRate();
        if(downloadRate - lastNotifiedRate > rateNotificationInterval) {
            lastNotifiedRate = downloadRate;
            for(WeakReference<Listener> lref : getListeners()) {
                Listener l = lref.get();
                if(l!=null) l.onDownloadManagerRate(this, downloadRate);
            }
        }
    }

    @Override
    public void onDownloadRate(Download download, float rate)
    {
        boolean isDownloading = isDownloadingDownloadWithId(download.getUniqueId());
        if(isDownloading) {
            for(WeakReference<Listener> lref : getListeners()) {
                Listener l = lref.get();
                if(l!=null) {
                    l.onDownloadRateInManager(download, rate, this);
                }
            }
            notifyCurrentRate(false);
        } else {
            Logger.error(getClass(), "Download already removed notified rate in manager");
        }
    }

    @Override
    public void onDownloadFinished(Download download)
    {
        boolean isDownloading = isDownloadingDownloadWithId(download.getUniqueId());
        if(isDownloading) {
            downloading.remove(download.getUniqueId());
            finished.append(download.getUniqueId(), download);
            
            for(WeakReference<Listener> lref : getListeners()) {
                Listener l = lref.get();
                if(l!=null) {
                    l.onDownloadCompletedInManager(download, this);
                }
            }
        } else {
            Logger.error(getClass(), "Download already removed notified finish in manager");
        }
        updateRunningQueue();
    }
    
    protected void updateRunningQueue()
    {
        if(state != State.Running)
            return;
        
        boolean checkFinished = false;
        synchronized (this) {
            int freeThreads = numThreads - downloading.size();
            if(freeThreads >= 0) {
                int remaining = queued.size();
                if(remaining > 0) {
                    state = State.Running;
                    checkFinished = false;
                    int toAdd = (remaining<freeThreads) ? remaining : freeThreads;
                    
                    for(int i=0; i<toAdd; ++i) {
                        Download d = queued.get(0);
                        queued.remove(0);
                        
                        downloading.put(d.getUniqueId(), d);
                        d.resume();
                    }
                } else {
                    checkFinished = true;
                }
            }
        }
        
        if(checkFinished) {
            if(downloading.size() == 0) {
                state = State.Finished;
                for(WeakReference<Listener> lref : getListeners()) {
                    Listener l = lref.get();
                    if(l!=null) l.onDownloadManagerCompleted(this);
                }
            }
        }
        
        notifyCurrentRate(true);
    }
    
    public static TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {

                public java.security.cert.X509Certificate[] getAcceptedIssuers()
                {
                    return new java.security.cert.X509Certificate[] {};
                }
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                {
                    //No need to implement.
                }
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                {
                    //No need to implement.
                }
            }
    };
}
