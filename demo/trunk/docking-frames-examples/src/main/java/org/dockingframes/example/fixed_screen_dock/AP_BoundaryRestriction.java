package org.dockingframes.example.fixed_screen_dock;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bibliothek.gui.dock.station.screen.BoundaryRestriction;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;

public class AP_BoundaryRestriction implements BoundaryRestriction {

	static private final Logger log = LoggerFactory
			.getLogger(AP_BoundaryRestriction.class);

	private final Rectangle left;
	private final Rectangle right;

	public AP_BoundaryRestriction() {

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

		log.info("screen : {}", screen);

		left = new Rectangle(0, 0, screen.width / 6, screen.height);

		right = new Rectangle(screen.width - screen.width / 6, 0,
				screen.width / 6, screen.height);

	}

	@Override
	public Rectangle check(ScreenDockWindow window) {

		log.info("check 1");

		Rectangle bounds = window.getWindowBounds();

		if (areaOverlap(left, bounds) > areaOverlap(right, bounds)) {
			return left;
		} else {
			return right;
		}

	}

	@Override
	public Rectangle check(ScreenDockWindow window, Rectangle target) {

		log.info("check 2; window : {}", window);
		log.info("check 2; target : {}", target);

		int overLeft = areaOverlap(left, target);
		int overRight = areaOverlap(right, target);

		log.info("check 2; left: {} right: {}", overLeft, overRight);

		if (overLeft > overRight) {
			return left;
		} else {
			return right;
		}

	}

	private int areaOverlap(Rectangle a, Rectangle b) {

		if (a.intersects(b)) {

			Rectangle dest = new Rectangle();

			Rectangle.intersect(a, b, dest);

			return dest.width * dest.height;

		}

		return 0;

	}

}
