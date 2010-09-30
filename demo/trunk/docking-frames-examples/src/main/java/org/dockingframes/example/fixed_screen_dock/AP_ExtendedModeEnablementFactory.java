package org.dockingframes.example.fixed_screen_dock;

import bibliothek.gui.dock.facile.mode.LocationModeManager;
import bibliothek.gui.dock.facile.mode.status.ExtendedModeEnablement;
import bibliothek.gui.dock.facile.mode.status.ExtendedModeEnablementFactory;

public class AP_ExtendedModeEnablementFactory implements
		ExtendedModeEnablementFactory {

	@Override
	public ExtendedModeEnablement create(LocationModeManager<?> manager) {
		return new AP_ExtendedModeEnablement(manager);
	}

}
