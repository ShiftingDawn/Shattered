package shattered.lib.gui;

import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import shattered.lib.event.EventBus;
import shattered.lib.event.SubscriberToken;
import shattered.lib.gfx.Tessellator;
import shattered.lib.util.TickType;

public final class GuiManager {

	private final Deque<GuiScreen> stack = new ConcurrentLinkedDeque<>();
	private SubscriberToken currentSubscriber;
	private GuiScreen lastFullScreen;

	public GuiManager() {
		this.postStackUpdate();
	}

	private void postStackUpdate() {
		if (this.stack.isEmpty()) {
			this.push(new GuiMainMenu());
			this.lastFullScreen = this.stack.peek();
		}
		if (this.currentSubscriber == null) {
			this.currentSubscriber = EventBus.bus().register(this.stack.peek());
		}
	}

	public void push(final GuiScreen screen) {
		if (this.stack.contains(screen)) {
			throw new IllegalStateException("Screen %s is already opened".formatted(screen.getClass().getName()));
		}
		if (this.currentSubscriber != null) {
			this.currentSubscriber.unsubscribe();
			this.currentSubscriber = null;
		}
		this.stack.add(screen);
		this.postStackUpdate();
	}

	public void pop() {
		if (this.currentSubscriber != null) {
			this.currentSubscriber.unsubscribe();
			this.currentSubscriber = null;
		}
		this.stack.pop();
		this.postStackUpdate();
	}

	public void popUntil(final Class<? extends GuiScreen> screenType) {
		if (this.currentSubscriber != null) {
			this.currentSubscriber.unsubscribe();
			this.currentSubscriber = null;
		}
		while (!this.stack.isEmpty()) {
			final GuiScreen popped = this.stack.pop();
			if (screenType.isAssignableFrom(popped.getClass())) {
				break;
			}
		}
		this.postStackUpdate();
	}

	public void tick() {
		final Iterator<GuiScreen> iterator = this.stack.descendingIterator();
		int i = 0;
		while (iterator.hasNext()) {
			final GuiScreen screen = iterator.next();
			screen.tick(i++ == 0 ? TickType.ACTIVE : TickType.INACTIVE);
		}
	}

	public void render(final Tessellator tessellator) {
		final Iterator<GuiScreen> iterator = this.stack.descendingIterator();
		while (iterator.hasNext()) {
			final GuiScreen screen = iterator.next();
			screen.render(tessellator);
			if (screen == this.lastFullScreen) {
				break;
			}
		}
	}
}
