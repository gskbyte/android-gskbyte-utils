package org.gskbyte.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;

import org.gskbyte.util.IOUtils;

import android.content.Context;

public class DiskDownload
extends Download
{
private FileOutputStream tempOutputStream;
private String tempFilePath;

private final Context context;
private final int localFileLocation;
private final String localFileName;

public static class Request extends Download.Request
{
    private static final long serialVersionUID = 4480930156991551348L;
    
    private final Context context;
    private final int localFileLocation;
    private final String localFileName;

    public Request(URL remoteUrl, Context context, int location, String filePath)
    {
        super(remoteUrl);
        this.context = context;
        this.localFileLocation = location;
        this.localFileName = filePath;
    }
    
    public Request(URL remoteUrl, Context context, int location, String filePath, int tag)
    {
        super(remoteUrl, tag);
        this.context = context;
        this.localFileLocation = location;
        this.localFileName = filePath;
    }
    
    public Request(Request requestToClone)
    {
        super(requestToClone);
        this.context = requestToClone.context;
        this.localFileLocation = requestToClone.localFileLocation;
        this.localFileName = new String(requestToClone.localFileName);
    }

    public Context getContext()
    {return context; }
    
    public int getLocalFileLocation()
    { return localFileLocation; }
    
    public String getLocalFileName()
    { return localFileName;}
    
    @Override
    public boolean savesToDisk()
    { return true; }
}

public DiskDownload(Request request)
{
    super(request);
    this.context = request.context;
    this.localFileLocation = request.localFileLocation;
    this.localFileName = request.localFileName;
}

public DiskDownload(URL remoteURL, Context context, int fileLocation, String localFilePath)
{
    super(remoteURL);
    this.context = context;
    this.localFileLocation = fileLocation;
    this.localFileName = localFilePath;
}

public Context getContext()
{ return context; }

public int getLocalFileLocation()
{ return localFileLocation; }

public String getLocalFileName()
{ return localFileName;}

@Override
public final boolean savesToDisk()
{ return true; }

@Override
protected void resetTemporalStuff()
{
    super.resetTemporalStuff();
    tempOutputStream = null;
}

@Override
public synchronized boolean stop()
{
    if( super.stop() && tempFilePath != null) {
        IOUtils.DeleteFile(localFileLocation, tempFilePath, context);
        tempFilePath = null;
        return true;
    } else {
        return false;
    }
}

@Override
public InputStream getInputStream() throws IOException 
{
    return IOUtils.GetInputStream(localFileLocation, localFileName, context);
}
@Override
protected DownloadTask getDownloadTask()
{
    return new DiskDownload.DownloadTask();
}

public long getDataLength()
{
    File f = IOUtils.GetFile(localFileLocation, localFileName, context);
    return f.length();
}

@Override
public boolean isCorrect()
{ return getDataLength()>0;}

protected class DownloadTask extends Download.DownloadTask
{
    @Override
    protected void readFromStream() throws IOException
    {
        final Random r = new Random(System.currentTimeMillis());
        tempFilePath = localFileName + ".dl_" + r.nextInt(1000000);
        
        tempOutputStream = IOUtils.GetFileOutputStream(localFileLocation, tempFilePath, context);
        
        byte [] tempArray = new byte[REMOTE_READ_BYTES];
        int readBytes = connectionStream.read(tempArray);
        
        while(readBytes>0 && state == State.Running) {
            buffer.append(tempArray, 0, readBytes);
            downloadedSize += readBytes;
            
            readBytes = connectionStream.read(tempArray);
            
            if(totalSize > 0)
                rate = (float)downloadedSize / (float) totalSize;
            
            if(downloadedSize-lastNotificationSize > notificationSizeDif || downloadedSize == totalSize) {
                lastNotificationSize = downloadedSize;
                publishProgress(rate);
                //Logger.error(getClass(), "------> "+readBytes);
            }
            
            if(buffer.length() > DEFAULT_BUFFER_SIZE) {
                tempOutputStream.write(buffer.buffer(), 0, buffer.length());
                buffer.clear();
                tempOutputStream.flush();
            }
        }
        
        tempOutputStream.write(buffer.buffer(), 0, buffer.length());
        tempOutputStream.flush();
        tempOutputStream.close();

        // Release memory!!!
        buffer.clear();
        buffer = null;        
        byteArray = null;
        
        IOUtils.MoveFile(localFileLocation, tempFilePath, localFileName, context);
        tempFilePath = null;
    }

}
}
