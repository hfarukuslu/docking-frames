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
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.layout.FullLockConflictResolver;

/**
 * 
 * http://forum.byte-welt.net/showthread.php?t=3036&page=2
 * 
 * */
public class Main_3036_2 {

	static private final Logger log = LoggerFactory.getLogger(Main_3036_2.class);

	public static void main(String[] args) {

		log.info("started");

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(1, 1));

		//

		CControl control = new CControl(frame);

		control.putProperty(ScreenDockStation.WINDOW_FACTORY,
				new AP_ScreenDockWindowFactory());

		control.putProperty(ScreenDockStation.BOUNDARY_RESTRICTION,
				new AP_BoundaryRestriction());

		control.putProperty(CControl.RESIZE_LOCK_CONFLICT_RESOLVER,
				new FullLockConflictResolver());

		CGridArea gridOne = new AP_GridArea(control, "grid one");
		CGridArea gridTwo = new AP_GridArea(control, "grid two");

		control.add(gridOne);
		control.add(gridTwo);

		CLocation locationOne = CLocation.external(0, 0, 560, 1500);
		CLocation locationTwo = CLocation.external(2800, 0, 560, 1500);

		gridOne.setLocation(locationOne);
		gridTwo.setLocation(locationTwo);

		//

		DefaultSingleCDockable red = create("Red", Color.RED);
		DefaultSingleCDockable green = create("Green", Color.GREEN);

		control.add(red);
		control.add(green);

		//

		CGrid gridOneLayout = new CGrid();
		gridOneLayout.add(0, 0, 1, 1, red);
		gridOne.deploy(gridOneLayout);

		//

		CGrid gridTwoLayout = new CGrid();
		gridTwoLayout.add(0, 0, 1, 1, green);
		gridTwo.deploy(gridTwoLayout);

		//

		logStations(control);

		//

		frame.add(control.getContentArea());
		frame.setSize(new Dimension(700, 700));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		log.info("finished");

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
