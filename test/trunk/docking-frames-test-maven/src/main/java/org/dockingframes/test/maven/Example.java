package org.dockingframes.test.maven;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.layout.FullLockConflictResolver;

public class Example {

	public static void main(String[] args) {

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//

		CControl control = new CControl(frame);
		frame.add(control.getContentArea());

		control.putProperty(CControl.RESIZE_LOCK_CONFLICT_RESOLVER,
				new FullLockConflictResolver());

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

		freeze(red);
		freeze(blue);
		freeze(green);

		//

		frame.setSize(new Dimension(500, 500));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

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

}
