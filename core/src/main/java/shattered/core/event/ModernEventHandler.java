package shattered.core.event;

import java.util.function.Consumer;
import shattered.lib.event.Event;

final class ModernEventHandler extends EventHandler {

	private final Consumer<Event> consumer;

	public ModernEventHandler(final Class<?> filteredEventType, final int priority, final Consumer<Event> consumer) {
		super(filteredEventType, null, priority);
		this.consumer = consumer;
	}

	@Override
	public void postEvent(final Event event) {
		this.consumer.accept(event);
	}
}
