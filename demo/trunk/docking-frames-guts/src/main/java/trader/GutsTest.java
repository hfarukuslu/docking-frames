package trader;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.RootPaneContainer;

import net.guts.common.injection.InjectionListeners;
import net.guts.gui.simple.util.Helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class GutsTest {

	static private final Logger log = LoggerFactory.getLogger(GutsTest.class);

	public static <R extends RootPaneContainer> void inject(
			final Object instance) {

		final Runnable launchTask = new Runnable() {

			@Override
			public void run() {

				log.debug("instance : {}", instance);

				List<Module> moduleList = new ArrayList<Module>();

				// hacks
				Helper.initMods(moduleList);

				Injector injector = Guice.createInjector(moduleList);

				InjectionListeners.injectListeners(injector);

				injector.injectMembers(instance);

			}

		};

		try {
			EventQueue.invokeAndWait(launchTask);
		} catch (Exception e) {
			log.error("", e);
		}

		log.debug("done");

	}

	static class Wrapper<T> {
		T value;
	}

	public static <T> T getInstance(final Class<T> klaz, final List<Module> list) {

		final Wrapper<T> wrapper = new Wrapper<T>();

		final Runnable launchTask = new Runnable() {

			@Override
			public void run() {

				log.debug("klaz : {}", klaz);

				// hacks
				Helper.initMods(list);

				Injector injector = Guice.createInjector(list);

				InjectionListeners.injectListeners(injector);

				wrapper.value = injector.getInstance(klaz);

			}

		};

		try {
			EventQueue.invokeAndWait(launchTask);
		} catch (Exception e) {
			log.error("", e);
		}

		log.debug("done");

		return wrapper.value;

	}

}
