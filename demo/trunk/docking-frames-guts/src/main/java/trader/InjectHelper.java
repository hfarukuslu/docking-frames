package trader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

public class InjectHelper {

	static private final Logger log = LoggerFactory
			.getLogger(InjectHelper.class);

	// public static <T> Module newModule(final T instance) {
	//
	// @SuppressWarnings("unchecked")
	// final Class<T> klaz = (Class<T>) instance.getClass();
	//
	// log.debug("klaz : {}", klaz.getName());
	//
	// final Module mod = new AbstractModule() {
	// @Override
	// protected void configure() {
	// bind(klaz).toInstance(instance);
	// }
	// };
	//
	// return mod;
	//
	// }

	public static <T> Module newModule(final Class<T> klaz, final T instance) {

		log.debug("klaz : {}", klaz.getName());
		log.debug("instance : {}", instance);

		final Module mod = new AbstractModule() {
			@Override
			protected void configure() {
				bind(klaz).toInstance(instance);
			}
		};

		return mod;

	}

	// public static <R extends RootPaneContainer> Module newModule(
	// final R instance) {
	//
	// log.debug("RootPaneContainer : {}", instance);
	//
	// final Module mod = new AbstractModule() {
	// @Override
	// protected void configure() {
	// bind(RootPaneContainer.class).toInstance(instance);
	// }
	// };
	//
	// return mod;
	//
	// }

	// public static <W extends WindowProvider> Module newModule(final W
	// instance) {
	//
	// log.debug("WindowProvider : {}", instance);
	//
	// final Module mod = new AbstractModule() {
	// @Override
	// protected void configure() {
	// bind(WindowProvider.class).toInstance(instance);
	// }
	// };
	//
	// return mod;
	//
	// }

	public static <B, C extends B> Module newModule(//
			final Class<B> klazBase, final Class<C> klazDerived) {

		log.debug("base : {}   derived : {}", klazBase, klazDerived);

		final Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(klazBase).to(klazDerived);
			}
		};

		return module;

	}
}
