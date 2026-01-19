package dawn.app.screen;

import dawn.app.init.AppTextures;
import dawn.gfx.FontRenderer;
import dawn.gfx.Tessellator;
import dawn.gui.GuiScreen;
import dawn.gui.Interactivity;
import dawn.input.Input;

abstract class FullscreenWindow extends GuiScreen {

	@Override
	public void init() {
		this.setFullscreen();
		super.init();
	}

	@Override
	public void renderBackground(final Tessellator tessellator, final FontRenderer fontRenderer, final Interactivity interactivity, final Input input) {
		tessellator.render(AppTextures.ARGON, t -> t.pos(this.getBounds()));
	}
}
