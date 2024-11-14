package shattered.lib.util.math;

import java.util.Objects;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Point {

	public static final Point EMPTY = Point.create(0, 0);
	@Getter
	private final boolean mutable;
	private double x, y;

	private Point(final double x, final double y, final boolean mutable) {
		this.mutable = mutable;
		this.x = x;
		this.y = y;
	}

	@NotNull
	public Point setX(final double x) {
		if (!this.mutable) {
			return new Point(x, this.y, false);
		}
		this.x = x;
		return this;
	}

	@NotNull
	public Point setY(final double y) {
		if (!this.mutable) {
			return new Point(this.x, y, false);
		}
		this.y = y;
		return this;
	}

	@NotNull
	public Point moveX(final double amount) {
		return this.setX(this.x + amount);
	}

	@NotNull
	public Point moveY(final double amount) {
		return this.setY(this.y + amount);
	}

	@NotNull
	public Point move(final double amountX, final double amountY) {
		if (!this.mutable) {
			return new Point(this.x + amountX, this.y + amountY, false);
		}
		this.x += amountX;
		this.y += amountY;
		return this;
	}

	@NotNull
	public Point move(@NotNull final Dimension amount) {
		return this.move(amount.getWidth(), amount.getHeight());
	}

	@NotNull
	public Point add(@NotNull final Point point) {
		return this.move(point.getX(), point.getY());
	}

	@NotNull
	public Point subtract(@NotNull final Point point) {
		return this.move(-point.getX(), -point.getY());
	}

	public int getX() {
		return (int) this.x;
	}

	public int getY() {
		return (int) this.y;
	}

	public double getDoubleX() {
		return this.x;
	}

	public double getDoubleY() {
		return this.y;
	}

	@NotNull
	public Point toMutable() {
		return this.mutable ? this : new Point(this.x, this.y, true);
	}

	@NotNull
	public Point toImmutable() {
		return this.mutable ? new Point(this.x, this.y, false) : this;
	}

	@NotNull
	public Point copy() {
		return new Point(this.x, this.y, this.mutable);
	}

	@Override
	public boolean equals(@Nullable final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof final Point other)) {
			return false;
		}
		return this.x == other.x && this.y == other.y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.x, this.y);
	}

	@Override
	public String toString() {
		return "Point[X=" + this.x + ",Y=" + this.y + ']';
	}

	@NotNull
	public static Point create(final int x, final int y) {
		return new Point(x, y, false);
	}

	@NotNull
	public static Point create(final double x, final double y) {
		return new Point(x, y, false);
	}

	@NotNull
	public static Point createMutable(final int x, final int y) {
		return new Point(x, y, true);
	}

	@NotNull
	public static Point createMutable(final double x, final double y) {
		return new Point(x, y, true);
	}
}
