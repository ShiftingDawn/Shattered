package shattered.bootstrap;

import java.io.File;
import java.util.Locale;

final class BootstrapWorkspace {

	static File getRootDir() {
		final String rootPathProperty = System.getProperty("shattered.workspace.root");
		if (rootPathProperty != null) {
			final File result = new File(rootPathProperty);
			if (!result.exists() && !result.mkdirs()) {
				throw new IllegalArgumentException("Cannot use path as workspace: " + rootPathProperty);
			}
			return result;
		}
		final int os = BootstrapWorkspace.getOperatingSystem();
		File result = new File(System.getProperty("user.home"), ".shattered");
		if (os == 1) {
			final String appdata = System.getenv("APPDATA");
			if (appdata != null) {
				result = new File(appdata, "Shattered");
			}
		} else if (os == 2) {
			result = new File(System.getProperty("user.home"), "Library/Application Support/Shattered");
		}
		if (!result.exists() && !result.mkdirs()) {
			throw new IllegalArgumentException("Cannot make workspace: " + result.getAbsolutePath());
		}
		return result;
	}

	private static int getOperatingSystem() {
		final String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
		if (os.contains("win")) {
			return 1;
		} else if (os.contains("mac") || os.contains("darwin")) {
			return 2;
		}
		return 0;
	}

	private BootstrapWorkspace() {
	}
}
