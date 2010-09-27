package org.dockingframes.example.flap_title_direction.beni;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.EmptyMultipleCDockableFactory;
import bibliothek.gui.dock.common.MultipleCDockable;
import bibliothek.gui.dock.common.MultipleCDockableFactory;
import bibliothek.gui.dock.common.MultipleCDockableLayout;
import bibliothek.gui.dock.common.layout.ThemeMap;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitle.Orientation;
import bibliothek.gui.dock.title.OrientationToRotationStrategy;
import bibliothek.gui.dock.title.OrientationToRotationStrategyListener;
import bibliothek.gui.dock.util.swing.Rotation;

/**
 * example by Beni:
 * 
 * http://forum.byte-welt.net/showthread.php?t=3038
 * 
 * */
public class Dock15 {
	public static DefaultMultipleCDockable createDockable(
			MultipleCDockableFactory<MultipleCDockable, MultipleCDockableLayout> factory,
			String title, Color color) {
		JPanel panel = new JPanel();
		panel.setOpaque(true);
		panel.setBackground(color);
		final DefaultMultipleCDockable dockable = new DefaultMultipleCDockable(
				factory, title, panel);
		dockable.setTitleIcon(new RectIcon());

		return dockable;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("Demo");
				CControl control = new CControl(frame);

				control.putProperty(DockTitle.ORIENTATION_STRATEGY,
						new RotationStrategy());

				// ignore @deprecated
				control.setTheme(ThemeMap.KEY_ECLIPSE_THEME);

				frame.add(control.getContentArea(), BorderLayout.CENTER);

				CGrid grid = new CGrid(control);

				MultipleCDockableFactory<MultipleCDockable, MultipleCDockableLayout> factory = new EmptyMultipleCDockableFactory<MultipleCDockable>() {
					@Override
					public MultipleCDockable createDockable() {
						return null;
					}
				};

				control.addMultipleDockableFactory("color", factory);

				grid = new CGrid(control);
				grid.add(0, 0, 1, 1,
						createDockable(factory, "Yellow", Color.YELLOW));
				grid.add(0, 1, 1, 1, createDockable(factory, "Red", Color.RED));
				control.getContentArea().deploy(grid);

				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setBounds(20, 20, 400, 400);
				frame.setVisible(true);
			}
		});
	}

	private static class RectIcon implements Icon {
		@Override
		public int getIconWidth() {
			return 16;
		}

		@Override
		public int getIconHeight() {
			return 16;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(Color.RED);
			g.fillRect(x, y, 16, 16);
		}
	}

	private static class RotationStrategy implements
			OrientationToRotationStrategy, DockHierarchyListener {
		private final Set<DockTitle> titles = new HashSet<DockTitle>();
		private final List<OrientationToRotationStrategyListener> listeners = new ArrayList<OrientationToRotationStrategyListener>();

		@Override
		public Rotation convert(Orientation orientation, DockTitle title) {
			Dockable dockable = title.getDockable();
			if (dockable.getDockParent() instanceof FlapDockStation) {
				if (title.getOrigin() != null
						&& title.getOrigin().getID()
								.equals(FlapDockStation.BUTTON_TITLE_ID)) {
					if (orientation.isHorizontal()) {
						return Rotation.DEGREE_180;
					} else {
						return Rotation.DEGREE_270;
					}
				}
			}

			if (orientation.isHorizontal()) {
				return Rotation.DEGREE_0;
			} else {
				return Rotation.DEGREE_90;
			}
		}

		@Override
		public void install(DockTitle title) {
			if (!monitored(title.getDockable())) {
				title.getDockable().addDockHierarchyListener(this);
			}
			titles.add(title);
		}

		@Override
		public void uninstall(DockTitle title) {
			titles.remove(title);
			if (!monitored(title.getDockable())) {
				title.getDockable().removeDockHierarchyListener(this);
			}
		}

		@Override
		public void controllerChanged(DockHierarchyEvent event) {
			// ignore
		}

		@Override
		public void hierarchyChanged(DockHierarchyEvent event) {
			for (OrientationToRotationStrategyListener listener : listeners) {
				listener.rotationChanged(event.getDockable(), null);
			}
		}

		private boolean monitored(Dockable dockable) {
			for (DockTitle title : titles) {
				if (title.getDockable() == dockable) {
					return true;
				}
			}
			return false;
		}

		@Override
		public void addListener(OrientationToRotationStrategyListener listener) {
			listeners.add(listener);
		}

		@Override
		public void removeListener(
				OrientationToRotationStrategyListener listener) {
			listeners.remove(listener);
		}
	}
}