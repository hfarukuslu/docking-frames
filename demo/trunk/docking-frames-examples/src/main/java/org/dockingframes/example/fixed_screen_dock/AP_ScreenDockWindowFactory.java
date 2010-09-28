package org.dockingframes.example.fixed_screen_dock;

import java.awt.Window;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.AbstractScreenDockWindow;
import bibliothek.gui.dock.station.screen.DefaultScreenDockWindowFactory;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;

public class AP_ScreenDockWindowFactory extends DefaultScreenDockWindowFactory {

	static private final Logger log = LoggerFactory
			.getLogger(AP_ScreenDockWindowFactory.class);

	@Override
	public ScreenDockWindow createWindow(ScreenDockStation station) {

		log.debug("station : {}", station);

		Window stationOwner = station.getOwner();

		log.debug("stationOwner : {}", stationOwner);

		AbstractScreenDockWindow window;

		window = new AP_ScreenDockDialog(station, (JFrame) stationOwner);

		window.setShowTitle(isShowDockTitle());
		window.setTitleIcon(getTitleIcon());
		window.setTitleText(getTitleText());

		return window;

	}

}
