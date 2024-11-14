package shattered.core.registry;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import shattered.lib.registry.DuplicateRegistryEntryException;
import shattered.lib.registry.Identifier;
import shattered.lib.registry.Registry;
import shattered.lib.registry.RegistryObject;

@RequiredArgsConstructor
public final class RegistryImpl<T extends RegistryObject> implements Registry<T> {

	private final Map<Identifier, T> mapping = new ConcurrentHashMap<>();
	private final Map<T, Identifier> reverseMapping = new ConcurrentHashMap<>();
	private final Map<Identifier, T> immutableMapping = Collections.unmodifiableMap(this.mapping);
	private final AtomicBoolean frozen = new AtomicBoolean(false);
	@Getter
	private final String registryName;
	@Getter
	private final RegistryContentFactory<T> contentFactory;

	public void loadContent(final List<Identifier> items) {
		items.forEach(item -> this.contentFactory.make(this, item));
	}

	@Override
	public void register(final Identifier key, final T value) {
		if (this.frozen.get()) {
			throw new IllegalStateException("Cannot register entry in frozen registry");
		}
		if (this.mapping.containsKey(key)) {
			throw new DuplicateRegistryEntryException(this.registryName, key);
		}
		if (this.reverseMapping.containsKey(value)) {
			throw new DuplicateRegistryEntryException(this.registryName, key);
		}
		this.mapping.put(key, value);
		this.reverseMapping.put(value, key);
	}

	@Override
	public @Nullable T registerOrOverride(final Identifier key, final T value) {
		if (this.frozen.get()) {
			throw new IllegalStateException("Cannot register or override entry in frozen registry");
		}
		if (this.mapping.containsKey(key)) {
			throw new DuplicateRegistryEntryException(this.registryName, key);
		}
		if (this.reverseMapping.containsKey(value)) {
			throw new DuplicateRegistryEntryException(this.registryName, key);
		}
		final T currentEntry = this.mapping.get(key);
		if (currentEntry != null) {
			this.reverseMapping.remove(value);
		}
		this.mapping.put(key, value);
		this.reverseMapping.put(value, key);
		return currentEntry;
	}

	@Override
	public boolean containsKey(final Identifier key) {
		return this.mapping.containsKey(key);
	}

	@Override
	public boolean containsValue(final T value) {
		return this.reverseMapping.containsKey(value);
	}

	@Override
	public @UnknownNullability T get(final Identifier key) {
		return this.mapping.get(key);
	}

	@Override
	public @UnknownNullability Identifier getKey(final T value) {
		return this.reverseMapping.get(value);
	}

	@Override
	public @NotNull Iterator<T> iterator() {
		return this.immutableMapping.values().iterator();
	}
}
