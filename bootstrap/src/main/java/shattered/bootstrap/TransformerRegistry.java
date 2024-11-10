package shattered.bootstrap;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.util.Set;
import java.util.TreeSet;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import shattered.bridge.ClassTransformer;
import static shattered.bootstrap.Bootstrap.LOGGER;

final class TransformerRegistry {

	private static final boolean DUMP_CLASSES = Boolean.getBoolean("shattered.bootstrap.dumpclasses");
	private static final File DUMP_CLASSES_DIR = TransformerRegistry.DUMP_CLASSES ? new File(Bootstrap.ROOT_DIR, "debug/bootstrap/classdump") : null;
	private static final Set<ClassTransformer> TRANSFORMERS = new TreeSet<>((o1, o2) -> o1.priority() == o2.priority() ? -1 : Integer.compare(o2.priority(), o1.priority()));

	static {
		if (TransformerRegistry.DUMP_CLASSES) {
			TransformerRegistry.deleteRecursive(TransformerRegistry.DUMP_CLASSES_DIR);
			TransformerRegistry.DUMP_CLASSES_DIR.mkdirs();
		}
	}

	static byte[] transform(final String className, final byte[] data) {
		boolean hasTransformed = false;
		byte[] newData = data;
		ClassReader currentReader = new ClassReader(data);
		ClassNode currentNode = new ClassNode(Opcodes.ASM9);
		currentReader.accept(currentNode, 0);
		for (final ClassTransformer transformer : TransformerRegistry.TRANSFORMERS) {
			if (!transformer.canTransform(currentNode)) {
				continue;
			}
			LOGGER.debug("Transforming class {} with transformer {}", className, transformer.getClass().getName());
			final byte[] transformed = transformer.transform(newData);
			if (transformed != null && transformed.length > 0) {
				try {
					currentReader = new ClassReader(transformed);
					currentNode = new ClassNode(Opcodes.ASM9);
					currentReader.accept(currentNode, 0);
					newData = transformed;
					hasTransformed = true;
				} catch (final Exception e) {
					LOGGER.atError().withThrowable(e).log("Could not apply transformer {} to class {}", transformer.getClass().getName(), className);
				}
			}
		}
		if (hasTransformed && TransformerRegistry.DUMP_CLASSES) {
			TransformerRegistry.dumpClass(className, newData);
		}
		return newData;
	}

	static void registerTransformer(final String className, final ClassNode node) {
		if (!node.superName.equals(Type.getInternalName(Object.class)) || node.interfaces.size() != 1) {
			throw new BootstrapException("ClassTransformer %s is invalid".formatted(className));
		}
		try {
			final Class<?> clazz = Class.forName(className);
			final Constructor<?> constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			final Object instance = constructor.newInstance();
			TransformerRegistry.TRANSFORMERS.add((ClassTransformer) instance);
			LOGGER.debug("Registered ClassTransformer {}", className);
		} catch (final ClassNotFoundException e) {
			throw new BootstrapException();
		} catch (final Throwable e) {
			throw new BootstrapException("ClassTransformer %s is invalid".formatted(className));
		}
	}

	private static void dumpClass(final String name, final byte[] data) {
		final File file = new File(TransformerRegistry.DUMP_CLASSES_DIR, name.replace('.', '/') + ".class");
		file.getParentFile().mkdirs();
		try {
			Files.write(file.toPath(), data);
		} catch (final IOException e) {
		}
	}

	private static void deleteRecursive(final File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				for (final File child : file.listFiles()) {
					TransformerRegistry.deleteRecursive(child);
				}
			}
			file.delete();
		}
	}
}
