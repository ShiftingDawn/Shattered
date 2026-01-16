package dawn.lib;

import org.joml.Vector2i;
import org.joml.Vector2ic;

public record Dimension(int w, int h) {

	public Dimension(final Dimension other) {
		this(other.w(), other.h());
	}

	public Dimension(final Vector2ic other) {
		this(other.x(), other.y());
	}

	public Dimension moveX(final int amount) {
		return new Dimension(this.w + amount, this.h);
	}

	public Dimension moveY(final int amount) {
		return new Dimension(this.w, this.h + amount);
	}

	public Dimension move(final int wAmount, final int hAmount) {
		return new Dimension(this.w + wAmount, this.h + hAmount);
	}

	public Dimension copy() {
		return new Dimension(this);
	}

	public Vector2ic toVec() {
		return new Vector2i(this.w, this.h);
	}
}
