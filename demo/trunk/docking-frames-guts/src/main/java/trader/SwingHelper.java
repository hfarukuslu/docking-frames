package trader;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* central intercept for debugging, logging, statistics */

public final class SwingHelper {

	final static Logger log = LoggerFactory.getLogger(SwingHelper.class);

	final static String PROP_DEBUG_EDT = "barchart.debug.edt";

	final static boolean isDebugEDT;

	static {

		String prop = System.getProperty(PROP_DEBUG_EDT);

		if (prop == null) {
			isDebugEDT = false;
		} else {
			isDebugEDT = true;
		}

	}

	private SwingHelper() {
	}

	public static final boolean isEDT() {

		return EventQueue.isDispatchThread();

	}

	public static final void invokeFromEDT(final Runnable task) {

		if (EventQueue.isDispatchThread()) {
			task.run();
		} else {
			EventQueue.invokeLater(task);
		}

	}

	public static final void invokeLater(final Runnable task) {

		if (isDebugEDT) {
			if (EventQueue.isDispatchThread()) {
				Exception e = new Exception();
				StackTraceElement element = e.getStackTrace()[1];
				log.error("\n\t already in edt :: {}", element);
			}
		}

		EventQueue.invokeLater(task);

	}

	public static final void invokeWaiting(final Runnable task) {

		try {
			EventQueue.invokeAndWait(task);
		} catch (Exception e) {
			log.error("", e);
		}

	}

	public static void logTree(Component child) {

		log.debug("child : {}", child);

		for (Container parent = child.getParent(); parent != null; parent = parent
				.getParent()) {

			log.debug("parent : {}", parent);

		}

	}

}
