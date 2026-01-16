package dawn.core.asset;

import dawn.asset.Texture;
import dawn.registry.ProtoTexture;

record TextureImpl(ProtoTexture proto, int id, int width, int height, int[] uv) implements Texture {

	@Override
	public int imageWidth() {
		return this.width;
	}

	@Override
	public int imageHeight() {
		return this.height;
	}

	@Override
	public int u0() {
		return this.uv[0];
	}

	@Override
	public int v0() {
		return this.uv[1];
	}

	@Override
	public int u1() {
		return this.uv[2];
	}

	@Override
	public int v1() {
		return this.uv[3];
	}
}