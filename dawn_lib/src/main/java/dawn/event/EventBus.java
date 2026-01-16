package dawn.event;

import java.util.function.Consumer;
import dawn.internal.DawnLib;

public interface EventBus {

	static EventBus bus() {
		return DawnLib.BUS;
	}

	<T extends Event> SubscriberToken register(final Class<T> eventClass, final Consumer<T> listener);

	void remove(SubscriberToken token);

	void post(Event event);
}
