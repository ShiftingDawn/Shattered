package shattered.lib.util.gson;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Json {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface Ignore {

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface Required {

		OR[] group() default {};

		@Retention(RetentionPolicy.RUNTIME)
		@Target(ElementType.FIELD)
		@interface OR {

			String groupName();

			String groupIndex();
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface TypeControllerString {

		String controller() default "type";

		String[] accepted();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface TypeValue {

		String type();

		String controller() default "type";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface TypeControllerEnum {

		String type() default "type";
	}
}
