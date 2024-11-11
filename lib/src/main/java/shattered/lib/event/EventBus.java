package shattered.lib.event;

import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;
import shattered.lib.Internal;

public interface EventBus {

	static EventBus create(@Nullable final String name) {
		return Internal.EVENT_BUS_GENERATOR.apply(name);
	}

	static EventBus bus() {
		return EventBus.create(null);
	}

	SubscriberToken register(Object object);

	<T extends Event> SubscriberToken register(final Consumer<T> consumer, int priority);

	default <T extends Event> SubscriberToken register(final Consumer<T> consumer) {
		return this.register(consumer, 0);
	}

	void post(Event event);
}
