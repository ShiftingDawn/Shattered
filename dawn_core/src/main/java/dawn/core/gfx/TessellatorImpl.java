package dawn.core.gfx;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import dawn.Dawn;
import dawn.Identifier;
import dawn.asset.Shader;
import dawn.asset.Texture;
import dawn.gfx.Color;
import dawn.gfx.GlStateManager;
import dawn.gfx.QuickDraw;
import dawn.gfx.ShaderProps;
import dawn.gfx.Tessellator;
import dawn.lib.Dimension;
import dawn.lib.Point;
import dawn.lib.Rectangle;
import dawn.registry.ProtoShader;
import dawn.registry.ProtoTexture;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.jspecify.annotations.Nullable;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.GL_TRUE;

final class TessellatorImpl implements Tessellator {

	//TODO change shader access
	private final Dawn dawn;
	private final Function<Identifier, Texture> textureGetter;
	private final ConcurrentLinkedQueue<DrawCall> calls = new ConcurrentLinkedQueue<>();
	private boolean drawing = false;
	//TODO make stack size variable somehow
	private final Matrix4fStack matrixStack = new Matrix4fStack(32);
	private final float[] size = new float[2];
	private final float[] uv = new float[4];
	private final Color[] color = new Color[4];
	private @Nullable Texture texture;

	public TessellatorImpl(final Dawn dawn, final Function<Identifier, Texture> textureGetter) {
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
		this.uv(0, 0, 0, 0);
		Arrays.fill(this.color, Color.WHITE);
		this.texture = null;
	}

	@Override
	public Tessellator start() {
		if (this.drawing) {
			throw new IllegalStateException("Already tessellating");
		}
		this.drawing = true;
		return this;
	}

	@Override
	public Tessellator pushMatrix(@Nullable final Consumer<Matrix4f> mod) {
		this.testDrawing();
		this.matrixStack.pushMatrix();
		if (mod != null) {
			mod.accept(this.matrixStack);
		}
		return this;
	}

	@Override
	public Tessellator pushMatrix() {
		return this.pushMatrix(null);
	}

	@Override
	public Tessellator modMatrix(final Consumer<Matrix4f> mod) {
		this.testDrawing();
		mod.accept(this.matrixStack);
		return this;
	}

	@Override
	public Tessellator popMatrix() {
		this.testDrawing();
		this.matrixStack.popMatrix();
		return this;
	}

	@Override
	public Tessellator set(final Texture texture, final Color tint) {
		this.testDrawing();
		this.texture = texture;
		this.color(tint);
		this.size(texture.width(), texture.height());
		this.uv(0, 0, texture.width(), texture.height());
		return this;
	}

	@Override
	public Tessellator set(final Texture texture) {
		return this.set(texture, Color.WHITE);
	}

	@Override
	public Tessellator set(final Identifier texture, final Color tint) {
		return this.set(this.textureGetter.apply(texture), tint);
	}

	@Override
	public Tessellator set(final Identifier texture) {
		return this.set(texture, Color.WHITE);
	}

	@Override
	public Tessellator set(final Color color) {
		this.testDrawing();
		this.color(color);
		this.size(100, 100);
		return this;
	}

	@Override
	public Tessellator pos(final Rectangle pos) {
		this.pos(pos.pos());
		this.size(pos.size());
		return this;
	}

	@Override
	public Tessellator pos(final int x, final int y, final int width, final int height) {
		this.pos(x, y);
		this.size(width, height);
		return this;
	}

	@Override
	public Tessellator pos(final int x, final int y, final Dimension size) {
		return this.pos(x, y, size.w(), size.h());
	}

	@Override
	public Tessellator pos(final Point position, final int width, final int height) {
		return this.pos(position.x(), position.y(), width, height);
	}

	@Override
	public Tessellator pos(final Point position, final Dimension size) {
		return this.pos(position.x(), position.y(), size.w(), size.h());
	}

	@Override
	public Tessellator pos(final int x, final int y) {
		return this.pushMatrix(mat -> mat.translate(x, y, 0));
	}

	@Override
	public Tessellator pos(final Point position) {
		return this.pos(position.x(), position.y());
	}

	@Override
	public Tessellator size(final int width, final int height) {
		this.size[0] = width;
		this.size[1] = height;
		return this;
	}

	@Override
	public Tessellator size(final Dimension size) {
		return this.size(size.w(), size.h());
	}

	@Override
	public Tessellator centerX(final int maxWidth) {
		return this.pushMatrix(mat -> mat.translate((maxWidth - this.size[0]) / 2, 0, 0));
	}

	@Override
	public Tessellator centerY(final int maxHeight) {
		return this.pushMatrix(mat -> mat.translate(0, (maxHeight - this.size[1]) / 2, 0));
	}

	@Override
	public Tessellator uv(final int uMin, final int vMin, final int uMax, final int vMax) {
		this.uv[0] = (float) uMin;
		this.uv[1] = (float) vMin;
		this.uv[2] = (float) uMax;
		this.uv[3] = (float) vMax;
		return this;
	}

	@Override
	public Tessellator color(final Color colorTopLeft, final Color colorTopRight, final Color colorBottomRight, final Color colorBottomLeft) {
		this.color[0] = colorTopLeft;
		this.color[1] = colorBottomLeft;
		this.color[2] = colorBottomRight;
		this.color[3] = colorTopRight;
		return this;
	}

	@Override
	public Tessellator color(final Color color) {
		return this.color(color, color, color, color);
	}

	@Override
	public Tessellator colorTopLeft(final Color color) {
		this.color[0] = color;
		return this;
	}

	@Override
	public Tessellator colorTopRight(final Color color) {
		this.color[3] = color;
		return this;
	}

	@Override
	public Tessellator colorBottomLeft(final Color color) {
		this.color[1] = color;
		return this;
	}

	@Override
	public Tessellator colorBottomRight(final Color color) {
		this.color[2] = color;
		return this;
	}

	@Override
	public Tessellator colorTop(final Color color) {
		return this.colorTopLeft(color).colorTopRight(color);
	}

	@Override
	public Tessellator colorBottom(final Color color) {
		return this.colorBottomLeft(color).colorBottomRight(color);
	}

	@Override
	public Tessellator colorLeft(final Color color) {
		return this.colorTopLeft(color).colorBottomLeft(color);
	}

	@Override
	public Tessellator colorRight(final Color color) {
		return this.colorTopRight(color).colorBottomRight(color);
	}

	@Override
	public Tessellator draw(final Shader shader) {
		final DrawCall call = new DrawCall(this.matrixStack, this.size, this.uv, this.color, this.texture);
		TessellatorImpl.render(call, shader, shader.proto());
		this.reset();
		return this;
	}

	@Override
	public Tessellator draw() {
		return this.draw(this.dawn.getRenderManager().getRootShader());
	}

	private static void render(final DrawCall call, final Shader shader, final ProtoShader proto) {
		if (call.texture() != null) {
			if (!proto.isCanTexture()) {
				throw new IllegalStateException("The currently bound shader '%s' does not support rendering textures".formatted(proto.getRegistryKey()));
			}
		} else if (!proto.isCanColor()) {
			throw new IllegalStateException("The currently bound shader '%s' does not support rendering colors".formatted(proto.getRegistryKey()));
		}
		final float w = call.bounding()[0];
		final float h = call.bounding()[1];
		if (call.texture() == null) {
			final BufferBuilder builder = new BufferBuilder(VertexFormats.FORMAT_COLOR, 4, GL_TRIANGLE_FAN, () -> {
				GlStateManager.gl().bindShader(shader.getProgram());
				if (proto.isCanTexture()) {
					ShaderProps.get().setUniform1(ShaderProps.get().getNamedLocation(shader, proto.getPropEnableTexture()), GL_FALSE);
				}
				ShaderProps.get().setUniform4(ShaderProps.get().getNamedLocation(shader, proto.getPropMatrixTessellatorTransform()), false, call.matrixStack());
			});
			builder.position(0, 0).color(call.color()[0]).endVertex();
			builder.position(0, h).color(call.color()[1]).endVertex();
			builder.position(w, h).color(call.color()[2]).endVertex();
			builder.position(w, 0).color(call.color()[3]).endVertex();
			builder.draw();
		} else {
			final Texture tex = call.texture();
			if (tex.proto() instanceof final ProtoTexture.Bordered bordered) {
				final float left = bordered.getBorderLeft();
				final float right = bordered.getBorderRight();
				final float top = bordered.getBorderTop();
				final float bottom = bordered.getBorderBottom();
				final float iw = w - left - right;
				final float ih = h - top - bottom;
				final float[] uvs = TessellatorImpl.normalizeUV(tex, call.uv());
				final float[] iuvs = TessellatorImpl.normalizeUV(tex, Dawn.make(new float[4], arr -> {
					arr[0] = call.uv[0] + bordered.getBorderLeft();
					arr[1] = call.uv[1] + bordered.getBorderTop();
					arr[2] = call.uv[2] - bordered.getBorderRight();
					arr[3] = call.uv[3] - bordered.getBorderBottom();
				}));
				final float[][] slices = new float[][] {
					{ left, top, 0, 0, uvs[0], uvs[1], iuvs[0], iuvs[1] },                     // top-left
					{ right, top, w - right, 0, iuvs[2], uvs[1], uvs[2], iuvs[1] },            // top-right
					{ left, bottom, 0, h - bottom, uvs[0], iuvs[3], iuvs[0], uvs[3] },         // bottom-left
					{ right, bottom, w - right, h - bottom, iuvs[2], iuvs[3], uvs[2], uvs[3] },// bottom-right
					{ iw, top, left, 0, iuvs[0], uvs[1], iuvs[2], iuvs[1] },                   // top
					{ left, ih, 0, top, uvs[0], iuvs[1], iuvs[0], iuvs[3] },                   // left
					{ right, ih, w - right, top, iuvs[2], iuvs[1], uvs[2], iuvs[3] },          // right
					{ iw, bottom, left, h - bottom, iuvs[0], iuvs[3], iuvs[2], uvs[3] },       // bottom
					{ iw, ih, left, top, iuvs[0], iuvs[1], iuvs[2], iuvs[3] }                  // center
				};
				final Matrix4f tempMatrix = new Matrix4f();
				for (final float[] slice : slices) {
					tempMatrix.set(call.matrixStack()).translate(slice[2], slice[3], 0);
					TessellatorImpl.renderInternal(shader, proto, tex, slice[0], slice[1], call.color(), tempMatrix, slice[4], slice[5], slice[6], slice[7]);
				}
			} else {
				final float[] uvs = TessellatorImpl.normalizeUV(tex, call.uv());
				TessellatorImpl.renderInternal(shader, proto, tex, w, h, call.color(), call.matrixStack(), uvs[0], uvs[1], uvs[2], uvs[3]);
			}
		}
	}

	private static void renderInternal(
		final Shader shader, final ProtoShader proto, final Texture texture, final float width, final float height, final Color[] colors, final Matrix4f transform,
		final float u0, final float v0, final float u1, final float v1
	) {
		final BufferBuilder builder = new BufferBuilder(VertexFormats.FORMAT_TEXTURE, 4, GL_TRIANGLE_FAN, () -> {
			GlStateManager.gl().bindShader(shader.getProgram());
			GlStateManager.gl().bindTexture(texture.id());
			ShaderProps.get().setUniform1(ShaderProps.get().getNamedLocation(shader, proto.getPropEnableTexture()), GL_TRUE);
			ShaderProps.get().setUniform4(ShaderProps.get().getNamedLocation(shader, proto.getPropMatrixTessellatorTransform()), false, transform);
			GlStateManager.gl().textureFilterHard();
		});
		builder.position(0, 0).color(colors[0]).uv(u0, v0).endVertex();
		builder.position(0, height).color(colors[1]).uv(u0, v1).endVertex();
		builder.position(width, height).color(colors[2]).uv(u1, v1).endVertex();
		builder.position(width, 0).color(colors[3]).uv(u1, v0).endVertex();
		builder.draw();
	}

	@Override
	public void end() {
		this.drawing = false;
	}

	private static float[] normalizeUV(final Texture texture, final float[] uvs) {
		return new float[] {
			TessellatorImpl.normalizeUV(texture, uvs[0] + texture.u0(), true),
			TessellatorImpl.normalizeUV(texture, uvs[1] + texture.v0(), false),
			TessellatorImpl.normalizeUV(texture, uvs[2] + texture.u0(), true),
			TessellatorImpl.normalizeUV(texture, uvs[3] + texture.v0(), false),
		};
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

	private Tessellator quickDraw(final Runnable selfSetter, final Consumer<QuickDraw> propSetter) {
		final boolean alreadyDrawing = this.drawing;
		if (!alreadyDrawing) {
			this.start();
		}
		selfSetter.run();
		propSetter.accept(new TessellatorQuickDrawImpl(this));
		this.draw();
		if (!alreadyDrawing) {
			this.end();
		}
		return this;
	}

	@Override
	public Tessellator render(final Texture texture, final Color tint, final Consumer<QuickDraw> propSetter) {
		return this.quickDraw(() -> this.set(texture, tint), propSetter);
	}

	@Override
	public Tessellator render(final Texture texture, final Consumer<QuickDraw> propSetter) {
		return this.quickDraw(() -> this.set(texture), propSetter);
	}

	@Override
	public Tessellator render(final Identifier texture, final Color tint, final Consumer<QuickDraw> propSetter) {
		return this.quickDraw(() -> this.set(texture, tint), propSetter);
	}

	@Override
	public Tessellator render(final Identifier texture, final Consumer<QuickDraw> propSetter) {
		return this.quickDraw(() -> this.set(texture), propSetter);
	}

	@Override
	public Tessellator render(final Color color, final Consumer<QuickDraw> propSetter) {
		return this.quickDraw(() -> this.set(color), propSetter);
	}
}
