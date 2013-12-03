package org.gskbyte.ui.iconifiedMainMenuList;

import java.util.List;

import org.gskbyte.ui.ListAdapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.SparseArray;

public class MainMenuAdapter
extends ListAdapter<MenuEntry, EntryView>
{

protected final SparseArray<Fragment> fragments = new SparseArray<Fragment>();

public MainMenuAdapter(Context context, List<MenuEntry> models)
{
    super(context, models);
}

@Override
protected void configureView(EntryView view, MenuEntry entry)
{ view.set(entry); }

@Override
protected EntryView initializeView()
{ return new EntryView(context, null); }

}
