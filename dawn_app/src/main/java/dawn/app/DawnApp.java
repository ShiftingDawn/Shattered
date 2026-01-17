package dawn.app;

import dawn.app.screen.ScreenMainMenu;
import dawn.core.app.IBootApp;

public final class DawnApp extends IBootApp {

	@SuppressWarnings("NotNullFieldNotInitialized")
	private static DawnApp INSTANCE;

	public DawnApp() {
		DawnApp.INSTANCE = this;
	}

	@Override
	public void init() {
		this.getDawn().getGuiManager().openScreen(new ScreenMainMenu());
	}

	public static DawnApp get() {
		return DawnApp.INSTANCE;
	}
}
