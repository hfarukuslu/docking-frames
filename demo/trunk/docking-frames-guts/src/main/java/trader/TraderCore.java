package trader;

import java.awt.Color;
import java.awt.Component;
import java.awt.Window;

import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

import net.guts.gui.exit.ExitController;
import net.guts.gui.message.MessageFactory;
import net.guts.gui.message.UserChoice;
import net.guts.gui.simple.application.AP_WindowController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class TraderCore {

	static private final Logger log = LoggerFactory.getLogger(TraderCore.class);

	private final TraderMode mode;
	private final RootPaneContainer root;
	private final JnlpManager jnlpManager;
	private final ExitController exitController;
	private final AP_WindowController windowController;
	private final TraderGui gui;
	private final MessageFactory messageFactory;

	private final AP_CControl dockControl;

	@Inject
	TraderCore(//
			final TraderMode mode, //
			final TraderGui gui, //
			final RootPaneContainer root, //
			final JnlpManager jnlpManager, //
			final AP_WindowController windowController, //
			final MessageFactory messageFactory, //
			final ExitController exitController, //
			final AP_CControl dockControl, //
			final Object none //
	) {

		this.mode = mode;
		this.gui = gui;
		this.root = root;
		this.jnlpManager = jnlpManager;
		this.exitController = exitController;
		this.windowController = windowController;
		this.messageFactory = messageFactory;
		this.dockControl = dockControl;

		setName();

		log.debug("mode :\n\t {}\n\t root : {}", mode, root);

		SwingHelper.logTree(root.getRootPane());

	}

	void setName() {

		String name = RootResources.ROOT_NAME;

		log.debug("name : {}", name);

		((Component) root).setName(name);

		Window window = SwingUtilities.getWindowAncestor(root.getRootPane());

		window.setName(name);

	}

	void showGui() {

		root.getContentPane().add(gui);

		root.getContentPane().setBackground(Color.BLACK);

		windowController.show(root);

	}

	void showtime() {

		log.debug("showtime");

		jnlpManager.registerSingleInstance();

		Runnable task = new Runnable() {
			@Override
			public void run() {
				showGui();
			}
		};

		SwingHelper.invokeWaiting(task);

	}

	void shutdown() {

		switch (mode) {
		case APPLICATION:
			UserChoice choice = messageFactory.showMessage("confirm-exit");
			switch (choice) {
			case YES:
				break;
			default:
				return;
			}
		}

		jnlpManager.unregisterSingleInstance();

		exitController.shutdown();

		log.debug("shutdown");

	}

}
