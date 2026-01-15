package dawn.gui;

import dawn.gfx.FontRenderer;
import dawn.gfx.Tessellator;

public abstract class GuiBase {

	public void tick() {
	}

	public void renderBackground(final Tessellator tessellator, final FontRenderer fontRenderer, final int mouseX, final int mouseY) {
	}

	public void renderForeground(final Tessellator tessellator, final FontRenderer fontRenderer, final int mouseX, final int mouseY) {
	}

	public boolean onMousePressed(final int button, final int mouseX, final int mouseY) {
		return false;
	}

	public boolean onMouseReleased(final int button, final int mouseX, final int mouseY) {
		return false;
	}

	public boolean onMouseClicked(final int button, final int mouseX, final int mouseY) {
		return false;
	}

	public abstract int getX();

	public abstract int getY();

	public abstract int getWidth();

	public abstract int getHeight();

	public boolean contains(final int x, final int y) {
		return x >= this.getX() && x <= this.getX() + this.getWidth() && y >= this.getY() && y <= this.getY() + this.getHeight();
	}
}
