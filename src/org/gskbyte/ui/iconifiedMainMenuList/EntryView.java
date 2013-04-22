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

import org.gskbyte.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EntryView extends RelativeLayout
{

protected ImageView icon;
protected TextView title, subtitle;

public EntryView(Context context, AttributeSet attrs)
{
    super(context, attrs);
    LayoutInflater.from(context).inflate(R.layout.icon_menu_item, this);
    initViews();
}

protected void initViews()
{
    icon = (ImageView) findViewById(R.id.icon);
    title = (TextView) findViewById(R.id.title);
    subtitle = (TextView) findViewById(R.id.subtitle);
}

public void set(MenuEntry entry)
{
    icon.setImageResource(entry.iconRes);
    title.setText(entry.title);
    
    subtitle.setVisibility( (entry.subtitle.length() == 0) ? View.GONE : View.VISIBLE);
    subtitle.setText(entry.subtitle);
    
    setTag(entry);
}
}
