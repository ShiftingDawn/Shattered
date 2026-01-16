package dawn.internal;

import com.google.gson.JsonObject;
import org.jspecify.annotations.Nullable;

public interface JsonValidator {

	void validate(JsonObject object, Class<?> type, @Nullable String path);
}
