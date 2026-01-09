package dawn.asset;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import dawn.Dawn;
import dawn.lib.Workspace;
import dawn.registry.Identifier;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public final class AssetResolver {

	private final Workspace workspace;

	public String getPath(Identifier identifier) {
		return "/assets/%s/%s".formatted(identifier.getDomain(), identifier.getPath());
	}

	public @Nullable URL findResource(Identifier identifier) throws IOException {
		return findResource(getPath(identifier));
	}

	public @Nullable URL findResource(String path) throws IOException {
		File overriddenFile = this.workspace.getBinFile(path);
		if (overriddenFile.exists()) {
			return overriddenFile.toURI().toURL();
		}
		if (path.startsWith("/")) path = path.substring(1);
		return Dawn.class.getClassLoader().getResource(path);
	}

	public @Nullable InputStream findResourceStream(Identifier identifier) throws IOException {
		URL result = findResource(identifier);
		return result == null ? null : result.openStream();
	}

	public @Nullable InputStream findResourceStream(String path) throws IOException {
		URL result = findResource(path);
		return result == null ? null : result.openStream();
	}
}
