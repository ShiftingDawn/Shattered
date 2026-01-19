package dawn.gfx;

import dawn.asset.Font;
import dawn.lib.lang.Text;

public interface FontRenderer {

	FontRenderer start();

	FontRenderer set(Text text, Color tint);

	FontRenderer set(Text text);

	default FontRenderer set(final String text, final Color tint) {
		return this.set(Text.literal(text), tint);
	}

	default FontRenderer set(final String text) {
		return this.set(Text.literal(text));
	}

	FontRenderer size(int size);

	FontRenderer pos(int x, int y);

	FontRenderer color(Color color);

	FontRenderer write();

	void end();

	int getStringWidth(Text text, int fontSize);

	default int getStringWidth(final String str, final int fontSize) {
		return this.getStringWidth(Text.literal(str), fontSize);
	}

	int getStringHeight(int fontSize);

	static int getStringWidth(final Font font, final Text text, final int fontSize) {
		float value = 0;
		for (final char c : text.getString().toCharArray()) {
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

	static int getStringWidth(final Font font, final String str, final int fontSize) {
		return FontRenderer.getStringWidth(font, Text.literal(str), fontSize);
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
