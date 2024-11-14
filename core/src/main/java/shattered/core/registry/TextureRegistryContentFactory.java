package shattered.core.registry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Optional;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shattered.core.ResourceLoader;
import shattered.lib.gfx.TextureType;
import shattered.lib.registry.Identifier;
import shattered.lib.registry.Registry;
import shattered.lib.resource.TextureAsset;
import shattered.lib.util.Utils;
import shattered.lib.util.gson.GsonHelper;
import static shattered.Shattered.LOGGER;

public final class TextureRegistryContentFactory implements RegistryContentFactory<TextureAsset> {

	@Override
	public void make(final Registry<TextureAsset> registry, final Identifier key) {
		LOGGER.debug("Loading texture definition {}", key);
		final JsonTextureData data = Optional.ofNullable(TextureRegistryContentFactory.loadJsonData(key)).orElseGet(JsonTextureData.Default::new);
		if (data.variants == null) {
			data.variants = Utils.make(new HashMap<>(), map -> map.put(Identifier.DEFAULT_VARIANT, key));
		}
		data.variants.forEach((variant, variantTextureLocation) -> {
			final Identifier variantIdentifier = key.toVariant(variant);
			LOGGER.debug("\tLoading texture-variant definition {}", variantIdentifier);
			JsonTextureData variantMetadata = TextureRegistryContentFactory.loadVariantJsonData(variantIdentifier);
			if (variantMetadata == null) {
				variantMetadata = data;
			}
			final TextureAsset texture = switch (variantMetadata.textureType) {
				case DEFAULT -> TextureRegistryContentFactory.makeDefault(key, (JsonTextureData.Default) variantMetadata);
				case STITCHED -> TextureRegistryContentFactory.makeStitched(key, (JsonTextureData.Stitched) variantMetadata);
				case BORDERED -> TextureRegistryContentFactory.makeBordered(key, (JsonTextureData.Bordered) variantMetadata);
				case ANIMATION -> TextureRegistryContentFactory.makeAnimation(key, (JsonTextureData.Animation) variantMetadata);
			};
			registry.register(variantIdentifier, texture);
		});
	}

	private static TextureAsset makeDefault(final Identifier key, final JsonTextureData.Default data) {
		return new TextureAssetImpl.Default(key);
	}

	private static TextureAsset makeStitched(final Identifier key, final JsonTextureData.Stitched data) {
		final int spriteWidth = data.spriteSize != null ? data.spriteSize : data.spriteWidth;
		final int spriteHeight = data.spriteSize != null ? data.spriteSize : data.spriteHeight;
		return new TextureAssetImpl.Stitched(key, data.spriteCount, spriteWidth, spriteHeight);
	}

	private static TextureAsset makeBordered(final Identifier key, final JsonTextureData.Bordered data) {
		final int top = data.borderTop != null ? data.borderTop : -1;
		final int bottom = data.borderBottom != null ? data.borderBottom : -1;
		final int left = data.borderLeft != null ? data.borderLeft : -1;
		final int right = data.borderRight != null ? data.borderRight : -1;
		return new TextureAssetImpl.Bordered(key, top, bottom, left, right);
	}

	private static TextureAsset makeAnimation(final Identifier key, final JsonTextureData.Animation data) {
		return new TextureAssetImpl.Animation(key, data.fps, data.frameMapping);
	}

	@Nullable
	public static JsonTextureData loadJsonData(@NotNull final Identifier resource) {
		final String path = "/assets/%s/texture/%s.json".formatted(resource.getNamespace(), resource.getPath());
		try {
			return TextureRegistryContentFactory.loadJsonDataInternal(path);
		} catch (final FileNotFoundException ignored) {
			LOGGER.error("Registered texture \"{}\" has no matching metadata file!", resource);
			LOGGER.error("\tExpected filepath: {}", path);
			LOGGER.error("\tAssuming it's a default texture without variants");
			return null;
		} catch (final IOException | JsonIOException | JsonSyntaxException e) {
			LOGGER.error("Could not read texture metadata from texture \"{}\"", resource);
			LOGGER.error(e);
			LOGGER.error("\tIgnoring the metadata and loading as a default texture without variants");
			return null;
		}
	}

	@Nullable
	public static JsonTextureData loadVariantJsonData(final Identifier variant) {
		try {
			return TextureRegistryContentFactory.loadJsonDataInternal("/assets/%s/texture/%s.png.json".formatted(variant.getNamespace(), variant.getPath()));
		} catch (final FileNotFoundException ignored) {
			return null;
		} catch (final IOException | JsonIOException | JsonSyntaxException e) {
			LOGGER.error("Could not read texture variant metadata from texture \"{}\"", variant);
			LOGGER.error(e);
			LOGGER.error("\tIgnoring the variant metadata and using the parent metadata");
			return null;
		}
	}

	private static JsonTextureData loadJsonDataInternal(final String path) throws IOException, JsonIOException, JsonSyntaxException {
		try (InputStream stream = ResourceLoader.getResourceAsStream(path)) {
			final InputStreamReader reader = new InputStreamReader(stream);
			final JsonElement json = GsonHelper.deserialize(reader);
			if (json == null || !json.isJsonObject()) {
				throw new IOException("Input is not a JSON object!");
			}
			final TextureType textureType = Utils.safeGet(() -> TextureType.getByName(GsonHelper.getString(json.getAsJsonObject(), "type")), () -> TextureType.DEFAULT);
			if (textureType == null) {
				throw new JsonSyntaxException("Invalid texture type: " + GsonHelper.getString(json.getAsJsonObject(), "type"));
			}
			return GsonHelper.deserialize(json, JsonTextureData.getCorrectClass(textureType));
		}
	}
}
