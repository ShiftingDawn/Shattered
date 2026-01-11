package dawn.asset;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import dawn.Dawn;
import dawn.lib.Util;
import dawn.lib.Workspace;
import dawn.registry.Identifier;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.lwjgl.BufferUtils;

@RequiredArgsConstructor
public final class ResourceResolver {

	private final Workspace workspace;

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

	public @Nullable URL getResource(final Identifier identifier, @Nullable final String pathPrefix, @Nullable final String extension) throws IOException {
		return this.getResource(this.makePath(identifier, pathPrefix, extension));
	}

	public @Nullable URL getResource(String path) throws IOException {
		final File overriddenFile = this.workspace.getBinFile(path);
		if (overriddenFile.exists()) {
			return overriddenFile.toURI().toURL();
		}
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		return Dawn.class.getClassLoader().getResource(path);
	}

	public @Nullable InputStream getStream(final Identifier identifier, @Nullable final String pathPrefix, @Nullable final String extension) throws IOException {
		final URL url = this.getResource(identifier, pathPrefix, extension);
		return url == null ? null : url.openStream();
	}

	public @Nullable InputStream getStream(final String path) throws IOException {
		final URL url = this.getResource(path);
		return url == null ? null : url.openStream();
	}

	public @Nullable ByteBuffer getBuffer(final Identifier identifier, @Nullable final String pathPrefix, @Nullable final String extension) throws IOException {
		return this.getBuffer(this.makePath(identifier, pathPrefix, extension));
	}

	public @Nullable ByteBuffer getBuffer(final String path) throws IOException {
		try (InputStream stream = this.getStream(path)) {
			if (stream == null) {
				return null;
			}
			final byte[] bytes = stream.readAllBytes();
			return Util.make(BufferUtils.createByteBuffer(bytes.length), buffer -> {
				buffer.put(bytes);
				buffer.flip();
			});
		}
	}
}
