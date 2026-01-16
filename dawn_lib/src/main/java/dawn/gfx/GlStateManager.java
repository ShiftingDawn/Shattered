package dawn.gfx;

import dawn.internal.DawnLib;

public interface GlStateManager {

	static GlStateManager gl() {
		return DawnLib.GL;
	}

	void bindShader(int shader);

	void bindTexture(int texture);

	void blend(boolean blend, int src, int dst);

	void blendSimple();

	void blendNone();

	void textureFilterMin(int value);

	void textureFilterMag(int value);

	void textureFilterAll(int value);

	void textureFilterHard();

	void textureFilterSmooth();
}
