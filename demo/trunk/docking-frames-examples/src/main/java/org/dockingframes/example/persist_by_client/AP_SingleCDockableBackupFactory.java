package org.dockingframes.example.persist_by_client;

import java.awt.Color;

import javax.swing.JPanel;

import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.SingleCDockableBackupFactory;

public class AP_SingleCDockableBackupFactory implements
		SingleCDockableBackupFactory {

	@Override
	public SingleCDockable createBackup(String id) {

		JPanel panel = new JPanel();
		panel.setOpaque(true);

		Color color = Color.RED;

		panel.setBackground(color);

		String title = "red";

		DefaultSingleCDockable dockable = new DefaultSingleCDockable(title,
				title, panel);

		return dockable;

	}

}
