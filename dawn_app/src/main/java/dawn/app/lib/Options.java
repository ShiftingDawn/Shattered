package dawn.app.lib;

import java.io.IOException;
import dawn.app.DawnApp;
import dawn.core.app.OptionChangedEventImpl;
import dawn.dawndb.DawnDB;
import dawn.event.EventBus;
import dawn.lib.option.ManagedBoolean;
import dawn.lib.option.ManagedInt;
import dawn.lib.option.OptionSupplier;
import static dawn.init.Options.ENABLE_VERTICAL_SYNC;
import static dawn.init.Options.FULLSCREEN;
import static dawn.init.Options.GUI_SCALE;

public final class Options implements OptionSupplier {

	private final ManagedBoolean fullScreen = this.makeBoolean(FULLSCREEN, false);
	private final ManagedBoolean vsync = this.makeBoolean(ENABLE_VERTICAL_SYNC, true);
	private final ManagedInt guiScale = this.makeInt(GUI_SCALE, 0);
	private DawnDB db;

	public void reload() {
		try {
			this.db = DawnDB.load("options.dat");
		} catch (final IOException e) {
			DawnApp.LOGGER.error("Could not load options from disk", e);
			this.db = DawnDB.newDB();
		}
	}

	public void save() {
		try {
			this.db.save("options.dat");
		} catch (final IOException e) {
			DawnApp.LOGGER.error("Could not save options to disk", e);
		}
	}

	private ManagedBoolean makeBoolean(final String key, final boolean fallback) {
		return ManagedBoolean.of(() -> this.db.getBoolean(key, fallback), i -> {
			this.db.set(key, i);
			this.save();
			EventBus.bus().post(new OptionChangedEventImpl(key, this));
		});
	}

	private ManagedInt makeInt(final String key, final int fallback) {
		return ManagedInt.of(() -> this.db.getInt(key, fallback), i -> {
			this.db.set(key, i);
			this.save();
			EventBus.bus().post(new OptionChangedEventImpl(key, this));
		});
	}

	@Override
	public ManagedBoolean isFullscreen() {
		return this.fullScreen;
	}

	@Override
	public ManagedBoolean enableVerticalSync() {
		return this.vsync;
	}

	@Override
	public ManagedInt getGuiScale() {
		return this.guiScale;
	}
}
