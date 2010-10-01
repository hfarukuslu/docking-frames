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

	private final Rectangle parkLeft;
	private final Rectangle parkRight;

	private Rectangle parkCurrent;

	public static final int RATIO = 6;

	public AP_BoundaryRestriction() {

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

		log.info("screen : {}", screen);

		parkLeft = new Rectangle(0, 0, screen.width / RATIO, screen.height);

		log.info("left : {}", parkLeft);

		parkRight = new Rectangle(screen.width - screen.width / RATIO, 0,
				screen.width / RATIO, screen.height);

		log.info("right : {}", parkRight);

	}

	@Override
	public Rectangle check(ScreenDockWindow window) {

		log.info("check 1");

		Rectangle bounds = window.getWindowBounds();

		int overLeft = areaOverlap(parkLeft, bounds);
		int overRight = areaOverlap(parkRight, bounds);

		if (overLeft >= overRight) {
			parkCurrent = parkLeft;
		} else {
			parkCurrent = parkRight;
		}

		return parkCurrent;

	}

	@Override
	public Rectangle check(ScreenDockWindow window, Rectangle target) {

		// log.info("check 2; window : {}", window.getWindowBounds());
		// log.info("check 2; target : {}", target);

		int overLeft = areaOverlap(parkLeft, target);
		int overRight = areaOverlap(parkRight, target);

		// log.info("check 2; left: {} right: {}", overLeft, overRight);

		if (overLeft > overRight) {
			parkCurrent = parkLeft;
		}

		if (overLeft < overRight) {
			parkCurrent = parkRight;
		}

		return parkCurrent;

	}

	private int areaOverlap(Rectangle a, Rectangle b) {

		if (a.intersects(b)) {

			Rectangle overlap = new Rectangle();

			Rectangle.intersect(a, b, overlap);

			return overlap.width * overlap.height;

		}

		return 0;

	}

}
