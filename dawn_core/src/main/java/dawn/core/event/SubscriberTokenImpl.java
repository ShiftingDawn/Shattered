package dawn.core.event;

import dawn.event.EventBus;
import dawn.event.SubscriberToken;

final class SubscriberTokenImpl implements SubscriberToken {

	@Override
	public void unsubscribe() {
		EventBus.bus().remove(this);
	}
}
