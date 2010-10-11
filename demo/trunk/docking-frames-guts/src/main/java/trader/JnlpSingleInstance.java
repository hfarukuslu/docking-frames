package trader;

import javax.jnlp.SingleInstanceListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Singleton
public class JnlpSingleInstance implements SingleInstanceListener {

	static private final Logger log = LoggerFactory
			.getLogger(JnlpSingleInstance.class);

	// private final DialogFactory dialogFactory;

	// @Inject
	// public JnlpSingleInstance(DialogFactory dialogFactory) {
	// this.dialogFactory = dialogFactory;
	// }

	@Override
	public void newActivation(String[] args) {

		log.warn("trying to activate : {}", args);

		// dialogFactory.showDialog(clazz, bounds, state);

	}

}
