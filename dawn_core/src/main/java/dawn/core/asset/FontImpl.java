package dawn.core.asset;

import dawn.asset.Font;
import dawn.asset.Texture;
import dawn.registry.ProtoFont;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import org.jspecify.annotations.Nullable;

record FontImpl(ProtoFont proto, Texture texture, Char2ObjectMap<GlyphImpl> glyphs, float lineHeight, float baseline, int builtInSize) implements Font {

	@Override
	public Texture getTexture() {
		return this.texture;
	}

	@SuppressWarnings("DataFlowIssue")
	@Override
	public @Nullable Glyph get(final char c) {
		return this.glyphs.get(c);
	}

	record GlyphImpl(float x0, float y0, float x1, float y1, float xOffset, float yOffset, float advance) implements Glyph {

		@Override
		public float[] getNormalizedUvs(final Font font) {
			final float[] result = new float[4];
			result[0] = this.x0 / (float) font.getTexture().width();
			result[1] = this.y0 / (float) font.getTexture().height();
			result[2] = this.x1 / (float) font.getTexture().width();
			result[3] = this.y1 / (float) font.getTexture().height();
			return result;
		}
	}
}
