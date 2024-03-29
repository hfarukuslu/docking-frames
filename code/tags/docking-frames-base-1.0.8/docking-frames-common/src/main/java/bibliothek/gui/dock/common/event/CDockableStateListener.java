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
package bibliothek.gui.dock.common.event;

import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * A listener added to a {@link CDockable}, this listener will get informed
 * about state changes of {@link CDockable}.
 * @author Benjamin Sigg
 * @see CDockablePropertyListener
 */
@Todo( priority=Priority.MINOR, compatibility=Compatibility.BREAK_MAJOR, target=Version.VERSION_1_1_0, 
		description="Support all the ExtendedModes that exist. Make an abstract subclass which offers the current methods." )
public interface CDockableStateListener {

    /**
     * Called when the {@link CDockable#isVisible() visibility}-property
     * has changed. Please read the notes of {@link CDockable#isVisible()} to
     * learn more about the exact meaning of visibility in the context of a 
     * {@link CDockable}.
     * @param dockable the source of the event
     */
    public void visibilityChanged( CDockable dockable );
    
    /**
     * Called when the <code>dockable</code> has been minimized.
     * @param dockable the source of the event
     */
    public void minimized( CDockable dockable );
 
    /**
     * Called when the <code>dockable</code> has been maximized.
     * @param dockable the source of the event
     */
    public void maximized( CDockable dockable );
    
    /**
     * Called when the <code>dockable</code> has been normalized.
     * @param dockable the source of the event
     */
    public void normalized( CDockable dockable );
    
    /**
     * Called when the <code>dockable</code> has been externalized.
     * @param dockable the source of the event
     */
    public void externalized( CDockable dockable );
}
