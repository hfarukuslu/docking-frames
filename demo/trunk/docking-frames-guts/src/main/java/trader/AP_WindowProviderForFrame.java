package trader;

import java.awt.Window;

import javax.swing.RootPaneContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bibliothek.gui.dock.util.DirectWindowProvider;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AP_WindowProviderForFrame extends DirectWindowProvider {

	static private final Logger log = LoggerFactory
			.getLogger(AP_WindowProviderForFrame.class);

	@Inject
	public AP_WindowProviderForFrame(//
			final RootPaneContainer root, //
			final Object none) {

		super((Window) root);

		log.debug("init");

	}

}
