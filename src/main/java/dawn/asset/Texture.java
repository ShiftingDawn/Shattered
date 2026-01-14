package dawn.asset;

public interface Texture {

	TextureAsset asset();

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
