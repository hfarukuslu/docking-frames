package bibliothek.gui.dock.station.split;

import java.awt.Dimension;
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.split.SplitDockTree.Key;
import bibliothek.util.Path;

/**
 * A placeholder is a set of {@link Path}-keys, each key stands for a 
 * {@link Dockable} that is currently not visible. Placeholders are not
 * visible to the user (they have no graphical representation), they
 * are only used to place {@link Dockable}s at their former position.
 * @author Benjamin Sigg
 */
public class Placeholder extends SplitNode {
	
	/**
	 * Creates a new placeholder
	 * @param access access to the {@link SplitDockStation}
	 * @param id the unique id of this placeholder
	 */
	public Placeholder( SplitDockAccess access, long id ){
		super( access, id );
	}
	
	@Override
	public void evolve( Key key, boolean checkValidity, Map<Leaf, Dockable> linksToSet ){
		setPlaceholders( key.getTree().getPlaceholders( key ) );
	}

	@Override
	public int getChildLocation( SplitNode child ){
		return 0;
	}

	@Override
	public Node getDividerNode( int x, int y ){
		return null;
	}

	@Override
	public Leaf getLeaf( Dockable dockable ){
		return null;
	}

	@Override
	public Dimension getMinimumSize(){
		return null;
	}

	@Override
	public PutInfo getPut( int x, int y, double factorW, double factorH, Dockable drop ){
		return null;
	}

	@Override
	public boolean insert( SplitDockPlaceholderProperty property, Dockable dockable ){
		Path placeholder = property.getPlaceholder();
		if( hasPlaceholder( placeholder )){
			// replace this placeholder with a leaf
			Leaf leaf = create( dockable, getId() );
			if( leaf == null )
				return false;
			getAccess().getPlaceholderSet().set( null, placeholder, this );
			leaf.setPlaceholders( getPlaceholders() );
			leaf.setPlaceholderMap( getPlaceholderMap() );
			replace( leaf );
			leaf.setDockable( dockable, true );
			return true;	
		}
		return false;
	}
	
	@Override
	public boolean insert( SplitDockPathProperty property, int depth, Dockable dockable ){
		// ignore
		return false;
	}

	@Override
	public boolean isInOverrideZone( int x, int y, double factorW, double factorH ){
		return false;
	}

	@Override
	public void setChild( SplitNode child, int location ){
		throw new IllegalArgumentException();
	}

	@Override
	public <N> N submit( SplitTreeFactory<N> factory ){
		return factory.placeholder( getId(), getPlaceholders(), getPlaceholderMap() );
	}
	
	@Override
	public boolean isVisible(){
		return false;
	}
	
	@Override
	public SplitNode getVisible(){
		return null;
	}
	
	@Override
	public boolean isOfUse(){
		if( !getAccess().isTreeAutoCleanupEnabled() ){
			return true;
		}
		return hasPlaceholders();
	}

	@Override
	public void visit( SplitNodeVisitor visitor ){
		visitor.handlePlaceholder( this );
	}

	@Override
	public void toString( int tabs, StringBuilder out ){
		out.append( "Placeholder: " );
		boolean first = true;
		for( Path key : getPlaceholders() ){
			if( first ){
				first = false;
			}
			else{
				out.append( ", " );
			}
			out.append( key );
		}
	}
}
