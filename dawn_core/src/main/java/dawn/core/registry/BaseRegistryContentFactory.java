package dawn.core.registry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import dawn.Identifier;
import dawn.lib.GsonHelper;
import dawn.lib.ResourceFinder;
import dawn.registry.RegistryContentFactory;
import dawn.registry.RegistryObject;
import org.apache.logging.log4j.Logger;

abstract class BaseRegistryContentFactory<VALUE extends RegistryObject> implements RegistryContentFactory<VALUE> {

	protected final void printDescription(final Logger logger, final String type, final Identifier resource) {
		logger.debug("Loading {} definition {}", type, resource);
	}

	protected final <T> Optional<T> loadJsonData(final Class<T> clazz, final Logger logger, final ResourceFinder resources, final Identifier resource, final String type, final boolean allowNulls) {
		final String path = resources.makePath(resource, type, "json");
		try (InputStream stream = resources.getStream(path)) {
			if (stream == null) {
				throw new FileNotFoundException();
			}
			final InputStreamReader reader = new InputStreamReader(stream);
			final JsonElement json = GsonHelper.deserialize(reader);
			if (json == null || !json.isJsonObject()) {
				throw new IOException("Input is not a JSON object!");
			}
			final T result = GsonHelper.deserialize(json, clazz);
			if (result == null) {
				throw new IOException("Could not parse json data");
			}
			return Optional.of(result);
		} catch (final FileNotFoundException ignored) {
			if (allowNulls) {
				logger.debug("Could not find metadata for {} \"{}\". Expected path: '{}'", type, resource, path);
			} else {
				logger.debug("Could not find metadata for {} \"{}\", skipping. Expected path: '{}'", type, resource, path);
			}
			return Optional.empty();
		} catch (final IOException | JsonIOException | JsonSyntaxException e) {
			logger.error("Could not read metadata for {} \"{}\"", type, resource);
			logger.error(e);
			return Optional.empty();
		}
	}
}
