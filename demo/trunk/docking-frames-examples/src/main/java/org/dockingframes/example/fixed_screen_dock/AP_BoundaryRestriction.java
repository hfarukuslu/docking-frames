package org.dockingframes.example.fixed_screen_dock;

import java.awt.Rectangle;

import bibliothek.gui.dock.station.screen.BoundaryRestriction;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;

public class AP_BoundaryRestriction implements BoundaryRestriction {

	@Override
	public Rectangle check(ScreenDockWindow window) {
		// TODO Auto-generated method stub

		return new Rectangle(0, 0, 500, 500);

	}

	@Override
	public Rectangle check(ScreenDockWindow window, Rectangle target) {
		// TODO Auto-generated method stub

		return new Rectangle(50, 50, 250, 500);

	}

}
