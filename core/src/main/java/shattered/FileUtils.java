package shattered;

import java.io.IOException;
import java.io.InputStream;
import org.jetbrains.annotations.Nullable;

public final class FileUtils {

	@Nullable
	public static String readFromClassPath(final String path) throws IOException {
		try (InputStream stream = Shattered.class.getResourceAsStream(path)) {
			if (stream != null) {
				return new String(stream.readAllBytes());
			}
		}
		return null;
	}

	private FileUtils() {
	}
}
