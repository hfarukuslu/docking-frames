package trader;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.RootPaneContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Module;

public class TraderApplication {

	static private final Logger log = LoggerFactory
			.getLogger(TraderApplication.class);

	public static void main(String... args) {

		log.debug("live");

		final JFrame root = new JFrame();
		root.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		List<Module> list = new ArrayList<Module>();
		list.add(InjectHelper.newModule(RootPaneContainer.class, root));
		TraderMode.APPLICATION.initMods(list);

		final TraderCore core = GutsTest.getInstance(TraderCore.class, list);

		root.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				core.shutdown();

			}
		});

		core.showtime();

	}

}
