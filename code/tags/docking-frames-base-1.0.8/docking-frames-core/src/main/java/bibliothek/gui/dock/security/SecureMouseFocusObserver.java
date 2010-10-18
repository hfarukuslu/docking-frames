/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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

package bibliothek.gui.dock.security;

import java.awt.AWTEvent;
import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.control.ControllerSetupCollection;
import bibliothek.gui.dock.control.FocusController;
import bibliothek.gui.dock.control.MouseFocusObserver;

/**
 * A {@link FocusController} which relies on {@link GlassedPane GlassedPanes}.
 * @author Benjamin Sigg
 */
public class SecureMouseFocusObserver extends MouseFocusObserver{
    /** A list of GlassPanes which know this controller */
    private List<GlassedPane> panes = new ArrayList<GlassedPane>();
    
    /**
     * Creates a new FocusController for <code>controller</code>.
     * @param controller the owner of this FocusController
     * @param setup an observable informing this object when <code>controller</code>
     * is set up.
     */
    public SecureMouseFocusObserver( DockController controller, ControllerSetupCollection setup ) {
        super(controller, setup);
    }

    @Override
    public void check( AWTEvent event ) {
        if( interact( event ))
            super.check(event);
    }
    
    /**
     * Registers a new GlassPane.
     * @param pane the new pane
     */
    public void addGlassPane( GlassedPane pane ){
        panes.add( pane );
        pane.setFocusController( this );
    }
    
    /**
     * Unregisters a previously added GlassPane.
     * @param pane the pane to remove
     */
    public void removeGlassPane( GlassedPane pane ){
        panes.remove( pane );
        pane.setFocusController( null );
    }
    
    @Override
    public void kill() {
        for( GlassedPane pane : panes ){
            pane.setFocusController( null );
        }
        panes.clear();
    }
}
