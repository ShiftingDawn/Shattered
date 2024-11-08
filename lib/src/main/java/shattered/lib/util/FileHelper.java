package shattered.lib.util;

import java.io.File;

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

	private FileHelper() {
	}
}
