package shattered.lib.resource;

import java.io.Serial;

public final class InvalidTextureException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -6133740135342007959L;

	public InvalidTextureException(final String message) {
		super(message);
	}
}
