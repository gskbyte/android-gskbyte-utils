package org.gskbyte.remote;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpResponseException;
import org.apache.http.util.ByteArrayBuffer;
import org.gskbyte.util.IOUtils;
import org.gskbyte.util.Logger;

public class URLRequest
{

public interface ProgressListener
{
    public void onProgressChanged(int readBytes, int totalBytes);
}

protected static final class Param
{
    final String key, value;
    public Param(String k, String v) {
        key = k;
        value = v;
    }
}

// this class is intended for small request, we shouldn't need too much memory (16KB by default)
public static final int DEFAULT_BUFFER_SIZE = 16 * 1024;
public static final int REMOTE_READ_BYTES   = 8 * 1024; // read every 8 KB

private final String urlBase;
private String user, password;
private volatile boolean running;
private boolean followRedirects = true;
private String requestMethod = "GET";
private List<Param> parameters;

private int responseCode = 0;
private int totalBytes = 0;

public URLRequest(String urlBase)
{
    this.urlBase = urlBase;
}

public URLRequest(String urlBase, String requestMethod)
{
    this.urlBase = urlBase;
    this.requestMethod = requestMethod;
}

public String getURLBase()
{ return urlBase; }

public synchronized boolean isRunning()
{ return running; }

public void setFollowRedirects(boolean follow)
{ this.followRedirects = follow; }

public void setRequestMethod(String method)
{ this.requestMethod = method; }

public int getResponseCode()
{ return this.responseCode; }

public int getTotalBytes()
{ return this.getTotalBytes(); }

public void setAuthentication(String user, String password)
{
    this.user = user;
    this.password = password;
}

private void initializeParameters()
{
    if(parameters == null) {
        parameters = new ArrayList<Param>();
    }
}

public void addParameter(String key, String value)
{
    initializeParameters();
    parameters.add(new Param(key, value));
}

public void addParameters(Map<String, String> params)
{
    initializeParameters();
    for(String key : params.keySet()) {
        String value = params.get(key);
        parameters.add( new Param(key, value) );
    }
}

public InputStream execute(ProgressListener progressListener)
        throws IOException
{
    URL url = new URL(urlBase);
    return internalExecute(url, progressListener);
}

public InputStream execute()
        throws IOException
{
    return execute(null);
}

public String readString(ProgressListener progressListener)
        throws IOException
{
    return IOUtils.InputStreamToString( this.execute(progressListener) );
}

public String readString()
        throws IOException
{
    return this.readString(null);
}

protected void configureConnection(HttpURLConnection conn)
        throws IOException
{
    conn.setDoInput(true);
    conn.setConnectTimeout(15000);
    conn.setReadTimeout(15000);
    
    if(user!=null && user.length()>0) {
        Authenticator au = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication (user, password.toCharArray());
            }
        };
        Authenticator.setDefault(au);
    }
    
    conn.setInstanceFollowRedirects(followRedirects);
    
    if(parameters != null) {
        conn.setDoOutput(true);
        conn.setRequestMethod(requestMethod);
        StringBuffer pstr = new StringBuffer();
        for(Param p : parameters) {
            if(pstr.length()>0) {
                pstr.append('&');
            }
            pstr.append(p.key + '=' + p.value);
        }
        
        DataOutputStream wr = new DataOutputStream( conn.getOutputStream() );
        wr.writeBytes(pstr.toString());
        wr.flush();
        wr.close();
    }
}

protected InputStream internalExecute(URL url, ProgressListener progressListener)
        throws IOException
{
    // create and configure connection
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    configureConnection(conn);
    conn.connect();
    
    // manage possible redirections
    responseCode = conn.getResponseCode();
    if (responseCode / 100 != 2) {
        conn.disconnect();
        if(responseCode / 100 == 3 && followRedirects) { // try redirection
            
            String newUrlString = conn.getHeaderField("Location");
            Logger.debug(getClass(), "Redirection code " + responseCode + " received, redirecting to " + newUrlString);

            URL newURL = new URL(newUrlString);
            return internalExecute(newURL, progressListener);
        }
        throw new HttpResponseException(responseCode, "Error connecting to ["+url+"], response code: "+responseCode);
    }
    
    totalBytes = conn.getContentLength(); // could be 0 or -1, be careful!!!
    
    int bufferSize = DEFAULT_BUFFER_SIZE;
    if(totalBytes > 0 && totalBytes<bufferSize) {
        bufferSize = totalBytes;
    }
    
    ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer(bufferSize);
    InputStream remoteStream = conn.getInputStream();
    
    byte [] tempArray = new byte[REMOTE_READ_BYTES];
    int readBytes = remoteStream.read(tempArray);
    while(readBytes>0) {
        byteArrayBuffer.append(tempArray, 0, readBytes);
        readBytes = remoteStream.read(tempArray);
        if(progressListener != null) {
            progressListener.onProgressChanged(readBytes, totalBytes);
        }
    }
    
    tempArray = null;
    remoteStream.close();
    conn.disconnect();
    
    return new ByteArrayInputStream(byteArrayBuffer.buffer());
}

}
