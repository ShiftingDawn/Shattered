package shattered.core;

import java.nio.file.Paths;
import java.util.logging.Logger;
import shattered.bridge.ShatteredEntryPoint;
import shattered.core.event.EventBusImpl;
import shattered.lib.Internal;

@ShatteredEntryPoint
public final class Shattered {

	public static final Logger LOGGER = Logger.getLogger("Shattered");

	private Shattered(final String[] args) {
		Internal.ROOT_PATH = Paths.get(args[0]).toAbsolutePath();
		EventBusImpl.init();
	}
}
