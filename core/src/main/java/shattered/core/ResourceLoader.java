package shattered.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import shattered.Shattered;

public final class ResourceLoader {

	public static InputStream getResourceAsStream(final String path) throws IOException {
		final InputStream stream = Shattered.class.getResourceAsStream(path);
		if (stream == null) {
			throw new FileNotFoundException(path);
		}
		return stream;
	}

	private ResourceLoader() {
	}
}
