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
package bibliothek.gui.dock.common.intern;

import java.awt.Dimension;

import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.ColorMap;
import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.FontMap;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.event.CDockablePropertyListener;
import bibliothek.gui.dock.common.event.CDockableStateListener;
import bibliothek.gui.dock.common.event.CDoubleClickListener;
import bibliothek.gui.dock.common.event.CFocusListener;
import bibliothek.gui.dock.common.event.CKeyboardListener;
import bibliothek.gui.dock.common.event.CVetoClosingEvent;
import bibliothek.gui.dock.common.event.CVetoClosingListener;
import bibliothek.gui.dock.common.intern.action.CloseActionSource;
import bibliothek.gui.dock.common.layout.RequestDimension;
import bibliothek.gui.dock.common.mode.CLocationModeManager;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.util.FrameworkOnly;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * A basic element representing some {@link java.awt.Component} and a wrapper
 * around a {@link Dockable}.<br>
 * <b>Note:</b> This interface is not intended to be implemented by clients. 
 * Clients should either extend the class {@link AbstractCDockable} or use
 * one of {@link DefaultSingleCDockable} or {@link DefaultMultipleCDockable}.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public interface CDockable {
	/**
	 * Key for an action of {@link #getAction(String)}. The action behind this
	 * key should call {@link #setExtendedMode(ExtendedMode)}
	 * with an argument of {@link ExtendedMode#MINIMIZED}.
	 */
	public static final String ACTION_KEY_MINIMIZE = "cdockable.minimize";

	/**
	 * Key for an action of {@link #getAction(String)}. The action behind this
	 * key should call {@link #setExtendedMode(ExtendedMode)}
	 * with an argument of {@link ExtendedMode#MAXIMIZED}.
	 */
	public static final String ACTION_KEY_MAXIMIZE = "cdockable.maximize";

	/**
	 * Key for an action of {@link #getAction(String)}. The action behind this
	 * key should call {@link #setExtendedMode(ExtendedMode)}
	 * with an argument of {@link ExtendedMode#NORMALIZED}.
	 */
	public static final String ACTION_KEY_NORMALIZE = "cdockable.normalize";

	/**
	 * Key for an action of {@link #getAction(String)}. The action behind this
     * key should call {@link #setExtendedMode(ExtendedMode)}
     * with an argument of {@link ExtendedMode#EXTERNALIZED}.
     */
	public static final String ACTION_KEY_EXTERNALIZE = "cdockable.externalize";
	
	/**
	 * Key for an action of {@link #getAction(String)}. The action behind this
	 * key should call {@link #setExtendedMode(ExtendedMode)} with
	 * an argument of {@link ExtendedMode#NORMALIZED}.
	 */
	public static final String ACTION_KEY_UNEXTERNALIZE = "cdockable.unexternalize";
	
	/**
	 * Key for an action of {@link #getAction(String)}. The action behind this
	 * key should call {@link #setExtendedMode(ExtendedMode)} with
	 * an argument of {@link ExtendedMode#EXTERNALIZED}.
	 */
	public static final String ACTION_KEY_UNMAXIMIZE_EXTERNALIZED = "cdockable.unmaximize_externalized";

	/**
	 * Key for an action of {@link #getAction(String)}. The action behind this
	 * key should call {@link #setVisible(boolean)} with the argument
	 * <code>false</code>.
     */
	public static final String ACTION_KEY_CLOSE = "cdockable.close";
	
	/**
	 * Adds a state listener to this dockable, the listener will be informed of
	 * changes of this dockable.
	 * @param listener the new listener
	 */
	public void addCDockableStateListener( CDockableStateListener listener );
	
    /**
     * Adds a property listener to this dockable, the listener will be informed of
     * changes of this dockable.
     * @param listener the new listener
     */
	public void addCDockablePropertyListener( CDockablePropertyListener listener );
	
	/**
	 * Removes a state listener from this dockable.
	 * @param listener the listener to remove
	 */
	public void removeCDockableStateListener( CDockableStateListener listener );

	/**
     * Removes a property listener from this dockable.
     * @param listener the listener to remove
     */
	public void removeCDockablePropertyListener( CDockablePropertyListener listener );
	
	/**
	 * Adds a focus listener to this dockable. The focus listener gets informed
	 * when this dockable gains or loses the focus.
	 * @param listener the new listener
	 */
	public void addFocusListener( CFocusListener listener );
	
	/**
	 * Removes a focus listener from this dockable.
	 * @param listener the listener to remove
	 */
	public void removeFocusListener( CFocusListener listener );
	
	/**
	 * Adds a keyboard listener to this dockable. The listener gets informed
	 * when a key is pressed or released on this dockable.
	 * @param listener the new listener
	 */
	public void addKeyboardListener( CKeyboardListener listener );
	
    /**
     * Removes a listener from this dockable.
     * @param listener the listener to remove
     */
	public void removeKeyboardListener( CKeyboardListener listener );
	
	/**
	 * Adds a new listener to this dockable. The listener gets informed
	 * when the mouse is clicked twice on this dockable.
	 * @param listener the new listener
	 */
	public void addDoubleClickListener( CDoubleClickListener listener );
	
	/**
	 * Removes a listener from this dockable.
	 * @param listener the listener to remove
	 */
	public void removeDoubleClickListener( CDoubleClickListener listener );
	
	/**
	 * Adds a veto-listener to this dockable, the listener will be informed before this
	 * dockable gets closed and can issue a veto. The listener will receive a
	 * {@link CVetoClosingEvent} which contains only this {@link CDockable} (even if
	 * other dockables are closed at the same time).<br>
	 * {@link CVetoClosingListener}s added to the {@link CControl} are invoked before listeners that
	 * are added to a {@link CDockable}.
	 * @param listener the new listener
	 * @see CControl#addVetoClosingListener(CVetoClosingListener)
	 */
	public void addVetoClosingListener( CVetoClosingListener listener );
	
	/**
	 * Removes <code>listener</code> from this <code>CDockable</code>.
	 * @param listener the listener to remove
	 */
	public void removeVetoClosingListener( CVetoClosingListener listener );
	
	/**
	 * Tells whether this <code>CDockable</code> can be minimized by the user.
	 * @return <code>true</code> if this element can be minimized
	 */
	public boolean isMinimizable();
	
	/**
	 * Tells whether this <code>CDockable</code> can be maximized by the user.
	 * @return <code>true</code> if this element can be maximized
	 */
	public boolean isMaximizable();
	
	/**
	 * Tells whether this <code>CDockable</code> can be externalized by the user.
	 * @return <code>true</code> if this element can be externalized
	 */
	public boolean isExternalizable();
	
	/**
	 * Tells whether this <code>CDockable</code> can be combined with another
	 * <code>Dockable</code> to create a stack.
	 * @return <code>true</code> if this element can be combined with
	 * another <code>Dockable</code>, normally <code>true</code> should be the answer.
	 */
	public boolean isStackable();
	
	/**
	 * Tells whether this <code>CDockable</code> can be closed by the user. A close-button
	 * has to be provided by the <code>CDockable</code> itself. The best way to do that is
	 * to instantiate a {@link CloseActionSource} and include this source
	 * in the array that is returned by {@link CommonDockable#getSources()}.
	 * @return <code>true</code> if this element can be closed
	 */
	public boolean isCloseable();
	
	/**
	 * Tells whether the height of this <code>CDockable</code> should remain the same when
	 * its parent changes the size. This has only effect if the parent can
	 * choose the size of its children. A lock is no guarantee for staying
	 * with the same size, the user still can resize this <code>CDockable</code>.
	 * @return <code>true</code> if the height of this <code>CDockable</code> should remain
	 * the same during resize events of the parent.
	 */
	public boolean isResizeLockedVertically();
	
	/**
     * Tells whether the width of this <code>CDockable</code> should remain the same when
     * its parent changes the size. This has only effect if the parent can
     * choose the size of its children. A lock is no guarantee for staying
     * with the same size, the user still can resize this <code>CDockable</code>.
     * @return <code>true</code> if the width of this <code>CDockable</code> should remain
     * the same during resize events of the parent.
     */
	public boolean isResizeLockedHorizontally();
	
	/**
	 * Gets the preferred size of this {@link CDockable}. The preferred size
	 * will be used to resize this <code>CDockable</code> when 
	 * {@link CControl#handleResizeRequests()} is called. There are no guarantees
	 * that the request can be granted, or will be handled at all.<br>
	 * Calling this method should delete the request, so calling this method
	 * twice should have the effect, that the second time <code>null</code> is
	 * returned.
	 * @return the next requested size or <code>null</code>
	 */
	public RequestDimension getAndClearResizeRequest();
	
	/**
	 * Shows or hides this <code>CDockable</code>. If this <code>CDockable</code> is not visible and
	 * is made visible, then the framework tries to set its location at
	 * the last known position.<br>
	 * Subclasses should call {@link CControlAccess#show(CDockable)} or
	 * {@link CControlAccess#hide(CDockable)}.
	 * @param visible the new visibility state
	 * @see #isVisible()
	 * @throws IllegalStateException if this dockable can't be made visible
	 */
	public void setVisible( boolean visible );
	
	/**
	 * Tells whether this <code>CDockable</code> is currently visible or not. Visibility
	 * means that this <code>CDockable</code> is in the tree structure of DockingFrames. Being
	 * in the structure does not imply being visible on the screen. If some
	 * <code>JFrame</code> is not shown, or some <code>DockStation</code> not
	 * properly added to a parent component, then a visible <code>CDockable</code> can
	 * be invisible for the user.<br>
	 * Subclasses should return the result of {@link CControlAccess#isVisible(CDockable)}.
	 * @return <code>true</code> if this <code>CDockable</code> can be accessed by the user
	 * through a graphical user interface.
	 */
	public boolean isVisible();
	
	/**
	 * Sets the location of this <code>CDockable</code>. If this <code>CDockable</code> is visible, than
	 * this method will take immediately effect. Otherwise the location will be
	 * stored in a cache and read as soon as this <code>CDockable</code> is made visible.<br>
	 * Note that the location can only be seen as a hint, the framework tries
	 * to fit the location as good as possible, but there are no guarantees.<br>
	 * Subclasses should call {@link CControlAccess#getLocationManager()} and 
	 * {@link CLocationModeManager#setLocation(bibliothek.gui.Dockable, CLocation)}.
	 * @param location the new location, <code>null</code> is possible, but
	 * will not move the <code>CDockable</code> immediately
	 * @see #getBaseLocation()
	 */
	public void setLocation( CLocation location );
	
	/**
	 * Gets the location of this <code>CDockable</code>. If this <code>CDockable</code> is visible, then
	 * a location will always be returned. Otherwise a location will only
	 * be returned if it just was set using {@link #setLocation(CLocation)}.
	 * @return the location or <code>null</code>
	 */
	public CLocation getBaseLocation();
	
    /**
     * Sets how and where this <code>CDockable</code> should be shown. Conflicts with
     * {@link #isExternalizable()}, {@link #isMaximizable()} and {@link #isMinimizable()}
     * will just be ignored. Implementations should call {@link CLocationModeManager#setMode(Dockable, ExtendedMode)}.
     * @param extendedMode the size and location
     */
    public void setExtendedMode( ExtendedMode extendedMode );
	
    /**
     * Gets the size and location of this <code>CDockable</code>. Implementations should
     * return {@link CLocationModeManager#getMode(Dockable)}.
     * @return the size and location or <code>null</code> if this <code>CDockable</code>
     * is not part of an {@link CControl}.
     */
    public ExtendedMode getExtendedMode();
    
    /**
     * Sets the parent of this <code>CDockable</code>. This method can be called by the client
     * or indirectly through {@link #setLocation(CLocation)}.
     * @param area the new parent or <code>null</code>
     */
    public void setWorkingArea( CStation<?> area );
    
    /**
     * Gets the parent of this <code>CDockable</code>, this should be the same as
     * set by the last call of {@link #setWorkingArea(CStation)}.
     * @return the parent or <code>null</code>
     */
    public CStation<?> getWorkingArea();
    
    /**
     * Sets the size of this <code>CDockable</code> when this <code>CDockable</code> is minimized and
     * on a popup window.
     * @param size the size
     */
    public void setMinimizedSize( Dimension size );
    
    /**
     * Gets the size which is used when this <code>CDockable</code> is minimzed and
     * on a popup window. If a value below 0 is set, then the default size
     * is used.
     * @return the size
     */
    public Dimension getMinimizedSize();
    
    /**
     * Sets whether this <code>CDockable</code> should remain visible when minimized
     * and without focus.
     * @param hold whether to remain visible
     */
    public void setMinimizedHold( boolean hold );
    
    /**
     * Tells whether this <code>CDockable</code> remains visible when minimized and 
     * without focus.
     * @return <code>true</code> if this remains visible, <code>false</code>
     * otherwise 
     */
    public boolean isMinimizedHold();
    
    /**
     * Tells whether this <code>CDockable</code> shows its title or not. Note that some
     * {@link DockTheme}s might override this setting.
     * @return <code>true</code> if the title is shown, <code>false</code>
     * otherwise.
     */
    public boolean isTitleShown();
    
    /**
     * Tells whether a single tab should be shown for this <code>CDockable</code>. Some
     * {@link DockTheme}s might ignore this setting.
     * @return <code>true</code> if a single tab should be shown,
     * <code>false</code> if not
     */
    public boolean isSingleTabShown();
    
	/**
	 * Gets the intern representation of this <code>CDockable</code>.
	 * @return the intern representation.
	 */
	public CommonDockable intern();
	
	/**
	 * Sets the {@link CControl} which is responsible for this <code>CDockable</code>. Subclasses
	 * must call {@link CControlAccess#link(CDockable, CDockableAccess)} to grant
	 * the <code>CControl</code> access to the internal properties of this
	 * {@link CDockable}. <code>link</code> can also be used to revoke access.
	 * @param control the new control or <code>null</code>
	 */
	public void setControl( CControlAccess control );
	
	/**
	 * Gets the control which is responsible for this dockable. Clients
	 * should not use this method unless they know exactly what they are doing.
	 * @return the control
	 */
	@Todo( priority=Priority.MINOR, compatibility=Compatibility.BREAK_MINOR, target=Version.VERSION_1_1_0,
			description="Return CControl instead of CControlAccess" )
	public CControlAccess getControl();
	
	/**
	 * Gets an action which is not added to the title by this {@link CDockable}
	 * put by another module.
	 * @param key the name of the action
	 * @return an action or <code>null</code>
	 */
	public CAction getAction( String key );
	
	/**
	 * Gets a mutable map of colors. Clients can put colors into this map, and
	 * the colors will be presented on the screen by various effects.
	 * @return the map, this has always to be the same object
	 */
	public ColorMap getColors();
	
	/**
	 * Gets a mutable map of fonts. Clients can put fonts into this map, and
	 * the fonts will be presented on the screen through various effects.
	 * @return the map, this has always to be the same object
	 */
	public FontMap getFonts();
}
