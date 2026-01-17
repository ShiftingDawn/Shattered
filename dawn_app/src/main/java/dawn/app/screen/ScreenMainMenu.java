package dawn.app.screen;

import dawn.app.DawnApp;
import dawn.gfx.FontRenderer;
import dawn.gfx.Tessellator;
import dawn.gui.GuiScreen;
import dawn.init.Textures;
import dawn.input.Input;

public final class ScreenMainMenu extends GuiScreen {

	private final ButtonWidget buttonExit = this.add(new ButtonWidget("Exit", this::onButtonExit));

	public ScreenMainMenu() {
		this.buttonExit.setX(() -> this.getX() + this.getWidth() / 2 - 120);
		this.buttonExit.setY(() -> this.getY() + this.getHeight() - 24 - 10);
		this.buttonExit.setWidth(240);
		this.buttonExit.setHeight(24);
	}

	@Override
	public void init() {
		this.setFullscreen();
		super.init();
	}

	@Override
	public void renderBackground(final Tessellator tessellator, final FontRenderer fontRenderer, final Input input) {
		tessellator.start()
			.set(Textures.ARGON).pos(this.getX(), this.getY(), this.getWidth(), this.getHeight()).draw()
			.set(Textures.LOGO).pos(this.getX(), this.getY() + 10).centerX(this.getWidth()).draw()
			.end();
	}

	private void onButtonExit() {
		DawnApp.get().stop();
	}
}
