package dawn;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import dawn.lib.ExitException;
import dawn.lib.Workspace;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Dawn {

	public static final String NAME = "Shattered";
	public static final String NAME_LOW = NAME.toLowerCase(Locale.ROOT);
	public static final Logger LOGGER = LogManager.getLogger(NAME);
	@SuppressWarnings("NotNullFieldNotInitialized")
	private static @Getter Dawn dawn;
	public final Workspace workspace;

	private Dawn(File rootDir, String[] args) {
		dawn = this;
		//TODO handle args
		try {
			this.workspace = new Workspace(rootDir);
		} catch (IOException e) {
			LOGGER.error("Could not create workspace", e);
			throw new ExitException();
		}
		init();
		//TODO runtime start
		//TODO destroy
	}

	private void init() {
	}

	public static void start(File rootDir, String[] args) {
		//noinspection ConstantValue
		if (getDawn() != null) {
			throw new IllegalStateException();
		}
		new Dawn(rootDir, args);
	}
}
