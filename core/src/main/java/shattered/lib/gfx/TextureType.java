package shattered.lib.gfx;

import java.util.Arrays;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public enum TextureType {

	DEFAULT,
	STITCHED,
	BORDERED,
	ANIMATION;

	private final String name = super.toString().toLowerCase(Locale.ROOT);

	@Override
	public String toString() {
		return this.name;
	}

	@Nullable
	public static TextureType getByName(final String name) {
		return Arrays.stream(TextureType.values()).filter(type -> type.toString().equals(name)).findFirst().orElse(null);
	}
}
