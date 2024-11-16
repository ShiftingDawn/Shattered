package shattered.lib.gui;

import shattered.lib.gfx.Tessellator;
import shattered.lib.util.Color;

public final class GuiMainMenu extends GuiScreen {

	public GuiMainMenu() {
	}

	@Override
	protected void render(final Tessellator tessellator) {
		tessellator.start().set(this.getPosition(), this.getSize()).draw(Color.YELLOW).end();
	}
}
