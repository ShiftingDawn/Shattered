package dawn.registry;

import dawn.asset.ResourceResolver;
import org.apache.logging.log4j.Logger;

public interface RegistryContentFactory<T extends RegistryObject> {

	void make(Logger logger, ResourceResolver assets, Registry<T> registry, Identifier registryKey);
}
