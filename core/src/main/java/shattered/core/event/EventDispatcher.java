package shattered.core.event;

import shattered.lib.event.Event;

public interface EventDispatcher {

	void postEvent(Event event);
}
