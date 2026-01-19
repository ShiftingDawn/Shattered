package dawn.gui;

import dawn.gfx.FontRenderer;
import dawn.gfx.Tessellator;
import dawn.input.EventResult;
import dawn.input.Input;
import dawn.input.KeyMods;
import dawn.lib.Rectangle;

public abstract class GuiBase {

	public void tick() {
	}

	public void renderBackground(final Tessellator tessellator, final FontRenderer fontRenderer, final Interactivity interactivity, final Input input) {
	}

	public void renderForeground(final Tessellator tessellator, final FontRenderer fontRenderer, final Interactivity interactivity, final Input input) {
	}

	public EventResult onMousePressed(final int button, final int mouseX, final int mouseY) {
		return EventResult.DEFAULT;
	}

	public EventResult onMouseReleased(final int button, final int mouseX, final int mouseY) {
		return EventResult.DEFAULT;
	}

	public EventResult onMouseClicked(final int button, final int mouseX, final int mouseY) {
		return EventResult.DEFAULT;
	}

	public EventResult onKeyPressed(final int keyCode, final KeyMods mods) {
		return EventResult.DEFAULT;
	}

	public EventResult onKeyReleased(final int keyCode, final KeyMods mods) {
		return EventResult.DEFAULT;
	}

	public EventResult onKeyRepeat(final int keyCode, final KeyMods mods) {
		return EventResult.DEFAULT;
	}

	public abstract int getX();

	public abstract int getY();

	public abstract int getWidth();

	public abstract int getHeight();

	public final int getMaxX() {
		return this.getX() + this.getWidth();
	}

	public final int getMaxY() {
		return this.getY() + this.getHeight();
	}

	public final Rectangle getBounds() {
		return new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}

	public boolean contains(final int x, final int y) {
		return x >= this.getX() && x <= this.getX() + this.getWidth() && y >= this.getY() && y <= this.getY() + this.getHeight();
	}

	public boolean isBlockingInteractionBelow() {
		return true;
	}

	public boolean shouldCloseOnEsc() {
		return true;
	}
}
