package dawn.core.event;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import dawn.event.Event;
import dawn.event.EventBus;
import dawn.event.SubscriberToken;

public final class EventBusImpl implements EventBus {

	private static final ConcurrentHashMap<SubscriberToken, EventHandler<?>> LISTENERS = new ConcurrentHashMap<>();

	@Override
	public <T extends Event> SubscriberToken register(final Class<T> eventClass, final Consumer<T> listener) {
		final SubscriberToken token = new SubscriberTokenImpl();
		EventBusImpl.LISTENERS.put(token, new EventHandler<>(eventClass, listener, token));
		return token;
	}

	@Override
	public void remove(final SubscriberToken token) {
		EventBusImpl.LISTENERS.remove(token);
	}

	@Override
	public void post(final Event event) {
		for (final EventHandler<?> handler : EventBusImpl.LISTENERS.values()) {
			handler.post(event);
		}
	}
}
