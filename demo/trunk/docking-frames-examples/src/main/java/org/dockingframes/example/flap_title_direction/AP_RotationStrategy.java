package org.dockingframes.example.flap_title_direction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitle.Orientation;
import bibliothek.gui.dock.title.OrientationToRotationStrategy;
import bibliothek.gui.dock.title.OrientationToRotationStrategyListener;
import bibliothek.gui.dock.util.swing.Rotation;

public class AP_RotationStrategy implements OrientationToRotationStrategy,
		DockHierarchyListener {

	private final Set<DockTitle> titles = new HashSet<DockTitle>();

	private final List<OrientationToRotationStrategyListener> listeners = //
	new ArrayList<OrientationToRotationStrategyListener>();

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
	public void removeListener(OrientationToRotationStrategyListener listener) {
		listeners.remove(listener);
	}

}