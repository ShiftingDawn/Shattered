package dawn.input;

public enum EventResult {

	DEFAULT,
	CONSUME,
	IGNORE;

	public boolean consume(final boolean consumeOnDefault) {
		return this == EventResult.CONSUME || (this == EventResult.DEFAULT && consumeOnDefault);
	}
}
