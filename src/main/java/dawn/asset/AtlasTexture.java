package dawn.asset;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public final class AtlasTexture implements Texture {

	private final TextureAtlas atlas;
	private final TextureAsset asset;
	private final int width;
	private final int height;
	private final int[] bounds;

	@Override
	public TextureAsset asset() {
		return this.asset;
	}

	@Override
	public int id() {
		return this.atlas.id();
	}

	@Override
	public int width() {
		return this.width;
	}

	@Override
	public int height() {
		return this.height;
	}

	@Override
	public int imageWidth() {
		return this.atlas.imageWidth();
	}

	@Override
	public int imageHeight() {
		return this.atlas.imageHeight();
	}

	@Override
	public int u0() {
		return this.bounds[0];
	}

	@Override
	public int v0() {
		return this.bounds[1];
	}

	@Override
	public int u1() {
		return this.bounds[2];
	}

	@Override
	public int v1() {
		return this.bounds[3];
	}
}
