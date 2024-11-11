package shattered.bootstrap;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

final class BootstrapClassLoader extends ClassLoader {

	private final ClassLoader system;

	public BootstrapClassLoader(final ClassLoader parent) {
		super(parent);
		this.system = ClassLoader.getSystemClassLoader();
	}

	void defineClass(String name, byte[] data) {
		this.defineClass(name, data, 0, data.length);
	}

	@Override
	protected synchronized Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
		Class<?> c = this.findLoadedClass(name);
		if (c == null) {
			if (this.system != null) {
				try {
					c = this.system.loadClass(name);
				} catch (final ClassNotFoundException ignored) {
				}
			}
			if (c == null) {
				try {
					c = this.findClass(name);
				} catch (final ClassNotFoundException e) {
					c = super.loadClass(name, resolve);
				}
			}
		}
		if (resolve) {
			this.resolveClass(c);
		}
		return c;
	}

	@Override
	public URL getResource(final String name) {
		URL url = null;
		if (this.system != null) {
			url = this.system.getResource(name);
		}
		if (url == null) {
			url = this.findResource(name);
			if (url == null) {
				url = super.getResource(name);
			}
		}
		return url;
	}

	@Override
	public Enumeration<URL> getResources(final String name) throws IOException {
		Enumeration<URL> systemUrls = null;
		if (this.system != null) {
			systemUrls = this.system.getResources(name);
		}
		final Enumeration<URL> localUrls = this.findResources(name);
		Enumeration<URL> parentUrls = null;
		if (this.getParent() != null) {
			parentUrls = this.getParent().getResources(name);
		}
		final List<URL> urls = new ArrayList<>();
		if (systemUrls != null) {
			while (systemUrls.hasMoreElements()) {
				urls.add(systemUrls.nextElement());
			}
		}
		if (localUrls != null) {
			while (localUrls.hasMoreElements()) {
				urls.add(localUrls.nextElement());
			}
		}
		if (parentUrls != null) {
			while (parentUrls.hasMoreElements()) {
				urls.add(parentUrls.nextElement());
			}
		}
		return new Enumeration<>() {

			final Iterator<URL> iter = urls.iterator();

			@Override
			public boolean hasMoreElements() {
				return this.iter.hasNext();
			}

			@Override
			public URL nextElement() {
				return this.iter.next();
			}
		};
	}

	@Override
	public InputStream getResourceAsStream(final String name) {
		final URL url = this.getResource(name);
		try {
			return url != null ? url.openStream() : null;
		} catch (final IOException ignored) {
		}
		return null;
	}
}