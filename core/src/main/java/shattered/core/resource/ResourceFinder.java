package shattered.core.resource;

import java.net.URL;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import shattered.core.Shattered;
import shattered.lib.registry.Identifier;

@RequiredArgsConstructor
public final class ResourceFinder {

	private final String rootType;

	public String makePath(final Identifier id, @Nullable final String extension) {
		return "/%s/%s/%s%s".formatted(this.rootType, id.getNamespace(), id.getPath(), extension == null ? "" : '.' + extension);
	}

	public URL makeUrl(final Identifier id, @Nullable final String extension) {
		return Shattered.class.getResource(this.makePath(id, extension));
	}
}
