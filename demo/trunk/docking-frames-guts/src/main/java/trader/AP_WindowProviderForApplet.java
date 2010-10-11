package trader;

import java.awt.Component;

import javax.swing.RootPaneContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bibliothek.gui.dock.util.AppletWindowProvider;
import bibliothek.gui.dock.util.WindowProviderListener;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AP_WindowProviderForApplet extends AppletWindowProvider {

	static private final Logger log = LoggerFactory
			.getLogger(AP_WindowProviderForApplet.class);

	@Inject
	public AP_WindowProviderForApplet(//
			RootPaneContainer root, //
			Object none) {

		super((Component) root);

		assert SwingHelper.isEDT();

		log.debug("init");

	}

	@Override
	public void addWindowProviderListener(WindowProviderListener listener) {

		// TODO debug in parent: deadlock in:
		// component.addHierarchyListener( this.listener );
		super.addWindowProviderListener(listener);

	}

	@Override
	public void removeWindowProviderListener(WindowProviderListener listener) {

	}

}
