package dawn.gui;

import java.util.function.IntSupplier;

public abstract class GuiWidget extends GuiBase {

	private IntSupplier x = () -> 0;
	private IntSupplier y = () -> 0;
	private IntSupplier width = () -> 176;
	private IntSupplier height = () -> 166;

	public void init(final GuiScreen screen) {
	}

	public final void setX(final IntSupplier x) {
		this.x = x;
	}

	public final void setX(final int x) {
		this.setX(() -> x);
	}

	public final void setY(final IntSupplier y) {
		this.y = y;
	}

	public final void setY(final int y) {
		this.setY(() -> y);
	}

	public final void setWidth(final IntSupplier width) {
		this.width = width;
	}

	public final void setWidth(final int width) {
		this.setWidth(() -> width);
	}

	public final void setHeight(final IntSupplier height) {
		this.height = height;
	}

	public final void setHeight(final int height) {
		this.setHeight(() -> height);
	}

	@Override
	public final int getX() {
		return this.x.getAsInt();
	}

	@Override
	public final int getY() {
		return this.y.getAsInt();
	}

	@Override
	public final int getWidth() {
		return this.width.getAsInt();
	}

	@Override
	public final int getHeight() {
		return this.height.getAsInt();
	}
}
