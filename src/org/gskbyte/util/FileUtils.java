package org.gskbyte.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class FileUtils
{
    public static String InputStreamToString( InputStream is ) throws IOException
    {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}
