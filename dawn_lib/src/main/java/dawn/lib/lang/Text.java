package dawn.lib.lang;

public class Text {

	public static final Text EMPTY = new Text("");
	private final String text;

	protected Text(final String text) {
		this.text = text;
	}

	public boolean isEmpty() {
		return this == Text.EMPTY || this.getString().isBlank();
	}

	public String getString() {
		return this.text;
	}

	public static Text literal(final String str) {
		return str.isBlank() ? Text.EMPTY : new Text(str);
	}

	public static Text localize(final String key, final Object... format) {
		return key.isBlank() ? Text.EMPTY : new LocalizableText(key.trim(), format);
	}
}
