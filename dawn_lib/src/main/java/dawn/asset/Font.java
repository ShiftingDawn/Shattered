package dawn.asset;

import dawn.registry.ProtoFont;
import org.jspecify.annotations.Nullable;

public interface Font extends ProtoAsset<ProtoFont> {

	int SIZE = 32;

	Texture getTexture();

	@Nullable Glyph get(char c);

	float lineHeight();

	float baseline();

	int builtInSize();

	interface Glyph {

		float x0();

		float y0();

		float x1();

		float y1();

		float xOffset();

		float yOffset();

		float advance();

		float[] getNormalizedUvs(Font font);
	}
}
