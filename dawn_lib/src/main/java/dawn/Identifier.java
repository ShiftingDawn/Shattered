package dawn;

import java.util.function.UnaryOperator;
import dawn.internal.DawnLib;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

public final class Identifier {

	public static final char DOMAIN_SEPARATOR_CHAR = ':';
	public static final String DOMAIN_SEPARATOR = String.valueOf(Identifier.DOMAIN_SEPARATOR_CHAR);
	public static final String DEFAULT_DOMAIN = DawnLib.IDENTIFIER_DEFAULT_DOMAIN;
	private final @Getter String domain;
	private final @Getter String path;
	private final String packed;

	private Identifier(final String domain, final String path) {
		this.domain = domain;
		this.path = path;
		this.packed = domain + Identifier.DOMAIN_SEPARATOR + path;
	}

	public Identifier withPath(final String newPath) {
		return new Identifier(this.domain, newPath);
	}

	public Identifier withPath(final UnaryOperator<String> pathModifier) {
		return new Identifier(this.domain, pathModifier.apply(this.path));
	}

	public Identifier withPrefix(final String pathPrefix) {
		return this.withPath(pathPrefix + this.path);
	}

	public Identifier withSuffix(final String pathSuffix) {
		return this.withPath(this.path + pathSuffix);
	}

	@Override
	public int hashCode() {
		return this.packed.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof final Identifier o && this.packed.equals(o.packed);
	}

	@Override
	public String toString() {
		return this.packed;
	}

	public String toPathSafeString() {
		return this.toString().replaceAll(Identifier.DOMAIN_SEPARATOR, "_");
	}

	public static Identifier of(final String str) {
		String[] domainAndPath = str.split(Identifier.DOMAIN_SEPARATOR, 2);
		if (domainAndPath.length == 1) {
			domainAndPath = new String[] { Identifier.DEFAULT_DOMAIN, domainAndPath[0] };
		}
		Identifier.testDomain(str, domainAndPath[0]);
		final String domain = domainAndPath[0];
		Identifier.testPath(str, domainAndPath[1]);
		final String path = domainAndPath[1];
		return new Identifier(domain, path);
	}

	public static Identifier of(final String domain, final String path) {
		return Identifier.of(domain + Identifier.DOMAIN_SEPARATOR + path);
	}

	private static void testDomain(final String input, @Nullable final String domain) {
		if (domain == null || domain.isBlank()) {
			throw new MalformedIdentifierException("Identifier %s has an invalid domain".formatted(input));
		}
		final char invalidChar = Identifier.testDomainChars(domain);
		if (invalidChar != 0) {
			throw new MalformedIdentifierException("Domain of identifier %s contains invalid character '%s'. Only lowercase letters, numbers and underscores are allowed"
				.formatted(input, invalidChar));
		}
	}

	private static char testDomainChars(final String str) {
		for (final char c : str.toCharArray()) {
			if (c >= '0' && c <= '9') {
				continue;
			}
			if (c >= 'a' && c <= 'z') {
				continue;
			}
			if (c != '_') {
				return c;
			}
		}
		return 0;
	}

	private static void testPath(final String input, @Nullable final String path) {
		if (path == null || path.isBlank()) {
			throw new MalformedIdentifierException("Identifier %s has an invalid path".formatted(input));
		}
		final char invalidChar = Identifier.testPathChars(path);
		if (invalidChar != 0) {
			throw new MalformedIdentifierException("Path of identifier %s contains invalid character '%s'. Only lowercase letters, numbers, forward slashes, dashes, underscores and periods are allowed"
				.formatted(input, invalidChar));
		}
	}

	private static char testPathChars(final String str) {
		for (final char c : str.toCharArray()) {
			if (c >= '0' && c <= '9') {
				continue;
			}
			if (c >= 'a' && c <= 'z') {
				continue;
			}
			if (c != '-' && c != '_' && c != '/' && c != '.') {
				return c;
			}
		}
		return 0;
	}
}
