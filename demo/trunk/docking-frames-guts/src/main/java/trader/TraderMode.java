package trader;

import static trader.InjectHelper.*;

import java.applet.Applet;
import java.awt.Window;
import java.util.List;

import net.guts.event.EventModule;
import net.guts.gui.exception.ExceptionHandlingModule;
import net.guts.gui.exit.ExitModule;
import net.guts.gui.message.MessageModule;
import net.guts.gui.resource.ResourceModule;
import net.guts.gui.resource.Resources;
import net.guts.gui.session.Sessions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bibliothek.gui.dock.util.WindowProvider;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

public enum TraderMode implements InjectInit {

	TEST(null), //

	APPLET(AP_WindowProviderForApplet.class), //

	APPLICATION(AP_WindowProviderForFrame.class), //

	;

	static private final Logger log = LoggerFactory
			.getLogger(TraderApplication.class);

	private Class<? extends WindowProvider> klazWindowProvider;

	<T extends WindowProvider> TraderMode(Class<T> klaz) {
		this.klazWindowProvider = klaz;
	}

	@Override
	public void initMods(List<Module> list) {

		log.debug("run");

		list.add(newModule(TraderMode.class, this));

		list.add(newModule(WindowProvider.class, klazWindowProvider));

		initModsCommon(list);

	}

	private static void initModsCommon(List<Module> list) {

		list.add(new ResourceModule());

		list.add(new ExceptionHandlingModule());

		list.add(new MessageModule());

		list.add(new EventModule());

		list.add(new ExitModule());

		list.add(new AbstractModule() {

			@Override
			protected void configure() {

				//

				Sessions.bindApplicationClass(binder(), //
						TraderCore.class);

				Sessions.bindSessionConverter(binder(), //
						Applet.class).to(StateApplet.class);

				Sessions.bindSessionConverter(binder(), //
						Window.class).to(StateWindow.class);

				//

				Resources.bindRootBundle(binder(), //
						RootResources.class, RootResources.FILE_NAME);

			}

		});

	}

}
