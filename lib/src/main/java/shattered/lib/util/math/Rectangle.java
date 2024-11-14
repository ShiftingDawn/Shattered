package shattered.lib.util.math;

import java.util.Objects;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public final class Rectangle {

	public static final Rectangle EMPTY = Rectangle.create(Point.EMPTY, Dimension.EMPTY);
	@Getter
	private final boolean mutable;
	private final Point position;
	private final Dimension size;

	private Rectangle(final double x, final double y, final double width, final double height, final boolean mutable) {
		this.mutable = mutable;
		this.position = Point.createMutable(x, y);
		this.size = Dimension.createMutable(width, height);
	}

	@NotNull
	public Rectangle setX(final double x) {
		if (!this.mutable) {
			return new Rectangle(x, this.getDoubleY(), this.getDoubleWidth(), this.getDoubleHeight(), false);
		}
		this.position.setX(x);
		return this;
	}

	@NotNull
	public Rectangle setY(final double y) {
		if (!this.mutable) {
			return new Rectangle(this.getDoubleX(), y, this.getDoubleWidth(), this.getDoubleHeight(), false);
		}
		this.position.setY(y);
		return this;
	}

	@NotNull
	public Rectangle setPosition(final double x, final double y) {
		if (!this.mutable) {
			return new Rectangle(x, y, this.getDoubleWidth(), this.getDoubleHeight(), false);
		}
		this.position.setX(x);
		this.position.setY(y);
		return this;
	}

	@NotNull
	public Rectangle setPosition(@NotNull final Point position) {
		return this.setPosition(position.getDoubleX(), position.getDoubleY());
	}

	@NotNull
	public Rectangle setWidth(final double width) {
		if (!this.mutable) {
			return new Rectangle(this.getDoubleX(), this.getDoubleY(), width, this.getDoubleHeight(), false);
		}
		this.size.setWidth(width);
		return this;
	}

	@NotNull
	public Rectangle setHeight(final double height) {
		if (!this.mutable) {
			return new Rectangle(this.getDoubleX(), this.getDoubleY(), this.getDoubleWidth(), height, false);
		}
		this.size.setHeight(height);
		return this;
	}

	@NotNull
	public Rectangle setSize(final double width, final double height) {
		if (!this.mutable) {
			return new Rectangle(this.getDoubleX(), this.getDoubleY(), width, height, false);
		}
		this.size.setWidth(width);
		this.size.setHeight(height);
		return this;
	}

	@NotNull
	public Rectangle setSize(@NotNull final Dimension size) {
		return this.setSize(size.getDoubleWidth(), size.getDoubleHeight());
	}

	@NotNull
	public Rectangle moveX(final double amount) {
		return this.setX(this.getDoubleX() + amount);
	}

	@NotNull
	public Rectangle moveY(final double amount) {
		return this.setY(this.getDoubleY() + amount);
	}

	@NotNull
	public Rectangle move(final double amountX, final double amountY) {
		return this.setPosition(this.getDoubleX() + amountX, this.getDoubleY() + amountY);
	}

	@NotNull
	public Rectangle move(@NotNull final Dimension amount) {
		return this.move(amount.getDoubleWidth(), amount.getDoubleHeight());
	}

	@NotNull
	public Rectangle growX(final double amount) {
		return this.setWidth(this.getWidth() + amount);
	}

	@NotNull
	public Rectangle growY(final double amount) {
		return this.setHeight(this.getHeight() + amount);
	}

	@NotNull
	public Rectangle grow(final double amountX, final double amountY) {
		return this.setSize(this.getWidth() + amountX, this.getHeight() + amountY);
	}

	@NotNull
	public Rectangle shrinkX(final double amount) {
		return this.setWidth(this.getWidth() - amount);
	}

	@NotNull
	public Rectangle shrinkY(final double amount) {
		return this.setHeight(this.getHeight() - amount);
	}

	@NotNull
	public Rectangle shrink(final double amountX, final double amountY) {
		return this.setSize(this.getWidth() - amountX, this.getHeight() - amountY);
	}

	@NotNull
	public Rectangle grow(@NotNull final Dimension size) {
		return this.grow(size.getWidth(), size.getHeight());
	}

	public boolean contains(final double x, final double y) {
		return x >= this.getX() && x <= this.getMaxX() && y >= this.getY() && y <= this.getMaxY();
	}

	public boolean contains(@NotNull final Dimension position) {
		return this.contains(position.getWidth(), position.getHeight());
	}

	public boolean contains(final int x, final int y, final int width, final int height) {
		return x >= this.getX() && x + width <= this.getMaxX() && y >= this.getY() && y + height <= this.getMaxY();
	}

	public boolean contains(final int x, final int y, @NotNull final Dimension size) {
		return this.contains(x, y, size.getWidth(), size.getHeight());
	}

	public boolean contains(@NotNull final Dimension position, @NotNull final Dimension size) {
		return this.contains(position.getWidth(), position.getHeight(), size.getWidth(), size.getHeight());
	}

	public boolean contains(@NotNull final Dimension position, final int width, final int height) {
		return this.contains(position.getWidth(), position.getHeight(), width, height);
	}

	public boolean contains(@NotNull final Rectangle rectangle) {
		return this.contains(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
	}

	@NotNull
	public Point getPosition() {
		return this.position.toImmutable();
	}

	@NotNull
	public Dimension getSize() {
		return this.size.toImmutable();
	}

	public int getX() {
		return this.position.getX();
	}

	public double getDoubleX() {
		return this.position.getDoubleX();
	}

	public int getY() {
		return this.position.getY();
	}

	public double getDoubleY() {
		return this.position.getDoubleY();
	}

	public int getWidth() {
		return this.size.getWidth();
	}

	public double getDoubleWidth() {
		return this.size.getDoubleWidth();
	}

	public int getHeight() {
		return this.size.getHeight();
	}

	public double getDoubleHeight() {
		return this.size.getDoubleHeight();
	}

	public final int getCenterX() {
		return this.getX() + this.getWidth() / 2;
	}

	public final double getDoubleCenterX() {
		return this.getDoubleX() + this.getDoubleWidth() / 2.0;
	}

	public final int getCenterY() {
		return this.getY() + this.getHeight() / 2;
	}

	public final double getDoubleCenterY() {
		return this.getDoubleY() + this.getDoubleHeight() / 2.0;
	}

	@NotNull
	public final Point getCenter() {
		return Point.create(this.getDoubleCenterX(), this.getDoubleCenterY());
	}

	public int getMaxX() {
		return this.position.getX() + this.size.getWidth();
	}

	public double getDoubleMaxX() {
		return this.position.getDoubleX() + this.size.getDoubleWidth();
	}

	public int getMaxY() {
		return this.position.getY() + this.size.getHeight();
	}

	public double getDoubleMaxY() {
		return this.position.getDoubleY() + this.size.getDoubleHeight();
	}

	@NotNull
	public Point getMaxPosition() {
		return Point.create(this.getDoubleMaxX(), this.getDoubleMaxY());
	}

	@NotNull
	public Rectangle toMutable() {
		return this.mutable ? this : new Rectangle(
				this.getDoubleX(), this.getDoubleY(),
				this.getDoubleWidth(), this.getDoubleHeight(),
				true
		);
	}

	@NotNull
	public Rectangle toImmutable() {
		return this.mutable ? new Rectangle(
				this.getDoubleX(), this.getDoubleY(),
				this.getDoubleWidth(), this.getDoubleHeight(),
				false
		) : this;
	}

	@NotNull
	public Rectangle copy() {
		return new Rectangle(
				this.getDoubleX(), this.getDoubleY(),
				this.getDoubleWidth(), this.getDoubleHeight(),
				this.mutable
		);
	}

	@Override
	public int hashCode() {
		return Objects.hash(
				this.getDoubleX(), this.getDoubleY(),
				this.getDoubleWidth(), this.getDoubleHeight()
		);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof final Rectangle other)) {
			return false;
		}
		return this.getDoubleX() == other.getDoubleX() &&
				this.getDoubleY() == other.getDoubleY() &&
				this.getDoubleWidth() == other.getDoubleWidth() &&
				this.getDoubleHeight() == other.getDoubleHeight();
	}

	@Override
	public String toString() {
		return "Rectangle[X=" + this.getDoubleX() + ",Y=" + this.getDoubleY() +
				",Width=" + this.getDoubleWidth() + ",Height=" + this.getDoubleHeight() + ']';
	}

	@NotNull
	public static Rectangle create(final int x, final int y, final int width, final int height) {
		return new Rectangle(x, y, width, height, false);
	}

	@NotNull
	public static Rectangle create(final double x, final double y, final double width, final double height) {
		return new Rectangle(x, y, width, height, false);
	}

	@NotNull
	public static Rectangle create(@NotNull final Point position, @NotNull final Dimension size) {
		return new Rectangle(
				position.getDoubleX(), position.getDoubleY(),
				size.getDoubleWidth(), size.getDoubleHeight(),
				false
		);
	}

	@NotNull
	public static Rectangle create(final int x, final int Y, @NotNull final Dimension size) {
		return new Rectangle(x, Y, size.getDoubleWidth(), size.getDoubleHeight(), false);
	}

	@NotNull
	public static Rectangle create(final double x, final double Y, @NotNull final Dimension size) {
		return new Rectangle(x, Y, size.getDoubleWidth(), size.getDoubleHeight(), false);
	}

	@NotNull
	public static Rectangle create(@NotNull final Point position, final int width, final int height) {
		return new Rectangle(position.getDoubleX(), position.getDoubleY(), width, height, false);
	}

	@NotNull
	public static Rectangle create(@NotNull final Point position, final double width, final double height) {
		return new Rectangle(position.getDoubleX(), position.getDoubleY(), width, height, false);
	}

	@NotNull
	public static Rectangle createMutable(final double x, final double y, final double width, final double height) {
		return new Rectangle(x, y, width, height, true);
	}

	@NotNull
	public static Rectangle createMutable(final int x, final int y, final int width, final int height) {
		return new Rectangle(x, y, width, height, true);
	}

	@NotNull
	public static Rectangle createMutable(@NotNull final Point position, @NotNull final Dimension size) {
		return new Rectangle(
				position.getDoubleX(), position.getDoubleY(),
				size.getDoubleWidth(), size.getDoubleHeight(),
				true
		);
	}

	@NotNull
	public static Rectangle createMutable(final int x, final int Y, @NotNull final Dimension size) {
		return new Rectangle(x, Y, size.getDoubleWidth(), size.getDoubleHeight(), true);
	}

	@NotNull
	public static Rectangle createMutable(final double x, final double Y, @NotNull final Dimension size) {
		return new Rectangle(x, Y, size.getDoubleWidth(), size.getDoubleHeight(), true);
	}

	@NotNull
	public static Rectangle createMutable(@NotNull final Point position, final int width, final int height) {
		return new Rectangle(position.getDoubleX(), position.getDoubleY(), width, height, true);
	}

	@NotNull
	public static Rectangle createMutable(@NotNull final Point position, final double width, final double height) {
		return new Rectangle(position.getDoubleX(), position.getDoubleY(), width, height, true);
	}
}
