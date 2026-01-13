package dawn.registry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import dawn.asset.FontAsset;
import dawn.asset.ResourceResolver;
import dawn.lib.json.GsonHelper;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;

final class FontRegistryContentFactory implements RegistryContentFactory<FontAsset> {

	@Override
	public void make(final Logger logger, final ResourceResolver assets, final Registry<FontAsset> registry, final Identifier registryKey) {
		logger.debug("Loading font definition {}", registryKey);
		final JsonFontData data = FontRegistryContentFactory.loadJsonData(logger, assets, registryKey);
		if (data == null) {
			return;
		}
		registry.register(registryKey, new FontAsset(registryKey));
	}

	public static @Nullable JsonFontData loadJsonData(final Logger logger, final ResourceResolver assets, final Identifier resource) {
		final String path = assets.makePath(resource, "font", "json");
		try (InputStream stream = assets.getStream(path)) {
			if (stream == null) {
				throw new FileNotFoundException();
			}
			final InputStreamReader reader = new InputStreamReader(stream);
			final JsonElement json = GsonHelper.deserialize(reader);
			if (json == null || !json.isJsonObject()) {
				throw new IOException("Input is not a JSON object!");
			}
			final JsonFontData result = GsonHelper.deserialize(json, JsonFontData.class);
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
}
