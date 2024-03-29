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

package bibliothek.gui.dock.themes;

import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ResourceBundle;

import bibliothek.gui.DockTheme;
import bibliothek.gui.DockUI;

/**
 * A factory using the {@link ThemeProperties} of a {@link DockTheme} to 
 * create instances of that <code>DockTheme</code>.
 * @param <T> the type of theme created by this factory
 * @author Benjamin Sigg
 */
public class ThemePropertyFactory<T extends DockTheme> implements ThemeFactory {
    /** Default constructor of the theme */
    private Constructor<T> constructor;
    /** Information about the theme */
    private ThemeProperties properties;
    /** Bundle containing the text, may be <code>null</code> */
    private ResourceBundle bundle;
    /** Additional information, might be <code>null</code> */
    private DockUI ui;
    
    /**
     * Creates a new factory.
     * @param theme the class of a theme, must have the {@link ThemeProperties} annotation.
     */
    public ThemePropertyFactory( Class<T> theme ){
        this( theme, null, null );
    }

    /**
     * Creates a new factory.
     * @param theme the class of a theme, must have the {@link ThemeProperties} annotation.
     * @param bundle the bundle to retrieve text, might be <code>null</code> if the
     * bundle of the {@link DockUI} should be used.
     */
    public ThemePropertyFactory( Class<T> theme, ResourceBundle bundle ){
        this( theme, bundle, null );
    }

    /**
     * Creates a new factory.
     * @param theme the class of a theme, must have the {@link ThemeProperties} annotation.
     * @param ui the DockUI to retrieve more information, might be <code>null</code>
     */
    public ThemePropertyFactory( Class<T> theme, DockUI ui ){
        this( theme, null, ui );
    }
    
    /**
     * Creates a new factory.
     * @param theme the class of a theme, must have the {@link ThemeProperties} annotation.
     * @param bundle the bundle to retrieve text, might be <code>null</code> if the
     * bundle of the {@link DockUI} should be used.
     * @param ui the DockUI to retrieve more information, might be <code>null</code>
     */
    public ThemePropertyFactory( Class<T> theme, ResourceBundle bundle, DockUI ui ){
        if( theme == null )
            throw new IllegalArgumentException( "Theme must not be null" );
        
        properties = theme.getAnnotation( ThemeProperties.class );
        if( properties == null )
            throw new IllegalArgumentException( "Theme misses annotation ThemeProperties" );
        
        try {
            constructor = theme.getConstructor( new Class[0] );
        }
        catch( NoSuchMethodException e ){
            throw new IllegalArgumentException( "Missing default constructor", e );
        }
        
        this.bundle = bundle;
        this.ui = ui;
    }
    
    /**
     * Gets the bundle used to retrieve text for this factory.
     * @return the bundle or <code>null</code> if the bundle of
     * the {@link DockUI} is used.
     */
    public ResourceBundle getBundle() {
        return bundle;
    }
    
    /**
     * Gets the <code>DockUI</code> used with this factory.
     * @return the ui or <code>null</code> if the default-DockUI is used
     */
    public DockUI getUi() {
        return ui;
    }
    
    public T create() {
        try {
            return constructor.newInstance( new Object[0] );
        }
        catch( Exception e ){
            System.err.println( "Can't create theme due an unknown reason" );
            e.printStackTrace();
            return null;
        }
    }

    public String[] getAuthors() {
        return properties.authors();
    }
    
    protected String getString( String key ){
        if( bundle != null )
            return bundle.getString( key );
        if( ui != null )
            return ui.getString( key );
        return DockUI.getDefaultDockUI().getString( key );
    }

    public String getDescription() {
        return getString( properties.descriptionBundle() );
    }

    public String getName() {
        return getString( properties.nameBundle() );
    }

    public URI[] getWebpages() {
        try{
            String[] urls = properties.webpages();
            URI[] result = new URI[ urls.length ];
            for( int i = 0; i < result.length; i++ )
                result[i] = new URI( urls[i] );
        
            return result;
        }
        catch( URISyntaxException ex ){
            System.err.print( "Can't create urls due an unknown reason" );
            ex.printStackTrace();
            return null;
        }
    }
}
