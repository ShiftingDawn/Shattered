package dawn.app.screen;

import dawn.app.DawnApp;
import dawn.app.init.AppTextures;
import dawn.gfx.FontRenderer;
import dawn.gfx.Tessellator;
import dawn.gui.GuiScreen;
import dawn.gui.Interactivity;
import dawn.gui.widget.ButtonWidget;
import dawn.input.Input;

public final class ScreenMainMenu extends GuiScreen {

	private final ButtonWidget buttonOptions = this.add(new ButtonWidget("Options", this::onButtonOptions));
	private final ButtonWidget buttonExit = this.add(new ButtonWidget("Exit", this::onButtonExit));

	public ScreenMainMenu() {
		this.buttonExit.setPos(() -> this.getX() + this.getWidth() / 2 - 120, () -> this.getY() + this.getHeight() - 24 - 10);
		this.buttonOptions.setPos(() -> this.getX() + this.getWidth() / 2 - 120, () -> this.buttonExit.getY() - 24 - 10);
	}

	@Override
	public void init() {
		this.setFullscreen();
		super.init();
	}

	@Override
	public void renderBackground(final Tessellator tessellator, final FontRenderer fontRenderer, final Interactivity interactivity, final Input input) {
		tessellator.start()
			.set(AppTextures.ARGON).pos(this.getX(), this.getY(), this.getWidth(), this.getHeight()).draw()
			.set(AppTextures.LOGO).pos(this.getX(), this.getY() + 10).centerX(this.getWidth()).draw()
			.end();
	}

	private void onButtonOptions() {
		this.getGuiManager().openScreen(new ScreenOptions());
	}

	private void onButtonExit() {
		DawnApp.get().stop();
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
}
