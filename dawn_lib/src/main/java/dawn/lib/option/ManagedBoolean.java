package dawn.lib.option;

import java.util.function.BooleanSupplier;
import dawn.lib.BooleanConsumer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ManagedBoolean implements BooleanSupplier, BooleanConsumer {

	private final BooleanSupplier get;
	private final BooleanConsumer set;

	public static ManagedBoolean of(final BooleanSupplier get, final BooleanConsumer set) {
		return new ManagedBoolean(get, set);
	}

	@Override
	public void accept(final boolean b) {
		this.set.accept(b);
	}

	@Override
	public boolean getAsBoolean() {
		return this.get.getAsBoolean();
	}
}
