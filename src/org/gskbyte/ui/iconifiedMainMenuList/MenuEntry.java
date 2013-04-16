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

public class MenuEntry
{

public int iconRes;
public String title, subtitle;

public MenuEntry(int iconRes, String title, String subtitle)
{
    super();
    this.iconRes = iconRes;
    this.title = title;
    this.subtitle = subtitle;
}
}
