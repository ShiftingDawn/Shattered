package shattered.lib.util;

import java.util.Locale;

public enum OperatingSystem {

	WINDOWS,
	MACOS,
	LINUX;

	public static OperatingSystem get() {
		final String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
		if (os.contains("win")) {
			return OperatingSystem.WINDOWS;
		} else if (os.contains("mac") || os.contains("darwin")) {
			return OperatingSystem.MACOS;
		} else {
			return OperatingSystem.LINUX;
		}
	}
}
