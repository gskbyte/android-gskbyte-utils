/*******************************************************************************
 * Copyright (c) 2013 Jose Alcalá Correa.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * Contributors:
 *     Jose Alcalá Correa - initial API and implementation
 ******************************************************************************/
package org.gskbyte.ui.iconifiedMainMenuList;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
public class MainMenuAdapter extends BaseAdapter
{
    protected final Context context;
    protected final List<MenuEntry> entries;

    public MainMenuAdapter(Context context, List<MenuEntry> entries)
    {
        super();
        this.context = context;
        this.entries = entries;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        EntryView v = (EntryView) convertView;
        if (v == null) {
            v = EntryView_.build(context, null);
        }

        final MenuEntry entry = entries.get(position);
        if (entry != null) {
            v.set(entry);
        }
        return v;
    }

    @Override
    public int getCount()
    { return entries.size(); }

    @Override
    public Object getItem(int position)
    { return null; }

    @Override
    public long getItemId(int position)
    { return 0; }
}
