package dawn.gfx;

import java.util.Arrays;
import java.util.function.Consumer;
import dawn.asset.Texture;
import dawn.asset.TextureManager;
import dawn.lib.Color;
import dawn.lib.math.Dimension;
import dawn.lib.math.Point;
import dawn.registry.Identifier;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.jspecify.annotations.Nullable;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.GL_TRUE;

public final class Tessellator {

	private final Matrix4fStack matrixStack = new Matrix4fStack(6);
	private final float[] bounds = new float[4];
	private final Float[] uv = new Float[4];
	private final Shader shader;
	private final TextureManager textures;
	private boolean drawing = false;

	public Tessellator(final Shader shader, final TextureManager textures) {
		this.shader = shader;
		this.textures = textures;
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
		this.set(0, 0, 0, 0);
		Arrays.fill(this.uv, null);
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
		this.bounds[0] = x;
		this.bounds[1] = y;
		this.bounds[2] = width;
		this.bounds[3] = height;
		return this;
	}

	public Tessellator set(final int x, final int y, final Dimension size) {
		return this.set(x, y, size.getWidth(), size.getHeight());
	}

	public Tessellator set(final Point position, final int width, final int height) {
		return this.set(position.getX(), position.getY(), width, height);
	}

	public Tessellator set(final Point position, final Dimension size) {
		return this.set(position.getX(), position.getY(), size.getWidth(), size.getHeight());
	}

	public Tessellator position(final int x, final int y) {
		this.bounds[0] = x;
		this.bounds[1] = y;
		return this;
	}

	public Tessellator position(final Point position) {
		return this.position(position.getX(), position.getY());
	}

	public Tessellator size(final int width, final int height) {
		this.bounds[2] = width;
		this.bounds[3] = height;
		return this;
	}

	public Tessellator size(final Dimension size) {
		return this.size(size.getWidth(), size.getHeight());
	}

	public Tessellator uv(final int uMin, final int vMin, final int uMax, final int vMax) {
		this.uv[0] = (float) uMin;
		this.uv[1] = (float) vMin;
		this.uv[2] = (float) uMax;
		this.uv[3] = (float) vMax;
		return this;
	}

	public Tessellator draw(final Color color) {
		final BufferBuilder builder = new BufferBuilder(VertexFormats.FORMAT_COLOR, 4, GL_TRIANGLE_FAN, () -> {
			this.shader.bind();
			ShaderProps.setUniform1(ShaderProps.getNamedLocation(this.shader, "enableTextures"), GL_FALSE);
			ShaderProps.setUniform4(ShaderProps.getNamedLocation(this.shader, "globalTransformMatrix"), false, this.matrixStack);
		});
		final float maxX = this.bounds[0] + this.bounds[2];
		final float maxY = this.bounds[1] + this.bounds[3];
		builder.position(this.bounds[0], this.bounds[1]).color(color).endVertex();
		builder.position(this.bounds[0], maxY).color(color).endVertex();
		builder.position(maxX, maxY).color(color).endVertex();
		builder.position(maxX, this.bounds[1]).color(color).endVertex();
		builder.draw();
		return this;
	}

	public Tessellator draw(final Identifier texture, final Color tint) {
		final Texture tex = this.textures.getTexture(texture);
		final BufferBuilder builder = new BufferBuilder(VertexFormats.FORMAT_TEXTURE, 4, GL_TRIANGLE_FAN, () -> {
			this.shader.bind();
			GlStateManager.bindTexture(tex.id());
			ShaderProps.setUniform1(ShaderProps.getNamedLocation(this.shader, "enableTextures"), GL_TRUE);
			ShaderProps.setUniform4(ShaderProps.getNamedLocation(this.shader, "globalTransformMatrix"), false, this.matrixStack);
		});
		final float maxX = this.bounds[0] + this.bounds[2];
		final float maxY = this.bounds[1] + this.bounds[3];
		final float[] uvs = this.normalizeUV(tex, this.uv);
		builder.position(this.bounds[0], this.bounds[1]).color(tint).uv(uvs[0], uvs[1]).endVertex();
		builder.position(this.bounds[0], maxY).color(tint).uv(uvs[0], uvs[3]).endVertex();
		builder.position(maxX, maxY).color(tint).uv(uvs[2], uvs[3]).endVertex();
		builder.position(maxX, this.bounds[1]).color(tint).uv(uvs[2], uvs[1]).endVertex();
		builder.draw();
		return this;
	}

	public void end() {
		this.drawing = false;
	}

	private float[] normalizeUV(final Texture texture, final @Nullable Float[] uvs) {
		final float[] result = new float[4];
		result[0] = uvs[0] == null ? 0 : uvs[0] / (float) texture.width();
		result[1] = uvs[1] == null ? 0 : uvs[1] / (float) texture.height();
		result[2] = uvs[2] == null ? 1 : uvs[2] / (float) texture.width();
		result[3] = uvs[3] == null ? 1 : uvs[3] / (float) texture.height();
		return result;
	}
}
