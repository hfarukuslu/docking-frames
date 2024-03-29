package bibliothek.gui.dock.common.mode.station;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.location.CMaximizedLocation;
import bibliothek.gui.dock.common.mode.CLocationMode;
import bibliothek.gui.dock.common.mode.CLocationModeManager;
import bibliothek.gui.dock.common.mode.CMaximizedModeArea;
import bibliothek.gui.dock.common.mode.CNormalModeArea;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.event.DockRelocatorAdapter;
import bibliothek.gui.dock.event.DockRelocatorListener;
import bibliothek.gui.dock.event.SplitDockListener;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.facile.mode.LocationMode;
import bibliothek.gui.dock.facile.mode.LocationModeEvent;
import bibliothek.gui.dock.facile.mode.MaximizedMode;
import bibliothek.gui.dock.facile.mode.MaximizedModeArea;
import bibliothek.gui.dock.facile.mode.ModeArea;
import bibliothek.gui.dock.facile.mode.ModeAreaListener;
import bibliothek.gui.dock.facile.mode.NormalMode;
import bibliothek.gui.dock.facile.mode.NormalModeArea;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.split.SplitDockFullScreenProperty;
import bibliothek.gui.dock.station.split.SplitDockTree;
import bibliothek.gui.dock.support.mode.AffectedSet;
import bibliothek.gui.dock.support.mode.AffectingRunnable;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * Combination of {@link CMaximizedModeArea}, {@link CNormalModeArea} and a
 * {@link SplitDockStation}.
 * @author Benjamin Sigg
 */
public class CSplitDockStationHandle{
	/** the station which is handled by this handle */
	private CStation<SplitDockStation> station;
	
	/** normal-mode */
	private Normal normal = new Normal();
	/** maximized-mode */
	private Maximal maximal = new Maximal();
	
	/** the mode which is accessing this handler */
	private LocationMode normalMode;
	
	/** the mode which is accessing this handler */
	private MaximizedMode<?> maximizedMode;
	
	/** the manager in whose realm this handle is used */
	private CLocationModeManager manager;
	
	/** the listeners added to this {@link ModeArea} */
	private List<ModeAreaListenerWrapper> listeners = new ArrayList<ModeAreaListenerWrapper>();
	
	private SplitDockListener fullScreenListener = new SplitDockListener() {
		public void fullScreenDockableChanged( SplitDockStation station, Dockable oldFullScreen, Dockable newFullScreen ){
			Set<Dockable> affected = new HashSet<Dockable>();
			if( oldFullScreen != null )
				affected.add( oldFullScreen );
			if( newFullScreen != null )
				affected.add( newFullScreen );
			
			ModeAreaListenerWrapper[] array = listeners.toArray( new ModeAreaListenerWrapper[ listeners.size() ] );
			for( ModeAreaListenerWrapper listener : array ){
				listener.fire( affected );
			}
		}
	};
	
	/**
	 * This listener calls {@link MaximizedMode#unmaximize(Dockable, AffectedSet)} if some element is dropped
	 * onto this station.
	 */
	private DockRelocatorListener relocatorListener = new DockRelocatorAdapter() {
		public void drop( DockController controller, Dockable dockable, DockStation station ){
			MaximizedModeArea next = maximizedMode.getNextMaximizeArea( station );
			
			if( next == maximal ){
				manager.runTransaction( new AffectingRunnable() {
					public void run( AffectedSet set ){
						maximizedMode.unmaximize( (DockStation)getStation(), set );		
					}
				});
			}
		}
	};
	
	/**
	 * Creates a new handle.
	 * @param station the station to handle
	 * @param manager the manager in whose realm this handle is used
	 */
	public CSplitDockStationHandle( CStation<SplitDockStation> station, CLocationModeManager manager ){
		this.station = station;
		this.manager = manager;
	}
	
	private void add( ModeAreaListenerWrapper listener ){
		if( listener == null )
			throw new IllegalArgumentException( "listener must not be empty" );
		if( listeners.isEmpty() ){
			station.getStation().addSplitDockStationListener( fullScreenListener );
		}
		listeners.add( listener );
	}
	
	private void remove( ModeAreaListenerWrapper listener ){
		listeners.remove( listener );
		if( listeners.isEmpty() ){
			station.getStation().removeSplitDockStationListener( fullScreenListener );
		}
	}
	
	/**
	 * Gets the station which is managed by this handle.
	 * @return the station
	 */
	public SplitDockStation getStation(){
		return station.getStation();
	}
	
	/**
	 * Returns this as {@link NormalModeArea}
	 * @return a representation of <code>this</code>
	 */
	public CNormalModeArea asNormalModeArea(){
		return normal;
	}
	
	/**
	 * Returns this as {@link MaximizedModeArea}
	 * @return a representation of <code>this</code>
	 */
	public CMaximizedModeArea asMaximziedModeArea(){
		return maximal;
	}
	
	/**
	 * Ensures that <code>dockable</code> is a child of this
	 * station.
	 * @param dockable the element to drop, must not yet be a child of this station
	 * @throws IllegalStateException if <code>dockable</code> already
	 * is a child of this station.
	 */
	public void dropAside( Dockable dockable ){
		if( dockable.getDockParent() == station )
			throw new IllegalStateException( "dockable already a child" );
		
		SplitDockTree tree = getStation().createTree();
		if( tree.getRoot() == null )
			tree.root( dockable );
		else{
			tree.root( tree.horizontal( tree.put( dockable ), tree.unroot() ) );
		}
		getStation().dropTree( tree, false );
	}
	
	/**
	 * A wrapper for a {@link ModeAreaListener}.
	 * @author Benjamin Sigg
	 */
	private class ModeAreaListenerWrapper{
		/** the listener */
		private ModeAreaListener listener;
		/** the area */
		private ModeArea area;
		
		public ModeAreaListenerWrapper( ModeArea area, ModeAreaListener listener ){
			this.area = area;
			this.listener = listener;
		}
		
		/**
		 * Calls {@link ModeAreaListener#internalLocationChange(ModeArea, Set)} with
		 * <code>dockables</code> as set and {@link #area} as area.
		 * @param dockables the set of changed elements
		 */
		public void fire( Set<Dockable> dockables ){
			listener.internalLocationChange( area, dockables );
		}
		
		@Override
		public boolean equals( Object obj ){
			if( obj == this )
				return true;
			if( obj instanceof ModeAreaListenerWrapper ){
				ModeAreaListenerWrapper other = (ModeAreaListenerWrapper)obj;
				return other.area.equals( area ) && other.listener.equals( listener );
			}
			return false;
		}
		
		@Override
		public int hashCode(){
			return area.hashCode() ^ listener.hashCode();
		}
	}
	
	protected class Normal implements CNormalModeArea{
		public void setMode( LocationMode mode ){
			normalMode = mode;
		}
		
		public void addModeAreaListener( ModeAreaListener listener ){
			add( new ModeAreaListenerWrapper( this, listener ) );
		}
		
		public void removeModeAreaListener( ModeAreaListener listener ){
			remove(  new ModeAreaListenerWrapper( this, listener ) );	
		}
		
		public boolean autoDefaultArea() {
			return true;
		}
		
		public void setController( DockController controller ){
			// ignore	
		}
		
		public boolean isNormalModeChild( Dockable dockable ){
			return isChild( dockable ) && getStation().getFullScreen() != dockable;
		}

		public DockableProperty getLocation( Dockable child ){
			return DockUtilities.getPropertyChain( getStation(), child );
		}

		public String getUniqueId(){
			return station.getUniqueId();
		}

		public boolean isChild( Dockable dockable ){
			return dockable.getDockParent() == getStation() && !maximal.isChild( dockable );
		}

		public SplitDockStation getStation(){
			return station.getStation();
		}
		
		public boolean respectWorkingAreas(){
			return true;
		}
		
		public void setLocation( Dockable dockable, DockableProperty location, AffectedSet set ){
			Dockable fullscreen = getStation().getFullScreen();
			if( fullscreen != null ){
				maximal.setMaximized( fullscreen, false, null, set );
			}
			set.add( dockable );
			
			if( dockable.getDockParent() == station.getStation() ){
				if( location != null ){
					getStation().move( dockable, location );
				}
			}
			else{
				if( location != null ){
					if( !getStation().drop( dockable, location ))
						location = null;
				}
				if( location == null ){
					if( !DockUtilities.isAncestor( station.getStation(), dockable )){
						getStation().drop( dockable );
					}
				}
			}
		}
		
		public CLocation getCLocation( Dockable dockable ){
			DockableProperty property = DockUtilities.getPropertyChain( getStation(), dockable );
			return station.getStationLocation().expandProperty( property );
		}
		
		
		public CLocation getCLocation( Dockable dockable, Location location ){
			DockableProperty property = location.getLocation();
			if( property == null )
				return station.getStationLocation();
			
			return station.getStationLocation().expandProperty( property );
		}
		
		public CLocation getBaseLocation(){
			return station.getStationLocation();
		}
		
		public boolean isWorkingArea(){
			return station.isWorkingArea();
		}
	}
	
	protected class Maximal implements CMaximizedModeArea{
		/** the controller in whose realm this area works */
		private DockController controller;
		
		public void addModeAreaListener( ModeAreaListener listener ){
			add(  new ModeAreaListenerWrapper( this, listener ) );
		}
		
		public void removeModeAreaListener( ModeAreaListener listener ){
			remove(  new ModeAreaListenerWrapper( this, listener ) );	
		}
		
		public void setMode( LocationMode mode ){
			if( maximizedMode != null && mode != null )
				throw new IllegalStateException( "handle already in use" );
			maximizedMode = (MaximizedMode<?>)mode;
		}
		
		public void setController( DockController controller ){
			if( this.controller != null ){
				this.controller.getRelocator().removeDockRelocatorListener( relocatorListener );
			}
			this.controller = controller;
			if( controller != null ){
				controller.getRelocator().addDockRelocatorListener( relocatorListener );
			}
		}
		
		public boolean autoDefaultArea() {
			return true;
		}
		
		public LocationMode getUnmaximizedMode(){
			return normalMode;
		}
		
		public void prepareApply( Dockable dockable, AffectedSet affected ){
			CLocationMode normal = manager.getMode( NormalMode.IDENTIFIER );
			if( normal != null ){
				manager.apply( dockable, normal, affected, false );
			}
		}
		
		public void prepareApply( Dockable dockable, Location history, AffectedSet set ){
			boolean remaximize = history != null && history.getLocation() instanceof SplitDockFullScreenProperty; 
			
			if( !remaximize ){
				CLocationMode normal = manager.getMode( NormalMode.IDENTIFIER );
				if( normal != null ){
					manager.apply( dockable, normal, set, false );
				}
			}
		}
		
		public Runnable onApply( LocationModeEvent event ){
			if( event.isDone() )
				return null;
			
			Location location = event.getLocation();
        	Dockable dockable = event.getDockable();
        	
			DockableProperty property = location == null ? null : location.getLocation();
			
			if( event.getMode().getUniqueIdentifier().equals( NormalMode.IDENTIFIER )){
				// try to set the mode prematurely
		        if( property != null ){
		        	if( property.getSuccessor() == null ){
		            	CLocationMode last = manager.getCurrentMode( dockable );
		            	CLocationMode secondLast = manager.getPreviousMode( dockable );
		            	
		                if( last != null && secondLast != null ){
		                	if( NormalMode.IDENTIFIER.equals( secondLast.getUniqueIdentifier() ) &&
		                			MaximizedMode.IDENTIFIER.equals( last.getUniqueIdentifier() )){
		                    
		                		MaximizedModeArea area = maximizedMode.get( location.getRoot() );
		                		
		                        if( area == this ){
		                            area.setMaximized( dockable, false, null, event.getAffected() );
		                            event.done();
		                            return null;
		                        }
		                    }
		                }
		            }
		        }
		    }
			
	        // if the element is about to become a child of this station, ensure
	        // this station does not show a maximized element
	        if( location != null && getMaximized() != null ){
	        	Map<ExtendedMode, DockStation> roots = manager.getRepresentations( location.getRoot() );
	        	for( DockStation station : roots.values() ){
	        		if( DockUtilities.isAncestor( getStation(), station )){
	        			maximizedMode.unmaximize( this, event.getAffected() );	
	        			break;
	        		}
	        	}
	        }
	        
	        // if this station currently shows dockable as maximized element, ensure it is no longer maximized
			if( maximizedMode != null && event.getMode().getUniqueIdentifier().equals( NormalMode.IDENTIFIER )){
				MaximizedModeArea area = maximizedMode.getMaximizeArea( dockable );
				if( area == this ){
					maximizedMode.unmaximize( dockable, event.getAffected() );
				}
			}
	        
	        return null;
		}
		
		public Runnable onApply( final LocationModeEvent event, final Dockable replacement ){
			if( event.isDone() )
				return null;
			
			if( !event.getMode().getUniqueIdentifier().equals( NormalMode.IDENTIFIER )){
				maximizedMode.unmaximize( getStation().getFullScreen(), event.getAffected() );

		        return new Runnable() {
					public void run(){
						if( replacement != null && replacement.getDockParent() != null ){
							maximizedMode.maximize( Maximal.this, replacement, event.getAffected() );
						}		
					}
				};
			}
			
			return null;
		}
		
		public String getUniqueId(){
			return station.getUniqueId();
		}
		
		public boolean isChild( Dockable dockable ){
			return getStation().getFullScreen() == dockable;
		}
		
		public SplitDockStation getStation(){
			return station.getStation();
		}
		
		public boolean respectWorkingAreas(){
			return false;
		}
		
		public Dockable[] getMaximized(){
			Dockable dockable = getStation().getFullScreen();
			if( dockable == null ){
				return null;
			}
			return new Dockable[]{ dockable };
		}

		public void setMaximized( Dockable dockable, boolean maximized, Location location, AffectedSet set ){
			SplitDockStation station = getStation();
			
			if( !maximized ){
				if( station.getFullScreen() != null && DockUtilities.isAncestor( station.getFullScreen(), dockable )){
					station.setFullScreen( null );
				}
			}
			else{
				DockableProperty property = location == null ? null : location.getLocation();
				
				if( property instanceof SplitDockFullScreenProperty ){
					if( getMaximized() != null ){
						if( getStation().drop( dockable, property ) ){
			        		return;
			        	}
					}
				}
				
				if( dockable.getDockParent() == station ){
					station.setFullScreen( dockable );
		        }
		        else{
		            if( dockable.getDockParent() != null )
		                dockable.getDockParent().drag( dockable );
	
		            dropAside( dockable );
		            station.setFullScreen( dockable );
		        }
			}
			
			set.add( dockable );
		}
		
		public boolean isRepresenting( DockStation station ){
			return station == CSplitDockStationHandle.this.station.getStation();
		}
		
		public CLocation getCLocation( Dockable dockable ){
			CLocation stationLocation = station.getStationLocation();
			DockableProperty property = DockUtilities.getPropertyChain( getStation(), dockable );
			if( property != null ){
				property = property.getSuccessor();
			}
			
			CMaximizedLocation result = new CMaximizedLocation( stationLocation.findRoot() );
			if( property != null ){
				return result.expandProperty( property );
			}
			else{
				return result;
			}
		}
		
		public CLocation getCLocation( Dockable dockable, Location location ){
			return getCLocation( dockable );
		}
	}
}
