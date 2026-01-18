package dawn.core;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import dawn.Dawn;
import dawn.core.app.IBootApp;
import dawn.core.asset.AssetManagerImpl;
import dawn.core.dawndb.DDBHelperImpl;
import dawn.core.event.EventBusImpl;
import dawn.core.gfx.GlStateManagerImpl;
import dawn.core.gfx.RenderManagerImpl;
import dawn.core.gfx.ShaderPropsImpl;
import dawn.core.gfx.WindowImpl;
import dawn.core.gui.GuiManagerImpl;
import dawn.core.lib.ArgHandler;
import dawn.core.lib.json.GsonFactory;
import dawn.core.registry.RegistriesImpl;
import dawn.internal.DawnLib;
import dawn.lib.ResourceFinder;
import dawn.lib.Workspace;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class DawnImpl implements Dawn {

	public static final String NAME = "Shattered";
	public static final String NAME_LOW = DawnImpl.NAME.toLowerCase(Locale.ROOT);
	public static final Logger LOGGER = LogManager.getLogger(DawnImpl.NAME);
	private static final int TICKS_PER_SECOND = Integer.getInteger(DawnImpl.NAME_LOW + ".runtime.tickrate", 20);
	private final AtomicBoolean running = new AtomicBoolean(true);
	private final @Getter ArgHandler args;
	private final @Getter Workspace workspace;
	private final @Getter ResourceFinder resources;
	private final @Getter WindowImpl window;
	private final @Getter AssetManagerImpl assets;
	private final IBootApp bootApp;
	private @Getter RenderManagerImpl renderManager;
	private @Getter GuiManagerImpl guiManager;

	private DawnImpl(final File rootDir, final String[] args, final IBootApp bootApp) {
		this.bootApp = bootApp;
		DawnLib.INSTANCE = this;
		this.args = new ArgHandler(args);
		try {
			this.workspace = new Workspace(rootDir);
		} catch (final IOException e) {
			DawnImpl.LOGGER.error("Could not create workspace", e);
			throw new ExitException();
		}
		this.initLib();
		this.bootApp.preInit(this);
		this.resources = new ResourceFinderImpl(this.workspace);
		WindowImpl.initGlfw();
		this.window = new WindowImpl(this.args.displayWidth, this.args.displayHeight, this::stop, () -> this.bootApp.getOptions().getGuiScale().getAsInt());
		this.assets = new AssetManagerImpl(this.resources, this.workspace);
		this.init();
		this.run();
	}

	private void initLib() {
		DawnLib.IDENTIFIER_DEFAULT_DOMAIN = DawnImpl.NAME_LOW;
		GsonFactory.init();
		DawnLib.REGISTRIES = new RegistriesImpl();
		DawnLib.BUS = new EventBusImpl();
		DawnLib.GL = new GlStateManagerImpl();
		DawnLib.SHADER_PROPS = new ShaderPropsImpl();
		DawnLib.DDB_HELPER = new DDBHelperImpl(this.workspace);
	}

	private void init() {
		RegistriesImpl.load(this.resources);
		this.assets.init();
		this.renderManager = new RenderManagerImpl(this, this.assets);
		this.guiManager = new GuiManagerImpl(this.window);
		this.bootApp.init();
	}

	private void run() {
		final int millisPerTick = 1000 / DawnImpl.TICKS_PER_SECOND;
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
		WindowImpl.destroyGlfw();
	}

	public void stop() {
		DawnImpl.LOGGER.info("Shutdown has been requested");
		this.running.set(false);
	}

	public static void start(final File rootDir, final String[] args, final IBootApp bootApp) {
		//noinspection ConstantValue
		if (Dawn.getDawn() != null) {
			throw new IllegalStateException();
		}
		new DawnImpl(rootDir, args, bootApp);
	}
}
