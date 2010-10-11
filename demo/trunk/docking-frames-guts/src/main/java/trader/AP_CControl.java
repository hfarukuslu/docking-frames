package trader;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.util.WindowProvider;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AP_CControl extends CControl {

	@Inject
	public AP_CControl(//
			final WindowProvider windowProvider, //
			final Object none //
	) {

		// super((JFrame) null);
		super(windowProvider);

	}

}
