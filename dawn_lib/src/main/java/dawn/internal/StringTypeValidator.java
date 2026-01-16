package dawn.internal;

import com.google.gson.JsonObject;
import org.jspecify.annotations.Nullable;

public interface StringTypeValidator {

	void validate(final JsonObject object, final Class<?> clazz, final Object instance, @Nullable final String path);
}
