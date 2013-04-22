package org.gskbyte.view;

import java.io.IOException;
import java.io.InputStream;

import lombok.Getter;

import org.gskbyte.R;
import org.gskbyte.bitmap.AbstractBitmapManager;
import org.gskbyte.util.IOUtils;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * ImageView that supports loading of bitmaps in background.
 * Shows a ProgressBar if the file needs to be loaded.
 * 
 * Provides interaction with BitmapManager to ease loading of files even more.
 * 
 * TODO improve method documentation
 * */

public class AsyncImageView
extends FrameLayout
{

@Getter
private final ImageView imageView;
private final ProgressBar progressBar;

private AsyncTask<String, Void, Bitmap> currentTask;

public AsyncImageView(Context context, AttributeSet attrs, int defStyle)
{
    super(context, attrs, defStyle);
    LayoutInflater.from(context).inflate(R.layout.async_imageview, this, true);
    imageView = (ImageView) findViewById(R.id.asyncimageview_image);
    progressBar = (ProgressBar) findViewById(R.id.asyncimageview_progress_bar);
}

public AsyncImageView(Context context, AttributeSet attrs)
{
    super(context, attrs);
    LayoutInflater.from(context).inflate(R.layout.async_imageview, this, true);
    imageView = (ImageView) findViewById(R.id.asyncimageview_image);
    progressBar = (ProgressBar) findViewById(R.id.asyncimageview_progress_bar);
}

public void setLoading(boolean loading)
{
    imageView.setVisibility(loading ? GONE : VISIBLE);
    progressBar.setVisibility(loading ? VISIBLE : GONE);
}

public Drawable getDrawable()
{
    return imageView.getDrawable();
}

public synchronized void setImageBitmap(Bitmap bitmap)
{
    if(currentTask != null) {
        currentTask.cancel(true);
        currentTask = null;
    }
    
    if(bitmap != null && !bitmap.isRecycled()) {
        imageView.setImageBitmap(bitmap);
        setLoading(false);
    } else {
        imageView.setVisibility( GONE );
        progressBar.setVisibility( GONE );
    }
}

public synchronized void setImageBitmap(int location, String path)
{
    if(path != null) {
        if(currentTask != null) {
            currentTask.cancel(true);
            currentTask = null;
        }
        setLoading(true);
        
        currentTask = new LoadBitmapFromFileTask(location);
        currentTask.execute(path);
    } else {
        setImageBitmap(null);
    }
}

public synchronized void setImageBitmap(AbstractBitmapManager manager, String path)
{
    if(path != null && manager.isBitmapLoaded(path)) {
        setImageBitmap( manager.get(path) );
    } else {
        if(currentTask != null) {
            currentTask.cancel(true);
            currentTask = null;
        }
        setLoading(true);
        currentTask = new LoadBitmapFromManagerTask(manager);
        currentTask.execute(path);
    }
}

private final class LoadBitmapFromFileTask
extends AsyncTask<String, Void, Bitmap>
{
    private final int location;
    public LoadBitmapFromFileTask(int location)
    {
        this.location = location;
    }

    @Override
    protected Bitmap doInBackground(String... params)
    {
        try {
            InputStream is = IOUtils.GetInputStreamForDrawable(location, params[0], getContext());
            return BitmapFactory.decodeStream(is);
        } catch (NotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }
    
    @Override
    protected void onPostExecute( Bitmap result )
    {
        super.onPostExecute(result);
        setImageBitmap(result);  
    }

}

private final class LoadBitmapFromManagerTask
extends AsyncTask<String, Void, Bitmap>
{
    private final AbstractBitmapManager manager;    
    
    public LoadBitmapFromManagerTask(AbstractBitmapManager manager)
    {
        this.manager = manager;
    }
    
    @Override
    protected Bitmap doInBackground(String... params)
    {
        return manager.get(params[0]);
    }
    
    @Override
    protected void onPostExecute( Bitmap result )
    {
        super.onPostExecute(result);
        setImageBitmap(result);  
    }

}

}
