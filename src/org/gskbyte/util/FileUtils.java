package org.gskbyte.util;

import java.io.InputStream;
import java.util.Scanner;

public class FileUtils
{

public static String InputStreamToString(InputStream is)
{
    Scanner s = new Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
}

}
