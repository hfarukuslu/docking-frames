/**
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

package bibliothek.gui.dock.event;

import java.awt.Component;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.station.support.DockableVisibilityManager;

/**
 * This listener is added to a {@link DockStation}. It receives events on adding
 * or removing children of the station, or if the visibility of a child has
 * changed.
 * @author Benjamin Sigg
 */
public interface DockStationListener {    
    /**
     * Invoked before <code>dockable</code> is added to <code>station</code>.
     * @param station the station where the new child will be added
     * @param dockable the new child
     */
    public void dockableAdding( DockStation station, Dockable dockable );
    
    /**
     * Invoked before <code>dockable</code> is removed from <code>station</code>.
     * @param station the station where the old child will be removed
     * @param dockable the old child
     */
    public void dockableRemoving( DockStation station, Dockable dockable );
    
    /**
     * Invoked after <code>dockable</code> has been added to <code>station</code>.<br>
     * Note: this method is called when the tree of {@link DockElement}s contains
     * the new element, other properties - like the bounds of the
     * {@link Component} of <code>dockable</code> - might not yet be set.
     * @param station the station where the new child was added
     * @param dockable the new child
     */
    public void dockableAdded( DockStation station, Dockable dockable );
    
    /**
     * Invoked after <code>dockable</code> has been removed from
     * <code>station</code>.
     * @param station the station where the old child was removed
     * @param dockable the old child
     */
    public void dockableRemoved( DockStation station, Dockable dockable );
    
    /**
     * Invoked if the visibility of a child has been changed. The visibility
     * has to be implemented in a global scale. Callers may use the class
     * {@link DockableVisibilityManager} to organize the calls in an easy way.
     * @param station the station whose children have changed their visibility
     * @param dockable the {@link Dockable} whose visibility has changed
     * @param visible the new visibility-state
     */
    public void dockableVisibiltySet( DockStation station, Dockable dockable, boolean visible );
    
    /**
     * Called when <code>dockable</code> has been selected. The value of
     * <code>dockable</code> should be the same as {@link DockStation#getFrontDockable()}.
     * @param station the source of the event
     * @param oldSelection the element which was selected before the change, can be <code>null</code>
     * @param newSelection the current value of {@link DockStation#getFrontDockable()}, can be <code>null</code>
     */
    public void dockableSelected( DockStation station, Dockable oldSelection, Dockable newSelection );
}
