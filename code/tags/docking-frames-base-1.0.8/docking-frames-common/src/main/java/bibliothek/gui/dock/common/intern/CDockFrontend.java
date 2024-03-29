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
package bibliothek.gui.dock.common.intern;

import bibliothek.gui.DockController;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.layout.CLayoutChangeStrategy;
import bibliothek.gui.dock.common.mode.CLocationModeManager;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.facile.mode.LocationSettingConverter;
import bibliothek.gui.dock.frontend.Setting;
import bibliothek.gui.dock.support.mode.ModeSettings;
import bibliothek.util.FrameworkOnly;

/**
 * A {@link DockFrontend} that uses {@link CSetting} instead of {@link Setting}.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public class CDockFrontend extends DockFrontend{
    /** access to the internals of a {@link CControl} */
    private CControlAccess control;
    
    /**
     * Creates a new frontend.
     * @param control the owner of this object
     * @param controller the controller to use
     */
    public CDockFrontend( CControlAccess control, DockController controller ){
        super( controller );
        this.control = control;
        setLayoutChangeStrategy( new CLayoutChangeStrategy( control.getOwner() ) );
    }
    
    @Override
    public void remove( Dockable dockable ){
    	if( dockable instanceof CommonDockable ){
    		CDockable cdockable = ((CommonDockable)dockable).getDockable();
    		String key = control.shouldStore( cdockable );
    		if( key != null ){
    			addEmpty( key );
    		}
    	}
    	super.remove( dockable );
    }
    
    @Override
    protected Setting createSetting() {
        CSetting setting = new CSetting();
        CLocationModeManager manager = control.getLocationManager();
        ModeSettings<Location, ?> modeSettings = manager.createSettings( new LocationSettingConverter() );
        setting.setModes( modeSettings );
        return setting;
    }

    @Override
    public Setting getSetting( boolean entry ) {
    	CLocationModeManager manager = control.getLocationManager();
    	
    	CSetting setting = (CSetting)super.getSetting( entry );
        
        ModeSettings<Location, ?> modeSettings = manager.createSettings( new LocationSettingConverter() );
        setting.setModes( modeSettings );
        manager.writeSettings( modeSettings );
        
        control.fillMultiFactories( setting );
        return setting;
    }

    @Override
    public void setSetting( final Setting setting, final boolean entry ) {
        CLocationModeManager manager = control.getLocationManager();
        if( entry ){
            manager.resetWorkingAreaChildren();
        }

        // location manager reads first to be able to change modes of dockables
        manager.readSettings( ((CSetting)setting).getModes() );

        // set new layout as transaction, preventing the manager to react on events
        manager.runLayoutTransaction( new Runnable() {
			public void run(){
				CDockFrontend.super.setSetting( setting, entry );		
			}
        });
        
        manager.ensureBasicModes();
        manager.refresh();
    }
}
