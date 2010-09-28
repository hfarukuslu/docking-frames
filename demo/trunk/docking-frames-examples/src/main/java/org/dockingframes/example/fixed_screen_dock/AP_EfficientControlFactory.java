package org.dockingframes.example.fixed_screen_dock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bibliothek.gui.dock.common.intern.EfficientControlFactory;

public class AP_EfficientControlFactory extends EfficientControlFactory {

	static private final Logger log = LoggerFactory
			.getLogger(AP_EfficientControlFactory.class);

	// @Override
	// public FlapDockStation createFlapDockStation(final Component expansion) {
	//
	// return new AP_FlapDockStation() {
	// @Override
	// public Rectangle getExpansionBounds() {
	// Point point = new Point(0, 0);
	// point = SwingUtilities.convertPoint(this.getComponent(), point,
	// expansion);
	// return new Rectangle(-point.x, -point.y, expansion.getWidth(),
	// expansion.getHeight());
	// }
	// };
	//
	// }

}
