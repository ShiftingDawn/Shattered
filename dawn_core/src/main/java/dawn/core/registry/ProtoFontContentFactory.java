package dawn.core.registry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import dawn.Identifier;
import dawn.core.ExitException;
import dawn.lib.GsonHelper;
import dawn.lib.ResourceFinder;
import dawn.registry.ProtoFont;
import dawn.registry.RegistryContentFactory;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

final class ProtoFontContentFactory implements RegistryContentFactory<ProtoFont> {

	@Override
	public ProtoFont make(final Logger logger, final ResourceFinder resources, final Identifier registryKey) {
		logger.debug("Loading font definition {}", registryKey);
		final JsonData data = ProtoFontContentFactory.loadJsonData(logger, resources, registryKey);
		if (data == null) {
			throw new ExitException();
		}
		return new ProtoFontImpl(registryKey);
	}

	private static @Nullable JsonData loadJsonData(final Logger logger, final ResourceFinder resources, final Identifier resource) {
		final String path = resources.makePath(resource, "font", "json");
		try (InputStream stream = resources.getStream(path)) {
			if (stream == null) {
				throw new FileNotFoundException();
			}
			final InputStreamReader reader = new InputStreamReader(stream);
			final JsonElement json = GsonHelper.deserialize(reader);
			if (json == null || !json.isJsonObject()) {
				throw new IOException("Input is not a JSON object!");
			}
			final JsonData result = GsonHelper.deserialize(json, JsonData.class);
			if (result == null) {
				throw new IOException("Could not parse json data");
			}
			return result;
		} catch (final FileNotFoundException ignored) {
			logger.debug("Could not find metadata for font \"{}\", skipping. Expected path: '{}'", resource, path);
			return null;
		} catch (final IOException | JsonIOException | JsonSyntaxException e) {
			logger.error("Could not read font metadata for font \"{}\"", resource);
			logger.error(e);
			return null;
		}
	}

	@NullUnmarked
	private static class JsonData {
	}
}
