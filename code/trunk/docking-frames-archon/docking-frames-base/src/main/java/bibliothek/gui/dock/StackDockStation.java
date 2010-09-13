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

package bibliothek.gui.dock;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.displayer.DockableDisplayerHints;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.event.DockStationListener;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.AbstractDockableStation;
import bibliothek.gui.dock.station.DisplayerCollection;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.DockableDisplayerListener;
import bibliothek.gui.dock.station.OverpaintablePanel;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.stack.DefaultStackDockComponent;
import bibliothek.gui.dock.station.stack.StackDockComponent;
import bibliothek.gui.dock.station.stack.StackDockComponentFactory;
import bibliothek.gui.dock.station.stack.StackDockComponentParent;
import bibliothek.gui.dock.station.stack.StackDockComponentRepresentative;
import bibliothek.gui.dock.station.stack.StackDockProperty;
import bibliothek.gui.dock.station.stack.StackDockStationFactory;
import bibliothek.gui.dock.station.stack.TabContentFilterListener;
import bibliothek.gui.dock.station.stack.TabContent;
import bibliothek.gui.dock.station.stack.tab.TabContentFilter;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.DisplayerFactoryWrapper;
import bibliothek.gui.dock.station.support.DockableVisibilityManager;
import bibliothek.gui.dock.station.support.PlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItemAdapter;
import bibliothek.gui.dock.station.support.PlaceholderListItemConverter;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.support.StationPaintWrapper;
import bibliothek.gui.dock.station.support.PlaceholderList.Filter;
import bibliothek.gui.dock.station.support.PlaceholderList.Level;
import bibliothek.gui.dock.title.ControllerTitleFactory;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.property.ConstantPropertyFactory;
import bibliothek.util.Path;

/**
 * On this station, only one of many children is visible. The other children
 * are hidden behind the visible child. There are some buttons where the
 * user can choose which child is visible. This station behaves like
 * a {@link JTabbedPane}.<br>
 * This station tries to register a {@link DockTitleFactory} to its 
 * {@link DockController} with the key {@link #TITLE_ID}.
 * @author Benjamin Sigg
 */
public class StackDockStation extends AbstractDockableStation implements StackDockComponentParent{
    /** The id of the titlefactory which is used by this station */
    public static final String TITLE_ID = "stack";
    
    /** Key used to read the current {@link StackDockComponentFactory} */
    public static final PropertyKey<StackDockComponentFactory> COMPONENT_FACTORY =
        new PropertyKey<StackDockComponentFactory>( "stack dock component factory" );
    
    /** Key for setting the side at which the tabs appear in relation to the selected dockable */
    public static final PropertyKey<TabPlacement> TAB_PLACEMENT = 
    	new PropertyKey<TabPlacement>( "stack dock station tab side",
    			new ConstantPropertyFactory<TabPlacement>( TabPlacement.TOP_OF_DOCKABLE ), true );

    /** Key for setting the {@link TabContentFilter} */
    public static final PropertyKey<TabContentFilter> TAB_CONTENT_FILTER =
    	new PropertyKey<TabContentFilter>( "stack dock tab content filter" );
    
    /** A list of all children */
    private PlaceholderList<StationChildHandle> dockables = new PlaceholderList<StationChildHandle>();
    
    /**
     * A list of {@link MouseInputListener MouseInputListeners} which are
     * registered at this station.
     */
    private List<MouseInputListener> mouseInputListeners = new ArrayList<MouseInputListener>();
    
    /** A manager for firing events if a child changes its visibility-state */
    private DockableVisibilityManager visibility;
    
    /** A paint to draw lines */
    private StationPaintWrapper paint = new StationPaintWrapper();
    
    /** A factory to create {@link DockableDisplayer} */
    private DisplayerFactoryWrapper displayerFactory = new DisplayerFactoryWrapper();
    
    /** The set of displayers shown on this station */
    private DisplayerCollection displayers;
    
    /** The {@link Dockable} which is currently moved or dropped */
    private Dockable dropping;
    
    /** Tells whether some lines have to be painted, or not */
    private boolean draw = false;
    
    /** The preferred location where {@link #dropping} should be added */
    private Insert insert;
    
     /** The graphical representation of this station */
    private Background background;
    
    /** The panel where components are added */
    private JComponent panel;
    
    /** A Component which shows two or more children of this station */
    private StackDockComponent stackComponent;
    
    /** Responsible for updating a {@link DockElementRepresentative} that covers the empty areas of {@link #stackComponent} */
    private StackDockComponentRepresentative stackComponentRepresentative;
    
    /** The current component factory */
    private PropertyValue<StackDockComponentFactory> stackComponentFactory;
    
    /** Where to put tabs */
    private PropertyValue<TabPlacement> tabPlacement;
    
    /** The version of titles which should be used for this station */
    private DockTitleVersion title;
    
    /** A listener observing the children for changes of their icon or titletext */
    private Listener listener = new Listener();
    
    /** strategy for selecting placeholders */
    private PropertyValue<PlaceholderStrategy> placeholderStrategy = new PropertyValue<PlaceholderStrategy>( PlaceholderStrategy.PLACEHOLDER_STRATEGY ) {
		@Override
		protected void valueChanged( PlaceholderStrategy oldValue, PlaceholderStrategy newValue ){
			dockables.setStrategy( newValue );
		}
	};
	
	/** filter for setting appearance of tabs */
	private PropertyValue<TabContentFilter> tabContentFilter = new PropertyValue<TabContentFilter>( TAB_CONTENT_FILTER ) {
		@Override
		protected void valueChanged( TabContentFilter oldValue, TabContentFilter newValue ){
			if( oldValue != newValue ){
				if( oldValue != null ){
					oldValue.removeListener( tabContentFilterListener );
					oldValue.uninstall( StackDockStation.this );
				}
				if( newValue != null ){
					newValue.install( StackDockStation.this );
					newValue.addListener( tabContentFilterListener );
				}
				tabContentFilterListener.contentChanged();
			}
		}
	};
    
	/** a listener to {@link #tabContentFilter} */
	private TabContentFilterListener tabContentFilterListener = new TabContentFilterListener() {
		public void contentChanged(){
			int count = getDockableCount();
			for( int i = 0; i < count; i++ ){
				updateContent( i );
			}
		}
		
		public void contentChanged( StackDockComponent component ){
			// ignore	
		}
		
		public void contentChanged( StackDockStation station ){
			if( StackDockStation.this == station ){
				contentChanged();
			}
		}
		
		public void contentChanged( Dockable dockable ){
			if( dockable.getDockParent() == StackDockStation.this ){
				int index = indexOf( dockable );
				if( index >= 0 ){
					updateContent( index );
				}
			}
		}
	};
	
    /**
     * A listener added to the parent of this station. The listener ensures
     * that the visibility-state is always correct. 
     */
    private VisibleListener visibleListener;
    
    /**
     * The dockable which was or is currently selected.
     */
    private Dockable lastSelectedDockable = null;
    
    /**
     * Constructs a new StackDockStation
     */
    public StackDockStation(){
    	this( null );
    }
    
    /**
     * Constructs a new station and sets the theme.
     * @param theme the theme of the station, may be <code>null</code>
     */
    public StackDockStation( DockTheme theme ){
    	super( theme );
    	init();
    }
    
    /**
     * Creates a new station.
     * @param theme the theme of this station, can be <code>null</code>
     * @param init <code>true</code> if the fields of this object should
     * be initialized, <code>false</code> otherwise. If <code>false</code>,
     * then the subclass has to call {@link #init()} exactly once.
     */
    protected StackDockStation( DockTheme theme, boolean init ){
    	super( theme );
    	if( init )
    		init();
    }
    
    /**
     * Initializes the fields of this object, has to be called exactly once.
     */
    protected void init(){
        visibleListener = new VisibleListener();
        visibility = new DockableVisibilityManager( listeners );
        
        displayers = new DisplayerCollection( this, displayerFactory );
        displayers.addDockableDisplayerListener( new DockableDisplayerListener(){
        	public void discard( DockableDisplayer displayer ){
	        	StackDockStation.this.discard( displayer );	
        	}
        });
        
        background = createBackground();
        panel = background.getContentPane();
        
        stackComponentFactory = new PropertyValue<StackDockComponentFactory>( COMPONENT_FACTORY ){
            @Override
            protected void valueChanged( StackDockComponentFactory oldValue, StackDockComponentFactory newValue ) {
                if( newValue == null )
                    setStackComponent( createStackDockComponent() );
                else
                    setStackComponent( newValue.create( StackDockStation.this ) );
            }
        };
        
        tabPlacement = new PropertyValue<TabPlacement>( TAB_PLACEMENT ){
        	@Override
        	protected void valueChanged( TabPlacement oldValue, TabPlacement newValue ){
        		if( stackComponent != null ){
        			stackComponent.setTabPlacement( newValue );
        		}
        	}
        };
        
        stackComponent = createStackDockComponent();
        stackComponent.addChangeListener( visibleListener );
        
        stackComponentRepresentative = new StackDockComponentRepresentative();
        stackComponentRepresentative.setComponent( stackComponent );
        stackComponentRepresentative.setTarget( this );
        
        addDockStationListener( new DockStationAdapter() {
        	@Override
        	public void dockableSelected( DockStation station, Dockable oldSelection, Dockable newSelection ){
        		lastSelectedDockable = newSelection;
        	}
		});
    }
    
    /**
     * Creates the panel onto which this station will lay its children.
     * @return the new background
     */
    protected Background createBackground(){
    	return new Background();
    }
    
    /**
     * Creates the {@link StackDockComponent} which will be shown on
     * this station if the station has more then one child.<br>
     * This method is called directly by the constructor.
     * @return the new component
     */
    protected StackDockComponent createStackDockComponent(){
        return new DefaultStackDockComponent();
    }
    
    public DockStation getStation(){
	    return this;
    }
    
    /**
     * Tells this station where to put the tabs.
     * @param placement the side or <code>null</code> to use the default value
     */
    public void setTabPlacement( TabPlacement placement ){
    	tabPlacement.setValue( placement );
    }
    
    /**
     * Gets the location where tabs are currently placed.
     * @return the side at which tabs are
     */
    public TabPlacement getTabPlacement(){
    	return tabPlacement.getValue();
    }
    
    /**
     * Sets the filter that tells this station how to set the content of the tabs. 
     * @param filter the filter, can be <code>null</code>
     */
    public void setTabContentFilter( TabContentFilter filter ){
		tabContentFilter.setValue( filter );
	}
    
    /**
     * Gets the filter that tells this station how to set the content of the tabs.
     * @return the filter, may be <code>null</code>
     */
    public TabContentFilter getTabContentFilter(){
    	return tabContentFilter.getValue();
    }
    
    /**
     * Tells whether this station should show its {@link StackDockComponent} even if it
     * has only one child. This property may only be changed if the {@link StackDockComponent}
     * is exchanged as well.
     * @return <code>true</code> if the {@link StackDockComponent} is to be always shown
     */
    protected boolean singleTabStackDockComponent(){
    	StackDockComponent component = getStackComponent();
    	if( component == null ){
    		return false;
    	}
    	return component.isSingleTabComponent();
    }
    
    /**
     * Sets the {@link StackDockComponent} which should be used by this 
     * station. The component is shown when this station has more then 
     * one child. Note that the <code>stackComponent</code> depends also
     * on the property {@link #COMPONENT_FACTORY}, and will be automatically
     * exchanged if that property changes. Clients should use
     * {@link #setStackComponentFactory(StackDockComponentFactory)} if they
     * want to exchange the component permanently. 
     * @param stackComponent the new component
     * @throws IllegalArgumentException if <code>stackComponent</code> is <code>null</code>
     */
    public void setStackComponent( StackDockComponent stackComponent ) {
        if( stackComponent == null )
            throw new IllegalArgumentException( "Component must not be null" );
        
        if( stackComponent != this.stackComponent ){
            int selected = -1;
            
            if( this.stackComponent != null ){
            	this.stackComponent.setController( null );
                Component component = this.stackComponent.getComponent();
                for( MouseInputListener listener : mouseInputListeners ){
                    component.removeMouseListener( listener );
                    component.removeMouseMotionListener( listener );
                }
                
                selected = this.stackComponent.getSelectedIndex();
                this.stackComponent.removeChangeListener( visibleListener );
                this.stackComponent.removeAll();
            }
            
            this.stackComponent = stackComponent;
            stackComponent.setTabPlacement( tabPlacement.getValue() );
            stackComponentRepresentative.setComponent( stackComponent );
            
            if( getDockableCount() < 2 && !singleTabStackDockComponent() ){
                stackComponent.addChangeListener( visibleListener );
            }
            else{
                panel.removeAll();
                
                for( StationChildHandle handle : dockables.dockables() ){
                	DockableDisplayer displayer = handle.getDisplayer();
                    int index = stackComponent.getTabCount();
                    insertTab( displayer, index );
                }
                
                panel.add( stackComponent.getComponent() );
                if( selected >= 0 && selected < stackComponent.getTabCount() )
                    stackComponent.setSelectedIndex( selected );
                
                stackComponent.addChangeListener( visibleListener );
            }
            
            Component component = stackComponent.getComponent();
            stackComponent.setController( getController() );
            for( MouseInputListener listener : mouseInputListeners ){
                component.addMouseListener( listener );
                component.addMouseMotionListener( listener );
            }
            
            updateConfigurableDisplayerHints();
        }
    }
    
    /**
     * Gets the currently used {@link StackDockComponent}
     * @return the component
     * @see #setStackComponent(StackDockComponent)
     */
    public StackDockComponent getStackComponent() {
		return stackComponent;
	}
   
    /**
     * Sets the factory which will be used to create a {@link StackDockComponent}
     * for this station.
     * @param factory the new factory, can be <code>null</code> if the default-factory
     * should be used
     */
    public void setStackComponentFactory( StackDockComponentFactory factory ){
        stackComponentFactory.setValue( factory );
    }
    
    /**
     * Gets the factory which is used to create a {@link StackDockComponent}.
     * This method returns <code>null</code> if no factory was set through
     * {@link #setStackComponentFactory(StackDockComponentFactory)}.
     * @return the factory or <code>null</code>
     */
    public StackDockComponentFactory getStackComponentFactory(){
        return stackComponentFactory.getOwnValue();
    }
    
    @Override
    protected void callDockUiUpdateTheme() throws IOException {
    	DockUI.updateTheme( this, new StackDockStationFactory());
    }
   
    @Override
    public void setDockParent( DockStation station ) {
        DockStation old = getDockParent();
        if( old != null )
            old.removeDockStationListener( visibleListener );
        
        super.setDockParent(station);
        
        if( station != null )
            station.addDockStationListener( visibleListener );
        
        visibility.fire();
    }
    
    @Override
    public void setController( DockController controller ) {
        if( this.getController() != controller ){
            for( StationChildHandle handle : dockables.dockables() ){
            	handle.setTitleRequest( null );
            }
            
            boolean wasNull = getController() == null;
            
            stackComponentFactory.setProperties( controller );
            super.setController(controller);
            stackComponent.setController( controller );
            tabPlacement.setProperties( controller );
            placeholderStrategy.setProperties( controller );
            tabContentFilter.setProperties( controller );
            stackComponentRepresentative.setController( controller );
            
            if( controller != null ){
                title = controller.getDockTitleManager().getVersion( TITLE_ID, ControllerTitleFactory.INSTANCE );
            }
            else{
                title = null;
            }
            
            displayers.setController( controller );
            
            boolean isNull = controller == null;
            if( wasNull != isNull ){
            	if( wasNull ){
            		dockables.bind();
            	}
            	else{
            		dockables.unbind();
            	}
            }
            
            for( StationChildHandle handle : dockables.dockables() ){
            	handle.setTitleRequest( title, true );
            }
        }
    }
    
    /**
     * Gets a {@link StationPaint} which is used to paint some lines onto
     * this station. Use a {@link StationPaintWrapper#setDelegate(StationPaint) delegate}
     * to exchange the paint.
     * @return the paint
     */
    public StationPaintWrapper getPaint() {
        return paint;
    }
   
    /**
     * Gets a {@link DisplayerFactory} which is used to create new
     * {@link DockableDisplayer} for this station. Use a 
     * {@link DisplayerFactoryWrapper#setDelegate(DisplayerFactory) delegate}
     * to exchange the factory.
     * @return the factory
     */
    public DisplayerFactoryWrapper getDisplayerFactory() {
        return displayerFactory;
    }
    
    /**
     * Gets the set of {@link DockableDisplayer displayers} used on this station.
     * @return the set of displayers
     */
    public DisplayerCollection getDisplayers() {
        return displayers;
    }
    
    @Override
    public boolean isStationVisible() {
        DockStation parent = getDockParent();
        if( parent != null )
            return parent.isVisible( this );
        else
            return panel.isDisplayable();
    }
    
    @Override
    public boolean isVisible( Dockable dockable ) {
        return isStationVisible() && (dockables.dockables().size() == 1 || indexOf( dockable ) == stackComponent.getSelectedIndex() );
    }
    
    public int getDockableCount() {
        return dockables.dockables().size();
    }

    public Dockable getDockable( int index ) {
        return dockables.dockables().get( index ).getDockable();
    }
    
    public DockableProperty getDockableProperty( Dockable dockable, Dockable target ) {
    	int index = indexOf( dockable );
    	PlaceholderStrategy strategy = getPlaceholderStrategy();
    	Path placeholder = null;
    	if( strategy != null ){
    		placeholder = strategy.getPlaceholderFor( target == null ? dockable : target );
    		if( placeholder != null ){
    			dockables.dockables().addPlaceholder( index, placeholder );
    		}
    	}
    	
        return new StackDockProperty( index, placeholder );
    }
    
    public Dockable getFrontDockable() {
        if( dockables.dockables().size() == 0 )
            return null;
        if( dockables.dockables().size() == 1 )
            return dockables.dockables().get( 0 ).getDockable();
        
        int index = stackComponent.getSelectedIndex();
        if( index >= 0 )
            return dockables.dockables().get( index ).getDockable();
        
        return null;
    }
    
    public void setFrontDockable( Dockable dockable ) {
        if( dockables.dockables().size() > 1 && dockable != null )
            stackComponent.setSelectedIndex( indexOf( dockable ));
        
        fireDockableSelected();
    }
    
    /**
     * Informs all {@link DockStationListener}s that the selected element of this station changed. 
     * This method only fires if there really is a change, hence it can be safely called multiple times.
     */
    protected void fireDockableSelected(){
    	Dockable selection = getFrontDockable();
    	if( lastSelectedDockable != selection ){
    		listeners.fireDockableSelected( lastSelectedDockable, selection );
    	}
    }
    
    /**
     * Gets the index of a child.
     * @param dockable the child which is searched
     * @return the index of <code>dockable</code> or -1 if it was not found
     */
    public int indexOf( Dockable dockable ){
    	int index = 0;
    	
    	for( StationChildHandle check : dockables.dockables() ){
    		if( check.getDockable() == dockable ){
    			return index;
    		}
    		index++;
    	}
    	
    	return -1;
    }
    
    public PlaceholderMap getPlaceholders(){
    	return dockables.toMap();
    }
    
    public void setPlaceholders( PlaceholderMap placeholders ){
    	if( getDockableCount() > 0 ){
    		throw new IllegalStateException( "there are children on this station" );
    	}
    	try{
    		PlaceholderList<StationChildHandle> next = new PlaceholderList<StationChildHandle>( placeholders );
    		if( getController() != null ){
    			dockables.setStrategy( null );
    			dockables.unbind();
    			dockables = next;
    			dockables.bind();
    			dockables.setStrategy( getPlaceholderStrategy() );
    		}
    		else{
    			dockables = next;
    		}
    	}
    	catch( IllegalArgumentException ex ){
    		// ignore
    	}
    }
    
    /**
     * Gets the placeholders of this station using a {@link PlaceholderListItemConverter} to
     * encode the children of this station. To be exact, the converter puts the following
     * parameters for each {@link Dockable} into the map:
     * <ul>
     * 	<li>id: the integer from <code>children</code></li>
     * 	<li>index: the location of the element in the dockables-list</li>
     *  <li>placeholder: the placeholder of the element, might not be written</li>
     * </ul> 
     * @param children a unique identifier for each child of this station
     * @return the map 
     */
    public PlaceholderMap getPlaceholders( final Map<Dockable, Integer> children ){
    	dockables.insertAllPlaceholders();
    	
    	final PlaceholderStrategy strategy = getPlaceholderStrategy();
    	
    	return dockables.toMap( new PlaceholderListItemAdapter<StationChildHandle>() {
    		@Override
    		public ConvertedPlaceholderListItem convert( int index, StationChildHandle dockable ){
    			ConvertedPlaceholderListItem item = new ConvertedPlaceholderListItem();
    			item.putInt( "id", children.get( dockable.getDockable() ) );
    			item.putInt( "index", index );
    			if( strategy != null ){
    				Path placeholder = strategy.getPlaceholderFor( dockable.getDockable() );
    				if( placeholder != null ){
    					item.putString( "placeholder", placeholder.toString() );
    					item.setPlaceholder( placeholder );
    				}
    			}
    			return item;
    		}
    		
    		public StationChildHandle convert( ConvertedPlaceholderListItem item ){
    			// ignore
    			return null;
    		}
		});
    }
    
    /**
     * Sets all placeholders and children of this station.
     * @param placeholders the new children and placeholders
     * @param children map to convert items to {@link Dockable}s
     * @throws IllegalStateException if there are still children on this station
     */
    public void setPlaceholders( PlaceholderMap placeholders, final Map<Integer, Dockable> children ){
    	if( getDockableCount() > 0 ){
    		throw new IllegalStateException( "there are children on this station" );
    	}
    	
    	dockables.setStrategy( null );
		dockables.unbind();
		PlaceholderList<StationChildHandle> next = new PlaceholderList<StationChildHandle>();
    	
    	dockables = next;
    	next.read( placeholders, new PlaceholderListItemAdapter<StationChildHandle>() {
    		private int size = 0;
    		
    		@Override
    		public StationChildHandle convert( ConvertedPlaceholderListItem item ){
    			int id = item.getInt( "id" );
    			Dockable dockable = children.get( id );
    			if( dockable == null ){
    				return null;
    			}
    			
    			listeners.fireDockableAdding( dockable );
    			dockable.addDockableListener( listener );
    			StationChildHandle handle = new StationChildHandle( StackDockStation.this, getDisplayers(), dockable, title );
    			
    			return handle;
    		}
    		
    		@Override
    		public void added( StationChildHandle handle ){
    			Dockable dockable = handle.getDockable();
    			dockable.setDockParent( StackDockStation.this );
    			handle.updateDisplayer();
    			addToPanel( handle, size, size );
    			size++;
    			listeners.fireDockableAdded( dockable );
    		}
		});
    	
		if( getController() != null ){
			dockables.bind();
			dockables.setStrategy( getPlaceholderStrategy() );
		}
    }

    /**
     * Gets the {@link PlaceholderStrategy} that is currently in use.
     * @return the current strategy, may be <code>null</code>
     */
    public PlaceholderStrategy getPlaceholderStrategy(){
    	return placeholderStrategy.getValue();
    }
    
    /**
     * Sets the {@link PlaceholderStrategy} to use, <code>null</code> will set
     * the default strategy.
     * @param strategy the new strategy, can be <code>null</code>
     */
    public void setPlaceholderStrategy( PlaceholderStrategy strategy ){
    	placeholderStrategy.setValue( strategy );
    }
    
    public boolean prepareDrop( int x, int y, int titleX, int titleY, boolean checkOverrideZone, Dockable dockable ){
    	if( SwingUtilities.isDescendingFrom( getComponent(), dockable.getComponent() )){
    		setInsert( null, null );
    		return false;
    	}
    	
        DockStation parent = getDockParent();
        Point point = new Point( x, y );
        SwingUtilities.convertPointFromScreen( point, panel );
        
        if( parent != null ){
            if( checkOverrideZone && parent.isInOverrideZone( x, y, this, dockable )){
                if( dockables.dockables().size() > 1 ){
                    if( setInsert( exactTabIndexAt( point.x, point.y ), dockable ))
                        return true;
                }
                else if( dockables.dockables().size() == 1 ){
                    DockTitle title = dockables.dockables().get( 0 ).getDisplayer().getTitle();
                    if( title != null ){
                        Component component = title.getComponent();
                        Point p = new Point( x, y );
                        SwingUtilities.convertPointFromScreen( p, component );

                        if( component.getBounds().contains( p )){
                            return setInsert( new Insert( 0, true ), dockable );
                        }
                    }
                }
                return false;
            }
        }
        
        return setInsert( tabIndexAt( point.x, point.y ), dockable );
    }

    public void drop(){
    	listeners.fireDockableAdding( dropping );
        add( dropping, insert.tab + (insert.right ? 1 : 0), null, false );
        listeners.fireDockableAdded( dropping );
    }

    public void drop( Dockable dockable ) {
    	drop( dockable, true );
    }
    
    /**
     * Adds <code>dockable</code> to this station.
     * @param dockable the element to drop
     * @param autoPlaceholder whether the {@link PlaceholderStrategy} can be invoked to search for a matching placeholder
     */
    public void drop( Dockable dockable, boolean autoPlaceholder ){
    	Path placeholder = null;
    	if( autoPlaceholder ){
    		PlaceholderStrategy strategy = getPlaceholderStrategy();
    		placeholder = strategy == null ? null : strategy.getPlaceholderFor( dockable );
    	}
    	boolean done = false;
    	
    	if( placeholder != null ){
    		done = drop( dockable, new StackDockProperty( dockables.dockables().size(), placeholder ) );
    	}
    	if( !done ){
    		listeners.fireDockableAdding( dockable );
        	add( dockable, dockables.dockables().size(), null, false );
        	listeners.fireDockableAdded( dockable );
    	}
    }
    
    public boolean drop( Dockable dockable, DockableProperty property ) {
        if( property instanceof StackDockProperty ){
            return drop( dockable, (StackDockProperty)property );
        }
        else
            return false;
    }
    
    /**
     * Adds a new child to this station, and tries to match the <code>property</code>
     * as good as possible.
     * @param dockable the new child
     * @param property the preferred location of the child
     * @return <code>true</code> if the child could be added, <code>false</code>
     * if the child couldn't be added
     */
    public boolean drop( Dockable dockable, StackDockProperty property ){
        DockUtilities.ensureTreeValidity( this, dockable );
        int index = property.getIndex();
        Path placeholder = property.getPlaceholder();
        
        boolean acceptable = acceptable( dockable );
        DockableProperty successor = property.getSuccessor();
        
        boolean result = false;
        
        if( placeholder != null && successor != null ){
        	StationChildHandle preset = dockables.getDockableAt( placeholder );
        	if( preset != null ){
        		DockStation station = preset.getDockable().asDockStation();
        		if( station != null ){
        			if( station.drop( dockable, successor )){
        				result = true;
        				dockables.removeAll( placeholder );
        			}
        		}
        	}
        }
        else if( placeholder != null ){
        	if( acceptable && dockables.hasPlaceholder( placeholder )){
        		add( dockable, 0, placeholder, true );
        		result = true;
        	}
        }
        
        if( !result && dockables.dockables().size() == 0 ){
            if( acceptable ){
            	drop( dockable, false );
                result = true;
            }
        }
        
        if( !result ){
	        index = Math.min( index, dockables.dockables().size() );
	        
	        if( index < dockables.dockables().size() && successor != null ){
	            DockStation station = dockables.dockables().get( index ).getDockable().asDockStation();
	            if( station != null ){
	                if( station.drop( dockable, successor )){
	                    result = true;
	                }
	            }
	        }
	        
	        if( !result && acceptable ){
	        	add( dockable, index );	
	        	result = true;
	        }
        }
        
        return result;
    }
    
    public boolean prepareMove( int x, int y, int titleX, int titleY, boolean checkOverrideZone, Dockable dockable ) {
        DockStation parent = getDockParent();
        Point point = new Point( x, y );
        SwingUtilities.convertPointFromScreen( point, panel );
        
        if( parent != null ){
            if( checkOverrideZone && parent.isInOverrideZone( x, y, this, dockable )){
                if( dockables.dockables().size() > 1 ){
                    if( setInsert( exactTabIndexAt( point.x, point.y ), dockable ) )
                        return true;
                }
                return false;
            }
        }
        
        return setInsert( tabIndexAt( point.x, point.y ), dockable );
    }
    
    /**
     * Checks whether <code>child</code> can be inserted at <code>insert</code>.
     * If so, then the field {@link #insert} and {@link #dropping} are set.
     * @param insert the new location
     * @param child the element to insert
     * @return <code>true</code> if the combination is valid
     */
    private boolean setInsert( Insert insert, Dockable child ){
        if( insert != null && accept( child ) && child.accept( this ) && getController().getAcceptance().accept( this, child )){
            this.insert = insert;
            this.dropping = child;
        }
        else{
            this.insert = null;
            this.dropping = null;
        }
        
        return this.insert != null;
    }
    
    /**
     * Gets the location where {@link #drop()} or {@link #move()} will insert the next
     * {@link Dockable}.
     * @return the insertion location, can be <code>null</code>
     */
    public Insert getInsert(){
		return insert;
	}

    public void move() {
        int index = indexOf( dropping );
        if( index >= 0 ){
            int drop = insert.tab + (insert.right ? 1 : 0 );
            if( drop > index ){
            	drop--;
            }
            move( index, drop );
        }
    }
    
    public void move( Dockable dockable, DockableProperty property ) {
        if( property instanceof StackDockProperty ){
            int index = indexOf( dockable );
            if( index < 0 )
                throw new IllegalArgumentException( "dockable not child of this station" );
            
            int destination = ((StackDockProperty)property).getIndex();
            destination = Math.min( destination, getDockableCount()-1 );
            move( index, destination );
        }
    }
        
    private void move( int source, int destination ){
    	if( source != destination ){
    		dockables.dockables().move( source, destination );
    		stackComponent.moveTab( source, destination );
    	}
    }
    
    /**
     * Tells which gap (between tabs) is chosen if the mouse has the coordinates x/y.
     * If there is no tab at this location, a default-tab is chosen.
     * @param x x-coordinate in the system of this station
     * @param y y-coordinate in the system of this station
     * @return the location of a tab
     */
    protected Insert tabIndexAt( int x, int y ){
        if( dockables.dockables().size() == 0 )
            return new Insert( 0, false );
        if( dockables.dockables().size() == 1 )
            return new Insert( 1, false );
        
        Insert insert = exactTabIndexAt( x, y );
        if( insert == null )
            insert = new Insert( dockables.dockables().size()-1, true );
        
        return insert;
    }
    
    /**
     * Gets the gap which is selected when the mouse is at x/y.
     * @param x x-coordinate in the system of this station
     * @param y y-coordinate in the system of this station
     * @return the location of a tab or <code>null</code> if no tab is
     * under x/y
     */
    protected Insert exactTabIndexAt( int x, int y ){
        Point point = SwingUtilities.convertPoint( panel, x, y, stackComponent.getComponent() );
        
        for( int i = 0, n = dockables.dockables().size(); i<n; i++ ){
            Rectangle bounds = stackComponent.getBoundsAt( i );
            if( bounds != null && bounds.contains( point )){
            	if( tabPlacement.getValue().isHorizontal() )
            		return new Insert( i, bounds.x + bounds.width/2 < point.x );
            	else
            		return new Insert( i, bounds.y + bounds.height/2 < point.y );
            }
        }
               
        return null;
    }    

    public void draw() {
        draw = true;
        panel.repaint();
    }

    public void forget() {
        draw = false;
        insert = null;
        dropping = null;
        panel.repaint();
    }

    public <D extends Dockable & DockStation> boolean isInOverrideZone( int x,
            int y, D invoker, Dockable drop ){
        
        DockStation parent = getDockParent();
        if( parent != null )
            return parent.isInOverrideZone( x, y, invoker, drop );
        
        return false;
    }

    public boolean canDrag( Dockable dockable ) {
        return true;
    }
    
    public void drag( Dockable dockable ) {
        if( dockable.getDockParent() != this )
            throw new IllegalArgumentException( "The dockable can't be dragged, it is not child of this station" );
        
        int index = indexOf( dockable );
        if( index < 0 )
            throw new IllegalArgumentException( "The dockable is not part of this station." );
        
        listeners.fireDockableRemoving( dockable );
        remove( index, false );
        listeners.fireDockableRemoved( dockable );
    }
    
    public boolean canReplace( Dockable old, Dockable next ) {
        return true;
    }
    
    public void replace( DockStation old, Dockable next ){
	    replace( old.asDockable(), next, true );	
    }
    
    public void replace( Dockable old, Dockable next ) {
    	replace( old, next, false );
    }
    
    public void replace( Dockable old, Dockable next, boolean station ) {
    	DockController controller = getController();
    	try{
    		if( controller != null )
    			controller.freezeLayout();
    			
    		int index = indexOf( old );
    		
    		int listIndex = dockables.levelToBase( index, Level.DOCKABLE );
    		PlaceholderList<StationChildHandle>.Item oldItem = dockables.list().get( listIndex );
    		remove( index );
    		add( next, index );
    		PlaceholderList<StationChildHandle>.Item newItem = dockables.list().get( listIndex );
    		if( station ){
    			newItem.setPlaceholderMap( old.asDockStation().getPlaceholders() );
    		}
    		else{
    			newItem.setPlaceholderMap( oldItem.getPlaceholderMap() );
    		}
    		newItem.setPlaceholderSet( oldItem.getPlaceholderSet() );

    	}
    	finally{
    		if( controller != null )
    			controller.meltLayout();
    	}
    }
    
    /**
     * Adds a child to this station at the location <code>index</code>.
     * @param dockable the new child
     * @param index the preferred location of the new child
     */
    public void add( Dockable dockable, int index ){
        add( dockable, index, null, true );
    }
    
    /**
     * Adds a child to this station at the location <code>index</code>.
     * @param dockable the new child
     * @param index the preferred location of the new child
     * @param placeholder the preferred location of the new child, can be <code>null</code>
     * @param fire if <code>true</code> the method should fire events for
     * adding a new {@link Dockable}, otherwise the method will run silently
     */
    protected void add( Dockable dockable, int index, Path placeholder, boolean fire ){
        DockUtilities.ensureTreeValidity( this, dockable );
        
        if( fire ){
        	listeners.fireDockableAdding( dockable );
        }
        dockable.setDockParent( this );
        
        StationChildHandle handle = new StationChildHandle( this, getDisplayers(), dockable, title );
        handle.updateDisplayer();
        
        int inserted = placeholder == null ? -1 : dockables.put( placeholder, handle );
        if( inserted == -1 ){
        	dockables.dockables().add( index, handle );
        }
        else{
        	index = inserted;
        }

        addToPanel( handle, index, dockables.dockables().size()-1 );
        
        dockable.addDockableListener( listener );
        
        if( fire ){
        	listeners.fireDockableAdded( dockable );
        }
        
        fireDockableSelected();
    }
    
    /**
     * Adds the contents of <code>handle</code> to the {@link #stackComponent} of this station. The new
     * <code>handle</code> may or may not already be stored in {@link #dockables}. 
     * @param handle the handle to add
     * @param index the index where to add the handle
     * @param size the current amount of children of the panel, must be either the size of {@link #dockables} if
     * <code>handle</code> is not yet stored or the size of {@link #dockables}-1 is <code>handle</code> already is
     * stored.
     */
    protected void addToPanel( StationChildHandle handle, int index, int size ){
    	if( size == 0 && !singleTabStackDockComponent() ){
    		DockableDisplayer displayer = handle.getDisplayer();
            panel.add( displayer.getComponent() );
        }
        else{
        	int selectionIndex = index;
        	
            if( size == 1 && !singleTabStackDockComponent() ){
                panel.removeAll();
                
                Filter<StationChildHandle> list = dockables.dockables();
                if( list.get( 0 ) == handle ){
                	if( list.size() != 2 ){
                		throw new IllegalStateException( "handle is stored, size is 1, but number of known dockables is not 2" );
                	}
                	handle = list.get( 1 );
                	index = 1;
                }
                
                DockableDisplayer child = list.get( 0 ).getDisplayer();
                insertTab( child, 0 );
                panel.add( stackComponent.getComponent() );
            }
            
            DockableDisplayer displayer = handle.getDisplayer();
            insertTab( displayer, index );
            stackComponent.setSelectedIndex( selectionIndex );
        }
        panel.revalidate();
        panel.repaint();
    }
    
    private void insertTab( DockableDisplayer displayer, int index ){
    	Dockable dockable = displayer.getDockable();
    	String title = dockable.getTitleText();
		String tooltip = dockable.getTitleToolTip();
		Icon icon = dockable.getTitleIcon();
		
    	TabContentFilter filter = getTabContentFilter();
    	if( filter != null ){
    		TabContent content = new TabContent( icon, title, tooltip );
    		content = filter.filter( content, this, dockable );
    		if( content == null ){
    			title = null;
    			tooltip = null;
    			icon = null;
    		}
    		else{
    			title = content.getTitle();
    			tooltip = content.getTooltip();
    			icon = content.getIcon();
    		}
    	}
    	
    	stackComponent.insertTab( title, icon, displayer.getComponent(), dockable, index );
    	stackComponent.setTooltipAt( index, tooltip );
    }
    
    /**
     * Replaces <code>displayer</code> with a new instance.
     * @param displayer the displayer to replace
     */
    protected void discard( DockableDisplayer displayer ){
    	Dockable dockable = displayer.getDockable();
    	
    	int index = indexOf( dockable );
    	if( index < 0 )
    		throw new IllegalArgumentException( "displayer is not a child of this station: " + displayer );
    	
    	StationChildHandle handle = dockables.dockables().get( index );
    	handle.updateDisplayer();
    	displayer = handle.getDisplayer();
    	
    	if( dockables.dockables().size() == 1 && singleTabStackDockComponent() ){
    		panel.removeAll();
    		panel.add( displayer.getComponent() );
    	}
    	else{
    		stackComponent.setComponentAt( index, displayer.getComponent() );
    	}
    }
    
    /**
     * Removes the child of location <code>index</code>.<br>
     * Note: clients may need to invoke {@link DockController#freezeLayout()}
     * and {@link DockController#meltLayout()} to ensure noone else adds or
     * removes <code>Dockable</code>s.
     * @param index the location of the child which will be removed
     */
    public void remove( int index ){
        remove( index, true );
    }

    /**
     * Removes the child of location <code>index</code>.
     * @param index the location of the child which will be removed
     * @param fire <code>true</code> if the method should fire some events,
     * <code>false</code> if the method should run silently
     */
    private void remove( int index, boolean fire ){
        if( index < 0 || index >= dockables.dockables().size() )
            throw new IllegalArgumentException( "Index out of bounds" );
        
        StationChildHandle handle = dockables.dockables().get( index );
        Dockable dockable = handle.getDockable();
        
        if( fire )
        	listeners.fireDockableRemoving( dockable );
        
        if( dockables.dockables().size() == 1 ){
        	if( singleTabStackDockComponent() ){
        		stackComponent.remove( 0 );
        	}
        	else{
        		panel.remove( dockables.dockables().get( 0 ).getDisplayer().getComponent() );
        	}
            dockables.remove( 0 );
            panel.repaint();
        }
        else if( dockables.dockables().size() == 2 && !singleTabStackDockComponent() ){
            panel.remove( stackComponent.getComponent() );
            dockables.remove( index );
            stackComponent.removeAll();
            panel.add( dockables.dockables().get( 0 ).getDisplayer().getComponent() );
        }
        else{
        	dockables.remove( index );
        	stackComponent.remove( index );
        }

        handle.destroy();
        
        dockable.removeDockableListener( listener );
        panel.revalidate();
        
        dockable.setDockParent( null );
        if( fire )
        	listeners.fireDockableRemoved( dockable );
        
        fireDockableSelected();
    }

    public Component getComponent() {
        return background;
    }
    
    @Override
    public void configureDisplayerHints( DockableDisplayerHints hints ) {
        super.configureDisplayerHints( hints );
        updateConfigurableDisplayerHints();
    }
    
    /**
     * Updates the {@link #getConfigurableDisplayerHints() displayer hints}
     * of this station.
     */
    protected void updateConfigurableDisplayerHints(){
        DockableDisplayerHints hints = getConfigurableDisplayerHints();
        if( hints != null ){
            hints.setShowBorderHint( !getStackComponent().hasBorder() );
        }
    }
    
    @Override
    public void addMouseInputListener( MouseInputListener listener ) {
        panel.addMouseListener( listener );
        panel.addMouseMotionListener( listener );
        mouseInputListeners.add( listener );
        
        if( stackComponent != null ){
            stackComponent.getComponent().addMouseListener( listener );
            stackComponent.getComponent().addMouseMotionListener( listener );
        }
    }
    
    @Override
    public void removeMouseInputListener( MouseInputListener listener ) {
        panel.removeMouseListener( listener );
        panel.removeMouseMotionListener( listener );
        mouseInputListeners.remove( listener );
        
        if( stackComponent != null ){
            stackComponent.getComponent().removeMouseListener( listener );
            stackComponent.getComponent().removeMouseMotionListener( listener );
        }
    }
    
    public String getFactoryID() {
        return StackDockStationFactory.ID;
    }
    
    private void updateContent( int index ){
    	if( index >= 0 && (getDockableCount() > 1 || singleTabStackDockComponent()) ){
    		Dockable dockable = getDockable( index );
    		TabContentFilter filter = getTabContentFilter();
    		
    		TabContent content = new TabContent( dockable.getTitleIcon(), dockable.getTitleText(), dockable.getTitleToolTip() );
    		if( filter != null ){
    			content = filter.filter( content, this, dockable );
    		}
    		if( content == null ){
    			stackComponent.setTitleAt( index, null );
    			stackComponent.setIconAt( index, null );
    			stackComponent.setTooltipAt( index, null );
    		}
    		else{
    			stackComponent.setTitleAt( index, content.getTitle() );
    			stackComponent.setIconAt( index, content.getIcon() );
    			stackComponent.setTooltipAt( index, content.getTooltip() );
    		}
    	}
    }
    
    /**
     * A listener for the parent of this station. This listener will fire
     * events if the visibility-state of this station changes.<br>
     * This listener is also added to the {@link StackDockStation#getStackComponent() stack-component}
     * of the station, and ensures that the visible child has the focus.
     * @author Benjamin Sigg
     */
    private class VisibleListener extends DockStationAdapter implements ChangeListener{
        @Override
        public void dockableVisibiltySet( DockStation station, Dockable dockable, boolean visible ) {
            visibility.fire();
        }
        
        public void stateChanged( ChangeEvent e ) {
            DockController controller = getController();
            if( controller != null ){
                Dockable selection = getFrontDockable();
                if( selection != null )
                    controller.setFocusedDockable( selection, false );
                
                fireDockableSelected();
            }
            
            visibility.fire();
        }
    }
    
    /**
     * This listener is added to the children of the station. Whenever the
     * icon or the title-text of a child changes, the listener will inform
     * the {@link StackDockStation#getStackComponent() stack-component} about
     * the change.
     * @author Benjamin Sigg
     */
    private class Listener implements DockableListener{
        public void titleBound( Dockable dockable, DockTitle title ) {
            // do nothing
        }

        public void titleUnbound( Dockable dockable, DockTitle title ) {
            // do nothing
        }

        public void titleTextChanged( Dockable dockable, String oldTitle, String newTitle ) {
            if( dockables.dockables().size() > 1 ){
                int index = indexOf( dockable );
                updateContent( index );
            }
        }
        
        public void titleToolTipChanged( Dockable dockable, String oldTooltip, String newTooltip ) {
            if( dockables.dockables().size() > 1 ){
                int index = indexOf( dockable );
                updateContent( index );
            }
        }

        public void titleIconChanged( Dockable dockable, Icon oldIcon, Icon newIcon ) {
            if( dockables.dockables().size() > 1 ){
                int index = indexOf( dockable );
                updateContent( index );
            }            
        }
        
        public void titleExchanged( Dockable dockable, DockTitle title ) {
            // ignore
        }
    }
    
    /**
     * This panel is used as base of the station. All children of the station 
     * have this panel as parent too.
     * @author Benjamin Sigg
     */
    protected class Background extends OverpaintablePanel{
    	/**
    	 * Creates a new panel
    	 */
        public Background(){
            getContentPane().setLayout( new GridLayout( 1, 1 ));
        }
        
        @Override
        protected void paintOverlay( Graphics g ) {
            StationPaint paint = getPaint();
            
            if( draw && dockables.dockables().size() > 1 && insert != null ){
                Rectangle bounds = null;
                
                if( insert.tab >= 0 && insert.tab < stackComponent.getTabCount() )
                    bounds = stackComponent.getBoundsAt( insert.tab );
                
                if( bounds != null ){
                    Point a = new Point();
                    Point b = new Point();

                    if( insert.right ){
                   		insertionLine( bounds, insert.tab+1 < stackComponent.getTabCount() ? stackComponent.getBoundsAt( insert.tab+1 ) : null, a, b, true );
                    }
                    else{
                   		insertionLine( insert.tab > 0 ? stackComponent.getBoundsAt( insert.tab-1 ) : null, bounds, a, b, false );
                    }
                    
                    paint.drawInsertionLine( g, StackDockStation.this, a.x, a.y, b.x, b.y );
                }
            }
            
            if( draw ){
                Rectangle bounds = new Rectangle( 0, 0, getWidth(), getHeight() );
                Rectangle insert = null;
                if( getDockableCount() < 2 )
                    insert = bounds;
                else{
                	int index = stackComponent.getSelectedIndex();
                	if( index >= 0 ){
	                    Component front = dockables.dockables().get( index ).getDisplayer().getComponent();
	                    Point location = new Point( 0, 0 );
	                    location = SwingUtilities.convertPoint( front, location, this );
	                    insert = new Rectangle( location.x, location.y, front.getWidth(), front.getHeight() );
                	}
                }
                
                if( insert != null ){
                	paint.drawInsertion( g, StackDockStation.this, bounds, insert );
                }
            }
        }
    }
    
    /**
     * When dropping or moving a {@link Dockable}, a line has to be painted
     * between two tabs. This method determines the exact location of that line.
     * @param left the bounds of the tab left to the line, might be <code>null</code> if
     * <code>leftImportant</code> is <code>false</code>.
     * @param right the bounds of the tab right to the line, might be <code>null</code> if
     * <code>leftImportant</code> is <code>true</code>.
     * @param a the first point of the line, should be used as output of this method
     * @param b the second point of the line, should be used as output of this method
     * @param leftImportant <code>true</code> if the mouse is over the left tab, <code>false</code>
     * if the mouse is over the right tab.
     */
    protected void insertionLine( Rectangle left, Rectangle right, Point a, Point b, boolean leftImportant ){
    	if( tabPlacement.getValue().isHorizontal() ){
    		if( left != null && right != null ){
    			int top = Math.max( left.y, right.y );
    			int bottom = Math.min( left.y + left.height, right.y + right.height );

    			if( bottom > top ){
    				int dif = bottom - top;
    				if( dif >= 0.8*left.height && dif >= 0.8*right.height ){
    					a.x = (left.x+left.width+right.x) / 2;
    					a.y = top;

    					b.x = a.x;
    					b.y = bottom;

    					return;
    				}
    			}
    		}

    		if( leftImportant ){
    			a.x = left.x + left.width;
    			a.y = left.y;

    			b.x = a.x;
    			b.y = a.y + left.height;
    		}
    		else{
    			a.x = right.x;
    			a.y = right.y;

    			b.x = a.x;
    			b.y = a.y + right.height;
    		}
    	}
    	else{
    		if( left != null && right != null ){
    			int top = Math.max( left.x, right.x );
    			int bottom = Math.min( left.x + left.width, right.x + right.width );

    			if( bottom > top ){
    				int dif = bottom - top;
    				if( dif >= 0.8*left.width && dif >= 0.8*right.width ){
    					a.y = (left.y+left.height+right.y) / 2;
    					a.x = top;

    					b.y = a.y;
    					b.x = bottom;

    					return;
    				}
    			}
    		}

    		if( leftImportant ){
    			a.y = left.y + left.height;
    			a.x = left.x;

    			b.y = a.y;
    			b.x = a.x + left.width;
    		}
    		else{
    			a.y = right.y;
    			a.x = right.x;

    			b.y = a.y;
    			b.x = a.x + right.width;
    		}
    	}
    }
    
    /**
     * Describes the gap between two tabs.
     * @author Benjamin Sigg
     */
    public static class Insert{
        /** The location of a base-tab */
        private final int tab;
        /** Whether the gap is left or right of {@link #tab}*/
        private final boolean right;
        
        /**
         * Constructs a new Gap-location
         * @param tab The location of a base-tab
         * @param right Whether the gap is left or right of <code>tab</code>
         */
        public Insert( int tab, boolean right ){
            this.tab = tab;
            this.right = right;
        }
        
        /**
         * A reference for this gap, the gap is either at the left
         * or at the right side of this tab.
         * @return the reference
         */
        public int getTab(){
			return tab;
		}
        
        /**
         * Whether the tab is at the left or the right of {@link #getTab()}. 
         * @return <code>true</code> if the gab is at the right side
         */
        public boolean isRight(){
			return right;
		}
    }
}