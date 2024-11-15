package shattered.lib.gui;

import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import shattered.lib.gfx.Tessellator;
import shattered.lib.util.TickType;

public final class GuiManager {

	private final Deque<GuiScreen> stack = new ConcurrentLinkedDeque<>();
	private GuiScreen lastFullScreen;

	public GuiManager() {
		this.ensureStackNotEmpty();
	}

	private void ensureStackNotEmpty() {
		if (this.stack.isEmpty()) {
			this.push(new GuiMainMenu());
			this.lastFullScreen = this.stack.peek();
		}
	}

	public void push(final GuiScreen screen) {
		if (this.stack.contains(screen)) {
			throw new IllegalStateException("Screen %s is already opened".formatted(screen.getClass().getName()));
		}
		this.stack.add(screen);
	}

	public void pop() {
		this.stack.pop();
		this.ensureStackNotEmpty();
	}

	public void popUntil(final Class<? extends GuiScreen> screenType) {
		while (!this.stack.isEmpty()) {
			final GuiScreen popped = this.stack.pop();
			if (screenType.isAssignableFrom(popped.getClass())) {
				break;
			}
		}
		this.ensureStackNotEmpty();
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
