package dawn.gfx;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import dawn.Dawn;
import dawn.asset.AtlasTexture;
import dawn.asset.ShaderAsset;
import dawn.asset.Texture;
import dawn.asset.TextureAsset;
import dawn.lib.Color;
import dawn.lib.Util;
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

	//TODO change shader access
	private final Dawn dawn;
	private final Function<Identifier, Texture> textureGetter;
	private final ConcurrentLinkedQueue<DrawCall> calls = new ConcurrentLinkedQueue<>();
	private boolean drawing = false;
	//TODO make stack size variable somehow
	private final Matrix4fStack matrixStack = new Matrix4fStack(6);
	private final float[] bounds = new float[4];
	private final float[] uv = new float[4];
	private final Color[] color = new Color[4];
	private @Nullable Texture texture;

	public Tessellator(final Dawn dawn, final Function<Identifier, Texture> textureGetter) {
		this.dawn = dawn;
		this.textureGetter = textureGetter;
		this.reset();
	}

	private void testDrawing() {
		if (!this.drawing) {
			throw new IllegalStateException("Not tessellating");
		}
	}

	private void reset() {
		this.matrixStack.clear();
		this.pos(0, 0, 0, 0);
		this.uv(0, 0, 0, 0);
		Arrays.fill(this.color, Color.WHITE);
		this.texture = null;
	}

	public Tessellator start() {
		if (this.drawing) {
			throw new IllegalStateException("Already tessellating");
		}
		this.drawing = true;
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

	public Tessellator set(final Texture texture, final Color tint) {
		this.testDrawing();
		this.texture = texture;
		this.color(tint);
		this.size(texture.width(), texture.height());
		this.uv(0, 0, texture.width(), texture.height());
		return this;
	}

	public Tessellator set(final Texture texture) {
		return this.set(texture, Color.WHITE);
	}

	public Tessellator set(final Identifier texture, final Color tint) {
		return this.set(this.textureGetter.apply(texture), tint);
	}

	public Tessellator set(final Identifier texture) {
		return this.set(texture, Color.WHITE);
	}

	public Tessellator set(final Color color) {
		this.testDrawing();
		this.color(color);
		this.size(100, 100);
		return this;
	}

	public Tessellator pos(final int x, final int y, final int width, final int height) {
		this.bounds[0] = x;
		this.bounds[1] = y;
		this.bounds[2] = width;
		this.bounds[3] = height;
		return this;
	}

	public Tessellator pos(final int x, final int y, final Dimension size) {
		return this.pos(x, y, size.getWidth(), size.getHeight());
	}

	public Tessellator pos(final Point position, final int width, final int height) {
		return this.pos(position.getX(), position.getY(), width, height);
	}

	public Tessellator pos(final Point position, final Dimension size) {
		return this.pos(position.getX(), position.getY(), size.getWidth(), size.getHeight());
	}

	public Tessellator pos(final int x, final int y) {
		this.bounds[0] = x;
		this.bounds[1] = y;
		return this;
	}

	public Tessellator pos(final Point position) {
		return this.pos(position.getX(), position.getY());
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

	public Tessellator color(final Color colorTopLeft, final Color colorTopRight, final Color colorBottomRight, final Color colorBottomLeft) {
		this.color[0] = colorTopLeft;
		this.color[1] = colorBottomLeft;
		this.color[2] = colorBottomRight;
		this.color[3] = colorTopRight;
		return this;
	}

	public Tessellator color(final Color color) {
		return this.color(color, color, color, color);
	}

	public Tessellator colorTopLeft(final Color color) {
		this.color[0] = color;
		return this;
	}

	public Tessellator colorTopRight(final Color color) {
		this.color[3] = color;
		return this;
	}

	public Tessellator colorBottomLeft(final Color color) {
		this.color[1] = color;
		return this;
	}

	public Tessellator colorBottomRight(final Color color) {
		this.color[2] = color;
		return this;
	}

	public Tessellator colorTop(final Color color) {
		return this.colorTopLeft(color).colorTopRight(color);
	}

	public Tessellator colorBottom(final Color color) {
		return this.colorBottomLeft(color).colorBottomRight(color);
	}

	public Tessellator colorLeft(final Color color) {
		return this.colorTopLeft(color).colorBottomLeft(color);
	}

	public Tessellator colorRight(final Color color) {
		return this.colorTopRight(color).colorBottomRight(color);
	}

	public Tessellator draw(final Shader shader) {
		final DrawCall call = new DrawCall(this.matrixStack, this.bounds, this.uv, this.color, this.texture);
		Tessellator.render(call, shader, shader.getAsset());
		this.reset();
		return this;
	}

	public Tessellator draw() {
		return this.draw(this.dawn.getRenderManager().getShader());
	}

	private static void render(final DrawCall call, final Shader shader, final ShaderAsset data) {
		if (call.texture() != null) {
			if (!data.isCanTexture()) {
				throw new IllegalStateException("The currently bound shader '%s' does not support rendering textures".formatted(data.getRegistryKey()));
			}
		} else if (!data.isCanColor()) {
			throw new IllegalStateException("The currently bound shader '%s' does not support rendering colors".formatted(data.getRegistryKey()));
		}
		final float x0 = call.bounding()[0];
		final float y0 = call.bounding()[1];
		final float x1 = x0 + call.bounding()[2];
		final float y1 = y0 + call.bounding()[3];
		if (call.texture() == null) {
			final BufferBuilder builder = new BufferBuilder(VertexFormats.FORMAT_COLOR, 4, GL_TRIANGLE_FAN, () -> {
				shader.bind();
				if (data.isCanTexture()) {
					ShaderProps.setUniform1(ShaderProps.getNamedLocation(shader, data.getPropEnableTexture()), GL_FALSE);
				}
				ShaderProps.setUniform4(ShaderProps.getNamedLocation(shader, data.getPropMatrixTessellatorTransform()), false, call.matrixStack());
			});
			builder.position(x0, y0).color(call.color()[0]).endVertex();
			builder.position(x0, y1).color(call.color()[1]).endVertex();
			builder.position(x1, y1).color(call.color()[2]).endVertex();
			builder.position(x1, y0).color(call.color()[3]).endVertex();
			builder.draw();
		} else {
			final Texture tex = call.texture();
			if (tex.asset() instanceof final TextureAsset.Bordered bordered) {
				final float ix0 = x0 + bordered.borderLeft;
				final float iy0 = y0 + bordered.borderTop;
				final float ix1 = x1 - bordered.borderRight;
				final float iy1 = y1 - bordered.borderBottom;
				final float[] uvs = Tessellator.normalizeUV(tex, call.uv());
				final float[] iuvs = Tessellator.normalizeUV(tex, Util.make(new float[4], arr -> {
					arr[0] = call.uv[0] + bordered.borderLeft;
					arr[1] = call.uv[1] + bordered.borderTop;
					arr[2] = call.uv[2] - bordered.borderRight;
					arr[3] = call.uv[3] - bordered.borderBottom;
				}));
				//Corners
				Tessellator.renderInternal(shader, data, tex, x0, y0, ix0, iy0, call.color(), call.matrixStack(), uvs[0], uvs[1], iuvs[0], iuvs[1]);
				Tessellator.renderInternal(shader, data, tex, ix1, y0, x1, iy0, call.color(), call.matrixStack(), iuvs[2], uvs[1], uvs[2], iuvs[1]);
				Tessellator.renderInternal(shader, data, tex, x0, iy1, ix0, y1, call.color(), call.matrixStack(), uvs[0], iuvs[3], iuvs[0], uvs[3]);
				Tessellator.renderInternal(shader, data, tex, ix1, iy1, x1, y1, call.color(), call.matrixStack(), iuvs[2], iuvs[3], uvs[2], uvs[3]);
				//Sides
				Tessellator.renderInternal(shader, data, tex, ix0, y0, ix1, iy0, call.color(), call.matrixStack(), iuvs[0], uvs[1], iuvs[2], iuvs[1]);
				Tessellator.renderInternal(shader, data, tex, x0, iy0, ix0, iy1, call.color(), call.matrixStack(), uvs[0], iuvs[1], iuvs[0], iuvs[3]);
				Tessellator.renderInternal(shader, data, tex, ix1, iy0, x1, iy1, call.color(), call.matrixStack(), iuvs[2], iuvs[1], uvs[2], iuvs[3]);
				Tessellator.renderInternal(shader, data, tex, ix0, iy1, ix1, y1, call.color(), call.matrixStack(), iuvs[0], iuvs[3], iuvs[2], uvs[3]);
				//Center
				Tessellator.renderInternal(shader, data, tex, ix0, iy0, ix1, iy1, call.color(), call.matrixStack(), iuvs[0], iuvs[1], iuvs[2], iuvs[3]);
			} else {
				final float[] uvs = Tessellator.normalizeUV(tex, call.uv());
				Tessellator.renderInternal(shader, data, tex, x0, y0, x1, y1, call.color(), call.matrixStack(), uvs[0], uvs[1], uvs[2], uvs[3]);
			}
		}
	}

	private static void renderInternal(
		final Shader shader, final ShaderAsset data, final Texture texture, final float x0, final float y0, final float x1, final float y1, final Color[] colors, final Matrix4f transform,
		final float u0, final float v0, final float u1, final float v1
	) {
		final BufferBuilder builder = new BufferBuilder(VertexFormats.FORMAT_TEXTURE, 4, GL_TRIANGLE_FAN, () -> {
			shader.bind();
			GlStateManager.bindTexture(texture.id());
			ShaderProps.setUniform1(ShaderProps.getNamedLocation(shader, data.getPropEnableTexture()), GL_TRUE);
			ShaderProps.setUniform4(ShaderProps.getNamedLocation(shader, data.getPropMatrixTessellatorTransform()), false, transform);
		});
		builder.position(x0, y0).color(colors[0]).uv(u0, v0).endVertex();
		builder.position(x0, y1).color(colors[1]).uv(u0, v1).endVertex();
		builder.position(x1, y1).color(colors[2]).uv(u1, v1).endVertex();
		builder.position(x1, y0).color(colors[3]).uv(u1, v0).endVertex();
		builder.draw();
	}

	public void end() {
		this.drawing = false;
	}

	private static float[] normalizeUV(final Texture texture, final float[] uvs) {
		if (texture instanceof final AtlasTexture atlasTexture) {
			final float[] newUvs = new float[4];
			System.arraycopy(uvs, 0, newUvs, 0, 4);
			newUvs[0] += atlasTexture.u0();
			newUvs[1] += atlasTexture.v0();
			newUvs[2] += atlasTexture.u0();
			newUvs[3] += atlasTexture.v0();
			return new float[] {
				Tessellator.normalizeUV(texture, newUvs[0], true),
				Tessellator.normalizeUV(texture, newUvs[1], false),
				Tessellator.normalizeUV(texture, newUvs[2], true),
				Tessellator.normalizeUV(texture, newUvs[3], false),
			};
		} else {
			return new float[] {
				Tessellator.normalizeUV(texture, uvs[0], true),
				Tessellator.normalizeUV(texture, uvs[1], false),
				Tessellator.normalizeUV(texture, uvs[2], true),
				Tessellator.normalizeUV(texture, uvs[3], false),
			};
		}
	}

	private static float normalizeUV(final Texture texture, final float uv, final boolean x) {
		return uv / (float) (x ? texture.imageWidth() : texture.imageHeight());
	}

	private record DrawCall(
		Matrix4fStack matrixStack,
		float[] bounding, float[] uv,
		Color[] color, @Nullable Texture texture
	) {
	}
}
