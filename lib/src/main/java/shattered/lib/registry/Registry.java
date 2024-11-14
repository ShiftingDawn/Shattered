package shattered.lib.registry;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public interface Registry<T extends RegistryObject> extends Iterable<T> {

	void register(Identifier key, T value);

	@Nullable
	T registerOrOverride(Identifier key, T value);

	boolean containsKey(Identifier key);

	boolean containsValue(T value);

	@UnknownNullability
	T get(Identifier key);

	@UnknownNullability
	Identifier getKey(T value);

	String getRegistryName();
}
