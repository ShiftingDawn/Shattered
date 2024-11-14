package shattered.lib.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Utils {

	public static <T> T make(final T obj, final Consumer<T> consumer) {
		consumer.accept(obj);
		return obj;
	}

	public static <T> T safeGet(final ThrowingSupplier<T> supplier, final Supplier<T> fallback) {
		try {
			return supplier.get();
		} catch (final Throwable ignored) {
			return fallback.get();
		}
	}

	private Utils() {
	}
}
