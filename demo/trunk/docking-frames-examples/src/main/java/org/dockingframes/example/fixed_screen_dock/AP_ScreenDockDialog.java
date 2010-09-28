package org.dockingframes.example.fixed_screen_dock;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.screen.ScreenDockDialog;

/**
 * 
 * http://forum.byte-welt.net/showthread.php?t=3036
 * 
 */

public class AP_ScreenDockDialog extends ScreenDockDialog {

	static private final Logger log = LoggerFactory
			.getLogger(AP_ScreenDockDialog.class);

	private SplitDockStation station;

	private final MouseInputListener listener = new MouseInputAdapter() {

		private Rectangle bounds;
		private Point start;

		@Override
		public void mousePressed(MouseEvent e) {

			log.debug("press");

			bounds = getWindowBounds();

			start = e.getPoint();

		}

		@Override
		public void mouseDragged(MouseEvent e) {

			log.debug("drag");

			Point point = e.getPoint();

			SwingUtilities.convertPointToScreen(point, e.getComponent());

			Rectangle bounds = new Rectangle(this.bounds);

			int dx = point.x - start.x;
			int dy = point.y - start.y;

			bounds.x += dx;
			bounds.y += dy;

			setWindowBounds(bounds);

			invalidate();
			validate();

		}

	};

	public AP_ScreenDockDialog(ScreenDockStation station, JFrame owner) {

		super(station, owner, true);

	}

	@Override
	public void setDockable(Dockable dockable) {

		super.setDockable(dockable);

		log.debug("dockable : {}", dockable);

		if (dockable instanceof SplitDockStation) {

			setStation((SplitDockStation) dockable);

		} else {

			setStation(null);

		}

	}

	private void setStation(SplitDockStation station) {

		if (this.station != null) {

			Component c = this.station.getContentPane();

			c.removeMouseListener(listener);
			c.removeMouseMotionListener(listener);

		}

		this.station = station;

		if (this.station != null) {

			Component c = this.station.getContentPane();

			c.addMouseListener(listener);
			c.addMouseMotionListener(listener);

		}

	}

	@Override
	public void destroy() {

		if (station != null) {
			station.removeMouseInputListener(listener);
		}

		super.destroy();

	}

}