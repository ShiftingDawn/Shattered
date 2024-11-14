package shattered.lib.registry;

import java.util.Locale;
import java.util.Objects;
import lombok.Getter;
import shattered.lib.Internal;
import shattered.lib.util.StringUtil;

public final class Identifier {

	public static final String DEFAULT_NAMESPACE = Internal.NAME.toLowerCase(Locale.ROOT);
	public static final String DEFAULT_VARIANT = "";
	public static final String SEPARATOR = ":";
	public static final String VARIANT_CHAR = "#";
	@Getter
	private final String namespace;
	@Getter
	private final String path;
	@Getter
	private final String variant;
	private final String compacted;

	private Identifier(final String namespace, final String path, final String variant) {
		this.namespace = namespace;
		this.path = path;
		this.variant = variant;
		this.compacted = namespace + Identifier.SEPARATOR + path + (!StringUtil.isNullOrEmpty(variant) ? (Identifier.VARIANT_CHAR + variant) : "");
	}

	public Identifier toVariant(final String variant) {
		if (!StringUtil.isNullOrEmpty(Objects.requireNonNull(variant)) && !StringUtil.isValidNamespace(variant)) {
			throw new MalformedIdentifierException(variant, 2);
		}
		return new Identifier(this.namespace, this.path, variant);
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
		return this.compacted;
	}

	private static String[] make(final String str) {
		String namespace = Identifier.DEFAULT_NAMESPACE;
		String path = str;
		String variant = Identifier.DEFAULT_VARIANT;
		if (path.contains(Identifier.SEPARATOR)) {
			final String[] parts = path.split(Identifier.SEPARATOR, 2);
			namespace = parts[0];
			path = parts[1];
		}
		if (path.contains(Identifier.VARIANT_CHAR)) {
			final String[] parts = path.split(Identifier.VARIANT_CHAR, 2);
			path = parts[0];
			variant = parts[1];
		}
		return new String[] { namespace, path, variant };
	}

	public static Identifier of(final String namespace, final String path, final String variant) {
		if (!StringUtil.isValidNamespace(Objects.requireNonNull(namespace))) {
			throw new MalformedIdentifierException(namespace, 0);
		}
		if (!StringUtil.isValidResourcePath(Objects.requireNonNull(path))) {
			throw new MalformedIdentifierException(path, 1);
		}
		if (!StringUtil.isNullOrEmpty(Objects.requireNonNull(variant)) && !StringUtil.isValidNamespace(variant)) {
			throw new MalformedIdentifierException(variant, 2);
		}
		return new Identifier(namespace, path, variant);
	}

	public static Identifier of(final String namespace, final String path) {
		return Identifier.of(namespace, path, Identifier.DEFAULT_VARIANT);
	}

	public static Identifier of(final String str) {
		final String[] parts = Identifier.make(Objects.requireNonNull(str));
		return Identifier.of(parts[0], parts[1], parts[2]);
	}
}
