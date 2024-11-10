package shattered.core;

import java.io.Serial;

public final class ExitShatteredException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 7109946973493719259L;

	public ExitShatteredException() {
	}

	public ExitShatteredException(final String message) {
		super(message);
	}
}
