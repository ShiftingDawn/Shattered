package dawn.registry;

import dawn.Dawn;
import dawn.lib.MalformedIdentifierException;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

public final class Identifier {

	private static final String DOMAIN_SEPARATOR = ":";
	private static final String VARIANT_SEPARATOR = "#";
	private static final String DEFAULT_DOMAIN = Dawn.NAME_LOW;
	private static final String DEFAULT_VARIANT = "default";
	private final @Getter String domain;
	private final @Getter String path;
	private final @Getter String variant;
	private final String packed;

	private Identifier(String domain, String path, String variant) {
		this.domain = domain;
		this.path = path;
		this.variant = variant;
		this.packed = domain + DOMAIN_SEPARATOR + path + VARIANT_SEPARATOR + variant;
	}

	@Override
	public int hashCode() {
		return this.packed.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Identifier o && this.packed.equals(o.packed);
	}

	@Override
	public String toString() {
		return this.packed;
	}

	public static Identifier of(String str) {
		String[] domainAndPath = str.split(DOMAIN_SEPARATOR, 2);
		if (domainAndPath.length == 1) {
			domainAndPath = new String[] { DEFAULT_DOMAIN, domainAndPath[0] };
		}
		testDomain(str, domainAndPath[0]);
		final String domain = domainAndPath[0];
		String[] pathAndVariant = domainAndPath[1].split(VARIANT_SEPARATOR, 2);
		if (pathAndVariant.length == 1) {
			pathAndVariant = new String[] { pathAndVariant[0], DEFAULT_VARIANT };
		}
		testPath(str, pathAndVariant[0]);
		final String path = pathAndVariant[0];
		testVariant(str, pathAndVariant[1]);
		final String variant = pathAndVariant[1];
		return new Identifier(domain, path, variant);
	}

	private static void testDomain(String input, @Nullable String domain) {
		if (domain == null || domain.isBlank()) {
			throw new MalformedIdentifierException("Identifier %s has an invalid domain".formatted(input));
		}
		char invalidChar = testDomainChars(domain);
		if (invalidChar != 0) {
			throw new MalformedIdentifierException("Domain of identifier %s contains invalid character '%s'. Only lowercase letters, numbers and underscores are allowed"
				.formatted(input, invalidChar));
		}
	}

	private static char testDomainChars(String str) {
		for (char c : str.toCharArray()) {
			if (c >= '0' && c <= '9') continue;
			if (c >= 'a' && c <= 'z') continue;
			if (c != '_') return c;
		}
		return 0;
	}

	private static void testPath(String input, @Nullable String path) {
		if (path == null || path.isBlank()) {
			throw new MalformedIdentifierException("Identifier %s has an invalid path".formatted(input));
		}
		char invalidChar = testPathChars(path);
		if (invalidChar != 0) {
			throw new MalformedIdentifierException("Path of identifier %s contains invalid character '%s'. Only lowercase letters, numbers, forward slashes, dashes, underscores and periods are allowed"
				.formatted(input, invalidChar));
		}
	}

	private static char testPathChars(String str) {
		for (char c : str.toCharArray()) {
			if (c >= '0' && c <= '9') continue;
			if (c >= 'a' && c <= 'z') continue;
			if (c != '-' && c != '_' && c != '/' && c != '.') return c;
		}
		return 0;
	}

	private static void testVariant(String input, @Nullable String variant) {
		if (variant == null || variant.isBlank()) {
			throw new MalformedIdentifierException("Identifier %s has an invalid variant".formatted(input));
		}
		char invalidChar = testVariantChars(variant);
		if (invalidChar != 0) {
			throw new MalformedIdentifierException("Variant of identifier %s contains invalid character '%s'. Only lowercase letters and numbers are allowed"
				.formatted(input, invalidChar));
		}
	}

	private static char testVariantChars(String str) {
		for (char c : str.toCharArray()) {
			if (c >= '0' && c <= '9') continue;
			if (c >= 'a' && c <= 'z') continue;
			return c;
		}
		return 0;
	}
}
