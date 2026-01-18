package dawn.core.event;

import java.util.function.Consumer;
import dawn.event.Event;
import dawn.event.SubscriberToken;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class EventHandler<T extends Event> {

	private final Class<T> eventClass;
	private final boolean exact;
	private final Consumer<T> listener;
	private final SubscriberToken token;

	@SuppressWarnings("unchecked")
	public void post(final Event event) {
		final boolean accepted = this.exact
			? this.eventClass == event.getClass()
			: this.eventClass.isAssignableFrom(event.getClass());
		if (accepted) {
			this.listener.accept((T) event);
		}
	}
}
