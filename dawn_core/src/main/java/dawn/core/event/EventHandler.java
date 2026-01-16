package dawn.core.event;

import java.lang.ref.WeakReference;
import java.util.function.Consumer;
import dawn.event.Event;
import dawn.event.SubscriberToken;

final class EventHandler<T extends Event> {

	private final Class<T> eventClass;
	private final WeakReference<Consumer<T>> listener;
	private final SubscriberToken token;

	EventHandler(final Class<T> eventClass, final Consumer<T> listener, final SubscriberToken token) {
		this.eventClass = eventClass;
		this.listener = new WeakReference<>(listener);
		this.token = token;
	}

	@SuppressWarnings("unchecked")
	public void post(final Event event) {
		if (this.eventClass.isAssignableFrom(event.getClass())) {
			final Consumer<T> realListener = this.listener.get();
			if (realListener == null) {
				this.token.unsubscribe();
			} else {
				realListener.accept((T) event);
			}
		}
	}
}
