package dawn.lib;

import org.joml.Vector4i;

public record Rectangle(int x, int y, int w, int h) {

	public Rectangle(final Rectangle other) {
		this(other.x(), other.y(), other.w(), other.h());
	}

	public int xMax() {
		return this.x + this.w;
	}

	public int yMax() {
		return this.y + this.h;
	}

	public int xCenter() {
		return this.x + this.w / 2;
	}

	public int yCenter() {
		return this.y + this.h / 2;
	}

	public Point endPos() {
		return new Point(this.xMax(), this.yMax());
	}

	public Rectangle moveX(final int amount) {
		return new Rectangle(this.x + amount, this.y, this.w, this.h);
	}

	public Rectangle moveY(final int amount) {
		return new Rectangle(this.x, this.y + amount, this.w, this.h);
	}

	public Rectangle move(final int xAmount, final int yAmount) {
		return new Rectangle(this.x + xAmount, this.y + yAmount, this.w, this.h);
	}

	public Rectangle growW(final int amount) {
		return new Rectangle(this.x, this.y, this.w + amount, this.h);
	}

	public Rectangle growH(final int amount) {
		return new Rectangle(this.x, this.y, this.w, this.h + amount);
	}

	public Rectangle grow(final int wAmount, final int hAmount) {
		return new Rectangle(this.x, this.y, this.w + wAmount, this.h + hAmount);
	}

	public Rectangle growWCentered(final int amount) {
		return new Rectangle(this.x - amount / 2, this.y, this.w + amount, this.h);
	}

	public Rectangle growYCentered(final int amount) {
		return new Rectangle(this.x, this.y - amount / 2, this.w, this.h + amount);
	}

	public Rectangle growCentered(final int wAmount, final int hAmount) {
		return new Rectangle(this.x - wAmount / 2, this.y - hAmount / 2, this.w + wAmount, this.h + hAmount);
	}

	public Rectangle copy() {
		return new Rectangle(this);
	}

	public Point pos() {
		return new Point(this.x, this.y);
	}

	public Dimension size() {
		return new Dimension(this.w, this.h);
	}

	public Vector4i toVec() {
		return new Vector4i(this.x, this.y, this.w, this.h);
	}
}
