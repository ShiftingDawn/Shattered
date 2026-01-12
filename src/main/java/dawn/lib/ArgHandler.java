package dawn.lib;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import static dawn.Dawn.LOGGER;

public final class ArgHandler {

	public final int displayWidth;
	public final int displayHeight;

	public ArgHandler(final String[] args) {
		final Map<String, @Nullable String> map = new HashMap<>();
		for (int i = 0; i < args.length; ++i) {
			final String[] split = args[i].split("=", 2);
			String key = split[0].toLowerCase(Locale.ROOT);
			if (key.length() == 2 && key.charAt(0) == '-') {
				key = String.valueOf(key.charAt(1));
			} else if (key.startsWith("--")) {
				key = key.substring(2);
			}
			map.put(key, split.length == 2 ? split[1] : null);
		}
		this.displayWidth = this.getInt(map, 800, "w", "width");
		this.displayHeight = this.getInt(map, 600, "h", "height");
	}

	private @Nullable Tuple<String, @Nullable String> find(final Map<String, @Nullable String> map, final String... keys) {
		for (final String key : keys) {
			if (map.containsKey(key)) {
				return new Tuple<>(key, map.get(key));
			}
		}
		return null;
	}

	private int getInt(final Map<String, @Nullable String> map, final int defaultValue, final String... keys) {
		final Tuple<String, String> found = this.find(map, keys);
		if (found == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(found.b());
		} catch (final NumberFormatException e) {
			LOGGER.fatal("Argument '{}' has invalid value '{}'.", found.a(), found.b());
			throw new ExitException();
		}
	}
}
