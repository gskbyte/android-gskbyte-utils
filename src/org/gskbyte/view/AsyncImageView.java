package org.gskbyte.view;

import org.gskbyte.R;
import org.gskbyte.bitmap.AbstractBitmapManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
implements AbstractBitmapManager.BackgroundLoadListener
{

private ImageView imageView;
private ProgressBar progressBar;
private volatile boolean loading;
private volatile String path;

public AsyncImageView(Context context, AttributeSet attrs, int defStyle)
{
    super(context, attrs, defStyle);
    commonInit(context);
}

public AsyncImageView(Context context, AttributeSet attrs)
{
    super(context, attrs);
    commonInit(context);
}

private void commonInit(Context context)
{
    LayoutInflater.from(context).inflate(R.layout.async_imageview, this, true);
    imageView = (ImageView) findViewById(R.id.asyncimageview_image);
    progressBar = (ProgressBar) findViewById(R.id.asyncimageview_progress_bar);
    
    setLoading(true);
}

public boolean isLoading()
{ return this.loading; }

public synchronized void setLoading(boolean loading)
{
    this.loading = loading;
    if( !isInEditMode() ) {
        imageView.setVisibility(loading ? GONE : VISIBLE);
        progressBar.setVisibility(loading ? VISIBLE : GONE);
    }
}

public ImageView getInternalImageView()
{ return this.imageView; }

public Drawable getDrawable()
{
    return imageView.getDrawable();
}

private synchronized void setBitmap(Bitmap bitmap)
{
    if(bitmap != null && !bitmap.isRecycled()) {
        setLoading(false);
        imageView.setImageBitmap(bitmap);
    } else {
        imageView.setVisibility( GONE );
        progressBar.setVisibility( GONE );
    }
}

public synchronized void setImageBitmap(AbstractBitmapManager manager, String path)
{
    this.path = path;
    Bitmap bitmap = manager.getInBackground(path, this);
    if(bitmap != null) {
        setBitmap(bitmap);
    } else {
        setLoading(true);
    }
}

public void setImageBitmap(Bitmap b)
{
    setLoading(false);
    imageView.setImageBitmap(b);
}

public void setImageResource(int res)
{
    setLoading(false);
    imageView.setImageResource(res);
}


@Override
public synchronized void bitmapLoadedInManager(Bitmap bitmap, String loadedPath, AbstractBitmapManager manager)
{
    if(loadedPath.equals(this.path)) {
        setBitmap(bitmap);
    }
}

}
