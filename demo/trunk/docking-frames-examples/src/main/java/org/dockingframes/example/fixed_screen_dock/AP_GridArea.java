package org.dockingframes.example.fixed_screen_dock;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGridArea;

public class AP_GridArea extends CGridArea {

	public AP_GridArea(CControl control, String guid) {
		super(control, guid);
	}

	@Override
	public boolean isExternalizable() {
		return true;
	}

}
