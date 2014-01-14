package org.gskbyte.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class ArrayAdapterWithDefaultValue
extends BaseAdapter
{

private final Context context;
private final int textViewResource, textViewResourceInView;
private final List<?> list;
private final String defaultValue;

public ArrayAdapterWithDefaultValue(Context context, int viewResource, List<?> list, String defaultValue)
{
    this.context = context;
    this.textViewResource = viewResource;
    this.textViewResourceInView = 0;
    this.list = list;
    this.defaultValue = defaultValue;
}

public ArrayAdapterWithDefaultValue(Context context, int viewResource, int textViewResourceInView, List<?> list, String defaultValue)
{
    this.context = context;
    this.textViewResource = viewResource;
    this.textViewResourceInView = textViewResourceInView;
    this.list = list;
    this.defaultValue = defaultValue;
}

@Override
public int getCount()
{ return list.size() + 1; }

@Override
public Object getItem(int position)
{
    if(position>0) {
        return list.get(position-1);
    } else {
        return null;
    }
}

@Override
public long getItemId(int position)
{ return position; }

@Override
public View getView(int position, View convertView, ViewGroup parent)
{
    if(convertView == null) {
        convertView = LayoutInflater.from(context).inflate(textViewResource, null);
    }
    
    String text;
    if(position > 0) {
        Object item = getItem(position);
        text = item.toString();
    } else {
        text = defaultValue;
    }
    
    TextView textView;
    if(textViewResourceInView != 0) {
        textView = (TextView) convertView.findViewById(textViewResource);
    } else {
        textView = (TextView) convertView;
    }

    textView.setText(text);
    return convertView;
}


}
