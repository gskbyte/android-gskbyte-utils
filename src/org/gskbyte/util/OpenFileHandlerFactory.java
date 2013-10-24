package org.gskbyte.util;

import java.util.HashMap;

import org.gskbyte.R;

import android.content.Context;

public class OpenFileHandlerFactory
{

private static final HashMap<String, FileHandlerDescriptor> AppsForMimeTypes = new HashMap<String, FileHandlerDescriptor>();
static {
    AppsForMimeTypes.put("application/pdf", new FileHandlerDescriptor(
                         R.string.openfilehandler_title_pdf,
                         new String[] {"Adobe Reader", "PDF Reader"},
                         new String[] {"com.adobe.reader", "com.foobnix.pdf.reader"}
                         )
    );
}


public static final OpenFileHandler HandlerForMIMEType(Context context, String mimetype)
{
    FileHandlerDescriptor descriptor = AppsForMimeTypes.get(mimetype);
    OpenFileHandler handler;
    if(descriptor != null) {
        handler = new OpenFileHandler(context, mimetype, descriptor.apps, descriptor.packages);
        if(descriptor.titleResource != 0)
            handler.setDialogTitle( context.getString(descriptor.titleResource) );
    } else {
        handler = new OpenFileHandler(context, mimetype, new String[]{}, new String[]{});
    }
    return handler;
}


private static class FileHandlerDescriptor
{
private final int titleResource;
private final String [] apps, packages;

@SuppressWarnings("unused")
public FileHandlerDescriptor(String[] apps, String[] packages)
{
    this.titleResource = 0;
    this.apps = apps;
    this.packages = packages;
}

public FileHandlerDescriptor(int titleResource, String[] apps, String[] packages)
{
    this.titleResource = titleResource;
    this.apps = apps;
    this.packages = packages;
}
}
}
