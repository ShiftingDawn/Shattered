package dawn.lib;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class RunOnce {

	private final AtomicBoolean ran = new AtomicBoolean(false);

	public void test(Supplier<String> msg) {
		if (this.ran.getAndSet(true)) {
			throw new IllegalStateException(msg.get());
		}
	}
}
