package dawn.core.registry;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonObject;
import dawn.Identifier;
import dawn.lib.GsonHelper;
import dawn.lib.ResourceFinder;
import dawn.registry.ProtoLanguage;
import org.apache.logging.log4j.Logger;

final class ProtoLanguageContentFactory extends BaseRegistryContentFactory<ProtoLanguage> {

	@Override
	public ProtoLanguage make(final Logger logger, final ResourceFinder resources, final Identifier registryKey) {
		this.printDescription(logger, "language", registryKey);
		final JsonObject json = this.loadJsonData(JsonObject.class, logger, resources, registryKey, "language", true)
			.orElseGet(JsonObject::new);
		final Map<String, String> entries = new HashMap<>();
		for (final String key : json.keySet()) {
			final String value = GsonHelper.getString(json, key);
			entries.put(key, value);
		}
		return new ProtoLanguageImpl(registryKey, Collections.unmodifiableMap(entries));
	}
}
