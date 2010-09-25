package org.dockingframes.example.flap_title_direction;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.SwingUtilities;

import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.common.intern.EfficientControlFactory;

public class AP_EfficientControlFactory extends EfficientControlFactory {

	@Override
	public FlapDockStation createFlapDockStation(final Component expansion) {

		return new AP_FlapDockStation() {
			@Override
			public Rectangle getExpansionBounds() {
				Point point = new Point(0, 0);
				point = SwingUtilities.convertPoint(this.getComponent(), point,
						expansion);
				return new Rectangle(-point.x, -point.y, expansion.getWidth(),
						expansion.getHeight());
			}
		};

	}

}
