package dawn;

import java.io.Serial;

public final class MalformedIdentifierException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 5148111260703825424L;

	MalformedIdentifierException(final String message) {
		super(message);
	}
}
