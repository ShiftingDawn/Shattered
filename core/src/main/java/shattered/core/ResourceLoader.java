package shattered.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;
import shattered.Shattered;
import shattered.lib.util.Utils;

public final class ResourceLoader {

	public static InputStream getResourceAsStream(final String path) throws IOException {
		final InputStream stream = Shattered.class.getResourceAsStream(path);
		if (stream == null) {
			throw new FileNotFoundException(path);
		}
		return stream;
	}

	public static ByteBuffer getResourceAsBuffer(final String path) throws IOException {
		try (InputStream stream = ResourceLoader.getResourceAsStream(path)) {
			final byte[] bytes = stream.readAllBytes();
			return Utils.make(BufferUtils.createByteBuffer(bytes.length), b -> {
				b.put(bytes);
				b.flip();
			});
		}
	}

	private ResourceLoader() {
	}
}
