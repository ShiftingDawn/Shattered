package dawn.core.event;

import java.util.function.Consumer;
import dawn.event.Event;
import dawn.event.SubscriberToken;

final class EventHandler<T extends Event> {

	private final Class<T> eventClass;
	private final Consumer<T> listener;
	private final SubscriberToken token;

	EventHandler(final Class<T> eventClass, final Consumer<T> listener, final SubscriberToken token) {
		this.eventClass = eventClass;
		this.listener = listener;
		this.token = token;
	}

	@SuppressWarnings("unchecked")
	public void post(final Event event) {
		if (this.eventClass.isAssignableFrom(event.getClass())) {
			this.listener.accept((T) event);
		}
	}
}
