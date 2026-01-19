package dawn;

import java.util.function.Consumer;
import java.util.function.Supplier;
import dawn.asset.AssetManager;
import dawn.gfx.RenderManager;
import dawn.gfx.Window;
import dawn.gui.GuiManager;
import dawn.internal.DawnLib;
import dawn.lib.Localizer;
import dawn.lib.ResourceFinder;
import dawn.lib.ThrowingSupplier;
import org.jspecify.annotations.Nullable;

public interface Dawn {

	static Dawn getDawn() {
		return DawnLib.INSTANCE;
	}

	Window getWindow();

	ResourceFinder getResources();

	AssetManager getAssets();

	Localizer getLocalizer();

	//TODO move to window?
	RenderManager getRenderManager();

	//TODO move to window?
	GuiManager getGuiManager();

	static long clock() {
		return System.nanoTime() / 1000_000;
	}

	static <T> T make(final T obj, final Consumer<T> consumer) {
		consumer.accept(obj);
		return obj;
	}

	static <T> @Nullable T safeGet(final ThrowingSupplier<T> supplier, final Supplier<T> fallback) {
		try {
			return supplier.get();
		} catch (final Throwable ignored) {
			return fallback.get();
		}
	}

	static String makeKey(final Identifier id, final String type, @Nullable final String suffix) {
		String result = type + '.' + id.toString().replace(Identifier.DOMAIN_SEPARATOR_CHAR, '.').replace('/', '.');
		if (suffix != null) {
			result = result + '.' + suffix;
		}
		return result;
	}

	static String makeKey(final Identifier id, final String type) {
		return Dawn.makeKey(id, type, null);
	}
}
