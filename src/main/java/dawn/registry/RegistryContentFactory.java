package dawn.registry;

import dawn.asset.AssetResolver;

public interface RegistryContentFactory<T extends RegistryObject> {

	void make(AssetResolver assets, Registry<T> registry, Identifier registryKey);
}
