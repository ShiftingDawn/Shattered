package dawn.asset;

import dawn.registry.ProtoTexture;

public interface Texture extends ProtoAsset<ProtoTexture> {

	int id();

	int width();

	int height();

	int imageWidth();

	int imageHeight();

	int u0();

	int v0();

	int u1();

	int v1();
}
