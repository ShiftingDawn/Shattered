package shattered.core;

import java.io.File;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shattered.core.event.EventBusImpl;
import shattered.core.gfx.Display;
import shattered.core.resource.ResourceFinder;
import shattered.lib.Internal;

public final class Shattered {

	public static final String NAME = "Shattered";
	public static final Logger LOGGER = LogManager.getLogger(Shattered.NAME);
	@Getter
	private static Shattered shattered;
	@Getter
	private final Runtime runtime = new Runtime();
	@Getter
	private ResourceFinder assets;
	@Getter
	private ResourceFinder data;

	private Shattered(final File rootDir, final String[] args) {
		Shattered.shattered = this;
		Internal.NAME = Shattered.NAME;
		Internal.ROOT_PATH = rootDir.toPath().toAbsolutePath();
		//TODO handle args
		this.init();
		this.start();
	}

	private void init() {
		EventBusImpl.init();
		this.assets = new ResourceFinder("assets");
		Display.init();
		this.data = new ResourceFinder("data");
	}

	private void start() {
		try {
			this.runtime.start();
		} catch (final ExitShatteredException e) {
			if (e.getMessage() != null) {
				Shattered.LOGGER.fatal(e);
			}
			try {
				this.stop();
			} catch (final Throwable ignored) {
				//Don't care, we are already in an error-state
			}
		}
	}

	public void stop() {
		Shattered.LOGGER.warn("{} will now exit", Shattered.NAME);
		this.runtime.stop();
	}

	public static void start(final File rootDir, final String[] args) {
		if (Shattered.getShattered() != null) {
			throw new IllegalStateException("The start-method can only be called once!");
		}
		new Shattered(rootDir, args);
	}
}
