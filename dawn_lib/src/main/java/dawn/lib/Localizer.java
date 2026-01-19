package dawn.lib;

import dawn.Dawn;
import dawn.Identifier;

public interface Localizer {

	String DEFAULT_LANGUAGE = "en_us";

	String localize(String key);

	default String localize(final String key, final Object... format) {
		return String.format(this.localize(key), format);
	}

	default String localize(final Identifier id, final String type) {
		return this.localize(Dawn.makeKey(id, type));
	}

	default String localize(final Identifier id, final String type, final Object... format) {
		return String.format(this.localize(id, type), format);
	}

	void setActiveLanguage(String languageKey);

	String getActiveLanguage();
}
