package dawn.registry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import dawn.asset.ResourceResolver;
import dawn.asset.TextureAsset;
import dawn.asset.TextureType;
import dawn.lib.Util;
import dawn.lib.json.GsonHelper;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;

final class TextureRegistryContentFactory implements RegistryContentFactory<TextureAsset> {

	@Override
	public void make(final Logger logger, final ResourceResolver assets, final Registry<TextureAsset> registry, final Identifier registryKey) {
		logger.debug("Loading texture definition {}", registryKey);
		final JsonTextureData textureData = Optional.ofNullable(TextureRegistryContentFactory.loadJsonData(logger, assets, registryKey)).orElseGet(JsonTextureData.Default::new);
		final TextureAsset texture = switch (textureData.textureType) {
			case DEFAULT -> TextureRegistryContentFactory.makeDefault(registryKey, (JsonTextureData.Default) textureData);
			case STITCHED -> TextureRegistryContentFactory.makeStitched(registryKey, (JsonTextureData.Stitched) textureData);
			case BORDERED -> TextureRegistryContentFactory.makeBordered(registryKey, (JsonTextureData.Bordered) textureData);
			case ANIMATION -> TextureRegistryContentFactory.makeAnimation(registryKey, (JsonTextureData.Animation) textureData);
		};
		registry.register(registryKey, texture);
	}

	private static TextureAsset makeDefault(final Identifier registryKey, final JsonTextureData.Default data) {
		return new TextureAsset.Default(registryKey);
	}

	private static TextureAsset makeStitched(final Identifier registryKey, final JsonTextureData.Stitched data) {
		final int spriteWidth = data.spriteSize != null ? data.spriteSize : data.spriteWidth;
		final int spriteHeight = data.spriteSize != null ? data.spriteSize : data.spriteHeight;
		return new TextureAsset.Stitched(registryKey, data.spriteCount, spriteWidth, spriteHeight);
	}

	private static TextureAsset makeBordered(final Identifier registryKey, final JsonTextureData.Bordered data) {
		final int top = data.borderTop != null ? data.borderTop : -1;
		final int bottom = data.borderBottom != null ? data.borderBottom : -1;
		final int left = data.borderLeft != null ? data.borderLeft : -1;
		final int right = data.borderRight != null ? data.borderRight : -1;
		return new TextureAsset.Bordered(registryKey, top, bottom, left, right);
	}

	private static TextureAsset makeAnimation(final Identifier registryKey, final JsonTextureData.Animation data) {
		return new TextureAsset.Animation(registryKey, data.fps, data.frameMapping);
	}

	public static @Nullable JsonTextureData loadJsonData(final Logger logger, final ResourceResolver assets, final Identifier resource) {
		final String path = assets.makePath(resource, "texture", "json");
		try {
			return TextureRegistryContentFactory.loadJsonDataInternal(assets, path);
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

	private static @Nullable JsonTextureData loadJsonDataInternal(final ResourceResolver assets, final String path) throws IOException, JsonIOException, JsonSyntaxException {
		try (InputStream stream = assets.getStream(path)) {
			if (stream == null) {
				throw new FileNotFoundException();
			}
			final InputStreamReader reader = new InputStreamReader(stream);
			final JsonElement json = GsonHelper.deserialize(reader);
			if (json == null || !json.isJsonObject()) {
				throw new IOException("Input is not a JSON object!");
			}
			final TextureType textureType = Util.safeGet(() -> TextureType.getByName(GsonHelper.getString(json.getAsJsonObject(), "type")), () -> TextureType.DEFAULT);
			if (textureType == null) {
				throw new JsonSyntaxException("Invalid texture type: " + GsonHelper.getString(json.getAsJsonObject(), "type"));
			}
			return GsonHelper.deserialize(json, JsonTextureData.getCorrectClass(textureType));
		}
	}
}
