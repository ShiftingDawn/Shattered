package dawn.event;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class EventBus {

	private static final ConcurrentHashMap<SubscriberToken, EventHandler<?>> LISTENERS = new ConcurrentHashMap<>();

	public static <T extends Event> SubscriberToken register(Class<T> eventClass, Consumer<T> listener) {
		SubscriberToken token = new SubscriberToken();
		LISTENERS.put(token, new EventHandler<>(eventClass, listener, token));
		return token;
	}

	static void remove(SubscriberToken token) {
		LISTENERS.remove(token);
	}

	public static void post(Event event) {
		for (EventHandler<?> handler : LISTENERS.values()) {
			handler.post(event);
		}
	}

	private EventBus() {
	}
}
