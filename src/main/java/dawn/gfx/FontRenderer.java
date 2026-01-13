package dawn.gfx;

import java.util.function.Function;
import dawn.Dawn;
import dawn.asset.Font;
import dawn.asset.ShaderAsset;
import dawn.init.Fonts;
import dawn.lib.Color;
import dawn.registry.Identifier;
import org.joml.Vector2i;
import org.jspecify.annotations.Nullable;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.GL_TRUE;

public final class FontRenderer {

	private final Dawn dawn;
	private final Function<Identifier, Font> fontGetter;
	private boolean writing = false;
	private final Vector2i position = new Vector2i(0, 0);
	private int fontSize = Font.SIZE;
	private Color color = Color.WHITE;
	private @Nullable String txt;

	public FontRenderer(final Dawn dawn, final Function<Identifier, Font> fontGetter) {
		this.dawn = dawn;
		this.fontGetter = fontGetter;
		this.reset();
	}

	private void testWriting() {
		if (!this.writing) {
			throw new IllegalStateException("Not writing");
		}
	}

	private void reset() {
		this.position.set(0, 0);
		this.color = Color.WHITE;
		this.txt = null;
		this.fontSize = Font.SIZE;
	}

	public FontRenderer start() {
		if (this.writing) {
			throw new IllegalStateException("Already writing");
		}
		this.writing = true;
		return this;
	}

	public FontRenderer set(final String text, final Color tint) {
		this.testWriting();
		this.txt = text;
		this.color = tint;
		return this;
	}

	public FontRenderer set(final String text) {
		return this.set(text, Color.WHITE);
	}

	public FontRenderer size(final int size) {
		this.testWriting();
		this.fontSize = size > 0 ? size : Font.SIZE;
		return this;
	}

	public FontRenderer pos(final int x, final int y) {
		this.testWriting();
		this.position.set(x, y);
		return this;
	}

	public FontRenderer color(final Color color) {
		this.testWriting();
		this.color = color;
		return this;
	}

	public FontRenderer write() {
		this.testWriting();
		if (this.txt != null) {
			final WriteCall call = new WriteCall(this.txt, this.fontSize, this.position.x(), this.position.y(), this.fontGetter.apply(Fonts.ROOT), this.color);
			final Shader shader = this.dawn.getRenderManager().getShader();
			FontRenderer.render(call, shader, shader.getAsset());
		}
		this.reset();
		return this;
	}

	private static void render(final WriteCall call, final Shader shader, final ShaderAsset data) {
		if (!data.isCanTexture()) {
			throw new IllegalStateException("The currently bound shader '%s' does not support rendering textures (needed for font)".formatted(data.getRegistryKey()));
		}
		float penX = call.x();
		final float penY = call.y();
		final BufferBuilder builder = new BufferBuilder(VertexFormats.FORMAT_TEXTURE, call.txt().length() * 6, GL_TRIANGLE_STRIP, () -> {
			shader.bind();
			GlStateManager.bindTexture(call.font().texture().id());
			ShaderProps.setUniform1(ShaderProps.getNamedLocation(shader, data.getPropEnableTexture()), GL_TRUE);
			ShaderProps.setUniform4(ShaderProps.getNamedLocation(shader, data.getPropMatrixTessellatorTransform()), false, MatrixUtils.identity());
		});
		for (int i = 0; i < call.txt().length(); ++i) {
			final char c = call.txt().charAt(i);
			final Font.Glyph glyph = call.font().get(c);
			if (glyph == null) {
				continue;
			}
			final float scale = FontRenderer.getFontScale(call.font, call.size);
			final float x0 = penX + (glyph.xOffset() / scale);
			final float y0 = penY + (glyph.yOffset() / scale);
			final float x1 = x0 + ((glyph.x1() - glyph.x0()) / scale);
			final float y1 = y0 + ((glyph.y1() - glyph.y0()) / scale);
			final float[] uvs = glyph.getNormalizedUvs(call.font());
			if (i > 0) {
				//Connect to previous char so we can write the whole string in 1 call
				builder.position(x0, y0).color(call.color()).uv(uvs[0], uvs[1]).endVertex();
			}
			builder.position(x0, y0).color(call.color()).uv(uvs[0], uvs[1]).endVertex();
			builder.position(x0, y1).color(call.color()).uv(uvs[0], uvs[3]).endVertex();
			builder.position(x1, y0).color(call.color()).uv(uvs[2], uvs[1]).endVertex();
			builder.position(x1, y1).color(call.color()).uv(uvs[2], uvs[3]).endVertex();
			if (i++ < call.txt().length() - 1) {
				//Connect to next char so we can write the whole string in 1 call
				builder.position(x1, y1).color(call.color()).uv(uvs[2], uvs[3]).endVertex();
			}
			penX += (glyph.advance() / scale);
		}
		builder.draw();
	}

	public void end() {
		this.writing = false;
	}

	public static int getStringWidth(final Font font, final String str, final int fontSize) {
		float value = 0;
		for (final char c : str.toCharArray()) {
			final Font.Glyph glyph = font.get(c);
			if (glyph != null) {
				value += glyph.advance();
			}
		}
		if (fontSize < 0) {
			return (int) value;
		}
		return (int) (value * FontRenderer.getFontScale(font, fontSize));
	}

	public static int getStringHeight(final Font font, final int fontSize) {
		if (fontSize < 0) {
			return (int) font.lineHeight();
		}
		return (int) (font.lineHeight() * FontRenderer.getFontScale(font, fontSize));
	}

	public static float getFontScale(final Font font, final int fontSize) {
		if (fontSize < 0) {
			return 1f;
		}
		return (float) font.builtInSize() / (float) fontSize;
	}

	private record WriteCall(String txt, int size, int x, int y, Font font, Color color) {
	}
}
