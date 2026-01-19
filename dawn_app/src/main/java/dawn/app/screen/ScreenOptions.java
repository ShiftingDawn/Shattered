package dawn.app.screen;

import dawn.app.DawnApp;
import dawn.app.init.AppTextures;
import dawn.gfx.Color;
import dawn.gfx.FontRenderer;
import dawn.gfx.Tessellator;
import dawn.gui.GuiScreen;
import dawn.gui.Interactivity;
import dawn.gui.widget.ToggleButtonWidget;
import dawn.input.Input;

public final class ScreenOptions extends GuiScreen {

	private final ToggleButtonWidget buttonEnableVerticalSync = this.add(new ToggleButtonWidget("Enable VSync", DawnApp.get().getOptions().enableVerticalSync()));
	private final ToggleButtonWidget buttonFullscreen = this.add(new ToggleButtonWidget("Fullscreen", DawnApp.get().getOptions().isFullscreen()));

	ScreenOptions() {
		this.buttonEnableVerticalSync.setPos(() -> this.getX() + 10, () -> this.getY() + 10);
		this.buttonFullscreen.setPos(() -> this.getX() + 10, () -> this.buttonEnableVerticalSync.getMaxY() + 2);
	}

	@Override
	public void init() {
		this.setSize(Math.min(this.getDisplayWidth(), 340), Math.min(this.getDisplayHeight(), 220));
		super.init();
	}

	@Override
	public void renderBackground(final Tessellator tessellator, final FontRenderer fontRenderer, final Interactivity interactivity, final Input input) {
		tessellator.start()
			.set(Color.BLACK.withAlpha(.75f)).pos(this.getX() + 2, this.getY() + 2, this.getWidth(), this.getHeight()).draw()
			.set(AppTextures.GUI_BACKGROUND).pos(this.getX(), this.getY(), this.getWidth(), this.getHeight()).draw()
			.set(Color.WHITE.withAlpha(.15f)).pos(this.getX() + 2, this.getY() + 2, this.getWidth() - 4, 16).draw()
			.end();
		final int w = fontRenderer.getStringWidth("Options", 16);
		final int h = fontRenderer.getStringHeight(16);
		fontRenderer.start().set("Options").size(16).pos(this.getX() + (this.getWidth() - w) / 2, this.getY() + 2 + (16 - h) / 2).write().end();
	}
}
