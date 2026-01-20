package dawn.asset;

import dawn.registry.ProtoFont;
import dawn.registry.ProtoShader;
import dawn.registry.ProtoTexture;

public interface AssetManager {

	ProtoAssetProvider<ProtoShader, Shader> shaders();

	ProtoAssetProvider<ProtoTexture, Texture> textures();

	ProtoAssetProvider<ProtoFont, Font> fonts();

	AudioManager audio();
}
