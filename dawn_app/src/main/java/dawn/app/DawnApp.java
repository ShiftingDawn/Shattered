package dawn.app;

import dawn.app.lib.Options;
import dawn.app.screen.ScreenMainMenu;
import dawn.core.app.IBootApp;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class DawnApp extends IBootApp {

	public static final Logger LOGGER = LogManager.getLogger("Dawn");
	@SuppressWarnings("NotNullFieldNotInitialized")
	private static DawnApp INSTANCE;
	private final @Getter Options options = new Options();

	public DawnApp() {
		DawnApp.INSTANCE = this;
	}

	@Override
	protected void preInit() {
		this.options.reload();
		this.options.save();
	}

	@Override
	public void init() {
		this.getDawn().getGuiManager().openScreen(new ScreenMainMenu());
	}

	public static DawnApp get() {
		return DawnApp.INSTANCE;
	}
}
