package shattered.lib.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.jetbrains.annotations.Nullable;

public final class FileHelper {

	public static void deleteRecursive(final File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				for (final File child : file.listFiles()) {
					FileHelper.deleteRecursive(child);
				}
			}
			file.delete();
		}
	}

	@Nullable
	public static String readFromClassPath(final String path) throws IOException {
		try (InputStream stream = FileHelper.class.getResourceAsStream(path)) {
			if (stream != null) {
				return new String(stream.readAllBytes());
			}
		}
		return null;
	}

	private FileHelper() {
	}
}
