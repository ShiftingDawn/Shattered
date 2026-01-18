package dawn.app.screen;

import dawn.gfx.Color;
import dawn.gfx.FontRenderer;
import dawn.gfx.Tessellator;
import dawn.gui.GuiScreen;
import dawn.gui.Interactivity;
import dawn.init.Textures;
import dawn.input.Input;

public final class ScreenOptions extends GuiScreen {

	ScreenOptions() {
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
