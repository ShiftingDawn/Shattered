package dawn.core.registry;

import dawn.Identifier;
import dawn.core.ExitException;
import dawn.lib.ResourceFinder;
import dawn.registry.ProtoFont;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NullUnmarked;

final class ProtoFontContentFactory extends BaseRegistryContentFactory<ProtoFont> {

	@Override
	public ProtoFont make(final Logger logger, final ResourceFinder resources, final Identifier registryKey) {
		this.printDescription(logger, "font", registryKey);
		final JsonData data = this.loadJsonData(JsonData.class, logger, resources, registryKey, "font", false)
			.orElseThrow(ExitException::new);
		return new ProtoFontImpl(registryKey);
	}

	@NullUnmarked
	private static class JsonData {
	}
}
