package org.dockingframes.example.flap_title_direction;

import static bibliothek.gui.dock.title.DockTitle.Orientation.*;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.title.DockTitle;

public class AP_FlapDockStation extends FlapDockStation {

	@Override
	protected DockTitle.Orientation orientation(Direction direction) {

		switch (direction) {

		case NORTH:
			return NORTH_SIDED;

		case SOUTH:
			return SOUTH_SIDED;

		case WEST:
			return WEST_SIDED;

		case EAST:
			return EAST_SIDED;

		}

		return null;

	}

}
