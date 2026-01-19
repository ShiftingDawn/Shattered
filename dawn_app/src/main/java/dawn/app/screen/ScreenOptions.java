package dawn.app.screen;

import dawn.Dawn;
import dawn.Identifier;
import dawn.app.DawnApp;
import dawn.gui.widget.ToggleButtonWidget;
import dawn.lib.lang.Text;

public final class ScreenOptions extends OverlayWindow {

	private static final Identifier SCREEN_ID = Identifier.of("options");
	private final ToggleButtonWidget buttonEnableVerticalSync = this.add(new ToggleButtonWidget(Text.localize(Dawn.makeKey(ScreenOptions.SCREEN_ID, "screen", "vsync")), DawnApp.get().getOptions().enableVerticalSync()));
	private final ToggleButtonWidget buttonFullscreen = this.add(new ToggleButtonWidget(Text.localize(Dawn.makeKey(ScreenOptions.SCREEN_ID, "screen", "fullscreen")), DawnApp.get().getOptions().isFullscreen()));

	ScreenOptions() {
		super(Text.localize(Dawn.makeKey(ScreenOptions.SCREEN_ID, "screen")));
		this.buttonEnableVerticalSync.setPos(() -> this.getInnerBounds().x() + 10, () -> this.getInnerBounds().y() + 10);
		this.buttonFullscreen.setPos(() -> this.getInnerBounds().x() + 10, () -> this.buttonEnableVerticalSync.getMaxY() + 2);
	}
}
