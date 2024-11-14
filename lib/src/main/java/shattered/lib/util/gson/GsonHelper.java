package shattered.lib.util.gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import shattered.lib.registry.Identifier;

public final class GsonHelper {

	public static final Gson GSON;

	static {
		final GsonBuilder builder = new GsonBuilder()
				//Properties
				.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
				.setExclusionStrategies(new AnnotatedExclusionStrategy())
				.serializeNulls()
				//Types
				.registerTypeAdapterFactory(new EnumAdapterFactory())
				.registerTypeAdapter(Identifier.class, GsonIdentifierAdapter.INSTANCE);
		MathTypeAdapters.register(builder);
		GSON = builder.create();
	}

	@Contract("null, _ -> false; !null, _ -> _")
	public static boolean hasField(@Nullable final JsonObject json, final String name) {
		return json != null && json.has(name);
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	@Contract("null, _ -> false; !null, _ -> _")
	public static boolean hasPrimitive(@Nullable final JsonObject json, final String name) {
		return GsonHelper.hasField(json, name) && json.get(name).isJsonPrimitive();
	}

	@Contract("null, _ -> false; !null, _ -> _")
	public static boolean hasBoolean(@Nullable final JsonObject json, final String name) {
		if (!GsonHelper.hasPrimitive(json, name)) {
			return false;
		}
		return json.getAsJsonPrimitive(name).isBoolean();
	}

	@Contract("null, _ -> false; !null, _ -> _")
	public static boolean hasNumber(@Nullable final JsonObject json, final String name) {
		if (!GsonHelper.hasPrimitive(json, name)) {
			return false;
		}
		return json.getAsJsonPrimitive(name).isNumber();
	}

	@Contract("null, _ -> false; !null, _ -> _")
	public static boolean hasString(@Nullable final JsonObject json, final String name) {
		if (!GsonHelper.hasPrimitive(json, name)) {
			return false;
		}
		return json.getAsJsonPrimitive(name).isString();
	}

	@Contract("null, _ -> false; !null, _ -> _")
	public static boolean hasObject(@Nullable final JsonObject json, final String name) {
		return GsonHelper.hasField(json, name) && json.get(name).isJsonObject();
	}

	@Contract("null, _ -> false; !null, _ -> _")
	public static boolean hasArray(@Nullable final JsonObject json, final String name) {
		return GsonHelper.hasField(json, name) && json.get(name).isJsonArray();
	}

	@Contract("null, _ -> true; !null, _ -> _")
	public static boolean isNull(@Nullable final JsonObject json, final String name) {
		return !GsonHelper.hasField(json, name) || json.get(name).isJsonNull();
	}

	public static JsonObject getAsObject(@Nullable final JsonElement json, final String name) {
		if (json == null || !json.isJsonObject()) {
			throw GsonHelper.wrongTypeException(json, name, "an object");
		}
		return json.getAsJsonObject();
	}

	public static JsonArray getAsArray(@Nullable final JsonElement json, final String name) {
		if (json == null || !json.isJsonArray()) {
			throw GsonHelper.wrongTypeException(json, name, "an array");
		}
		return json.getAsJsonArray();
	}

	public static JsonPrimitive getAsPrimitive(@Nullable final JsonElement json, final String name) {
		if (json == null || !json.isJsonPrimitive()) {
			throw GsonHelper.wrongTypeException(json, name, "a primitive");
		}
		return json.getAsJsonPrimitive();
	}

	public static boolean getAsBoolean(@Nullable final JsonElement json, final String name) {
		if (json == null || !json.isJsonPrimitive()) {
			throw GsonHelper.wrongTypeException(json, name, "a string");
		}
		final JsonPrimitive primitive = json.getAsJsonPrimitive();
		if (!primitive.isBoolean()) {
			throw GsonHelper.wrongTypeException(json, name, "a boolean");
		}
		return primitive.getAsBoolean();
	}

	public static JsonPrimitive getAsNumber(@Nullable final JsonElement json, final String name) {
		if (json == null || !json.isJsonPrimitive()) {
			throw GsonHelper.wrongTypeException(json, name, "a number");
		}
		final JsonPrimitive primitive = json.getAsJsonPrimitive();
		if (!primitive.isNumber()) {
			throw GsonHelper.wrongTypeException(json, name, "a number");
		}
		return primitive;
	}

	public static int getAsByte(@Nullable final JsonElement json, final String name) {
		return GsonHelper.getAsNumber(json, name).getAsByte();
	}

	public static int getAsShort(@Nullable final JsonElement json, final String name) {
		return GsonHelper.getAsNumber(json, name).getAsShort();
	}

	public static int getAsInt(@Nullable final JsonElement json, final String name) {
		return GsonHelper.getAsNumber(json, name).getAsInt();
	}

	public static float getAsFloat(@Nullable final JsonElement json, final String name) {
		return GsonHelper.getAsNumber(json, name).getAsFloat();
	}

	public static double getAsDouble(@Nullable final JsonElement json, final String name) {
		return GsonHelper.getAsNumber(json, name).getAsDouble();
	}

	public static long getAsLong(@Nullable final JsonElement json, final String name) {
		return GsonHelper.getAsNumber(json, name).getAsLong();
	}

	public static BigInteger getAsBigInteger(@Nullable final JsonElement json, final String name) {
		return GsonHelper.getAsNumber(json, name).getAsBigInteger();
	}

	public static BigDecimal getAsBigDecimal(@Nullable final JsonElement json, final String name) {
		return GsonHelper.getAsNumber(json, name).getAsBigDecimal();
	}

	public static String getAsString(@Nullable final JsonElement json, final String name) {
		if (json == null || !json.isJsonPrimitive()) {
			throw GsonHelper.wrongTypeException(json, name, "a string");
		}
		final JsonPrimitive primitive = json.getAsJsonPrimitive();
		if (!primitive.isString()) {
			throw GsonHelper.wrongTypeException(json, name, "a string");
		}
		return primitive.getAsString();
	}

	public static JsonObject getObject(@Nullable final JsonObject json, final String name) {
		if (!GsonHelper.hasField(json, name)) {
			throw GsonHelper.missingFieldException(name, "object");
		}
		if (!json.get(name).isJsonObject()) {
			throw GsonHelper.wrongTypeException(json, name, "an object");
		}
		return json.get(name).getAsJsonObject();
	}

	public static JsonArray getArray(@Nullable final JsonObject json, final String name) {
		if (!GsonHelper.hasField(json, name)) {
			throw GsonHelper.missingFieldException(name, "array");
		}
		if (!json.get(name).isJsonArray()) {
			throw GsonHelper.wrongTypeException(json, name, "an array");
		}
		return json.get(name).getAsJsonArray();
	}

	public static JsonPrimitive getPrimitive(@Nullable final JsonObject json, final String name) {
		if (!GsonHelper.hasField(json, name)) {
			throw GsonHelper.missingFieldException(name, "primitive");
		}
		if (!json.get(name).isJsonPrimitive()) {
			throw GsonHelper.wrongTypeException(json, name, "a primitive");
		}
		return json.get(name).getAsJsonPrimitive();
	}

	public static boolean getBoolean(@Nullable final JsonObject json, final String name) {
		if (!GsonHelper.hasField(json, name)) {
			throw GsonHelper.missingFieldException(name, "boolean");
		}
		if (!json.get(name).isJsonPrimitive()) {
			throw GsonHelper.wrongTypeException(json, name, "a boolean");
		}
		final JsonPrimitive primitive = json.get(name).getAsJsonPrimitive();
		if (!primitive.isBoolean()) {
			throw GsonHelper.wrongTypeException(json, name, "a boolean");
		}
		return primitive.getAsBoolean();
	}

	private static JsonPrimitive getNumberInternal(@Nullable final JsonObject json, final String name) {
		if (!GsonHelper.hasField(json, name)) {
			throw GsonHelper.missingFieldException(name, "number");
		}
		if (!json.get(name).isJsonPrimitive()) {
			throw GsonHelper.wrongTypeException(json, name, "a number");
		}
		final JsonPrimitive primitive = json.get(name).getAsJsonPrimitive();
		if (!primitive.isNumber()) {
			throw GsonHelper.wrongTypeException(json, name, "a number");
		}
		return primitive;
	}

	public static Number getNumber(@Nullable final JsonObject json, final String name) {
		return GsonHelper.getNumberInternal(json, name).getAsNumber();
	}

	public static byte getByte(@Nullable final JsonObject json, final String name) {
		return GsonHelper.getNumber(json, name).byteValue();
	}

	public static short getShort(@Nullable final JsonObject json, final String name) {
		return GsonHelper.getNumber(json, name).shortValue();
	}

	public static int getInt(@Nullable final JsonObject json, final String name) {
		return GsonHelper.getNumber(json, name).intValue();
	}

	public static float getFloat(@Nullable final JsonObject json, final String name) {
		return GsonHelper.getNumber(json, name).floatValue();
	}

	public static double getDouble(@Nullable final JsonObject json, final String name) {
		return GsonHelper.getNumber(json, name).doubleValue();
	}

	public static double getLong(@Nullable final JsonObject json, final String name) {
		return GsonHelper.getNumber(json, name).longValue();
	}

	public static BigInteger getBigInteger(@Nullable final JsonObject json, final String name) {
		return GsonHelper.getNumberInternal(json, name).getAsBigInteger();
	}

	public static BigDecimal getBigDecimal(@Nullable final JsonObject json, final String name) {
		return GsonHelper.getNumberInternal(json, name).getAsBigDecimal();
	}

	public static String getString(@Nullable final JsonObject json, final String name) {
		if (!GsonHelper.hasField(json, name)) {
			throw GsonHelper.missingFieldException(name, "string");
		}
		if (!json.get(name).isJsonPrimitive()) {
			throw GsonHelper.wrongTypeException(json, name, "a string");
		}
		final JsonPrimitive primitive = json.get(name).getAsJsonPrimitive();
		if (!primitive.isString()) {
			throw GsonHelper.wrongTypeException(json, name, "a string");
		}
		return primitive.getAsString();
	}

	@Nullable
	public static <T> T deserialize(final Reader reader, final Class<T> type) {
		try (final JsonReader json = new JsonReader(reader)) {
			if (json.peek() == JsonToken.BEGIN_OBJECT) {
				final JsonObject object = GsonHelper.GSON.fromJson(json, JsonObject.class);
				if (object == null) {
					return null;
				}
				JsonValidator.validate(object, type, null);
				final T result = GsonHelper.GSON.fromJson(object, type);
				StringTypeValidator.validate(object, type, result, null);
				return result;
			} else {
				return GsonHelper.GSON.fromJson(json, type);
			}
		} catch (final IOException e) {
			throw new JsonParseException(e);
		}
	}

	@Nullable
	public static <T> T deserialize(final String json, final Class<T> type) {
		return GsonHelper.deserialize(new StringReader(json), type);
	}

	@Nullable
	public static <T> T deserialize(final File file, final Class<T> type) throws FileNotFoundException {
		return GsonHelper.deserialize(new FileReader(file), type);
	}

	@Nullable
	public static <T> T deserialize(final InputStream stream, final Class<T> type) {
		return GsonHelper.deserialize(new InputStreamReader(stream), type);
	}

	@Nullable
	public static JsonElement deserialize(final Reader reader) {
		return GsonHelper.deserialize(reader, JsonElement.class);
	}

	@Nullable
	public static JsonElement deserialize(final String json) {
		return GsonHelper.deserialize(new StringReader(json));
	}

	@Nullable
	public static JsonElement deserialize(final File file) throws FileNotFoundException {
		return GsonHelper.deserialize(new FileReader(file));
	}

	@Nullable
	public static JsonElement deserialize(final InputStream stream) {
		return GsonHelper.deserialize(new InputStreamReader(stream));
	}

	@Nullable
	public static <T> T deserialize(final JsonElement json, final Class<T> type) {
		if (json.isJsonObject()) {
			JsonValidator.validate(json.getAsJsonObject(), type, null);
			final T result = GsonHelper.GSON.fromJson(json, type);
			StringTypeValidator.validate(json.getAsJsonObject(), type, result, null);
		}
		return GsonHelper.GSON.fromJson(json, type);
	}

	private static String toString(@Nullable final JsonElement json) {
		if (json == null) {
			return "null (missing)";
		} else if (json.isJsonNull()) {
			return "null (value)";
		} else if (json.isJsonArray()) {
			return "an array (" + json + ')';
		} else if (json.isJsonObject()) {
			return "an object (" + json + ')';
		} else if (json.isJsonPrimitive()) {
			final JsonPrimitive prim = json.getAsJsonPrimitive();
			if (prim.isBoolean()) {
				return "a boolean (" + json + ')';
			} else if (prim.isNumber()) {
				return "a number (" + json + ')';
			} else if (prim.isString()) {
				return "a string (" + json + ')';
			}
		}
		return json.toString();
	}

	private static JsonSyntaxException missingFieldException(final String name, final String expectedType) {
		return new JsonSyntaxException(String.format("Expected %s '%s' is not defined", expectedType, name));
	}

	private static JsonSyntaxException wrongTypeException(@Nullable final JsonElement json, final String fieldName, final String expectedType) {
		return new JsonSyntaxException(String.format("Expected field '%s' to be %s but found %s", fieldName, expectedType, GsonHelper.toString(json)));
	}

	private GsonHelper() {
	}
}
