package dawn.lib.lang;

import dawn.Dawn;

public class LocalizableText extends Text {

	private final Object[] format;

	LocalizableText(final String text, final Object[] format) {
		super(text);
		this.format = format;
	}

	@Override
	public String getString() {
		final String key = super.getString();
		if (this.format.length == 0) {
			return Dawn.getDawn().getLocalizer().localize(key);
		} else {
			return Dawn.getDawn().getLocalizer().localize(key, this.format);
		}
	}
}
