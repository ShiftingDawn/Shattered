package dawn.event;

public final class SubscriberToken {

	SubscriberToken() {
	}

	void unsubscribe() {
		EventBus.remove(this);
	}
}
