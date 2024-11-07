package shattered.bootstrap;

import java.io.Serial;

final class BootstrapException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 650435120193447437L;

	public BootstrapException() {
		this.setStackTrace(new StackTraceElement[0]);
	}

	public BootstrapException(final String message) {
		super(message);
		this.setStackTrace(new StackTraceElement[0]);
	}

	public BootstrapException(final String message, final Throwable cause) {
		super(message, cause);
		this.setStackTrace(new StackTraceElement[0]);
	}

	public BootstrapException(final Throwable cause) {
		super(cause);
		this.setStackTrace(new StackTraceElement[0]);
	}

	public BootstrapException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.setStackTrace(new StackTraceElement[0]);
	}
}
