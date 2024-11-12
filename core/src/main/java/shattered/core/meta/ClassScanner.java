package shattered.core.meta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import static shattered.Shattered.LOGGER;

final class ClassScanner {

	private static final Set<String> IGNORED_NAMES = Set.of("package-info.class", "module-info.class");
	private static final ExecutorService EXECUTOR_SERVICE = Executors.newVirtualThreadPerTaskExecutor();
	private static final List<CompletableFuture<ClassNode>> RESOLVED = Collections.synchronizedList(new ArrayList<>());

	public static Set<ClassNode> readAllClasses(final File file) throws IOException {
		ClassScanner.RESOLVED.clear();
		if (!file.exists()) {
			throw new FileNotFoundException(file.getAbsolutePath());
		}
		if (file.isDirectory()) {
			ClassScanner.loadDirContents(file);
		} else {
			ClassScanner.loadJarContents(file);
		}
		try {
			final Map<String, ClassNode> map = new Object2ObjectArrayMap<>();
			CompletableFuture.allOf(ClassScanner.RESOLVED.toArray(CompletableFuture[]::new)).get();
			ClassScanner.RESOLVED.forEach(future -> {
				try {
					final ClassNode node = future.get();
					map.put(node.name, node);
				} catch (final InterruptedException | ExecutionException e) {
					throw new RuntimeException(e);
				}
			});
			ClassScanner.RESOLVED.clear();
			return new ObjectArraySet<>(map.values());
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	private static void loadDirContents(final File directory) throws IOException {
		final Map<String, ClassNode> result = new HashMap<>();
		for (final File file : directory.listFiles()) {
			final String name = file.getName();
			if (file.isDirectory()) {
				ClassScanner.loadDirContents(file);
			} else if (name.endsWith(".class") && ClassScanner.IGNORED_NAMES.stream().noneMatch(name::equals)) {
				final CompletableFuture<ClassNode> future = CompletableFuture.supplyAsync(() -> {
					final ClassNode node;
					try {
						node = ClassScanner.readNode(Files.readAllBytes(file.toPath()));
					} catch (final IOException e) {
						throw new RuntimeException(e);
					}
					LOGGER.trace("\tFound class: {}", node.name);
					return node;
				}, ClassScanner.EXECUTOR_SERVICE);
				ClassScanner.RESOLVED.add(future);
			}
		}
	}

	private static Map<String, ClassNode> loadJarContents(final File jarFile) throws IOException {
		final Map<String, ClassNode> result = new HashMap<>();
		try (JarInputStream input = new JarInputStream(new FileInputStream(jarFile))) {
			JarEntry entry;
			while ((entry = input.getNextJarEntry()) != null) {
				final String name = entry.getRealName();
				if (!entry.isDirectory() && name.endsWith(".class") && ClassScanner.IGNORED_NAMES.stream().noneMatch(name::endsWith)) {
					final byte[] bytes = input.readAllBytes();
					final CompletableFuture<ClassNode> future = CompletableFuture.supplyAsync(() -> {
						final ClassNode node = ClassScanner.readNode(bytes);
						LOGGER.trace("\tFound class: {}", node.name);
						return node;
					}, ClassScanner.EXECUTOR_SERVICE);
					ClassScanner.RESOLVED.add(future);
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
