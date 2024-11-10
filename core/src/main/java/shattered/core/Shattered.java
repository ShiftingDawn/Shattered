package shattered.core;

import java.nio.file.Paths;
import java.util.logging.Logger;
import lombok.Getter;
import shattered.bridge.ShatteredEntryPoint;
import shattered.core.event.EventBusImpl;
import shattered.core.gfx.Display;
import shattered.lib.Internal;

@ShatteredEntryPoint
public final class Shattered {

	public static final String NAME = "Shattered";
	public static final Logger LOGGER = Logger.getLogger(Shattered.NAME);
	@Getter
	private static Shattered shattered;
	@Getter
	private final Runtime runtime = new Runtime();

	private Shattered(final String[] args) {
		Shattered.shattered = this;
		Internal.ROOT_PATH = Paths.get(args[0]).toAbsolutePath();
		EventBusImpl.init();
		Display.init();
		Display.openWindow();
		this.runtime.start();
	}
}
