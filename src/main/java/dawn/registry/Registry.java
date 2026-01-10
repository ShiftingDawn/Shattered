package dawn.registry;

public interface Registry<T extends RegistryObject> extends Iterable<T> {

	void register(Identifier registryKey, T value);

	T registerOrOverride(Identifier registryKey, T value);

	boolean containsKey(Identifier registryKey);

	boolean containsValue(T value);

	T get(Identifier registryKey);

	Identifier getKey(T value);

	String getRegistryName();
}
