package shattered.lib.util.gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

@SuppressWarnings({ "unchecked", "rawtypes" })
final class EnumAdapterFactory implements TypeAdapterFactory {

	@Override
	public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> token) {
		Class<? super T> type = token.getRawType();
		if (!Enum.class.isAssignableFrom(type) || type == Enum.class) {
			return null;
		}
		if (!type.isEnum()) {
			type = type.getSuperclass();
		}
		return (TypeAdapter<T>) new EnumTypeAdapter(type);
	}

	private static class EnumTypeAdapter<T extends Enum<T>> extends TypeAdapter<T> {

		private final Map<String, T> nameToConstant = new HashMap<>();
		private final Map<T, String> constantToName = new HashMap<>();

		public EnumTypeAdapter(final Class<T> clazz) {
			try {
				for (final T constant : clazz.getEnumConstants()) {
					String name = constant.toString();
					final SerializedName annotation = clazz.getField(constant.name()).getAnnotation(SerializedName.class);
					if (annotation != null) {
						name = annotation.value();
						for (final String alternate : annotation.alternate()) {
							this.nameToConstant.put(alternate, constant);
						}
					}
					this.nameToConstant.put(name, constant);
					this.nameToConstant.put(constant.name(), constant);
					this.constantToName.put(constant, name);
				}
			} catch (final NoSuchFieldException e) {
				throw new AssertionError(e);
			}
		}

		@Override
		public void write(final JsonWriter writer, final T value) throws IOException {
			writer.value(value == null ? null : this.constantToName.get(value));
		}

		@Override
		public T read(final JsonReader reader) throws IOException {
			if (reader.peek() == JsonToken.NULL) {
				reader.nextNull();
				return null;
			}
			final String name = reader.nextString();
			final T result = this.nameToConstant.get(name);
			if (result == null) {
				throw new JsonSyntaxException(
						"Invalid ENUM constant: " + name + ". Valid values: " + Arrays.toString(this.nameToConstant.keySet().toArray(new String[0]))
				);
			}
			return result;
		}
	}
}
