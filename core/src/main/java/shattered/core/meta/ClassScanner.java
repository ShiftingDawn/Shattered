package shattered.core.meta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import static shattered.Shattered.LOGGER;

final class ClassScanner {

	private static final Set<String> IGNORED_NAMES = Set.of("package-info.class", "module-info.class");

	public static Set<ClassNode> readAllClasses(final File file) throws IOException {
		if (!file.exists()) {
			throw new FileNotFoundException(file.getAbsolutePath());
		}
		final Map<String, ClassNode> classMap = file.isDirectory() ? ClassScanner.loadDirContents(file) : ClassScanner.loadJarContents(file);
		return new ObjectArraySet<>(classMap.values());
	}

	private static Map<String, ClassNode> loadDirContents(final File directory) throws IOException {
		final Map<String, ClassNode> result = new HashMap<>();
		for (final File file : directory.listFiles()) {
			final String name = file.getName();
			if (file.isDirectory()) {
				result.putAll(ClassScanner.loadDirContents(file));
			} else if (name.endsWith(".class") && ClassScanner.IGNORED_NAMES.stream().noneMatch(name::equals)) {
				final ClassNode node = ClassScanner.readNode(Files.readAllBytes(file.toPath()));
				result.put(node.name, node);
				LOGGER.trace("\tFound class: {}", node.name);
			}
		}
		return result;
	}

	private static Map<String, ClassNode> loadJarContents(final File jarFile) throws IOException {
		final Map<String, ClassNode> result = new HashMap<>();
		try (JarInputStream input = new JarInputStream(new FileInputStream(jarFile))) {
			JarEntry entry;
			while ((entry = input.getNextJarEntry()) != null) {
				final String name = entry.getRealName();
				if (!entry.isDirectory() && name.endsWith(".class") && ClassScanner.IGNORED_NAMES.stream().noneMatch(name::endsWith)) {
					final ClassNode node = ClassScanner.readNode(input.readAllBytes());
					result.put(node.name, node);
					LOGGER.trace("\tFound class: {}", node.name);
				}
			}
		}
		return result;
	}

	private static ClassNode readNode(final byte[] data) {
		final ClassNode result = new ClassNode(Opcodes.ASM9);
		final ClassReader reader = new ClassReader(data);
		reader.accept(result, 0);
		return result;
	}

	private ClassScanner() {
	}
}
