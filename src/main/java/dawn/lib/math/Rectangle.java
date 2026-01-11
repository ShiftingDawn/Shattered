package dawn.lib.math;

import java.util.Objects;
import lombok.Getter;

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

	public Rectangle setX(final double x) {
		if (!this.mutable) {
			return new Rectangle(x, this.getDoubleY(), this.getDoubleWidth(), this.getDoubleHeight(), false);
		}
		this.position.setX(x);
		return this;
	}

	public Rectangle setY(final double y) {
		if (!this.mutable) {
			return new Rectangle(this.getDoubleX(), y, this.getDoubleWidth(), this.getDoubleHeight(), false);
		}
		this.position.setY(y);
		return this;
	}

	public Rectangle setPosition(final double x, final double y) {
		if (!this.mutable) {
			return new Rectangle(x, y, this.getDoubleWidth(), this.getDoubleHeight(), false);
		}
		this.position.setX(x);
		this.position.setY(y);
		return this;
	}

	public Rectangle setPosition(final Point position) {
		return this.setPosition(position.getDoubleX(), position.getDoubleY());
	}

	public Rectangle setWidth(final double width) {
		if (!this.mutable) {
			return new Rectangle(this.getDoubleX(), this.getDoubleY(), width, this.getDoubleHeight(), false);
		}
		this.size.setWidth(width);
		return this;
	}

	public Rectangle setHeight(final double height) {
		if (!this.mutable) {
			return new Rectangle(this.getDoubleX(), this.getDoubleY(), this.getDoubleWidth(), height, false);
		}
		this.size.setHeight(height);
		return this;
	}

	public Rectangle setSize(final double width, final double height) {
		if (!this.mutable) {
			return new Rectangle(this.getDoubleX(), this.getDoubleY(), width, height, false);
		}
		this.size.setWidth(width);
		this.size.setHeight(height);
		return this;
	}

	public Rectangle setSize(final Dimension size) {
		return this.setSize(size.getDoubleWidth(), size.getDoubleHeight());
	}

	public Rectangle moveX(final double amount) {
		return this.setX(this.getDoubleX() + amount);
	}

	public Rectangle moveY(final double amount) {
		return this.setY(this.getDoubleY() + amount);
	}

	public Rectangle move(final double amountX, final double amountY) {
		return this.setPosition(this.getDoubleX() + amountX, this.getDoubleY() + amountY);
	}

	public Rectangle move(final Dimension amount) {
		return this.move(amount.getDoubleWidth(), amount.getDoubleHeight());
	}

	public Rectangle growX(final double amount) {
		return this.setWidth(this.getWidth() + amount);
	}

	public Rectangle growY(final double amount) {
		return this.setHeight(this.getHeight() + amount);
	}

	public Rectangle grow(final double amountX, final double amountY) {
		return this.setSize(this.getWidth() + amountX, this.getHeight() + amountY);
	}

	public Rectangle shrinkX(final double amount) {
		return this.setWidth(this.getWidth() - amount);
	}

	public Rectangle shrinkY(final double amount) {
		return this.setHeight(this.getHeight() - amount);
	}

	public Rectangle shrink(final double amountX, final double amountY) {
		return this.setSize(this.getWidth() - amountX, this.getHeight() - amountY);
	}

	public Rectangle grow(final Dimension size) {
		return this.grow(size.getWidth(), size.getHeight());
	}

	public boolean contains(final double x, final double y) {
		return x >= this.getX() && x <= this.getMaxX() && y >= this.getY() && y <= this.getMaxY();
	}

	public boolean contains(final Dimension position) {
		return this.contains(position.getWidth(), position.getHeight());
	}

	public boolean contains(final int x, final int y, final int width, final int height) {
		return x >= this.getX() && x + width <= this.getMaxX() && y >= this.getY() && y + height <= this.getMaxY();
	}

	public boolean contains(final int x, final int y, final Dimension size) {
		return this.contains(x, y, size.getWidth(), size.getHeight());
	}

	public boolean contains(final Dimension position, final Dimension size) {
		return this.contains(position.getWidth(), position.getHeight(), size.getWidth(), size.getHeight());
	}

	public boolean contains(final Dimension position, final int width, final int height) {
		return this.contains(position.getWidth(), position.getHeight(), width, height);
	}

	public boolean contains(final Rectangle rectangle) {
		return this.contains(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
	}

	public Point getPosition() {
		return this.position.toImmutable();
	}

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

	public Point getMaxPosition() {
		return Point.create(this.getDoubleMaxX(), this.getDoubleMaxY());
	}

	public Rectangle toMutable() {
		return this.mutable ? this : new Rectangle(
			this.getDoubleX(), this.getDoubleY(),
			this.getDoubleWidth(), this.getDoubleHeight(),
			true
		);
	}

	public Rectangle toImmutable() {
		return this.mutable ? new Rectangle(
			this.getDoubleX(), this.getDoubleY(),
			this.getDoubleWidth(), this.getDoubleHeight(),
			false
		) : this;
	}

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

	public static Rectangle create(final int x, final int y, final int width, final int height) {
		return new Rectangle(x, y, width, height, false);
	}

	public static Rectangle create(final double x, final double y, final double width, final double height) {
		return new Rectangle(x, y, width, height, false);
	}

	public static Rectangle create(final Point position, final Dimension size) {
		return new Rectangle(
			position.getDoubleX(), position.getDoubleY(),
			size.getDoubleWidth(), size.getDoubleHeight(),
			false
		);
	}

	public static Rectangle create(final int x, final int Y, final Dimension size) {
		return new Rectangle(x, Y, size.getDoubleWidth(), size.getDoubleHeight(), false);
	}

	public static Rectangle create(final double x, final double Y, final Dimension size) {
		return new Rectangle(x, Y, size.getDoubleWidth(), size.getDoubleHeight(), false);
	}

	public static Rectangle create(final Point position, final int width, final int height) {
		return new Rectangle(position.getDoubleX(), position.getDoubleY(), width, height, false);
	}

	public static Rectangle create(final Point position, final double width, final double height) {
		return new Rectangle(position.getDoubleX(), position.getDoubleY(), width, height, false);
	}

	public static Rectangle createMutable(final double x, final double y, final double width, final double height) {
		return new Rectangle(x, y, width, height, true);
	}

	public static Rectangle createMutable(final int x, final int y, final int width, final int height) {
		return new Rectangle(x, y, width, height, true);
	}

	public static Rectangle createMutable(final Point position, final Dimension size) {
		return new Rectangle(
			position.getDoubleX(), position.getDoubleY(),
			size.getDoubleWidth(), size.getDoubleHeight(),
			true
		);
	}

	public static Rectangle createMutable(final int x, final int Y, final Dimension size) {
		return new Rectangle(x, Y, size.getDoubleWidth(), size.getDoubleHeight(), true);
	}

	public static Rectangle createMutable(final double x, final double Y, final Dimension size) {
		return new Rectangle(x, Y, size.getDoubleWidth(), size.getDoubleHeight(), true);
	}

	public static Rectangle createMutable(final Point position, final int width, final int height) {
		return new Rectangle(position.getDoubleX(), position.getDoubleY(), width, height, true);
	}

	public static Rectangle createMutable(final Point position, final double width, final double height) {
		return new Rectangle(position.getDoubleX(), position.getDoubleY(), width, height, true);
	}
}
