package dawn;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import dawn.asset.AssetManager;
import dawn.asset.ResourceResolver;
import dawn.gfx.Display;
import dawn.gfx.GlfwSetup;
import dawn.lib.ArgHandler;
import dawn.lib.ExitException;
import dawn.lib.Workspace;
import dawn.registry.RegistrySetup;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Dawn {

	public static final String NAME = "Shattered";
	public static final String NAME_LOW = Dawn.NAME.toLowerCase(Locale.ROOT);
	public static final Logger LOGGER = Dawn.getLogger(Dawn.NAME);
	@SuppressWarnings("NotNullFieldNotInitialized")
	private static @Getter Dawn dawn;
	private final Runtime runtime = new Runtime();
	private final @Getter ArgHandler args;
	private final @Getter Workspace workspace;
	private final @Getter ResourceResolver resources;
	private final @Getter AssetManager assets;
	private @Getter RenderManager renderManager;

	private Dawn(final File rootDir, final String[] args) {
		Dawn.dawn = this;
		this.args = new ArgHandler(args);
		try {
			this.workspace = new Workspace(rootDir);
		} catch (final IOException e) {
			Dawn.LOGGER.error("Could not create workspace", e);
			throw new ExitException();
		}
		this.resources = new ResourceResolver(this.workspace);
		this.assets = new AssetManager(this.resources, this.workspace);
		this.init();
		this.runtime.start();
		this.shutdown();
	}

	private void init() {
		GlfwSetup.init(this.args.displayWidth, this.args.displayHeight);
		RegistrySetup.load(this.resources);
		Display.activate();
		this.assets.init();
		this.renderManager = new RenderManager(this.resources, this.assets);
		this.runtime.init();
	}

	public void stop() {
		Dawn.LOGGER.info("Shutdown has been requested");
		this.runtime.running.set(false);
	}

	private void shutdown() {
		GlfwSetup.destroy();
	}

	public static void start(final File rootDir, final String[] args) {
		//noinspection ConstantValue
		if (Dawn.getDawn() != null) {
			throw new IllegalStateException();
		}
		new Dawn(rootDir, args);
	}

	public static long clock() {
		return System.nanoTime() / 1000_000;
	}

	public static Logger getLogger(final String name) {
		return LogManager.getLogger(name);
	}
}
