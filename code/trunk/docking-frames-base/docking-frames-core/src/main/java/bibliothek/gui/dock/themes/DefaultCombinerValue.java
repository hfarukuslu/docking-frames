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

package bibliothek.gui.dock.themes;

import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.support.CombinerSource;
import bibliothek.gui.dock.station.support.CombinerTarget;
import bibliothek.gui.dock.themes.basic.BasicCombiner;

/**
 * A <code>CombinerWrapper</code> encloses a {@link Combiner} and uses
 * the combiner as delegate. If the wrapper has no delegate, it uses
 * the {@link DockUI} to get a combiner from the current {@link DockTheme}.
 * @author Benjamin Sigg
 *
 */
public class DefaultCombinerValue extends StationThemeItemValue<Combiner> implements CombinerValue, Combiner {
	/**
	 * Creates a new value.
	 * @param id the identifier of this value, used to read a resource from the {@link ThemeManager}
	 * @param station the owner of this object
	 */
    public DefaultCombinerValue( String id, DockStation station ){
    	super( id, KIND_COMBINER, ThemeManager.COMBINER_TYPE, station );
    }

    public CombinerTarget prepare( CombinerSource source, boolean force ){
    	Combiner combiner = get();
    	if( combiner == null ){
    		if( force ){
    			combiner = new BasicCombiner();
    		}
    		else{
    			return null;
    		}
    	}
    	
    	return combiner.prepare( source, force );
    }

    public Dockable combine( CombinerSource source, CombinerTarget target ){
    	Combiner combiner = get();
    	if( combiner == null ){
   			combiner = new BasicCombiner();
    	}

        return combiner.combine( source, target );
    }
}
