package org.dockingframes.example.flap_bugs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.common.CContentArea;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.layout.ThemeMap;

/**
 * 
 * http://forum.byte-welt.net/showthread.php?t=3046
 * 
 * */

public class Main_3046 {

	public static void main(String[] args) {

		JFrame frame = new JFrame();

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setLayout(new GridLayout(1, 1));

		//

		CControl control = new CControl(frame);

		control.setTheme(ThemeMap.KEY_ECLIPSE_THEME);

		CContentArea contentArea = control.getContentArea();

		frame.add(contentArea);

		//

		FlapDockStation north = contentArea.getNorth();
		SplitDockStation center = contentArea.getCenter();

		DefaultSingleCDockable red = create("red", Color.RED);
		DefaultSingleCDockable green = create("green", Color.GREEN);

		north.add(green.intern().asDockable());
		center.drop(red.intern().asDockable());

		//

		frame.setSize(new Dimension(700, 700));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

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