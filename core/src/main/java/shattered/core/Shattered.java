package shattered.core;

import java.util.logging.Logger;
import shattered.bridge.ShatteredEntryPoint;
import shattered.core.event.EventBusImpl;

@ShatteredEntryPoint
public final class Shattered {

	public static final Logger LOGGER = Logger.getLogger("Shattered");

	private Shattered(final String[] args) {
		EventBusImpl.init();
	}
}
