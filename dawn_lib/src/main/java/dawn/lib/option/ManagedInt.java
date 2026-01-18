package dawn.lib.option;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ManagedInt implements IntSupplier, IntConsumer {

	private final IntSupplier get;
	private final IntConsumer set;

	public static ManagedInt of(final IntSupplier get, final IntConsumer set) {
		return new ManagedInt(get, set);
	}

	@Override
	public void accept(final int i) {
		this.set.accept(i);
	}

	@Override
	public int getAsInt() {
		return this.get.getAsInt();
	}
}
