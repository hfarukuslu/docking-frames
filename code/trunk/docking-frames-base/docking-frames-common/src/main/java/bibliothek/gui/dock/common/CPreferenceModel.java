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
package bibliothek.gui.dock.common;

import bibliothek.extension.gui.dock.preference.PreferenceModel;
import bibliothek.extension.gui.dock.preference.PreferenceTreeModel;
import bibliothek.extension.gui.dock.preference.model.BubbleThemePreferenceModel;
import bibliothek.extension.gui.dock.preference.model.EclipseThemePreferenceModel;
import bibliothek.gui.DockController;
import bibliothek.gui.DockUI;
import bibliothek.gui.dock.common.preference.CKeyStrokePreferenceModel;
import bibliothek.gui.dock.common.preference.CLayoutPreferenceModel;
import bibliothek.util.Path;
import bibliothek.util.PathCombiner;

/**
 * A {@link PreferenceModel} that shows the settings of a {@link CControl}.
 * @author Benjamin Sigg
 */
public class CPreferenceModel extends PreferenceTreeModel{
    /**
     * Creates a new model. This constructor sets the behavior of how to
     * create paths for preferences to {@link PathCombiner#SECOND}. This
     * behavior allows reordering of models and preferences in future releases,
     * however forces any preference to have a truly unique path in a global
     * scale.
     * @param control the control whose settings can be changed by this model
     */
    public CPreferenceModel( CControl control ){
        this( control, PathCombiner.SECOND );
    }
    
    /**
     * Creates a new model.
     * @param control the control whose settings can be changed by this model
     * @param combiner how to combine paths of models and of preferences
     */
    public CPreferenceModel( CControl control, PathCombiner combiner ){
        super( combiner );
        DockController controller = control.intern().getController();
        
        put( new Path( "shortcuts" ),
                DockUI.getDefaultDockUI().getString( "preference.shortcuts" ), 
                new CKeyStrokePreferenceModel( controller.getProperties() ) );
        
        put( new Path( "layout" ),
                DockUI.getDefaultDockUI().getString( "preference.layout" ),
                new CLayoutPreferenceModel( control ));
        
        put( new Path( "layout.BubbleTheme" ),
                DockUI.getDefaultDockUI().getString( "theme.bubble" ),
                new BubbleThemePreferenceModel( controller.getProperties() ));
        
        put( new Path( "layout.EclipseTheme" ),
                DockUI.getDefaultDockUI().getString( "theme.eclipse" ),
                new EclipseThemePreferenceModel( controller.getProperties() ));
    }
}