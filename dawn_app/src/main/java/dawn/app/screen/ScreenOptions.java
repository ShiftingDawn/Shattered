package dawn.app.screen;

import dawn.app.DawnApp;
import dawn.gfx.Color;
import dawn.gfx.FontRenderer;
import dawn.gfx.Tessellator;
import dawn.gui.GuiScreen;
import dawn.gui.Interactivity;
import dawn.init.Textures;
import dawn.input.Input;

public final class ScreenOptions extends GuiScreen {

	private final ToggleButtonWidget buttonEnableVerticalSync = this.add(new ToggleButtonWidget("Enable VSync", DawnApp.get().getOptions().enableVerticalSync()));
	private final ToggleButtonWidget buttonFullscreen = this.add(new ToggleButtonWidget("Fullscreen", DawnApp.get().getOptions().isFullscreen()));

	ScreenOptions() {
		this.buttonEnableVerticalSync.setPos(() -> this.getX() + 10, () -> this.getY() + 10);
		this.buttonEnableVerticalSync.setSize(() -> this.getWidth() - 20, () -> 24);
		this.buttonFullscreen.setPos(() -> this.getX() + 10, () -> this.buttonEnableVerticalSync.getY() + 34);
		this.buttonFullscreen.setSize(() -> this.getWidth() - 20, () -> 24);
	}

	@Override
	public void init() {
		this.setSize(Math.min(this.getDisplayWidth(), 340), Math.min(this.getDisplayHeight(), 220));
		super.init();
	}

	@Override
	public void renderBackground(final Tessellator tessellator, final FontRenderer fontRenderer, final Interactivity interactivity, final Input input) {
		tessellator.start()
			.set(Textures.GUI_BACKGROUND, Color.BLACK.withAlpha(.75f)).pos(this.getX() + 2, this.getY() + 2, this.getWidth(), this.getHeight()).draw()
			.set(Textures.GUI_BACKGROUND).pos(this.getX(), this.getY(), this.getWidth(), this.getHeight()).draw()
			.end();
	}
}
