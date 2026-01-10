package dawn;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import dawn.asset.AssetResolver;
import dawn.gfx.GlfwSetup;
import dawn.lib.ExitException;
import dawn.lib.Workspace;
import dawn.registry.RegistrySetup;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Dawn {

	public static final String NAME = "Shattered";
	public static final String NAME_LOW = NAME.toLowerCase(Locale.ROOT);
	public static final Logger LOGGER = LogManager.getLogger(NAME);
	@SuppressWarnings("NotNullFieldNotInitialized")
	private static @Getter Dawn dawn;
	private final Runtime runtime = new Runtime();
	private final @Getter Workspace workspace;
	private final @Getter AssetResolver assets;
	private @Getter RenderManager renderManager;

	private Dawn(File rootDir, String[] args) {
		Dawn.dawn = this;
		//TODO handle args
		try {
			this.workspace = new Workspace(rootDir);
		} catch (IOException e) {
			LOGGER.error("Could not create workspace", e);
			throw new ExitException();
		}
		this.assets = new AssetResolver(this.workspace);
		this.init();
		this.runtime.start();
		this.shutdown();
	}

	private void init() {
		GlfwSetup.init();
		this.renderManager = new RenderManager(this.assets);
		RegistrySetup.load(this.assets);
		this.runtime.init();
	}

	public void stop() {
		LOGGER.info("Shutdown has been requested");
		this.runtime.running.set(false);
	}

	private void shutdown() {
		GlfwSetup.destroy();
	}

	public static void start(File rootDir, String[] args) {
		//noinspection ConstantValue
		if (Dawn.getDawn() != null) {
			throw new IllegalStateException();
		}
		new Dawn(rootDir, args);
	}

	public static long clock() {
		return System.nanoTime() / 1000_000;
	}
}
