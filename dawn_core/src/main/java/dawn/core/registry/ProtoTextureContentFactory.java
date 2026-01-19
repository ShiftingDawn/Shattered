package dawn.core.registry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import dawn.Dawn;
import dawn.Identifier;
import dawn.lib.GsonHelper;
import dawn.lib.Json;
import dawn.lib.ResourceFinder;
import dawn.registry.ProtoTexture;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

final class ProtoTextureContentFactory extends BaseRegistryContentFactory<ProtoTexture> {

	@Override
	public ProtoTexture make(final Logger logger, final ResourceFinder resources, final Identifier registryKey) {
		this.printDescription(logger, "texture", registryKey);
		JsonData textureData = this.loadJsonData(logger, resources, registryKey).orElse(null);
		if (textureData == null) {
			textureData = new JsonData.Default();
		}
		return switch (textureData.textureType) {
			case DEFAULT -> ProtoTextureContentFactory.makeDefault(registryKey, (JsonData.Default) textureData);
			case STITCHED -> ProtoTextureContentFactory.makeStitched(registryKey, (JsonData.Stitched) textureData);
			case BORDERED -> ProtoTextureContentFactory.makeBordered(registryKey, (JsonData.Bordered) textureData);
			case ANIMATION -> ProtoTextureContentFactory.makeAnimation(registryKey, (JsonData.Animation) textureData);
		};
	}

	private static ProtoTexture makeDefault(final Identifier registryKey, final JsonData.Default data) {
		return new ProtoTextureImpl.DefaultImpl(registryKey);
	}

	private static ProtoTexture makeStitched(final Identifier registryKey, final JsonData.Stitched data) {
		final int spriteWidth = data.spriteSize != null ? data.spriteSize : data.spriteWidth;
		final int spriteHeight = data.spriteSize != null ? data.spriteSize : data.spriteHeight;
		return new ProtoTextureImpl.StitchedImpl(registryKey, data.spriteCount, spriteWidth, spriteHeight);
	}

	private static ProtoTexture makeBordered(final Identifier registryKey, final JsonData.Bordered data) {
		final int top = data.borderTop != null ? data.borderTop : data.borderSize;
		final int bottom = data.borderBottom != null ? data.borderBottom : data.borderSize;
		final int left = data.borderLeft != null ? data.borderLeft : data.borderSize;
		final int right = data.borderRight != null ? data.borderRight : data.borderSize;
		return new ProtoTextureImpl.BorderedImpl(registryKey, top, bottom, left, right);
	}

	private static ProtoTexture makeAnimation(final Identifier registryKey, final JsonData.Animation data) {
		return new ProtoTextureImpl.AnimationImpl(registryKey, data.fps, data.frameMapping);
	}

	private Optional<? extends JsonData> loadJsonData(final Logger logger, final ResourceFinder resources, final Identifier resource) {
		final String path = resources.makePath(resource, "texture", "json");
		try (InputStream stream = resources.getStream(path)) {
			if (stream == null) {
				throw new FileNotFoundException();
			}
			final InputStreamReader reader = new InputStreamReader(stream);
			final JsonElement json = GsonHelper.deserialize(reader);
			if (json == null || !json.isJsonObject()) {
				throw new IOException("Input is not a JSON object!");
			}
			final TextureType textureType = Dawn.safeGet(() -> TextureType.getByName(GsonHelper.getString(json.getAsJsonObject(), "type")), () -> TextureType.DEFAULT);
			if (textureType == null) {
				throw new JsonSyntaxException("Invalid texture type: " + GsonHelper.getString(json.getAsJsonObject(), "type"));
			}
			return this.loadJsonData(JsonData.getCorrectClass(textureType), logger, resources, resource, "texture", true);
		} catch (final FileNotFoundException ignored) {
			logger.debug("Could not find metadata for texture \"{}\", assuming defaults. Expected path: {}", resource, path);
			return Optional.empty();
		} catch (final IOException | JsonIOException | JsonSyntaxException e) {
			logger.error("Could not read texture metadata for texture \"{}\"", resource);
			logger.error(e);
			logger.error("\tIgnoring the metadata and loading as a default texture");
			return Optional.empty();
		}
	}

	private enum TextureType {
		DEFAULT,
		STITCHED,
		BORDERED,
		ANIMATION;

		private final String name = super.toString().toLowerCase(Locale.ROOT);

		@Override
		public String toString() {
			return this.name;
		}

		public static @Nullable TextureType getByName(final String name) {
			return Arrays.stream(TextureType.values()).filter(type -> type.toString().equals(name)).findFirst().orElse(null);
		}
	}

	@NullUnmarked
	private static class JsonData {

		@SerializedName("type")
		public TextureType textureType = TextureType.DEFAULT;

		public static final class Default extends JsonData {

			public Default() {
				this.textureType = TextureType.DEFAULT;
			}
		}

		public static final class Stitched extends JsonData {

			@SerializedName("sprite_size")
			@Json.Required(group = @Json.Required.OR(groupName = "sprite_size", groupIndex = "1"))
			Integer spriteSize;

			@SerializedName("sprite_width")
			@Json.Required(group = @Json.Required.OR(groupName = "sprite_size", groupIndex = "2"))
			Integer spriteWidth;

			@SerializedName("sprite_height")
			@Json.Required(group = @Json.Required.OR(groupName = "sprite_size", groupIndex = "2"))
			Integer spriteHeight;

			@SerializedName("sprite_count")
			@Json.Required
			Integer spriteCount;
		}

		public static final class Bordered extends JsonData {

			@SerializedName("border")
			@Json.Required(group = @Json.Required.OR(groupName = "border_size", groupIndex = "1"))
			Integer borderSize;

			@SerializedName("border_top")
			@Json.Required(group = @Json.Required.OR(groupName = "border_size", groupIndex = "2"))
			Integer borderTop;

			@SerializedName("border_bottom")
			@Json.Required(group = @Json.Required.OR(groupName = "border_size", groupIndex = "2"))
			Integer borderBottom;

			@SerializedName("border_left")
			@Json.Required(group = @Json.Required.OR(groupName = "border_size", groupIndex = "2"))
			Integer borderLeft;

			@SerializedName("border_right")
			@Json.Required(group = @Json.Required.OR(groupName = "border_size", groupIndex = "2"))
			Integer borderRight;
		}

		public static final class Animation extends JsonData {

			@SerializedName("fps")
			@Json.Required
			Double fps;

			@SerializedName("frame_mapping")
			Integer[] frameMapping;
		}

		public static Class<? extends JsonData> getCorrectClass(final TextureType type) {
			return switch (type) {
				case DEFAULT -> Default.class;
				case STITCHED -> Stitched.class;
				case BORDERED -> Bordered.class;
				case ANIMATION -> Animation.class;
			};
		}
	}

}
