package shattered.core.event;

import java.util.function.Consumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import shattered.lib.event.SubscriberToken;

@RequiredArgsConstructor
final class SubscriberTokenImpl implements SubscriberToken {

	@Getter
	private final Object handler;
	private final Consumer<Object> unsubscribeFunction;

	@Override
	public void unsubscribe() {
		this.unsubscribeFunction.accept(this.handler);
	}
}
