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

		private Point pressPoint;

		private Rectangle currentBounds;

		@Override
		public void mousePressed(MouseEvent e) {

			currentBounds = new Rectangle(getWindowBounds());
			pressPoint = new Point(e.getPoint());

			SwingUtilities.convertPointToScreen(pressPoint, e.getComponent());

			log.debug("press : {}", pressPoint);

		}

		@Override
		public void mouseDragged(MouseEvent e) {

			Point draggPoint = new Point(e.getPoint());

			SwingUtilities.convertPointToScreen(draggPoint, e.getComponent());

			Rectangle draggBounds = new Rectangle(currentBounds);

			// log.debug("dragg : {}", draggPoint);

			int dx = draggPoint.x - pressPoint.x;
			int dy = draggPoint.y - pressPoint.y;

			draggBounds.x += dx;
			draggBounds.y += dy;

			//
			Rectangle updatedBounds = getStation().getBoundaryRestriction()
					.check(AP_ScreenDockDialog.this, draggBounds);

			if (currentBounds.x == updatedBounds.x) {
				// do not re-validate
				return;
			}

			// fire re-validate;
			setWindowBounds(draggBounds);

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