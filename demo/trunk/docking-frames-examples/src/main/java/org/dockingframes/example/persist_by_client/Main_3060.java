package org.dockingframes.example.persist_by_client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.common.CContentArea;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CControlRegister;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.SingleCDockableBackupFactory;
import bibliothek.gui.dock.common.layout.ThemeMap;
import bibliothek.gui.dock.support.util.ApplicationResourceManager;
import bibliothek.util.xml.XElement;

/**
 * 
 * http://forum.byte-welt.net/showthread.php?t=3060
 * 
 * */

public class Main_3060 {

	static private final Logger log = LoggerFactory.getLogger(Main_3060.class);

	public static void main(String[] args) {

		log.info("started");

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(1, 1));

		//

		CControl control = new CControl(frame);

		control.setTheme(ThemeMap.KEY_ECLIPSE_THEME);

		CContentArea contentArea = control.getContentArea();

		//

		SplitDockStation center = contentArea.getCenter();

		DefaultSingleCDockable red = create("red", Color.RED);

		center.drop(red.intern().asDockable());

		//

		ApplicationResourceManager resourceManager = control.getResources();

		XElement element = new XElement("control");

		resourceManager.writeXML(element);

		String elementString = element.toString();

		log.info("{}", elementString);

		//

		frame.add(contentArea);
		frame.setSize(new Dimension(700, 700));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		sleep(1 * 1000);

		log.info("read xml, no backup");

		resourceManager.readXML(element);

		sleep(1 * 1000);

		log.info("read xml, from backup");

		String id = "red";
		control.addSingleBackupFactory(id, backupFactory);

		resourceManager.readXML(element);

		sleep(1 * 1000);

		CControlRegister register = control.getRegister();

		List<SingleCDockable> dockableList = register.getSingleDockables();

		for (SingleCDockable dockable : dockableList) {

			log.info("dockable : {}", dockable);

			Component component = dockable.intern().getComponent();

			log.info("component : {}", component.getName());

			Container container = (Container) component;

			for (Component comp : container.getComponents()) {

				log.info("comp : {}", comp.getName());

			}

		}

		log.info("finished");

	}

	static SingleCDockableBackupFactory backupFactory = new AP_SingleCDockableFactory();

	static DefaultSingleCDockable create(String title, Color color) {

		JPanel panel = new JPanel();
		panel.setOpaque(true);
		panel.setBackground(color);

		DefaultSingleCDockable dockable = new DefaultSingleCDockable(title,
				title, panel);

		return dockable;

	}

	static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}