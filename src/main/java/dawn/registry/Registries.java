package dawn.registry;

import dawn.asset.ShaderAsset;
import dawn.asset.TextureAsset;
import static dawn.registry.RegistrySetup.makeRegistry;

public final class Registries {

	public static final Registry<ShaderAsset> SHADERS = makeRegistry("shader", new ShaderRegistryContentFactory());
	public static final Registry<TextureAsset> TEXTURES = makeRegistry("texture", new TextureRegistryContentFactory());

	static void init() {
		//NO-OP
	}

	private Registries() {
	}
}
