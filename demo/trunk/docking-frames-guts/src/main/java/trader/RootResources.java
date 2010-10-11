package trader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * provides "root bundle" reference
 * 
 * http://guts.kenai.com/guts-gui/apidocs/net/guts/gui/resource
 * /package-summary.html
 * 
 **/
/* do not change package of this class */
public final class RootResources {

	static private final Logger log = LoggerFactory
			.getLogger(RootResources.class);

	public static final String ROOT_NAME = "root-name";

	public static final String FILE_NAME = "resources";

	static {
		log.info("NOTE: " + FILE_NAME + ".properties should be located in {}",
				RootResources.class.getPackage().getName());
	}

}
