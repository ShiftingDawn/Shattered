package dawn.registry;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import dawn.asset.AssetResolver;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
final class RegistryImpl<T extends RegistryObject> implements Registry<T> {

	private final Map<Identifier, T> mapping = new ConcurrentHashMap<>();
	private final Map<T, Identifier> reverseMapping = new ConcurrentHashMap<>();
	private final Map<Identifier, T> immutableMapping = Collections.unmodifiableMap(this.mapping);
	private final AtomicBoolean frozen = new AtomicBoolean(false);
	@Getter
	private final String registryName;
	@Getter
	private final RegistryContentFactory<T> contentFactory;

	public void loadContent(AssetResolver assets, final List<Identifier> items) {
		items.forEach(item -> this.contentFactory.make(assets, this, item));
	}

	@Override
	public void register(final Identifier registryKey, final T value) {
		if (this.frozen.get()) {
			throw new IllegalStateException("Cannot register entry in frozen registry");
		}
		if (this.mapping.containsKey(registryKey)) {
			throw new DuplicateRegistryEntryException(this.registryName, registryKey);
		}
		if (this.reverseMapping.containsKey(value)) {
			throw new DuplicateRegistryEntryException(this.registryName, registryKey);
		}
		this.mapping.put(registryKey, value);
		this.reverseMapping.put(value, registryKey);
	}

	@Override
	public @Nullable T registerOrOverride(final Identifier registryKey, final T value) {
		if (this.frozen.get()) {
			throw new IllegalStateException("Cannot register or override entry in frozen registry");
		}
		if (this.mapping.containsKey(registryKey)) {
			throw new DuplicateRegistryEntryException(this.registryName, registryKey);
		}
		if (this.reverseMapping.containsKey(value)) {
			throw new DuplicateRegistryEntryException(this.registryName, registryKey);
		}
		final T currentEntry = this.mapping.get(registryKey);
		if (currentEntry != null) {
			this.reverseMapping.remove(value);
		}
		this.mapping.put(registryKey, value);
		this.reverseMapping.put(value, registryKey);
		return currentEntry;
	}

	@Override
	public boolean containsKey(final Identifier registryKey) {
		return this.mapping.containsKey(registryKey);
	}

	@Override
	public boolean containsValue(final T value) {
		return this.reverseMapping.containsKey(value);
	}

	@Override
	public T get(final Identifier registryKey) {
		return this.mapping.get(registryKey);
	}

	@Override
	public Identifier getKey(final T value) {
		return this.reverseMapping.get(value);
	}

	@Override
	public Iterator<T> iterator() {
		return this.immutableMapping.values().iterator();
	}
}
