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
import java.util.TreeMap;

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

private final String url;
private String user, password;
private volatile boolean running;
private String requestMethod = "GET";
private List<Param> parameters;

private int responseCode = 0;
private int totalBytes = 0;

private boolean followRedirects = true;
private String redirectionUrl;

private Map<String, List<String>> headerFields; // data obtained when connection is open
private Map<String, String> customHeaderFields; // to be added when creating connection

public URLRequest(String urlBase)
{
    this.url = urlBase;
}

public URLRequest(String urlBase, String requestMethod)
{
    this.url = urlBase;
    this.requestMethod = requestMethod;
}

public String getURL()
{ return url; }

public Map<String, List<String>> getConnectionHeaderFields()
{ return headerFields; }

public Map<String, String> getCustomHeaderFields()
{ return customHeaderFields; }

public void setCustomHeaderFields(Map<String, String> properties)
{ this.customHeaderFields = properties; }

public void putCustomHeaderField(String key, String value)
{
    if(this.customHeaderFields == null) {
        this.customHeaderFields = new TreeMap<String, String>();
    }
    this.customHeaderFields.put(key, value);
}

public synchronized boolean isRunning()
{ return running; }

public void setFollowRedirects(boolean follow)
{ this.followRedirects = follow; }

public void setRequestMethod(String method)
{ this.requestMethod = method; }

public int getResponseCode()
{ return this.responseCode; }

// returns something if received a redirection code (302 usually)
public String getRedirectionURL()
{ return this.redirectionUrl; }

public int getTotalBytes()
{ return this.totalBytes; }

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
    URL remoteUrl = new URL(this.url);
    return internalExecute(remoteUrl, progressListener);
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
    
    if(customHeaderFields != null) {
        for(String key : customHeaderFields.keySet()) {
            conn.addRequestProperty(key, customHeaderFields.get(key));
        }
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
    headerFields = conn.getHeaderFields();
    if (responseCode / 100 != 2) {
        conn.disconnect();
        
        if(responseCode / 100 == 3) { // try redirection
            redirectionUrl = conn.getHeaderField("Location");
            
            final String redirectionString = "Redirection code " + responseCode + " received -> " + redirectionUrl;
            Logger.debug(getClass(), redirectionString);
            
            if(followRedirects) {
                URL newURL = new URL(redirectionUrl);
                return internalExecute(newURL, progressListener);
            } else {
                throw new HttpResponseException(responseCode, redirectionString);
            }
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
    totalBytes = readBytes>=0 ? readBytes : 0;
    while(readBytes>0) {
        byteArrayBuffer.append(tempArray, 0, readBytes);
        readBytes = remoteStream.read(tempArray);
        if(readBytes > 0) {
            totalBytes += readBytes;
        }
        if(progressListener != null) {
            progressListener.onProgressChanged(totalBytes, totalBytes);
        }
    }
    
    tempArray = null;
    remoteStream.close();
    conn.disconnect();
    
    return new ByteArrayInputStream(byteArrayBuffer.buffer());
}

}
