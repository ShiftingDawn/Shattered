package shattered;

import java.io.File;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shattered.core.event.EventBusImpl;
import shattered.lib.Internal;

public final class Shattered {

	public static final String NAME = "Shattered";
	public static final Logger LOGGER = LogManager.getLogger(Shattered.NAME);
	@Getter
	private static Shattered shattered;

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
	}

	private void start() {
	}

	public void stop() {
		Shattered.LOGGER.warn("{} will now exit", Shattered.NAME);
	}

	public static void start(final File rootDir, final String[] args) {
		if (Shattered.getShattered() != null) {
			throw new IllegalStateException("The start-method can only be called once!");
		}
		new Shattered(rootDir, args);
	}
}
