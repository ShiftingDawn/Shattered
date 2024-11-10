package shattered.lib.event;

import org.jetbrains.annotations.Nullable;
import shattered.lib.Internal;

public interface EventBus {

	static EventBus create(@Nullable final String name) {
		return Internal.EVENT_BUS_GENERATOR.apply(name);
	}

	static EventBus bus() {
		return EventBus.create(null);
	}

	void register(Object object);

	void unregister(Object object);

	void post(Event event);
}
