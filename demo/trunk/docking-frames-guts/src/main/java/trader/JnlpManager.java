package trader;

import javax.jnlp.ServiceManager;
import javax.jnlp.SingleInstanceListener;
import javax.jnlp.SingleInstanceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

@Singleton
public class JnlpManager {

	static private final Logger log = LoggerFactory
			.getLogger(JnlpManager.class);

	// private final Injector guice;

	// @Inject
	// public JnlpManager(Injector guice) {
	// this.guice = guice;
	// }

	public boolean isJnlpPresent() {
		try {
			/*
			 * fails with various exceptions if javaws.jar is missing or not
			 * running in javaws mode
			 */
			ServiceManager.lookup(SingleInstanceListener.class.getName());
			return true;
		} catch (Throwable e) {
			log.debug("failed : {}", e.getMessage());
			return false;
		}
	}

	private JnlpSingleInstance instance;

	public synchronized void registerSingleInstance() {

		if (!isJnlpPresent()) {
			log.warn("javax jnlp is not present");
			return;
		}

		if (instance != null) {
			log.error("instance already registered");
			return;
		}

		try {

			// final JnlpSingleInstance single = guice
			// .getInstance(JnlpSingleInstance.class);

			JnlpSingleInstance single = new JnlpSingleInstance();

			final SingleInstanceService service = (SingleInstanceService) ServiceManager
					.lookup(SingleInstanceService.class.getName());

			service.addSingleInstanceListener(single);

			instance = single;

		} catch (Throwable e) {
			log.error("", e);
		}

	}

	public synchronized void unregisterSingleInstance() {

		if (!isJnlpPresent()) {
			log.warn("javax jnlp is not present");
			return;
		}

		if (instance == null) {
			log.error("no instance is registered");
			return;
		}

		try {

			final SingleInstanceService service = (SingleInstanceService) ServiceManager
					.lookup(SingleInstanceService.class.getName());

			service.removeSingleInstanceListener(instance);

			instance = null;

		} catch (Throwable e) {
			log.error("", e);
		}

	}

}
