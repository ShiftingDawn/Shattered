package shattered.lib.util.gson;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.google.gson.JsonObject;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import org.jetbrains.annotations.Nullable;

final class StringTypeValidator {

	public static void validate(final JsonObject object, final Class<?> clazz, final Object instance, @Nullable final String path) {
		try {
			final List<Field> controllers = Arrays.stream(clazz.getDeclaredFields()).filter(field -> {
				if (field.isAnnotationPresent(Json.TypeControllerString.class)) {
					return true;
				}
				return field.isAnnotationPresent(Json.TypeControllerEnum.class);
			}).collect(Collectors.toList());
			final List<Field> values = Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(Json.TypeValue.class)).collect(Collectors.toList());

			StringTypeValidator.validateStructure(clazz, controllers, values);
			final HashMap<String, String> controllerValues = StringTypeValidator.validateControllerValues(controllers, instance, path);
			StringTypeValidator.validateParsed(object, values, instance, controllerValues, path);

			//Validate nested objects
			for (final Field field : clazz.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				final Class<?> fieldType = field.getType();
				//Validate nested objects
				if (GsonHelper.GSON.getAdapter(fieldType) instanceof ReflectiveTypeAdapterFactory.Adapter) {
					final String jsonName = JsonValidator.getFieldName(field);
					StringTypeValidator.validate(object.getAsJsonObject(jsonName), fieldType, field.get(instance), (path == null ? "" : path + ".") + jsonName);
				}
			}
		} catch (final IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private static void validateStructure(final Class<?> clazz, final List<Field> controllers, final List<Field> values) {
		for (final Field field : controllers) {
			if (field.isAnnotationPresent(Json.TypeControllerString.class) && field.getType() != String.class) {
				throw new RuntimeException(String.format("Controller field %s.%s should be a string", clazz.getName(), field.getName()));
			}
			if (field.isAnnotationPresent(Json.TypeControllerEnum.class) && !field.getType().isEnum()) {
				throw new RuntimeException(String.format("Controller field %s.%s should be an enum!", clazz.getName(), field.getName()));
			}
		}

		//Controller names on value fields
		final Set<String> foundValueFieldControllers = values.stream().map(field -> field.getAnnotation(Json.TypeValue.class).controller()).collect(Collectors.toSet());

		//Check if every controller has at least one valid value
		for (final Field field : controllers) {
			final Json.TypeControllerString annotation = field.getAnnotation(Json.TypeControllerString.class);
			if (annotation != null && !foundValueFieldControllers.contains(annotation.controller())) {
				throw new RuntimeException(String.format("Controller field %s.%s has no references (name: '%s')", clazz.getName(), field.getName(), annotation.controller()));
			} else if (annotation == null) {
				final Json.TypeControllerEnum annotation2 = field.getAnnotation(Json.TypeControllerEnum.class);
				if (annotation2 != null && !foundValueFieldControllers.contains(annotation2.type())) {
					throw new RuntimeException(String.format("Controller field %s.%s has no references (name: '%s')", clazz.getName(), field.getName(), annotation2.type()));
				}
			}
		}

		final Set<String> foundControllerFieldNames = controllers.stream().map(field -> {
			if (field.isAnnotationPresent(Json.TypeControllerString.class)) {
				return field.getAnnotation(Json.TypeControllerString.class).controller();
			}
			return field.getAnnotation(Json.TypeControllerEnum.class).type();
		}).collect(Collectors.toSet());

		//Check if every value has a valid controller
		for (final Field field : values) {
			final Json.TypeValue annotation = field.getAnnotation(Json.TypeValue.class);
			if (!foundControllerFieldNames.contains(annotation.controller())) {
				throw new RuntimeException(String.format("Value field %s.%s references missing controller '%s'", clazz.getName(), field.getName(), annotation.type()));
			}
		}
	}

	private static HashMap<String, String> validateControllerValues(final List<Field> controllers, final Object instance, @Nullable final String path) throws IllegalAccessException {
		final HashMap<String, String> controllerValues = new HashMap<>();
		for (final Field field : controllers) {
			if (field.isAnnotationPresent(Json.TypeControllerString.class)) {
				final Json.TypeControllerString annotation = field.getAnnotation(Json.TypeControllerString.class);
				final String value = (String) field.get(instance);
				if (value == null && !field.isAnnotationPresent(Json.Required.class)) {
					continue;
				}
				if (!Arrays.asList(annotation.accepted()).contains(value)) {
					throw JsonValidator.createException("Field has invalid value (accepts: %s): '%s'", path, Arrays.toString(annotation.accepted()), JsonValidator.getFieldName(field));
				}
				controllerValues.put(annotation.controller(), value);
			} else if (field.isAnnotationPresent(Json.TypeControllerEnum.class)) {
				final Json.TypeControllerEnum annotation = field.getAnnotation(Json.TypeControllerEnum.class);
				final Enum<?> value = (Enum<?>) field.get(instance);
				if (value == null) {
					if (!field.isAnnotationPresent(Json.Required.class)) {
						continue;
					}
					throw JsonValidator.createException("Field has invalid value (accepts: %s): '%s'", path, Arrays.toString(field.getType().getEnumConstants()), JsonValidator.getFieldName(field));
				}
				controllerValues.put(annotation.type(), value.name());
			}
		}
		return controllerValues;
	}

	private static void validateParsed(final JsonObject object, final List<Field> values, final Object instance, final HashMap<String, String> controllerValues, @Nullable final String path)
			throws IllegalAccessException {
		for (final Field field : values) {
			final Json.TypeValue annotation = field.getAnnotation(Json.TypeValue.class);
			final String controllerValue = controllerValues.get(annotation.controller());
			if (field.isAnnotationPresent(Json.Required.class) && field.getAnnotation(Json.Required.class).group().length == 0 && controllerValue.equalsIgnoreCase(annotation.type())) {
				throw JsonValidator.createException("Field(%s=%s) was marked required but controller was not defined!", path, JsonValidator.getFieldName(field), annotation.type());
			}
			if (controllerValue == null || !controllerValue.equals(annotation.type())) {
				final Class<?> type = field.getType();
				if (!type.isPrimitive()) {
					field.set(instance, null);
				} else if (type == boolean.class) {
					field.setBoolean(instance, false);
				} else if (type == byte.class) {
					field.setInt(instance, 0);
				} else if (type == short.class) {
					field.setShort(instance, (short) 0);
				} else if (type == int.class) {
					field.setInt(instance, 0);
				} else if (type == float.class) {
					field.setFloat(instance, 0);
				} else if (type == double.class) {
					field.setDouble(instance, 0);
				} else if (type == long.class) {
					field.setDouble(instance, 0);
				} else if (type == char.class) {
					field.setChar(instance, (char) 0);
				}
				continue;
			}
			if (field.isAnnotationPresent(Json.Required.class) && field.getAnnotation(Json.Required.class).group().length == 0 && !field.isAnnotationPresent(Json.TypeValue.class)) {
				final String jsonName = JsonValidator.getFieldName(field);
				if (GsonHelper.isNull(object, jsonName)) {
					throw JsonValidator.createException("Missing (or invalid) required attribute '%s'", path, jsonName);
				}
			}
		}
		JsonValidator.validateRequiredGroups(object, values.stream().filter(field -> {
			if (!field.isAnnotationPresent(Json.Required.class)) {
				return false;
			}
			final Json.TypeValue annotation = field.getAnnotation(Json.TypeValue.class);
			final String controllerValue = controllerValues.get(annotation.controller());
			return controllerValue.equals(annotation.type());
		}).toArray(Field[]::new), path);
	}

	private StringTypeValidator() {
	}
}
