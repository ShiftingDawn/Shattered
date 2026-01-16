package dawn.registry;

import java.util.function.BiConsumer;
import dawn.Identifier;

public interface Registry<VALUE extends RegistryObject> extends Iterable<VALUE> {
	
	boolean containsKey(Identifier registryKey);

	boolean containsValue(VALUE value);

	VALUE get(Identifier registryKey);

	Identifier getKey(VALUE value);

	String getRegistryName();

	void forEach(BiConsumer<Identifier, ? super VALUE> action);
}
