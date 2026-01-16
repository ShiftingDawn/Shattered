package dawn.core.registry;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import dawn.Identifier;
import dawn.lib.ResourceFinder;
import dawn.registry.Registry;
import dawn.registry.RegistryContentFactory;
import dawn.registry.RegistryObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;

@RequiredArgsConstructor
final class RegistryImpl<VALUE extends RegistryObject> implements Registry<VALUE> {

	private final Map<Identifier, VALUE> mapping = new ConcurrentHashMap<>();
	private final Map<VALUE, Identifier> reverseMapping = new ConcurrentHashMap<>();
	private final Map<Identifier, VALUE> immutableMapping = Collections.unmodifiableMap(this.mapping);
	private final AtomicBoolean frozen = new AtomicBoolean(false);
	@Getter
	private final String registryName;
	@Getter
	private final RegistryContentFactory<VALUE> contentFactory;

	public CompletableFuture<Void> loadContent(final ExecutorService executor, final Logger logger, final ResourceFinder assets, final Identifier item) {
		return CompletableFuture.runAsync(() -> {
			final VALUE created = this.contentFactory.make(logger, assets, item);
			this.register(item, created);
		}, executor);
	}

	public void register(final Identifier registryKey, final VALUE value) {
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
	public boolean containsKey(final Identifier registryKey) {
		return this.mapping.containsKey(registryKey);
	}

	@Override
	public boolean containsValue(final VALUE value) {
		return this.reverseMapping.containsKey(value);
	}

	@Override
	public VALUE get(final Identifier registryKey) {
		return this.mapping.get(registryKey);
	}

	@Override
	public Identifier getKey(final VALUE value) {
		return this.reverseMapping.get(value);
	}

	@Override
	public Iterator<VALUE> iterator() {
		return this.immutableMapping.values().iterator();
	}

	@Override
	public void forEach(final BiConsumer<Identifier, ? super VALUE> action) {
		this.immutableMapping.forEach(action);
	}
}
