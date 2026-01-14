package dawn.asset;

import java.util.HashMap;
import java.util.Map;
import dawn.lib.math.Rectangle;
import dawn.registry.Identifier;
import org.jspecify.annotations.Nullable;

public final class TextureAtlas implements Texture {

	private final Map<Identifier, AtlasTexture> textures = new HashMap<>();
	private final Texture texture;

	TextureAtlas(final Texture texture, final Map<Identifier, Rectangle> elements, final Map<Identifier, TextureAsset> entries) {
		this.texture = texture;
		for (final Identifier entry : elements.keySet()) {
			final Rectangle bounds = elements.get(entry);
			final int[] uv = new int[] { bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() };
			this.textures.put(entry, new AtlasTexture(this, entries.get(entry), bounds.getWidth(), bounds.getHeight(), uv));
		}
	}

	public @Nullable Texture getTexture(final Identifier texture) {
		return this.textures.get(texture);
	}

	@Override
	public TextureAsset asset() {
		return this.texture.asset();
	}

	@Override
	public int id() {
		return this.texture.id();
	}

	@Override
	public int width() {
		return this.texture.width();
	}

	@Override
	public int height() {
		return this.texture.height();
	}

	@Override
	public int imageWidth() {
		return this.width();
	}

	@Override
	public int imageHeight() {
		return this.height();
	}

	@Override
	public int u0() {
		return this.texture.u0();
	}

	@Override
	public int v0() {
		return this.texture.v0();
	}

	@Override
	public int u1() {
		return this.texture.u1();
	}

	@Override
	public int v1() {
		return this.texture.v1();
	}
}
