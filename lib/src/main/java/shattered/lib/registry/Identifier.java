package shattered.lib.registry;

import java.util.Locale;
import java.util.Objects;
import lombok.Getter;
import shattered.lib.Internal;
import shattered.lib.util.StringUtil;

@Getter
public final class Identifier {

	public static final String DEFAULT_NAMESPACE = Internal.NAME.toLowerCase(Locale.ROOT);
	public static final String SEPARATOR = ":";
	private final String namespace;
	private final String path;

	private Identifier(final String namespace, final String path) {
		this.namespace = namespace;
		this.path = path;
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof final Identifier id && id.toString().equals(this.toString());
	}

	@Override
	public String toString() {
		return this.namespace + Identifier.SEPARATOR + this.path;
	}

	private static String[] make(final String str) {
		return str.contains(Identifier.SEPARATOR) ? str.split(Identifier.SEPARATOR, 2) : new String[] { Identifier.DEFAULT_NAMESPACE, str };
	}

	public static Identifier of(final String namespace, final String path) {
		if (!StringUtil.isValidNamespace(Objects.requireNonNull(namespace))) {
			throw new MalformedIdentifierException(namespace, true);
		}
		if (!StringUtil.isValidResourcePath(Objects.requireNonNull(path))) {
			throw new MalformedIdentifierException(path, false);
		}
		return new Identifier(namespace, path);
	}

	public static Identifier of(final String str) {
		final String[] parts = Identifier.make(Objects.requireNonNull(str));
		return Identifier.of(parts[0], parts[1]);
	}
}
