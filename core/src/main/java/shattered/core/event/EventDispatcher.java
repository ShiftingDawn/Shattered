package shattered.core.event;

import shattered.lib.event.Event;

interface EventDispatcher {

	void postEvent(Event event);
}
