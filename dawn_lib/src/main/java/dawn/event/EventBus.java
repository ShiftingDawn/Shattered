package dawn.event;

import java.util.function.Consumer;
import dawn.internal.DawnLib;

public interface EventBus {

	static EventBus bus() {
		return DawnLib.BUS;
	}

	<T extends Event> SubscriberToken register(final Class<T> eventClass, boolean exact, Object owner, final Consumer<T> listener);

	default <T extends Event> SubscriberToken register(final Class<T> eventClass, final Object owner, final Consumer<T> listener) {
		return this.register(eventClass, false, owner, listener);
	}

	default <T extends Event> SubscriberToken register(final Class<T> eventClass, final boolean exact, final Consumer<T> listener) {
		return this.register(eventClass, exact, listener, listener);
	}

	default <T extends Event> SubscriberToken register(final Class<T> eventClass, final Consumer<T> listener) {
		return this.register(eventClass, false, listener);
	}

	void remove(SubscriberToken token);

	void post(Event event);
}
