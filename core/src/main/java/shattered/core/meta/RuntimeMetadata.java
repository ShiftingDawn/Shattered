package shattered.core.meta;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import static shattered.Shattered.LOGGER;

public final class RuntimeMetadata {

	private static final Map<String, Set<String>> CLASS_ANNOTATIONS = new Object2ObjectArrayMap<>();

	public static Class<?>[] getAnnotatedClasses(final Class<? extends Annotation> annotation) {
		return RuntimeMetadata.CLASS_ANNOTATIONS.getOrDefault(annotation.getName(), Set.of()).stream().map(className -> {
			try {
				return Class.forName(className);
			} catch (final ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}).toArray(Class[]::new);
	}

	public static void scan(final File file) throws IOException {
		LOGGER.debug("Started metadata scan of {}", file);
		final Set<ClassNode> classData = ClassScanner.readAllClasses(file);
		classData.forEach(node -> {
			final String name = node.name.replace('/', '.');
			if (node.visibleAnnotations != null) {
				node.visibleAnnotations.forEach(a -> RuntimeMetadata.register(name, a));
			}
			if (node.invisibleAnnotations != null) {
				node.invisibleAnnotations.forEach(a -> RuntimeMetadata.register(name, a));
			}
		});
	}

	private static void register(final String className, final AnnotationNode node) {
		RuntimeMetadata.CLASS_ANNOTATIONS.computeIfAbsent(className, $ -> new ObjectArraySet<>())
				.add(Type.getType(node.desc).getClassName());
	}

	private RuntimeMetadata() {
	}
}
