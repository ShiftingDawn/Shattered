package dawn.asset;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import dawn.Dawn;
import dawn.lib.Workspace;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public final class AssetResolver {

	private final Workspace workspace;

	public @Nullable URL findResource(String path) throws IOException {
		File overriddenFile = this.workspace.getBinFile(path);
		if (overriddenFile.exists()) {
			return overriddenFile.toURI().toURL();
		}
		if (path.startsWith("/")) path = path.substring(1);
		return Dawn.class.getClassLoader().getResource(path);
	}
}
