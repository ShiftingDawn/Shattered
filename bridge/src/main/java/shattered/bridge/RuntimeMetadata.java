package shattered.bridge;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RuntimeMetadata {

	@InvocationIndex(0)
	private static final Map<String, List<String>> ANNOTATED_CLASSES = new HashMap<>();

	public static String[] getAnnotatedClasses(final Class<? extends Annotation> annotation) {
		return RuntimeMetadata.ANNOTATED_CLASSES.getOrDefault(annotation.getName(), List.of()).toArray(String[]::new);
	}
}
