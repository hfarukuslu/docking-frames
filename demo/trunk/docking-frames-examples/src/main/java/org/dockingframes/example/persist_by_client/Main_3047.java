package org.dockingframes.example.persist_by_client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.SingleCDockableBackupFactory;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;

/**
 * 
 * http://forum.byte-welt.net/showthread.php?t=3047&page=2
 * 
 * */

public class Main_3047 {

	public static void main(String[] args) throws IOException {

		// set up frame and control
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(1, 1));

		CControl control = new CControl(frame);

		frame.add(control.getContentArea());
		frame.setSize(new Dimension(700, 700));
		frame.setLocationRelativeTo(null);

		// register factories
		control.addSingleBackupFactory("red", new ViewFunctionFactory(
				new ColorViewFunction("red", Color.RED)));

		control.addSingleBackupFactory("green", new ViewFunctionFactory(
				new ColorViewFunction("green", Color.GREEN)));

		control.addSingleBackupFactory("blue", new ViewFunctionFactory(
				new ColorViewFunction("blue", Color.BLUE)));

		control.addSingleBackupFactory("magenta", new ViewFunctionFactory(
				new ColorViewFunction("magenta", Color.MAGENTA)));

		// load layout
		XElement root = XIO.read(createTestLayoutData());

		control.readXML(root);

		frame.setVisible(true);

	}

	// create a layout (yeah, not very nice. But currently there is no other
	// way)
	private static String createTestLayoutData() {

		CControl control = new CControl();

		CGrid grid = new CGrid(control);

		grid.add(0, 0, 50, 100, wrap(new ColorViewFunction("red", Color.RED)));

		grid.add(50, 0, 50, 50,
				wrap(new ColorViewFunction("green", Color.GREEN)));

		grid.add(50, 50, 50, 50,
				wrap(new ColorViewFunction("blue", Color.BLUE)));

		control.getContentArea().deploy(grid);

		XElement root = new XElement("root");

		control.writeXML(root);

		return root.toString();

	}

	// ViewFunction -> SingleCDockable
	public static SingleCDockable wrap(ViewFunction function) {
		return new DefaultSingleCDockable(function.getTitle(),
				function.getTitle(), function.getContent());
	}

	// Out backup-factory
	private static class ViewFunctionFactory implements
			SingleCDockableBackupFactory {

		private final ViewFunction function;

		public ViewFunctionFactory(ViewFunction function) {
			this.function = function;
		}

		@Override
		public SingleCDockable createBackup(String id) {
			System.out.println("using factory for: " + id);
			return wrap(function);
		}

	}

	// your view-function (simplified version)
	private static interface ViewFunction {

		public String getTitle();

		public JComponent getContent();

	}

	// finally an implementation of a view-function (also simplified)
	private static class ColorViewFunction implements ViewFunction {

		private final String title;

		private final Color color;

		public ColorViewFunction(String title, Color color) {
			this.title = title;
			this.color = color;
		}

		@Override
		public String getTitle() {
			return title;
		}

		@Override
		public JComponent getContent() {
			JPanel panel = new JPanel();
			panel.setOpaque(true);
			panel.setBackground(color);
			return panel;
		}

	}

}