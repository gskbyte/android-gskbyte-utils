package org.gskbyte.util;

import org.gskbyte.R;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class FrequentIntents
{

public static Intent GetOpenURLIntent(String url)
{
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(Uri.parse(url));
    return intent;
}

public static void OpenURL(Context context, String url)
{
    Intent intent = GetOpenURLIntent(url);
    try {
        Intent chooser = Intent.createChooser(intent, context.getString(R.string.intent_open_choose));
        context.startActivity(chooser);
    } catch (ActivityNotFoundException ex) {
        Toast.makeText(context, R.string.intent_open_error, Toast.LENGTH_SHORT).show();
    }
}

public static Intent GetCallPhoneIntent(String number)
{
    Intent callIntent = new Intent(Intent.ACTION_DIAL);
    callIntent.setData(Uri.parse("tel:"+number));
    return callIntent;
}

public static void CallPhone(Context context, String number)
{
    Intent intent = GetCallPhoneIntent(number);
    
    try {
        Intent chooser = Intent.createChooser(intent, context.getString(R.string.intent_phone_choose));
        context.startActivity(chooser);
    } catch (ActivityNotFoundException ex) {
        Toast.makeText(context, R.string.intent_phone_error, Toast.LENGTH_SHORT).show();
    }
}


public static Intent GetEmailIntent(String address, String subject, CharSequence content)
{
    Intent mailIntent = new Intent(Intent.ACTION_SEND);
    mailIntent.setType("message/rfc822");
    mailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{address});
    mailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
    mailIntent.putExtra(Intent.EXTRA_TEXT   , content);
    return mailIntent;
}

public static void SendEmail(Context context, String address, String subject, CharSequence content)
{
    Intent mailIntent = GetEmailIntent(address, subject, content);
    
    try {
        Intent chooser = Intent.createChooser(mailIntent, context.getString(R.string.intent_email_choose));
        context.startActivity(chooser);
    } catch (ActivityNotFoundException ex) {
        Toast.makeText(context, R.string.intent_email_error, Toast.LENGTH_SHORT).show();
    }
}


public static Intent GetMapCoordinatesIntent(double latitude, double longitude, String markerTitle)
{
    String uri = "geo:"+ latitude + "," + longitude + "?q="+ latitude +","+ longitude; 
    if(markerTitle != null && markerTitle.length()>0) {
        uri += "("+markerTitle+")";
    }
    return new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
}

public static void ShowMapCoordinates(Context context, double latitude, double longitude, String markerTitle)
{
    Intent intent = GetMapCoordinatesIntent(latitude, longitude, markerTitle);
    try {
        Intent chooser = Intent.createChooser(intent, context.getString(R.string.intent_map_choose));
        context.startActivity(chooser);
    } catch (ActivityNotFoundException ex) {
        Toast.makeText(context, R.string.intent_map_error, Toast.LENGTH_SHORT).show();
    }
}

public static Intent GetMapRouteIntent(double latitudeFrom, double longitudeFrom, double latitudeTo, double longitudeTo)
{
    Uri uri = Uri.parse("http://maps.google.com/maps?saddr="+latitudeFrom+","+longitudeFrom+"&daddr="+latitudeTo+","+longitudeTo+"");
    return new Intent(Intent.ACTION_VIEW, uri);
}

public static void ShowMapRoute(Context context, double latitudeFrom, double longitudeFrom, double latitudeTo, double longitudeTo)
{
    Intent intent = GetMapRouteIntent(latitudeFrom, longitudeFrom, latitudeTo, longitudeTo);
    try {
        Intent chooser = Intent.createChooser(intent, context.getString(R.string.intent_map_choose));
        context.startActivity(chooser);
    } catch (ActivityNotFoundException ex) {
        Toast.makeText(context, R.string.intent_map_error, Toast.LENGTH_SHORT).show();
    }
}

}
