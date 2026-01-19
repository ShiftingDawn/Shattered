package dawn.core.lib;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import dawn.lib.Localizer;
import dawn.registry.ProtoLanguage;
import dawn.registry.Registries;
import lombok.Getter;
import static dawn.core.DawnImpl.LOGGER;

public final class LocalizerImpl implements Localizer {

	private final Map<String, String> entries = new ConcurrentHashMap<>();
	private @Getter String activeLanguage = Localizer.DEFAULT_LANGUAGE;

	public void init() {
		LOGGER.info("Reloading localizer");
		this.entries.clear();
		//Add fallback entries first
		for (final ProtoLanguage language : Registries.get().languages()) {
			if (Localizer.DEFAULT_LANGUAGE.equals(language.getRegistryKey().getPath())) {
				this.entries.putAll(language.getEntries());
			}
		}
		//Override with active language entries
		if (!Localizer.DEFAULT_LANGUAGE.equals(this.activeLanguage)) {
			LOGGER.debug("    Loaded {} fallback entries", this.entries.size());
			int activeEntries = 0;
			for (final ProtoLanguage language : Registries.get().languages()) {
				if (this.activeLanguage.equals(language.getRegistryKey().getPath())) {
					this.entries.putAll(language.getEntries());
					activeEntries += language.getEntries().size();
				}
			}
			LOGGER.debug("    Loaded {} active entries", activeEntries);
		} else {
			LOGGER.debug("    Loaded {} entries", this.entries.size());
		}
	}

	@Override
	public String localize(final String key) {
		return this.entries.computeIfAbsent(key, Function.identity());
	}

	@Override
	public void setActiveLanguage(final String activeLanguage) {
		if (!this.activeLanguage.equals(activeLanguage.trim())) {
			this.activeLanguage = activeLanguage.trim();
			this.init();
		}
	}
}
