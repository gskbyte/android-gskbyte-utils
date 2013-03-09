package org.gskbyte.image;

import java.util.ArrayList;
import java.util.List;

import org.gskbyte.image.BitmapManager;

import android.graphics.Bitmap;


public class IndexedBitmaps extends ReferencedBitmaps
{

protected final List<String> pathList = new ArrayList<String>();

public IndexedBitmaps(BitmapManager manager)
{ super(manager); }

public void addPath(String path)
{
    super.addPath(path);
    pathList.add(path);
}

public String getPath(int index)
{ return pathList.get(index); }

public Bitmap get(int index)
{
    String path = pathList.get(index);
    return manager.get(path);
}

public void clear()
{
    super.clear();
    pathList.clear();
}

}
