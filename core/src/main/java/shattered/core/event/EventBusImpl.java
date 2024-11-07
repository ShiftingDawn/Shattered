package shattered.core.event;

import shattered.lib.Internal;
import shattered.lib.event.EventBus;

public class EventBusImpl implements EventBus {

	public static final EventBusImpl INSTANCE = new EventBusImpl();

	public static void init() {
		Internal.DEFAULT_EVENT_BUS = INSTANCE;
	}
}
