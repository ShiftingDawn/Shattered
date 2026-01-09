package dawn.lib;

import java.util.function.Consumer;

public final class Util {

	public static <T> T make(T obj, Consumer<T> mod) {
		mod.accept(obj);
		return obj;
	}

	private Util() {
	}
}
