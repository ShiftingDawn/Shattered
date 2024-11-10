package shattered.lib.util;

import java.util.function.Consumer;

public final class Utils {

	public static <T> T make(T obj, Consumer<T> consumer) {
		consumer.accept(obj);
		return obj;
	}

	private Utils() {
	}
}
