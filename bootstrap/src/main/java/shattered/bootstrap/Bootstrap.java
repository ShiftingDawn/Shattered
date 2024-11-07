package shattered.bootstrap;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import shattered.lib.ClassTransformer;
import shattered.lib.InvocationIndex;
import shattered.lib.RuntimeMetadata;
import shattered.lib.ShatteredEntryPoint;

public final class Bootstrap {

	static final BootstrapClassLoader LOADER = new BootstrapClassLoader();

	public static void init(final String[] args) {
		try {
			Bootstrap.LOADER.registerClass(InvocationIndex.class);
			Bootstrap.LOADER.registerClass(RuntimeMetadata.class);

			final URL self = Bootstrap.class.getProtectionDomain().getCodeSource().getLocation();
			final File selfFile = new File(self.toURI());
			final Map<String, byte[]> classData = ClassFinder.loadClasses(selfFile.getAbsolutePath());
			final Map<String, List<String>> loadingTree = Bootstrap.makeClassLoadingTree(classData);
			loadingTree.keySet().forEach(className -> Bootstrap.processClassRecursive(loadingTree, classData, className));
			final String[] bootClasses = RuntimeMetadata.getAnnotatedClasses(ShatteredEntryPoint.class);
			if (bootClasses.length != 1) {
				throw new RuntimeException();
			}
			final Constructor<?> constructor = Bootstrap.LOADER.loadClass(bootClasses[0]).getDeclaredConstructor(String[].class);
			constructor.setAccessible(true);
			constructor.newInstance((Object) args);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Map<String, List<String>> makeClassLoadingTree(final Map<String, byte[]> classData) {
		final Map<String, List<String>> parentClassMapping = new HashMap<>();
		for (final Map.Entry<String, byte[]> entry : classData.entrySet()) {
			Bootstrap.addDependenciesToTree(parentClassMapping, entry.getKey(), entry.getValue());
		}
		for (final String name : parentClassMapping.keySet()) {
			final String unresolved = Bootstrap.getMissingDependencies(parentClassMapping, name);
			if (unresolved != null) {
				throw new RuntimeException("Cannot resolve class " + name + "because of missing dependency: " + unresolved);
			}
		}
		return parentClassMapping;
	}

	private static void addDependenciesToTree(final Map<String, List<String>> tree, final String className, final byte[] classData) {
		final ClassReader reader = new ClassReader(classData);
		final List<String> dependencies = tree.computeIfAbsent(className, k -> new ArrayList<>());
		if (!Bootstrap.LOADER.hasParentLoaded(reader.getSuperName().replace('/', '.'))) {
			dependencies.add(reader.getSuperName().replace('/', '.'));
		}
		for (final String name : reader.getInterfaces()) {
			if (!Bootstrap.LOADER.hasParentLoaded(name.replace('/', '.'))) {
				dependencies.add(name.replace('/', '.'));
			}
		}
	}

	private static String getMissingDependencies(final Map<String, List<String>> map, final String name) {
		final List<String> parents = map.get(name);
		if (parents == null) {
			return name;
		}
		for (final String dependency : parents) {
			final String result = Bootstrap.getMissingDependencies(map, dependency);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	private static void processClassRecursive(final Map<String, List<String>> dependencyTree, final Map<String, byte[]> classData, final String classToLoad) {
		final List<String> parents = dependencyTree.get(classToLoad);
		parents.forEach(parentClass -> Bootstrap.processClassRecursive(dependencyTree, classData, parentClass));
		final ClassReader reader = new ClassReader(classData.get(classToLoad));
		final ClassNode node = new ClassNode(Opcodes.ASM9);
		reader.accept(node, 0);
		if (node.interfaces.contains(Type.getInternalName(ClassTransformer.class))) {
			TransformerRegistry.registerTransformer(classToLoad, node);
			return;
		}
		if (node.visibleAnnotations != null) {
			node.visibleAnnotations.forEach(annotationNode -> Bootstrap.registerAnnotatedClass(Type.getType(annotationNode.desc).getClassName(), classToLoad));
		}
		Bootstrap.LOADER.defineClassIfMissing(classToLoad, classData.get(classToLoad));
	}

	private static Map<String, List<String>> annotationMetaMap;

	@SuppressWarnings("unchecked")
	private static void registerAnnotatedClass(final String annotation, final String clazz) {
		if (Bootstrap.annotationMetaMap == null) {
			for (final Field field : RuntimeMetadata.class.getDeclaredFields()) {
				if (field.isAnnotationPresent(InvocationIndex.class) && field.getAnnotation(InvocationIndex.class).value() == 0) {
					try {
						field.setAccessible(true);
						Bootstrap.annotationMetaMap = (Map<String, List<String>>) field.get(null);
						field.setAccessible(false);
					} catch (final IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		if (Bootstrap.annotationMetaMap == null) {
			throw new RuntimeException();
		}
		Bootstrap.annotationMetaMap.computeIfAbsent(annotation, k -> new ArrayList<>()).add(clazz);
	}
}
