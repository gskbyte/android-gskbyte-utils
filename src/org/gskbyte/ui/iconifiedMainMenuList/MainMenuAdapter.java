package org.gskbyte.ui.iconifiedMainMenuList;

import java.util.List;

import org.gskbyte.ui.ListAdapter;

import com.actionbarsherlock.app.SherlockFragment;

import android.content.Context;
import android.util.SparseArray;

public class MainMenuAdapter
extends ListAdapter<MenuEntry, EntryView>
{

protected final SparseArray<SherlockFragment> fragments = new SparseArray<SherlockFragment>();

public MainMenuAdapter(Context context, List<MenuEntry> models)
{
    super(context, models);
}

@Override
protected void configureView(EntryView view, MenuEntry entry)
{ view.set(entry); }

@Override
protected EntryView initializeView()
{ return EntryView_.build(context, null); }

}
