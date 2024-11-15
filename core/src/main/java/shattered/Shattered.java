package shattered;

import java.io.File;
import java.io.IOException;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shattered.core.event.EventBusImpl;
import shattered.core.registry.JsonRegistryLoader;
import shattered.lib.Internal;
import shattered.lib.gfx.Window;

public final class Shattered {

	public static final String NAME = "Shattered";
	public static final Logger LOGGER = LogManager.getLogger(Shattered.NAME);
	@Getter
	private static Shattered shattered;
	private final Runtime runtime = new Runtime();
	//	@Getter
	//	private final GuiManager guiManager;

	private Shattered(final File rootDir, final String[] args) {
		Shattered.shattered = this;
		Internal.NAME = Shattered.NAME;
		Internal.ROOT_PATH = rootDir.toPath().toAbsolutePath();
		//TODO handle args
		//		this.guiManager = new GuiManager();
		this.init();
		this.runtime.start();
		Window.INSTANCE.destroy();
	}

	private void init() {
		EventBusImpl.init();
		Window.INSTANCE.init();
		JsonRegistryLoader.createRegistries();
		try {
			JsonRegistryLoader.loadRegistries(); //TODO async this
		} catch (final IOException e) {
			Shattered.LOGGER.fatal("Could not load registry data");
			throw new RuntimeException(e);
		}
		this.runtime.init();
	}

	public void stop() {
		Shattered.LOGGER.warn("{} will now exit", Shattered.NAME);
		this.runtime.running.set(false);
	}

	public static void start(final File rootDir, final String[] args) {
		if (Shattered.getShattered() != null) {
			throw new IllegalStateException("The start-method can only be called once!");
		}
		new Shattered(rootDir, args);
	}

	public static long clock() {
		return System.nanoTime() / 1000_000;
	}
}
