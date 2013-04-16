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

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.ViewById;

@EViewGroup(resName="icon_menu_item")
public class EntryView extends RelativeLayout
{
// IDs are the same in the XML file
// https://github.com/excilys/androidannotations/wiki/Library-projects
@ViewById
ImageView icon;

@ViewById
TextView title;

@ViewById
TextView subtitle;

public EntryView(Context context, AttributeSet attrs)
{ super(context, attrs); }

public void set(MenuEntry entry)
{
    icon.setImageResource(entry.iconRes);
    title.setText(entry.title);
    
    subtitle.setVisibility( (entry.subtitle.length() == 0) ? View.GONE : View.VISIBLE);
    subtitle.setText(entry.subtitle);
    
    setTag(entry);
}
}
