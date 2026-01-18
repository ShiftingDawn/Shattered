package dawn.app.screen;

import dawn.gfx.Color;
import dawn.gfx.FontRenderer;
import dawn.gfx.Tessellator;
import dawn.gui.GuiScreen;
import dawn.gui.RenderPhase;
import dawn.input.Input;

public final class ScreenOptions extends GuiScreen {

	ScreenOptions() {
	}

	@Override
	public void init() {
		this.setWidth(this.getDisplayWidth());
		this.setHeight(30);
		super.init();
		this.setY(this.getDisplayHeight() - 30);
	}

	@Override
	public void renderBackground(final Tessellator tessellator, final FontRenderer fontRenderer, final RenderPhase phase, final Input input) {
		if (phase != RenderPhase.BACKGROUND) {
			return;
		}
		tessellator.start().set(Color.RED).pos(this.getX(), this.getY(), this.getWidth(), this.getHeight()).draw().end();
	}
}
