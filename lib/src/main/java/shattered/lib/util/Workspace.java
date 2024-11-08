package shattered.lib.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import shattered.lib.Internal;

public final class Workspace {

	public static final Path ROOT_DIR = Internal.ROOT_PATH;
	public static final Path BINARY_DIR = Workspace.makeDir("bin");
	public static final Path CONFIG_DIR = Workspace.makeDir("config");
	public static final Path LOGS_DIR = Workspace.makeDir("logs");

	public static Path makeDir(final String name) {
		final Path result = Workspace.ROOT_DIR.resolve(name);
		try {
			Files.createDirectories(result);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	public static File getConfigFile(final String name) {
		return Workspace.CONFIG_DIR.resolve(name + ".cfg").toFile();
	}

	private Workspace() {
	}
}
