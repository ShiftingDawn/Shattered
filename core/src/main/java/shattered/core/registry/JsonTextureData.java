package shattered.core.registry;

import java.util.Map;
import com.google.gson.annotations.SerializedName;
import shattered.lib.gfx.TextureType;
import shattered.lib.registry.Identifier;
import shattered.lib.util.gson.Json;

public class JsonTextureData {

	@SerializedName("type")
	public TextureType textureType = TextureType.DEFAULT;

	@SerializedName("variants")
	public Map<String, Identifier> variants;

	public static final class Default extends JsonTextureData {

		public Default() {
			this.textureType = TextureType.DEFAULT;
		}
	}

	public static final class Stitched extends JsonTextureData {

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

	public static final class Bordered extends JsonTextureData {

		@SerializedName("border_top")
		Integer borderTop;

		@SerializedName("border_bottom")
		Integer borderBottom;

		@SerializedName("border_left")
		Integer borderLeft;

		@SerializedName("border_right")
		Integer borderRight;
	}

	public static final class Animation extends JsonTextureData {

		@SerializedName("fps")
		@Json.Required
		Double fps;

		@SerializedName("frame_mapping")
		Integer[] frameMapping;
	}

	public static Class<? extends JsonTextureData> getCorrectClass(final TextureType type) {
		return switch (type) {
			case DEFAULT -> Default.class;
			case STITCHED -> Stitched.class;
			case BORDERED -> Bordered.class;
			case ANIMATION -> Animation.class;
		};
	}
}
