package dawn.lib;

import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

public final class Util {

	public static <T> T make(final T obj, final Consumer<T> mod) {
		mod.accept(obj);
		return obj;
	}

	public static <T> @Nullable T safeGet(final ThrowingSupplier<T> supplier, final Supplier<T> fallback) {
		try {
			return supplier.get();
		} catch (final Throwable ignored) {
			return fallback.get();
		}
	}

	public static <T> T makeIf(final T obj, final boolean flag, final Consumer<T> mod) {
		if (flag) {
			mod.accept(obj);
		}
		return obj;
	}

	private Util() {
	}
}
