package shattered.lib.resource;

import shattered.lib.registry.Identifier;
import shattered.lib.registry.SimpleRegistryObject;

public abstract class TextureAsset extends SimpleRegistryObject {

	public TextureAsset(final Identifier registryKey) {
		super(registryKey);
	}
}
