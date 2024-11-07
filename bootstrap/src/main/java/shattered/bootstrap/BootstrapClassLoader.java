package shattered.bootstrap;

import java.util.HashMap;
import java.util.Map;

final class BootstrapClassLoader extends ClassLoader {

	private final Map<String, Class<?>> loadedClasses = new HashMap<>();

	public BootstrapClassLoader() {
		super(Bootstrap.class.getClassLoader().getParent());
	}

	void registerClass(final Class<?> clazz) {
		this.loadedClasses.put(clazz.getName(), clazz);
	}

	@Override
	protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
		final Class<?> existing = this.loadedClasses.get(name);
		if (existing != null) {
			return existing;
		}
		return super.loadClass(name, resolve);
	}

	boolean hasParentLoaded(final String name) {
		try {
			return this.getParent().loadClass(name) != null;
		} catch (final ClassNotFoundException e) {
			return false;
		}
	}

	void defineClassIfMissing(final String className, byte[] data) {
		if (!this.loadedClasses.containsKey(className)) {
			data = TransformerRegistry.transform(className, data);
			final Class<?> result = super.defineClass(className, data, 0, data.length);
			this.loadedClasses.put(className, result);
		}
	}

	int getLoadedClassCount() {
		return this.loadedClasses.size();
	}
}
