package shattered.core.lib.registry;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import shattered.core.Shattered;
import shattered.lib.event.EventBus;
import shattered.lib.event.Subscribe;
import shattered.lib.registry.Identifier;
import shattered.lib.registry.Registry;
import shattered.lib.registry.RegistryObject;
import shattered.lib.registry.SimpleRegistryObject;

public final class RegistryImpl<T extends RegistryObject> extends SimpleRegistryObject implements Registry<T> {

	private final Object2ObjectMap<Identifier, T> mapping = new Object2ObjectArrayMap<>();
	private final Object2ObjectMap<T, Identifier> reverseMapping = new Object2ObjectArrayMap<>();
	private final AtomicBoolean frozen = new AtomicBoolean(false);
	private final Object lock = new Object();

	public RegistryImpl(final Identifier registryKey) {
		super(registryKey);
		EventBus.create("system").register(this);
	}

	@Override
	public void register(final Identifier key, final T value) {
		if (this.frozen.get()) {
			throw new IllegalStateException("Cannot register entries in registry %s after initialization is complete".formatted(this.getRegistryKey()));
		}
		synchronized (this.lock) {
			if (this.mapping.containsKey(key) || this.reverseMapping.containsKey(value)) {
				throw new DuplicateRegistryEntryException();
			}
			this.mapping.put(key, value);
			this.reverseMapping.put(value, key);
			Shattered.LOGGER.trace("Registered {} in registry {}", key, this.getRegistryKey());
		}
	}

	@Override
	public @Nullable T registerOrOverride(final Identifier key, final T value) {
		if (this.frozen.get()) {
			throw new IllegalStateException("Cannot register entries in registry %s after initialization is complete".formatted(this.getRegistryKey()));
		}
		synchronized (this.lock) {
			if (this.mapping.containsKey(key) || this.reverseMapping.containsKey(value)) {
				throw new DuplicateRegistryEntryException();
			}
			final T oldValue = this.mapping.put(key, value);
			this.reverseMapping.put(value, key);
			if (oldValue == null) {
				Shattered.LOGGER.trace("Registered {} in registry {}", key, this.getRegistryKey());
			} else {
				Shattered.LOGGER.trace("Registered override {} in registry {}", key, this.getRegistryKey());
			}
			return oldValue;
		}
	}

	@Override
	public boolean containsKey(final Identifier key) {
		synchronized (this.lock) {
			return this.mapping.containsKey(key);
		}
	}

	@Override
	public boolean containsValue(final T value) {
		synchronized (this.lock) {
			return this.reverseMapping.containsKey(value);
		}
	}

	@Override
	public @UnknownNullability T get(final Identifier key) {
		synchronized (this.lock) {
			return this.mapping.get(key);
		}
	}

	@Override
	public @UnknownNullability Identifier getKey(final T value) {
		synchronized (this.lock) {
			return this.reverseMapping.get(value);
		}
	}

	@Override
	public @NotNull Iterator<T> iterator() {
		synchronized (this.lock) {
			return this.mapping.values().iterator();
		}
	}

	@Subscribe
	private void onFreezeRegistries(final FreezeRegistriesEvent ignored) {
		Shattered.LOGGER.debug("Freezing registry {}", this.getRegistryKey());
		this.frozen.set(true);
	}
}
