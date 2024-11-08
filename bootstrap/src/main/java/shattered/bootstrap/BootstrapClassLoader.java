package shattered.bootstrap;

import java.util.HashMap;
import java.util.Map;

final class BootstrapClassLoader extends ClassLoader {

	static final Map<String, byte[]> CLASS_DATA = new HashMap<>();

	public BootstrapClassLoader() {
		super(Bootstrap.class.getClassLoader());
	}

	@Override
	protected Class<?> findClass(final String name) throws ClassNotFoundException {
		if (BootstrapClassLoader.CLASS_DATA.containsKey(name)) {
			final byte[] data = BootstrapClassLoader.CLASS_DATA.get(name);
			final Class<?> result = this.defineClass(name, data, 0, data.length);
			BootstrapClassLoader.CLASS_DATA.remove(name);
			return result;
		}
		throw new ClassNotFoundException(name);
	}

	@Override
	protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
		Class<?> loadedClass = this.findLoadedClass(name);
		if (loadedClass == null) {
			try {
				loadedClass = this.findClass(name);
			} catch (final ClassNotFoundException e) {
				loadedClass = super.loadClass(name, resolve);
			}
		}
		if (resolve) {
			this.resolveClass(loadedClass);
		}
		return loadedClass;
	}
}
