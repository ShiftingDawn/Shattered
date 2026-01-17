package dawn.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import dawn.Dawn;
import dawn.Identifier;
import dawn.lib.Workspace;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.lwjgl.BufferUtils;

@RequiredArgsConstructor
final class ResourceFinderImpl implements dawn.lib.ResourceFinder {

	private final Workspace workspace;

	@Override
	public String makePath(final Identifier identifier, @Nullable final String pathPrefix, @Nullable final String extension) {
		final StringBuilder stringer = new StringBuilder("/assets/");
		stringer.append(identifier.getDomain()).append('/');
		if (pathPrefix != null) {
			stringer.append(pathPrefix).append('/');
		}
		stringer.append(identifier.getPath());
		if (extension != null) {
			stringer.append('.').append(extension);
		}
		return stringer.toString();
	}

	@Override
	public @Nullable URL getResource(final String path) throws IOException {
		final File overriddenFile = this.workspace.getBinFile(path);
		if (overriddenFile.exists()) {
			return overriddenFile.toURI().toURL();
		}
		return this.getClass().getResource(path);
	}

	@Override
	public @Nullable InputStream getStream(final String path) throws IOException {
		final URL url = this.getResource(path);
		return url == null ? null : url.openStream();
	}

	@Override
	public @Nullable ByteBuffer getBuffer(final String path) throws IOException {
		try (InputStream stream = this.getStream(path)) {
			if (stream == null) {
				return null;
			}
			final byte[] bytes = stream.readAllBytes();
			return Dawn.make(BufferUtils.createByteBuffer(bytes.length), buffer -> {
				buffer.put(bytes);
				buffer.flip();
			});
		}
	}
}
