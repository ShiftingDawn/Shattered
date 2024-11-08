package shattered.lib.event;

import shattered.lib.Internal;

public interface EventBus {

	static EventBus bus() {
		return Internal.DEFAULT_EVENT_BUS;
	}

	void register(Object instance);
}
