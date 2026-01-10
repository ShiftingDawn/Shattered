package dawn.lib;

import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface ThrowingSupplier<T> {

	@Nullable T get() throws Throwable;

}
