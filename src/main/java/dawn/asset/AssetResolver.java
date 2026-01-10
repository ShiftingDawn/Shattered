package dawn.asset;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import dawn.Dawn;
import dawn.lib.Workspace;
import dawn.registry.Identifier;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public final class AssetResolver {

	public static final Logger LOGGER = LogManager.getLogger("Loader");
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
}
