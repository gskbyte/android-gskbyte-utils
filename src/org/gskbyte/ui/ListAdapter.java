package org.gskbyte.ui;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class ListAdapter<ModelClass, ViewClass extends View>
extends BaseAdapter
{
    protected final Context context;
    private final List<ModelClass> models;

    public ListAdapter(Context context, List<ModelClass> models)
    {
        this.context = context;
        this.models = models;
    }

    @Override
    public int getCount()
    { return models.size(); }

    @Override
    public Object getItem(int position)
    { return models.get(position); }

    @Override
    public long getItemId(int position)
    { return 0; }

    @Override
    @SuppressWarnings("unchecked")
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewClass view = (ViewClass) convertView;
        if(view == null) {
            view = initializeView();
        }
        configureView(view, (ModelClass)getItem(position));
        return view;
    }
    
    protected abstract ViewClass initializeView();
    protected abstract void configureView(ViewClass view, ModelClass object);
}
