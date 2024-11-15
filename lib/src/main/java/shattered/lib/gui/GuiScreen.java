package shattered.lib.gui;

import shattered.lib.gfx.Tessellator;
import shattered.lib.util.TickType;
import shattered.lib.util.math.Dimension;
import shattered.lib.util.math.Point;
import shattered.lib.util.math.Rectangle;

public abstract class GuiScreen {

	private final Rectangle bounds = Rectangle.createMutable(0, 0, -1, -1);
	private Rectangle calculatedPosition = this.bounds.copy();

	public GuiScreen() {
		this.recalculatePosition();
	}

	public final void recalculatePosition() {
		this.calculatedPosition = Rectangle.create(
				DisplaySizingHelper.calcX(this.bounds.getX()),
				DisplaySizingHelper.calcY(this.bounds.getY()),
				DisplaySizingHelper.calcWidth(this.bounds.getWidth()),
				DisplaySizingHelper.calcHeight(this.bounds.getHeight())
		);
	}

	protected void tick(final TickType active) {
	}

	protected void render(final Tessellator tessellator) {
	}

	public final Rectangle getBounds() {
		return this.bounds.toImmutable();
	}

	public final Point getPosition() {
		return this.bounds.getPosition();
	}

	public final int getX() {
		return this.bounds.getX();
	}

	public final int getY() {
		return this.bounds.getX();
	}

	public final Dimension getSize() {
		return this.bounds.getSize();
	}

	public final int getWidth() {
		return this.bounds.getX();
	}

	public final int getHeight() {
		return this.bounds.getX();
	}
}
