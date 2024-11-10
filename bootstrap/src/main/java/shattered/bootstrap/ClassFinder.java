package shattered.bootstrap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import static shattered.bootstrap.Bootstrap.LOGGER;

final class ClassFinder {

	private static final Set<String> IGNORED_NAMES = Set.of("package-info.class", "module-info.class");

	public static Map<String, byte[]> loadClasses(final String selfPath) throws IOException {
		final Map<String, byte[]> result = new HashMap<>();
		final String[] paths = Objects.requireNonNull(System.getProperty("java.class.path")).split(";");
		for (final String path : paths) {
			if (path.equals(selfPath)) {
				continue;
			}
			final File f = new File(path);
			if (!f.exists()) {
				continue;
			}
			LOGGER.debug("Scanning classpath element: {}", path);
			if (f.isDirectory()) {
				final Map<String, byte[]> dirClasses = new HashMap<>();
				ClassFinder.loadDirContents(f, dirClasses, null);
				result.putAll(dirClasses);
			} else {
				result.putAll(ClassFinder.loadJarContents(f));
			}
		}
		return result;
	}

	private static void loadDirContents(final File directory, final Map<String, byte[]> map, final String path) throws IOException {
		for (final File file : directory.listFiles()) {
			final String name = file.getName();
			if (file.isDirectory()) {
				ClassFinder.loadDirContents(file, map, (path != null ? path + "." + name : name));
			} else if (name.endsWith(".class") && ClassFinder.IGNORED_NAMES.stream().noneMatch(name::equals)) {
				final String newName = (path != null ? path + "." : "") + name.replaceAll("/", ".").replace(".class", "");
				LOGGER.trace("\tFound class: {}", newName);
				map.put(newName, Files.readAllBytes(file.toPath()));
			}
		}
	}

	private static Map<String, byte[]> loadJarContents(final File f) throws IOException {
		final Map<String, byte[]> result = new HashMap<>();
		try (JarInputStream input = new JarInputStream(new FileInputStream(f))) {
			JarEntry entry;
			while ((entry = input.getNextJarEntry()) != null) {
				final String name = entry.getRealName();
				if (!entry.isDirectory() && name.endsWith(".class") && ClassFinder.IGNORED_NAMES.stream().noneMatch(name::endsWith)) {
					final String newName = name.replaceAll("/", ".").replace(".class", "");
					LOGGER.trace("\tFound class: {}", newName);
					result.put(newName, input.readAllBytes());
				}
			}
		}
		return result;
	}
}
