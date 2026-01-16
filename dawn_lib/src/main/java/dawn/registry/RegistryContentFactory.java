package dawn.registry;

import dawn.Identifier;
import dawn.lib.ResourceFinder;
import org.apache.logging.log4j.Logger;

public interface RegistryContentFactory<VALUE extends RegistryObject> {

	VALUE make(Logger logger, ResourceFinder resources, Identifier registryKey);
}
