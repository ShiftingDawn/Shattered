package dawn.core.lib.json;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.JsonObject;
import dawn.lib.GsonHelper;
import dawn.lib.Json;
import org.jspecify.annotations.Nullable;
import static dawn.core.lib.json.JsonValidatorImpl.createException;
import static dawn.core.lib.json.JsonValidatorImpl.getFieldName;

final class BooleanControlledFieldValidator {

	static void validateRequiredByBooleanFields(final JsonObject object, final Field[] fields, @Nullable final String path) {
		final Field[] valueFields = JsonValidatorImpl.filterFields(fields, field -> field.isAnnotationPresent(Json.Required.When.class) || field.isAnnotationPresent(Json.Required.WhenNot.class));
		if (valueFields.length == 0) {
			return;
		}
		//Test true
		final Map<Field, List<Field>> fieldsTrue = BooleanControlledFieldValidator.collectFields(fields, valueFields, true, path);
		for (final Map.Entry<Field, List<Field>> entry : fieldsTrue.entrySet()) {
			final Field valueField = entry.getKey();
			final String valueFieldName = getFieldName(valueField);
			final List<Field> requiredFields = entry.getValue();
			if (!GsonHelper.hasBoolean(object, valueFieldName) || !GsonHelper.getBoolean(object, valueFieldName)) {
				//Skip checking when field doesn't exist or is false
				continue;
			}
			for (final Field requiredField : requiredFields) {
				final String requiredFieldName = getFieldName(requiredField);
				if (!GsonHelper.isNull(object, requiredFieldName)) {
					continue;
				}
				throw createException("Field '%s' is required when field '%s' is true", path, requiredFieldName, valueFieldName);
			}
		}
		//Test False
		final Map<Field, List<Field>> fieldsFalse = BooleanControlledFieldValidator.collectFields(fields, valueFields, false, path);
		for (final Map.Entry<Field, List<Field>> entry : fieldsFalse.entrySet()) {
			final Field valueField = entry.getKey();
			final String valueFieldName = getFieldName(valueField);
			final List<Field> requiredFields = entry.getValue();
			if (GsonHelper.hasBoolean(object, valueFieldName) && GsonHelper.getBoolean(object, valueFieldName)) {
				//Skip checking when the field is true
				continue;
			}
			for (final Field requiredField : requiredFields) {
				final String requiredFieldName = getFieldName(requiredField);
				if (!GsonHelper.isNull(object, requiredFieldName)) {
					continue;
				}
				throw createException("Field '%s' is required when field '%s' is false", path, requiredFieldName, valueFieldName);
			}
		}
	}

	private static Map<Field, List<Field>> collectFields(final Field[] allFields, final Field[] valueFields, final boolean expected, @Nullable final String path) {
		final Map<Field, List<Field>> result = new HashMap<>();
		for (final Field field : valueFields) {
			final String expectedFieldName;
			if (expected && field.isAnnotationPresent(Json.Required.When.class)) {
				expectedFieldName = field.getAnnotation(Json.Required.When.class).fieldName();
			} else if (field.isAnnotationPresent(Json.Required.WhenNot.class)) {
				expectedFieldName = field.getAnnotation(Json.Required.WhenNot.class).fieldName();
			} else {
				continue;
			}
			if (expectedFieldName.isBlank()) {
				throw createException("Field '%s' contains invalid RequiredWhen fieldName '%s'", path, getFieldName(field), expectedFieldName);
			}
			final Field[] expectedFields = JsonValidatorImpl.filterFields(allFields, f -> expectedFieldName.equals(getFieldName(f)));
			if (expectedFields.length == 0) {
				throw createException("Field '%s' contains invalid RequiredWhen fieldName '%s'", path, getFieldName(field), expectedFieldName);
			}
			result.computeIfAbsent(expectedFields[0], _ -> new ArrayList<>()).add(field);
		}
		return result;
	}

	private BooleanControlledFieldValidator() {
	}
}
