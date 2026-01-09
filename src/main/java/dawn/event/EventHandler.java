package dawn.event;

import java.lang.ref.WeakReference;
import java.util.function.Consumer;

final class EventHandler<T extends Event> {

	private final Class<T> eventClass;
	private final WeakReference<Consumer<T>> listener;
	private final SubscriberToken token;

	EventHandler(Class<T> eventClass, Consumer<T> listener, SubscriberToken token) {
		this.eventClass = eventClass;
		this.listener = new WeakReference<>(listener);
		this.token = token;
	}

	@SuppressWarnings("unchecked")
	public void post(Event event) {
		if (this.eventClass.isAssignableFrom(event.getClass())) {
			Consumer<T> realListener = listener.get();
			if (realListener == null) {
				this.token.unsubscribe();
			} else {
				realListener.accept((T) event);
			}
		}
	}
}
