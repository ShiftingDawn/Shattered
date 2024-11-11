package shattered.lib.util;

public final class StringUtil {

	public static boolean isNullOrEmpty(final String str) {
		return str == null || str.isBlank();
	}

	public static boolean isValidNamespace(final String str) {
		if (StringUtil.isNullOrEmpty(str)) {
			return false;
		}
		for (final char c : str.toCharArray()) {
			if (!((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '-' || c == '_')) {
				return false;
			}
		}
		return true;
	}

	public static boolean isValidResourcePath(final String str) {
		if (StringUtil.isNullOrEmpty(str)) {
			return false;
		}
		if (str.startsWith("/") || str.endsWith("/")) {
			return false;
		}
		for (final char c : str.toCharArray()) {
			if (!((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '-' || c == '_' || c == '.' || c == '/')) {
				return false;
			}
		}
		return true;
	}

	private StringUtil() {
	}
}
