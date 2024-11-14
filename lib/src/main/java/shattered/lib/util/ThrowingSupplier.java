package shattered.lib.util;

@FunctionalInterface
public interface ThrowingSupplier<T> {

	T get() throws Throwable;

}
