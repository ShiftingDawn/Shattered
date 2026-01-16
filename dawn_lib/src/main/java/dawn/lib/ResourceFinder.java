package dawn.lib;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import dawn.Identifier;
import org.jspecify.annotations.Nullable;

public interface ResourceFinder {

	String makePath(Identifier identifier, @Nullable String pathPrefix, @Nullable String extension);

	@Nullable URL getResource(String path) throws IOException;

	@Nullable InputStream getStream(final String path) throws IOException;

	@Nullable ByteBuffer getBuffer(final String path) throws IOException;
}
