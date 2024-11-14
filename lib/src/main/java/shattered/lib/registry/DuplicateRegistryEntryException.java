package shattered.lib.registry;

import java.io.Serial;

public final class DuplicateRegistryEntryException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -7471916133502712632L;

	public DuplicateRegistryEntryException(final String registry, final Identifier name) {
		super("Duplicate entry '%s' in registry '%s'".formatted(name, registry));
	}
}
