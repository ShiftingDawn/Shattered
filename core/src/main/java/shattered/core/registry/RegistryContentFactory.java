package shattered.core.registry;

import shattered.lib.registry.Identifier;
import shattered.lib.registry.Registry;
import shattered.lib.registry.RegistryObject;

public interface RegistryContentFactory<T extends RegistryObject> {

	void make(Registry<T> registry, Identifier key);
}
