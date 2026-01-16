package dawn.gfx;

import dawn.asset.Font;

public interface FontRenderer {

	FontRenderer start();

	FontRenderer set(String text, Color tint);

	FontRenderer set(String text);

	FontRenderer size(int size);

	FontRenderer pos(int x, int y);

	FontRenderer color(Color color);

	FontRenderer write();

	void end();

	int getStringWidth(String str, int fontSize);

	int getStringHeight(int fontSize);

	static int getStringWidth(final Font font, final String str, final int fontSize) {
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

	static int getStringHeight(final Font font, final int fontSize) {
		if (fontSize < 0) {
			return (int) font.lineHeight();
		}
		return (int) (font.lineHeight() * FontRenderer.getFontScale(font, fontSize));
	}

	static float getFontScale(final Font font, final int fontSize) {
		if (fontSize < 0) {
			return 1f;
		}
		return (float) fontSize / (float) font.builtInSize();
	}
}
