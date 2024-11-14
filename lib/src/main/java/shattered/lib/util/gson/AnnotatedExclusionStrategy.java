package shattered.lib.util.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

final class AnnotatedExclusionStrategy implements ExclusionStrategy {

	@Override
	public boolean shouldSkipField(final FieldAttributes attributes) {
		return attributes.getAnnotation(Json.Ignore.class) != null;
	}

	@Override
	public boolean shouldSkipClass(final Class<?> clazz) {
		return clazz.isAnnotationPresent(Json.Ignore.class);
	}
}
