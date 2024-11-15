package shattered.lib.gfx;

import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import shattered.lib.util.Color;
import shattered.lib.util.math.Dimension;
import shattered.lib.util.math.Point;
import shattered.lib.util.math.Rectangle;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;

public final class Tessellator {

	private final Matrix4fStack matrixStack = new Matrix4fStack(6);
	private final Rectangle bounds = Rectangle.createMutable(0, 0, 0, 0);
	private final ShaderProgram shader;
	private boolean drawing = false;

	public Tessellator(final ShaderProgram shader) {
		this.shader = shader;
	}

	private void testDrawing() {
		if (!this.drawing) {
			throw new IllegalStateException("Not tessellating");
		}
	}

	public Tessellator start() {
		if (this.drawing) {
			throw new IllegalStateException("Already tessellating");
		}
		this.drawing = true;
		this.matrixStack.clear();
		this.bounds.setPosition(0, 0).setSize(0, 0);
		return this;
	}

	public Tessellator pushMatrix(@Nullable final Consumer<Matrix4f> mod) {
		this.testDrawing();
		this.matrixStack.pushMatrix();
		if (mod != null) {
			mod.accept(this.matrixStack);
		}
		return this;
	}

	public Tessellator pushMatrix() {
		return this.pushMatrix(null);
	}

	public Tessellator popMatrix() {
		this.testDrawing();
		this.matrixStack.popMatrix();
		return this;
	}

	public Tessellator set(final int x, final int y, final int width, final int height) {
		this.bounds.setPosition(x, y).setSize(width, height);
		return this;
	}

	public Tessellator set(final int x, final int y, final Dimension size) {
		this.bounds.setPosition(x, y).setSize(size);
		return this;
	}

	public Tessellator set(final Point position, final int width, final int height) {
		this.bounds.setPosition(position).setSize(width, height);
		return this;
	}

	public Tessellator set(final Point position, final Dimension size) {
		this.bounds.setPosition(position).setSize(size);
		return this;
	}

	public Tessellator position(final int x, final int y) {
		this.bounds.setPosition(x, y);
		return this;
	}

	public Tessellator position(final Point position) {
		this.bounds.setPosition(position);
		return this;
	}

	public Tessellator size(final int width, final int height) {
		this.bounds.setSize(width, height);
		return this;
	}

	public Tessellator size(final Dimension size) {
		this.bounds.setSize(size);
		return this;
	}

	public Tessellator draw(final Color color) {
		final BufferBuilder builder = new BufferBuilder(GeneralVertexFormats.FORMAT_COLOR, 4, GL_TRIANGLE_FAN, () -> {
			this.shader.bind();
			ShaderProps.setUniform1(ShaderProps.getNamedLocation(this.shader, "enableTextures"), GL_FALSE);
			ShaderProps.setUniform4(ShaderProps.getNamedLocation(this.shader, "globalTransformMatrix"), false, this.matrixStack);
		});
		builder.position(this.bounds.getX(), this.bounds.getY()).color(color).endVertex();
		builder.position(this.bounds.getX(), this.bounds.getMaxY()).color(color).endVertex();
		builder.position(this.bounds.getMaxX(), this.bounds.getMaxY()).color(color).endVertex();
		builder.position(this.bounds.getMaxX(), this.bounds.getY()).color(color).endVertex();
		builder.draw();
		return this;
	}

	public void end() {
		this.drawing = false;
	}
}
