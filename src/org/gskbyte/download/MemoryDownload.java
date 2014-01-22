package org.gskbyte.download;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.gskbyte.util.IOUtils;

public class MemoryDownload extends Download
{
    public static class Request extends Download.Request
    {
        private static final long serialVersionUID = 3701492086695972214L;

        public Request(URL remoteUrl)
        { super(remoteUrl); }
        
        public Request(URL remoteUrl, int tag)
        { super(remoteUrl, tag); }
        
        public Request(String remoteUrlString) throws MalformedURLException
        { super(remoteUrlString); }
        
        public Request(String remoteUrlString, int tag) throws MalformedURLException
        { super(remoteUrlString, tag); }
        
        public Request(Request requestToClone)
        { super(requestToClone); }

        @Override
        public boolean savesToDisk()
        { return false;  }
    }

    public MemoryDownload(Request request)
    { super(request); }
    
    public MemoryDownload(URL remoteUrl)
    { super(remoteUrl); }
    
    @Override
    public boolean savesToDisk()
    { return false; }

    @Override
    protected DownloadTask getDownloadTask()
    { return new MemoryDownload.DownloadTask(); }
    @Override
    public boolean isCorrect()
    { return byteArray.length()>0;}

    @Override
    public String getLocalFileName()
    { return "<MemoryDownload>"; }
    
    public int getLocalFileLocation()
    { return IOUtils.LOCATION_NONEXISTENT; }
    
    @Override
    public long getDataLength()
    { return byteArray.length(); }
    
    protected class DownloadTask extends Download.DownloadTask
    {

        @Override
        protected void readFromStream() throws IOException
        {
            byte [] tempArray = new byte[REMOTE_READ_BYTES];
            int readBytes = connectionStream.read(tempArray);
            
            while(readBytes>0 && state == State.Running) {
                buffer.append(tempArray, 0, readBytes);
                downloadedSize += readBytes;
                readBytes = connectionStream.read(tempArray);
                if(totalSize > 0) {
                    rate = (float)downloadedSize / (float) totalSize;
                    if(downloadedSize-lastNotificationSize > notificationSizeDif || downloadedSize == totalSize) {
                        lastNotificationSize = downloadedSize;
                        publishProgress(rate);
                    }
                }
            }
        }
    
    }

}
