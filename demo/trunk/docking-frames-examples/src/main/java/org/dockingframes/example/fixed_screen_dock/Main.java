package org.dockingframes.example.fixed_screen_dock;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.CGridArea;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.layout.FullLockConflictResolver;

public class Main {

	static private final Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {

		log.info("started");

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//

		// CControlFactory factory = new AP_EfficientControlFactory();
		// CControl control = new CControl(frame, factory);
		CControl control = new CControl(frame);

		frame.add(control.getContentArea());

		control.putProperty(ScreenDockStation.WINDOW_FACTORY,
				new AP_ScreenDockWindowFactory());

		control.putProperty(ScreenDockStation.BOUNDARY_RESTRICTION,
				new AP_BoundaryRestriction());

		control.putProperty(CControl.RESIZE_LOCK_CONFLICT_RESOLVER,
				new FullLockConflictResolver());

		CStation<ScreenDockStation> screenCommon = (CStation<ScreenDockStation>) control
				.getStation(CControl.EXTERNALIZED_STATION_ID);

		log.info("screen : {}", screenCommon.getUniqueId());

		CGridArea gridOne = control.createGridArea("grid one");
		// CGridArea gridTwo = control.createGridArea("grid two");

		control.add(gridOne);
		// control.add(gridTwo);

		ScreenDockStation screen = screenCommon.getStation();

		screen.drop(gridOne.getStation());
		// screen.drop(gridTwo.getStation());

		// CLocation locationOne = CLocation.external(0, 0, 400, 500);
		// CLocation locationTwo = CLocation.external(0, 600, 400, 500);

		// gridOne.setLocation(locationOne);
		// gridTwo.setLocation(locationTwo);

		logStations(control);

		frame.setLayout(new GridLayout(1, 1));

		//

		DefaultSingleCDockable red = create("Red", Color.RED);
		DefaultSingleCDockable green = create("Green", Color.GREEN);
		DefaultSingleCDockable blue = create("Blue", Color.BLUE);

		DefaultSingleCDockable gray1 = create("gray1", Color.GRAY);
		DefaultSingleCDockable gray2 = create("gray2", Color.GRAY);

		//
		control.add(red);
		control.add(green);
		control.add(blue);

		control.add(gray1);
		control.add(gray2);

		//

		CGrid grid = new CGrid();

		grid.add(0, 0, 1, 1, red);
		grid.add(1, 0, 1, 1, green);
		grid.add(2, 0, 1, 1, blue);

		grid.add(3, 0, 1, 1, gray1);
		grid.add(1, 1, 3, 1, gray2);

		control.getContentArea().deploy(grid);

		//

		// freeze(red);
		// freeze(blue);
		// freeze(green);

		//

		frame.setSize(new Dimension(700, 700));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		log.info("finished");

	}

	static void freeze(DefaultSingleCDockable dockable) {
		dockable.setCloseable(false);
		dockable.setMaximizable(false);
		dockable.setMinimizable(false);
		dockable.setResizeLocked(true);
	}

	static DefaultSingleCDockable create(String title, Color color) {

		JPanel panel = new JPanel();
		panel.setOpaque(true);
		panel.setBackground(color);

		DefaultSingleCDockable dockable = new DefaultSingleCDockable(title,
				title, panel);

		return dockable;

	}

	static void logStations(CControl control) {

		List<CStation<?>> list = control.getStations();

		for (CStation<?> station : list) {

			log.info("station : {}", station.getUniqueId());

		}

	}
}
