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

		private Point press;

		private Rectangle bounds;

		@Override
		public void mousePressed(MouseEvent e) {

			bounds = new Rectangle(getWindowBounds());
			press = new Point(e.getPoint());

			SwingUtilities.convertPointToScreen(press, e.getComponent());

			log.debug("press : {}", press);

		}

		@Override
		public void mouseDragged(MouseEvent e) {

			Point dragg = new Point(e.getPoint());

			SwingUtilities.convertPointToScreen(dragg, e.getComponent());

			Rectangle bounds = new Rectangle(this.bounds);

			log.debug("dragg : {}", dragg);

			int dx = dragg.x - press.x;
			int dy = dragg.y - press.y;

			bounds.x += dx;
			bounds.y += dy;

			// fires restriction.check()
			setWindowBounds(bounds);

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