package dawn.registry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import dawn.asset.ResourceResolver;
import dawn.asset.ShaderAsset;
import dawn.lib.json.GsonHelper;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;

final class ShaderRegistryContentFactory implements RegistryContentFactory<ShaderAsset> {

	@Override
	public void make(final Logger logger, final ResourceResolver assets, final Registry<ShaderAsset> registry, final Identifier registryKey) {
		logger.debug("Loading shader definition {}", registryKey);
		final JsonShaderData data = ShaderRegistryContentFactory.loadJsonData(logger, assets, registryKey);
		if (data == null) {
			return;
		}
		registry.register(registryKey, new ShaderAsset(registryKey, data.canTexture, data.canColor,
			data.propOutColor, data.propMatrixProjection, data.propMatrixModelView, data.propMatrixTessellatorTransform,
			data.propEnableTexture
		));
	}

	public static @Nullable JsonShaderData loadJsonData(final Logger logger, final ResourceResolver assets, final Identifier resource) {
		final String path = assets.makePath(resource, "shader", "json");
		try (InputStream stream = assets.getStream(path)) {
			if (stream == null) {
				throw new FileNotFoundException();
			}
			final InputStreamReader reader = new InputStreamReader(stream);
			final JsonElement json = GsonHelper.deserialize(reader);
			if (json == null || !json.isJsonObject()) {
				throw new IOException("Input is not a JSON object!");
			}
			final JsonShaderData result = GsonHelper.deserialize(json, JsonShaderData.class);
			if (result == null) {
				throw new IOException("Could not parse json data");
			}
			return result;
		} catch (final FileNotFoundException ignored) {
			logger.debug("Could not find metadata for texture \"{}\", assuming defaults. Expected path: {}", resource, path);
			return null;
		} catch (final IOException | JsonIOException | JsonSyntaxException e) {
			logger.error("Could not read texture metadata for texture \"{}\"", resource);
			logger.error(e);
			logger.error("\tIgnoring the metadata and loading as a default texture");
			return null;
		}
	}
}
