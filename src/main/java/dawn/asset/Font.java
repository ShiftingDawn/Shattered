package dawn.asset;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import org.jspecify.annotations.Nullable;

public record Font(FontAsset asset, Texture texture, Char2ObjectMap<Glyph> glyphs, float lineHeight, int builtInSize) {

	public static int SIZE = 32;

	public @Nullable Glyph get(final char c) {
		return this.glyphs.get(c);
	}

	public record Glyph(float x0, float y0, float x1, float y1, float xOffset, float yOffset, float advance) {

		public float[] getNormalizedUvs(final Font font) {
			final float[] result = new float[4];
			result[0] = this.x0 / (float) font.texture().width();
			result[1] = this.y0 / (float) font.texture().height();
			result[2] = this.x1 / (float) font.texture().width();
			result[3] = this.y1 / (float) font.texture().height();
			return result;
		}
	}
}
