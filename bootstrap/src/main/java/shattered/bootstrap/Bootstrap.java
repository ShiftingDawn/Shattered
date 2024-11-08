package shattered.bootstrap;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import shattered.bridge.ClassTransformer;
import shattered.bridge.InvocationIndex;
import shattered.bridge.RuntimeMetadata;
import shattered.bridge.ShatteredEntryPoint;

public final class Bootstrap {

	static final BootstrapClassLoader LOADER = new BootstrapClassLoader();
	static final Logger LOGGER;
	static final File ROOT_DIR = BootstrapWorkspace.getRootDir();

	static {
		Thread.currentThread().setContextClassLoader(Bootstrap.LOADER);
		Bootstrap.addFileAppender();
		LOGGER = LogManager.getLogger("Shattered");
	}

	public static void init(final String[] args) {
		Bootstrap.LOGGER.info("Bootstrapping Shattered!");
		try {
			final URL self = Bootstrap.class.getProtectionDomain().getCodeSource().getLocation();
			final File selfFile = new File(self.toURI());
			final Map<String, byte[]> classData = ClassFinder.loadClasses(selfFile.getAbsolutePath());
			classData.keySet().forEach(className -> Bootstrap.registerClassTransformers(className, classData.get(className)));
			classData.keySet().forEach(className -> classData.put(className, TransformerRegistry.transform(className, classData.get(className))));
			BootstrapClassLoader.CLASS_DATA.putAll(classData);
			Bootstrap.processClasses(classData);
		} catch (final Exception e) {
			Bootstrap.LOGGER.fatal("An error occurred while bootstrapping Shattered: {}", e.getMessage());
			System.exit(-1);
		}
		final String[] bootClasses = RuntimeMetadata.getAnnotatedClasses(ShatteredEntryPoint.class);
		if (bootClasses.length != 1) {
			Bootstrap.LOGGER.fatal("Could not find Shattered EntryPoint");
			System.exit(-1);
		}
		try {
			final Constructor<?> constructor = Bootstrap.LOADER.loadClass(bootClasses[0]).getDeclaredConstructor(String[].class);
			constructor.setAccessible(true);
			final String[] newArgs = new String[args.length + 1];
			newArgs[0] = Bootstrap.ROOT_DIR.getAbsolutePath();
			System.arraycopy(args, 0, newArgs, 1, args.length);
			constructor.newInstance((Object) newArgs);
		} catch (NoSuchMethodException | ClassNotFoundException | InstantiationException | IllegalAccessException ignored) {
			Bootstrap.LOGGER.fatal("Could not execute Shattered EntryPoint");
			System.exit(-1);
		} catch (final InvocationTargetException e) {
			final Throwable ex = e.getCause();
			Bootstrap.modifyStackTrace(ex);
			Bootstrap.LOGGER.atFatal().withThrowable(ex).log("An error occured, Shattered will now exit");
			System.exit(-1);
		}
	}

	private static void addFileAppender() {
		final File logsDir = new File(Bootstrap.ROOT_DIR, "logs");
		final String fullPath = new File(logsDir, "latest.log").getAbsolutePath();
		System.setProperty("shattered.logfile", fullPath);
		final String cleanedPath = logsDir.getAbsolutePath() + File.separator + "%d{yyyy-MM-dd}-%i.log.gz";
		System.setProperty("shattered.logfileclean", cleanedPath);
		final org.apache.logging.log4j.core.LoggerContext ctx = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
		ctx.reconfigure();
	}

	private static void modifyStackTrace(final Throwable e) {
		final List<StackTraceElement> newTrace = new ArrayList<>();
		int selfIndex = -1;
		for (int i = 0; i < e.getStackTrace().length; ++i) {
			if (e.getStackTrace()[i].getClassName().equals(Bootstrap.class.getName())) {
				selfIndex = i;
				break;
			}
		}
		for (int i = 0; i < selfIndex - 3; ++i) {
			newTrace.add(e.getStackTrace()[i]);
		}
		newTrace.add(e.getStackTrace()[selfIndex]);
		e.setStackTrace(newTrace.toArray(new StackTraceElement[0]));
	}

	private static void registerClassTransformers(final String classToLoad, final byte[] classData) {
		final ClassReader reader = new ClassReader(classData);
		final ClassNode node = new ClassNode(Opcodes.ASM9);
		reader.accept(node, 0);
		if (node.interfaces.contains(Type.getInternalName(ClassTransformer.class))) {
			TransformerRegistry.registerTransformer(classToLoad, node);
		}
	}

	private static void processClasses(final Map<String, byte[]> classData) {
		classData.forEach((className, classBytes) -> {
			final ClassReader reader = new ClassReader(classBytes);
			final ClassNode node = new ClassNode(Opcodes.ASM9);
			reader.accept(node, 0);
			if (node.visibleAnnotations != null) {
				node.visibleAnnotations.forEach(annotationNode -> Bootstrap.registerAnnotatedClass(Type.getType(annotationNode.desc).getClassName(), className));
			}
			if (node.invisibleAnnotations != null) {
				node.invisibleAnnotations.forEach(annotationNode -> Bootstrap.registerAnnotatedClass(Type.getType(annotationNode.desc).getClassName(), className));
			}
		});
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
