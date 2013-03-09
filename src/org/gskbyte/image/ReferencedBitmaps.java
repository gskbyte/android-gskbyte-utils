package org.gskbyte.image;

import java.util.HashSet;
import java.util.Set;

import org.gskbyte.image.BitmapManager;

import android.graphics.Bitmap;


public abstract class ReferencedBitmaps
{

protected final BitmapManager manager;
protected final Set<String> paths = new HashSet<String>();

public ReferencedBitmaps(BitmapManager manager)
{
    this.manager = manager;
}

public void addPath(String path)
{
    manager.addPath(path);
    paths.add(path);
}

public int count()
{ return paths.size(); }

public Bitmap get(String path)
{ return manager.get(path); }

public void clear()
{ paths.clear(); }


}
