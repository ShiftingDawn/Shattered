package dawn;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import dawn.asset.AssetManager;
import dawn.asset.ResourceResolver;
import dawn.gfx.Window;
import dawn.gui.GuiManager;
import dawn.gui.ScreenMainMenu;
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
	private static final int TICKS_PER_SECOND = Integer.getInteger(Dawn.NAME_LOW + ".runtime.tickrate", 20);
	@SuppressWarnings("NotNullFieldNotInitialized")
	private static @Getter Dawn dawn;
	private final AtomicBoolean running = new AtomicBoolean(true);
	private final @Getter ArgHandler args;
	private final @Getter Workspace workspace;
	private final @Getter ResourceResolver resources;
	private final @Getter Window window;
	private final @Getter AssetManager assets;
	private @Getter RenderManager renderManager;
	private @Getter GuiManager guiManager;

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
		Window.initGlfw();
		this.window = new Window(this.args.displayWidth, this.args.displayHeight, this::stop);
		this.assets = new AssetManager(this.resources, this.workspace);
		this.init();
		this.run();
	}

	private void init() {
		RegistrySetup.load(this.resources);
		this.assets.init();
		this.renderManager = new RenderManager(this, this.assets);
		this.guiManager = new GuiManager(this.window);
		this.guiManager.openScreen(new ScreenMainMenu());
	}

	private void run() {
		final int millisPerTick = 1000 / Dawn.TICKS_PER_SECOND;
		final long lastTickTime = Dawn.clock();
		while (this.running.get()) {
			final long currentTime = Dawn.clock();
			long delta = currentTime - lastTickTime;
			while (delta >= millisPerTick) {
				delta -= millisPerTick;
				this.guiManager.tick();
			}
			this.renderManager.render();
			this.window.update();
		}
		this.window.close();
		Window.destroyGlfw();
	}

	public void stop() {
		Dawn.LOGGER.info("Shutdown has been requested");
		this.running.set(false);
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
