package trader;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.RootPaneContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Module;

@SuppressWarnings("serial")
public class TraderApplet extends JApplet {

	static private final Logger log = LoggerFactory
			.getLogger(TraderApplet.class);

	private TraderCore core;

	public TraderApplet() {
		log.debug("live");
	}

	@Override
	public synchronized void init() {

		final JApplet root = this;

		List<Module> list = new ArrayList<Module>();
		list.add(InjectHelper.newModule(RootPaneContainer.class, root));
		TraderMode.APPLET.initMods(list);

		core = GutsTest.getInstance(TraderCore.class, list);

		core.showtime();

	}

	@Override
	public synchronized void destroy() {

		core.shutdown();

	}

}
