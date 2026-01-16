package dawn.core.registry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import dawn.Identifier;
import dawn.core.ExitException;
import dawn.lib.GsonHelper;
import dawn.lib.Json;
import dawn.lib.ResourceFinder;
import dawn.registry.ProtoShader;
import dawn.registry.RegistryContentFactory;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

final class ProtoShaderContentFactory implements RegistryContentFactory<ProtoShader> {

	@Override
	public ProtoShader make(final Logger logger, final ResourceFinder resources, final Identifier registryKey) {
		logger.debug("Loading shader definition {}", registryKey);
		final JsonData data = ProtoShaderContentFactory.loadJsonData(logger, resources, registryKey);
		if (data == null) {
			throw new ExitException();
		}
		return new ProtoShaderImpl(registryKey, data.canTexture, data.canColor,
			data.propOutColor, data.propMatrixProjection, data.propMatrixModelView, data.propMatrixTessellatorTransform,
			data.propEnableTexture
		);
	}

	private static @Nullable JsonData loadJsonData(final Logger logger, final ResourceFinder resources, final Identifier resource) {
		final String path = resources.makePath(resource, "shader", "json");
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
			logger.error("Could not find metadata for shader \"{}\", skipping. Expected path: {}", resource, path);
			return null;
		} catch (final IOException | JsonIOException | JsonSyntaxException e) {
			logger.error("Could not read shader metadata for shader \"{}\"", resource);
			logger.error(e);
			return null;
		}
	}

	@NullUnmarked
	private static class JsonData {

		@SerializedName("can_texture")
		public Boolean canTexture;

		@SerializedName("can_color")
		public Boolean canColor;

		@SerializedName("prop_out_color")
		@Json.Required
		public String propOutColor;

		@SerializedName("prop_matrix_projection")
		@Json.Required
		public String propMatrixProjection;

		@SerializedName("prop_matrix_modelview")
		@Json.Required
		public String propMatrixModelView;

		@SerializedName("prop_matrix_tessellator_transform")
		@Json.Required
		public String propMatrixTessellatorTransform;

		@SerializedName("prop_enable_textures")
		@Json.Required.When(fieldName = "can_texture")
		public String propEnableTexture;
	}
}
