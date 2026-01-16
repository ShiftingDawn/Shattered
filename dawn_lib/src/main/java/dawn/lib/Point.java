package dawn.lib;

import org.joml.Vector2i;
import org.joml.Vector2ic;

public record Point(int x, int y) {

	public Point(final Point other) {
		this(other.x(), other.y());
	}

	public Point(final Vector2ic other) {
		this(other.x(), other.y());
	}

	public Point moveX(final int amount) {
		return new Point(this.x + amount, this.y);
	}

	public Point moveY(final int amount) {
		return new Point(this.x, this.y + amount);
	}

	public Point move(final int xAmount, final int yAmount) {
		return new Point(this.x + xAmount, this.y + yAmount);
	}

	public Point copy() {
		return new Point(this);
	}

	public Vector2ic toVec() {
		return new Vector2i(this.x, this.y);
	}
}
