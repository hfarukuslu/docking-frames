/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.common.preference;

import bibliothek.extension.gui.dock.preference.DefaultPreferenceModel;
import bibliothek.extension.gui.dock.preference.preferences.ButtonContentPreference;
import bibliothek.extension.gui.dock.preference.preferences.choice.TabContentFilterPreference;
import bibliothek.extension.gui.dock.preference.preferences.choice.TabPlacementPreference;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.util.Path;

/**
 * A model showing various preferences that are used for the layout.
 * <ul>
 * 	<li>What content to show on the button for a minimized {@link Dockable} </li>
 * 	<li>Where to place tabs</li>
 * 	<li>What to show on tabs</li>
 * 	<li>Which {@link CControl#setTheme(String) theme} to use</li>
 * </ul>
 * @author Benjamin Sigg
 */
public class CLayoutPreferenceModel extends DefaultPreferenceModel{
    /**
     * Creates a new model.
     * @param control the control whose settings this model represents
     */
    public CLayoutPreferenceModel( CControl control ){
        add( new ButtonContentPreference( control.intern().getDockProperties(), new Path( "dock.layout.ButtonContent" )));
        add( new TabPlacementPreference( control.intern().getDockProperties(), new Path( "dock.layout.tabplacement" )));
        add( new TabContentFilterPreference( control.intern().getDockProperties(), new Path( "dock.layout.tabcontentfilter" )));
        add( new ThemePreference( control.intern().getDockProperties(), control.getThemes() ));
    }
}
