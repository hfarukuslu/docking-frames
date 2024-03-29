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
package bibliothek.extension.gui.dock.theme.eclipse.displayer;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.border.Border;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseThemeConnector.TitleBar;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.BorderedComponent;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.InvisibleTab;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.InvisibleTabPane;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.TabPainter;
import bibliothek.extension.gui.dock.util.ReverseCompoundBorder;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.displayer.DockableDisplayerHints;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.DockableDisplayerListener;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * A {@link DockableDisplayer} which is not able to show the {@link DockTitle} of
 * its {@link Dockable}. This displayer exchanges automatically its border
 * using the global {@link TabPainter} delivered through the {@link DockProperties}
 * and the key {@link EclipseTheme#TAB_PAINTER}.
 * @author Janni Kovacs
 */
public class NoTitleDisplayer extends JPanel implements DockableDisplayer, InvisibleTabPane, BorderedComponent {
	private Dockable dockable;
	private DockController controller;
	private DockStation station;
	private DockTitle title;
	private Location location;
	
	private PropertyValue<TabPainter> painter;
	
	private boolean defaultBorderHint;
	private Boolean borderHint;
	private DockableDisplayerHints hints;
	
	private boolean bordered;
	private boolean respectHints;
	
	private TitleBarObserver observer;
	
	private List<DockableDisplayerListener> listeners = new ArrayList<DockableDisplayerListener>();
	
	private InvisibleTab invisibleTab;
	
	private Border innerBorder;
	private Border outerBorder;
	
	public NoTitleDisplayer( DockStation station, Dockable dockable, TitleBar bar ){
		setLayout( new GridLayout( 1, 1, 0, 0 ) );
		setOpaque( false );
		
		bordered = bar == TitleBar.NONE_BORDERED || bar == TitleBar.NONE_HINTED_BORDERED;
		respectHints = bar == TitleBar.NONE_HINTED || bar == TitleBar.NONE_HINTED_BORDERED;
		
		observer = new TitleBarObserver( dockable, bar ){
			@Override
			protected void invalidated(){
				for( DockableDisplayerListener listener : listeners() ){
					listener.discard( NoTitleDisplayer.this );
				}
			}
		};
		
		if( respectHints ){
		    hints = new DockableDisplayerHints(){
		        public void setShowBorderHint( Boolean border ) {
		            borderHint = border;
		            updateFullBorder();
		        }
		    };
		}
		
		setStation( station );
        setDockable( dockable );
        setBorder( null );
        
        defaultBorderHint = bordered;
        
		painter = new PropertyValue<TabPainter>( EclipseTheme.TAB_PAINTER ){
			@Override
			protected void valueChanged( TabPainter oldValue, TabPainter newValue ) {
				updateFullBorder();
				updateInvisibleTab();
			}
		};
		
		updateFullBorder();
		updateInvisibleTab();
		
		setFocusCycleRoot( true );
		setFocusTraversalPolicy( new LayoutFocusTraversalPolicy() );
	}
	
	/**
	 * Gets the current hint whether a border should be shown or not.
	 * @return the current hint
	 */
	protected boolean getBorderHint(){
	    if( borderHint != null )
	        return borderHint.booleanValue();
	    
	    return defaultBorderHint;
	}
	
	/**
	 * Exchanges the border of this component, using the current
	 * {@link EclipseTheme#TAB_PAINTER} to determine the new border.
	 */
	public void updateFullBorder(){
	    if( (bordered || respectHints) && painter != null ){
    	    TabPainter painter = this.painter.getValue();
    	    
            if( controller == null || painter == null || dockable == null ){
                outerBorder = null;
            }
            else{
                if( hints == null || getBorderHint() ){
                    outerBorder = painter.getFullBorder( this, controller, dockable );
                }
                else{
                    outerBorder = null;
                }
            }
            updateBorder();
	    }
	}
	
	public void setBorder( Dockable dockable, Border border ){
		if( dockable != this.dockable )
			throw new IllegalArgumentException( "unknown dockable: " + dockable );
		
		if( bordered || respectHints ){
			if( hints == null || getBorderHint() ){
				innerBorder = border;
				updateBorder();		
			}
		}
	}
	
	private void updateBorder(){
		if( innerBorder == null && outerBorder == null )
			setBorder( null );
		else if( innerBorder == null )
			setBorder( outerBorder );
		else if( outerBorder == null )
			setBorder( innerBorder );
		else
			setBorder( new ReverseCompoundBorder( outerBorder, innerBorder ) );
	}

	protected void updateInvisibleTab(){
		if( invisibleTab != null ){
			invisibleTab.setController( null );
			invisibleTab = null;
		}
		
		if( dockable != null && painter != null ){
			TabPainter painter = this.painter.getValue();
			if( painter != null ){
				invisibleTab = painter.createInvisibleTab( this, dockable );
				invisibleTab.setController( getController() );
			}
		}
	}
	
	public TabPlacement getTabPlacement(){
		if( controller == null )
			return null;
		return controller.getProperties().get( StackDockStation.TAB_PLACEMENT );
	}
	
	public Insets getDockableInsets() {
	    Insets insets = getInsets();
	    if( insets == null )
	        return new Insets( 0,0,0,0 );
	    return insets;
	}
	
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        paintBorder(g);
    }
	
	public Component getComponent(){
		return this;
	}

	public DockController getController(){
		return controller;
	}

	public Dockable getDockable(){
		return dockable;
	}

	public DockStation getStation(){
		return station;
	}

	public DockTitle getTitle(){
		return title;
	}

	public Location getTitleLocation(){
		return location;
	}
	
	public void addDockableDisplayerListener( DockableDisplayerListener listener ){
		listeners.add( listener );
	}
	
	public void removeDockableDisplayerListener( DockableDisplayerListener listener ){
		listeners.remove( listener );	
	}
	
	/**
	 * Gets all listeners currently known to this displayer.
	 * @return the list of listeners
	 */
	protected DockableDisplayerListener[] listeners(){
		return listeners.toArray( new DockableDisplayerListener[ listeners.size() ] );
	}
	
	public Dockable getSelectedDockable(){
		return dockable;
	}

	public void setController( DockController controller ){
		this.controller = controller;
		if( painter != null )
		    painter.setProperties( controller == null ? null : controller.getProperties() );
		
		if( observer != null )
			observer.setController( controller );
		
		if( invisibleTab != null )
			invisibleTab.setController( controller );
		
		updateFullBorder();
	}

	public void setDockable( Dockable dockable ){
	    if( this.dockable != null )
	        this.dockable.configureDisplayerHints( null );
	    
	    if( invisibleTab != null ){
	    	invisibleTab.setController( null );
	    	invisibleTab = null;
	    }
	    
		this.dockable = dockable;
				
		if( observer != null ){
			observer.setDockable( dockable );
		}
		
		removeAll();
		if( dockable != null ){
			add( dockable.getComponent() );
			dockable.configureDisplayerHints( hints );
		}
		
		updateFullBorder();
		updateInvisibleTab();
	}

	public void setStation( DockStation station ){
		this.station = station;
	}

	public void setTitle( DockTitle title ){
		this.title = title;
	}

	public void setTitleLocation( Location location ){
		this.location = location;
	}

	public boolean titleContains( int x, int y ){
		return false;
	}
}
